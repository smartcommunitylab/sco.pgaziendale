import axios from "axios";
import { statsConfigurations } from "../pages/stats/statsConfigurations";
// import { VARIABLES } from "../variables";
import { campaignService, locationService } from '.';
// import { companyService } from ".";
import { employeeService } from ".";
// import { viewStatService } from "./viewStat.services";


export const statService = {
  setActiveViewType,
  getActiveConfiguration,
  getConfigurationByUser,
  getStat,
  getCsv,
  getItemsAggregation,
};


function setActiveViewType(activeViewType) {
  return Promise.resolve(activeViewType);
}

function getActiveConfiguration(configurationId) {
  return Promise.resolve(configurationId);
}

function getConfigurationByUser(user, temporaryAdmin) {
  let array = [];
  let profile = '';
  if (temporaryAdmin) profile = 'company';
  else if (user.canDo('view', 'stats')) profile = 'global';
  statsConfigurations.forEach((config) => {
    if (config.profile === profile) {
      array.push(config);
    }
  });
  return Promise.resolve(array);
}
function getItemsAggregation(itemAggregationValue, campaignId, companyId) {
  switch (itemAggregationValue) {
    case "EMPLOYEES":
      return employeeService.getAllEmployees(companyId).then((content) => {
        return content.filter(e => e.campaigns.indexOf(campaignId) >= 0)
        .map(e => {
          e.label = (e.surname && e.surname != '-') 
                  ? `${e.surname} ${e.name}` 
                  : (e.name && e.name != '-') 
                  ? `${e.name}`
                  : (e.code)
                  ? `- - (${e.code})`
                  : `${e.id}`;
          return e;
        });
      });
    case "LOCATIONS":
      return locationService.getAllLocations(companyId).then(content => {
        return content.map(l => {
          l.label = l.id;
          return l;
        });
  
      })
    case "COMPANIES":
      return campaignService.getAllCompaniesOfCampaign(campaignId).then(content => {
          return content.map(c => {
            c.label = c.name;
            return c;
          });
        });
    default:
      return Promise.resolve(null);

  }

}

/**
 * Call API with filters, aggregations, and fields. Convert data to flat representation with mode fields exploded
 * @param {*} campaignId 
 * @param {*} companyId 
 * @param {*} location 
 * @param {*} means
 * @param {*} employeeId 
 * @param {*} way
 * @param {*} timeGroupBy 
 * @param {*} dataGroupBy 
 * @param {*} from 
 * @param {*} to 
 * @param {*} fields 
 * @param {*} groupByMean 
 * @param {*} all 
 * @param {*} csv
 * @returns 
 */
function callStatsAPI(
  campaignId, 
  companyId, 
  location, 
  means,
  employeeId, 
  way,
  timeGroupBy, 
  dataGroupBy, 
  from, 
  to, 
  fields,
  groupByMean, 
  all, 
  csv) {
  let params = {
    ...(companyId ? { companyId } : {}),
    ...(location ? { location } : {}),
    ...(employeeId ? { employeeId } : {}),
    ...(means ? { means: means.join(',') } : {}),
    ...(way ? { way } : {}),
    ...(groupByMean ? { groupByMean } : {}),
    ...(from ? { from } : {}),
    ...(to ? { to } : {}),
    ...(timeGroupBy ? { timeGroupBy } : {}),
    ...(dataGroupBy ? { dataGroupBy } : {}),
    ...(fields ? { fields } : {fields: 'score'}),
    ...(all ? { all } : {}),
  }

  if (csv) {
    return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/track/csv",
      { params }
    )
    .then(res => {
      if (res && res.data) return Promise.resolve(res.data);
      else return Promise.reject(null);
    })  
    .catch(()=>{
      Promise.resolve(null);
    })
  }

  return axios
  .get(
    process.env.VUE_APP_BASE_URL +
    process.env.VUE_APP_CAMPAIGNS_API +
    "/" +
    campaignId +
    "/stats/track",
    { params }
  )
  .then(res => {
    let map = {};
    res.data.forEach(d => {
      d = flattenStat(d, timeGroupBy, fields, means, groupByMean);
      let key = d.name || '';
      if (!map[key]) {
        map[key] = [];
      }
      map[key].push(d);
    });
    let array = Object.keys(map).map(id => {
      return {key: id, values: map[id]};
    });
    array.sort((a,b) => a.key.localeCompare(b.key));
    return array;
  })
  .catch(()=>{
    Promise.resolve(null);
  })
}

