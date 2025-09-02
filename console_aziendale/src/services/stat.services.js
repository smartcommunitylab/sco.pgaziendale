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
function callTrackStatsAPI(
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
function callEmployeeStatsAPI(
  campaignId, 
  companyId, 
  location, 
  timeGroupBy, 
  dataGroupBy, 
  from, 
  to, 
  csv) {
  let params = {
    ...(companyId ? { companyId } : {}),
    ...(location ? { location } : {}),
    ...(from ? { from } : {}),
    ...(to ? { to } : {}),
    ...(timeGroupBy ? { timeGroupBy } : {}),
    ...(dataGroupBy ? { dataGroupBy } : {}),
  }

  if (csv) {
    return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/employee/csv",
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
    "/stats/employee",
    { params }
  )
  .then(res => {
    let map = {};
    res.data.forEach(d => {
      d.name = d.dataGroupName || d.dataGroup;
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

function getStat(configuration) {
  if (!configuration.campaign.id) {
    return Promise.resolve([]);
  }
  const agg = configuration.dataLevel.value;

  if (configuration.source === 'tracks') {
    return callTrackStatsAPI(
        configuration.campaign.id, 
        null, // companyId is null, filter later
        null, // locationId is null, filter later
        configuration.means,       // means
        agg === 'employee' ? configuration.puntualAggregationItems.map(i => i.id).join(',') : null, // employees
        configuration.direction, 
        configuration.timeUnit.apiField, 
        agg, 
        configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
        configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
        getFields(configuration),
        configuration.groupByMean,
        true, // TODO - handle backend side
        configuration.csv
      ).then(res => {
        if (agg === 'location' || agg === 'company') {
          const arr = configuration.puntualAggregationItems ? configuration.puntualAggregationItems.map(e => e.id) : [];
          if (arr.length == 0) return res;
          return res.filter(e => arr.indexOf(e.key) >= 0);
        }
        return res;
      });
  } else if (configuration.source === 'employee') {
      return callEmployeeStatsAPI(
        configuration.campaign.id, 
        null, // companyId is null, filter later
        null, // locationId is null, filter later
        configuration.timeUnit.apiField, 
        agg, 
        configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
        configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
        configuration.csv
      ).then(res => {
        if (agg === 'company') {
          const arr = configuration.puntualAggregationItems ? configuration.puntualAggregationItems.map(e => e.id) : [];
          if (arr.length == 0) return res;
          return res.filter(e => arr.indexOf(e.key) >= 0);
        }
        return res;
      });
  }
  
}
function getCsv(configuration) {
  configuration.csv = true;
  return getStat(configuration).finally(() => configuration.csv = false);
}