/**
 * Convert the data structure received from service to a flat object with mode fields exploded
 * @param {*} ds 
 * @returns 
 */
function flattenStat(ds, timeGroupBy, fields, means, groupByMean) {
  ds.stats = ds.stats || {};
  let res = {campaign: ds.campaign, id: ds.dataGroup, name: ds.dataGroupName || ds.dataGroup};
  res[timeGroupBy] = mapTimeLabel(timeGroupBy, ds.timeGroup);
  for (let field of fields.split(',')) {
    res[field] = adjustValue(ds.stats[field] || 0, field);
    if (groupByMean && means && means.length > 0 && ds.meanStatMap) {
      means.forEach(m => {
        res[m + '_mean_' + field] = adjustValue((ds.meanStatMap[m] || {})[field] || 0, field);
      });
    }
  }
  return res;
}

function mapTimeLabel(timeGroupBy, timeGroup) {
  if (timeGroupBy === 'dayOfWeek') {
    const days = {"MONDAY": "Lunedì", "TUESDAY": "Martedì", "WEDNESDAY": "Mercoledì", "THURSDAY": "Giovedì", "FRIDAY": "Venerdì", "SATURDAY": "Sabato", "SUNDAY": "Domenica"};
    return days[timeGroup];
  }
  return timeGroup;
}

function adjustValue(value, field) {
  if (field === 'distance') {
    return (value / 1000); //convert to km
  }
  if (field === 'duration') {
    return (value / 3600); //convert to hours
  }
  return value;
}
/**
 * Prepare fields parameter given the configuration
 * @param {*} configuration 
 * @returns 
 */
function getFields(configuration) {
  return configuration.dataColumns.map(c => {
    return c.value;
  })
  .filter(c => !!c)
  .join(',');
}


function getAllLocationsStats(configuration) {
  console.log('getAllLocationsStats', configuration);
  return callStatsAPI(
    configuration.campaign.id, // campaignId
    configuration.company.id,  // companyId
    null,                      // location
    configuration.means,       // means
    null,                      // employeeId
    configuration.direction,   // way
    configuration.timeUnit.apiField, // timeGroupBy
    'location',                      // dataGroupBy
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null, // from
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,     // to 
    getFields(configuration),      // fields
    configuration.groupByMean,     // groupByMean
    true,                          // all
    configuration.csv              // csv    
    );
}

function getAllEmployeesStats(configuration) {
  console.log('getAllEmployeesStat', configuration);
  return callStatsAPI(
    configuration.campaign.id, // campaignId
    configuration.company.id,  // companyId
    null,                      // location
    configuration.means,       // means
    null,                      // employeeId
    configuration.direction,   // way
    configuration.timeUnit.apiField, // timeGroupBy
    'employee',                      // dataGroupBy
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null, // from
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,     // to
    getFields(configuration),   // fields
    configuration.groupByMean,     // groupByMean
    true,                          // all
    configuration.csv              // csv    
    );
}

function getCompanyStats(configuration) {
  console.log('getCompanyStats', configuration);
  return callStatsAPI(
    configuration.campaign.id, // campaignId
    configuration.company.id,  // companyId
    null,                      // location
    configuration.means,       // means
    null,                      // employeeId
    configuration.direction,   // way
    configuration.timeUnit.apiField, // timeGroupBy
    'company',                      // dataGroupBy
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null, // from
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,     // to
    getFields(configuration),   // fields
    configuration.groupByMean,     // groupByMean
    false,                          // all
    configuration.csv              // csv 
    );

}

function aggregateByEmployeeStat(configuration) {
  console.log('aggregateByEmployeeStat', configuration);
  return callStatsAPI(
    configuration.campaign.id, // campaignId
    configuration.company.id,  // companyId
    null,                      // location
    configuration.means,       // means
    configuration.puntualAggregationItems.map(i => i.id).join(','), 
    configuration.direction,   // way
    configuration.timeUnit.apiField,  // timeGroupBy
    'employee', // dataGroupBy
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null, // from
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,  
    getFields(configuration), // fields
    configuration.groupByMean,     // groupByMean
    false,                    // all
    configuration.csv         // csv
  );  
}
function aggregateByLocationStat(configuration) {
  console.log('aggregateByLocationStat', configuration);
  return callStatsAPI(
    configuration.campaign.id, // campaignId
    configuration.company.id,  // companyId
    null,                      // location
    configuration.means,       // means
    null,                      // employeeId
    configuration.direction,   // way
    configuration.timeUnit.apiField, // timeGroupBy
    'location',                      // dataGroupBy
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null, // from
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,     // to 
    getFields(configuration),      // fields
    configuration.groupByMean,     // groupByMean
    true,                          // all
    configuration.csv              // csv    
  );  
}

function getCampaignCompanyStats(configuration) {
  console.log('getCampaignCompanyStats', configuration);
  if (!configuration.campaign.id) {
    return Promise.resolve([]);
  }

  return callStatsAPI(
    configuration.campaign.id, 
    null,
    null,  
    configuration.means,       // means
    null, 
    configuration.direction, 
    configuration.timeUnit.apiField, 
    'company', 
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
    getFields(configuration),
    configuration.groupByMean,
    true,
    configuration.csv
    );
}

function getCampaignStats(configuration) {
  console.log('getCampaignStats', configuration);
  if (!configuration.campaign.id) {
    return Promise.resolve([]);
  }
  return callStatsAPI(
    configuration.campaign.id, 
    null, 
    null, 
    configuration.means,       // means
    null,
    configuration.direction, 
    configuration.timeUnit.apiField, 
    'campaign', 
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
    getFields(configuration),
    configuration.groupByMean,
    false,
    configuration.csv
    );
}

function aggregateCompanyStat(configuration) {
  let arrayRequest = [];
  for (let companyId = 0; companyId < configuration.puntualAggregationItems.length; companyId++) {
    arrayRequest.push(callStatsAPI(
      configuration.campaign.id, 
      configuration.puntualAggregationItems[companyId].id, 
      null, 
      configuration.means,       // means
      null,
      configuration.direction, 
      configuration.timeUnit.apiField, 
      'company', 
      configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
      configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
      getFields(configuration),
      configuration.groupByMean,
      true,
      configuration.csv
    ));
  }
  return axios.all(arrayRequest).then(responses => {
    let res = [];
    for (let companyId = 0; companyId < configuration.puntualAggregationItems.length; companyId++) {
      if (responses[companyId].length === 0) {
        res.push({key: configuration.puntualAggregationItems[companyId].name, values:[]});
      } else {
        res = res.concat(responses[companyId]);
      }
    }
    return res;
  }).catch(errors => {
    console.log(errors)
    return Promise.reject(errors);

  })
}

function getStat(configuration) {
  //check configuration and ask the right stat
  let apiToCall = configuration.dataLevel.api
  if (configuration.puntualAggregationSelected && configuration.puntualAggregationSelected.function)
    apiToCall = configuration.puntualAggregationSelected.function
  console.log('apiToCall', apiToCall)
  switch (apiToCall) {
    //admin that check all companies of a campaign
    case "getCampaignCompanyStats":
      return getCampaignCompanyStats(configuration);
    case "getCampaignStats":
      return getCampaignStats(configuration);
    case "aggregateBycompany":
      return aggregateCompanyStat(configuration);
    case "getEmployeesStats":
      return getAllEmployeesStats(configuration);
    case "aggregateByemployee":
      return aggregateByEmployeeStat(configuration);
    case "aggregateByLocation":
      return aggregateByLocationStat(configuration).then(res => {
        const arr = configuration.puntualAggregationItems ? configuration.puntualAggregationItems.map(l => l.id) : [];
        if (arr.length == 0) return res;
        return res.filter(l => arr.indexOf(l.key) >= 0);
      });
    case "getLocationsStats":
      return getAllLocationsStats(configuration);
    case "getCompanyStats":
      return getCompanyStats(configuration);
    default:
      break;
  }
}
function getCsv(configuration) {
  configuration.csv = true;
  return getStat(configuration).finally(() => configuration.csv = false);
}
