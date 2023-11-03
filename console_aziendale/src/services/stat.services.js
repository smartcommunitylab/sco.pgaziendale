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
  console.log(array);
  return Promise.resolve(array);
}
function getItemsAggregation(itemAggregationValue, campaignId, companyId) {
  switch (itemAggregationValue) {
    case "EMPLOYEES":
      return employeeService.getAllEmployees(companyId).then((content) => {
        return content.filter(e => e.campaigns.indexOf(campaignId) >= 0)
        .map(e => {
          e.label = (e.surname) ? `${e.surname} ${e.name}` : `${e.name}`;
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
            c.label = c.companyId;
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
 * @param {*} employeeId 
 * @param {*} timeGroupBy 
 * @param {*} dataGroupBy 
 * @param {*} from 
 * @param {*} to 
 * @param {*} fields 
 * @returns 
 */
function callStatsAPI(campaignId, companyId, location, employeeId, timeGroupBy, dataGroupBy, from, to, fields, all, csv) {
  let params = {
    ...(companyId ? { companyId } : {}),
    ...(location ? { location } : {}),
    ...(employeeId ? { employeeId } : {}),
    ...(from ? { from } : {}),
    ...(to ? { to } : {}),
    ...(timeGroupBy ? { timeGroupBy } : {}),
    ...(dataGroupBy ? { dataGroupBy } : {}),
    ...(fields ? { fields } : {fields: 'score,limitedScore'}),
    ...(all ? { all } : {}),
  }

  if (csv) {
    return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/all/csv",
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
    "/stats/all",
    { params }
  )
  .then(res => {
    let map = {};
    res.data.forEach(d => {
      d = flattenStat(d);
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
function flattenStat(ds) {
  let res = ds;
  res.name = ds.playerId;
  res.score = res.score.score || 0;
  res.limitedScore = res.limitedScore.score || 0;
  let means = ['car', 'bike', 'walk', 'train', 'bus', 'boat'];
  means.forEach(m => {
    res[m + '_meanScore'] = res.meanScore[m] || 0;
    res[m + '_limitedMeanScore'] = res.limitedMeanScore[m] || 0;
    res[m + '_meanDistance'] = (res.meanDistance[m] || 0) / 1000;
    res[m + '_meanDuration'] = (res.meanDuration[m] || 0) / 3600;
    res[m + '_meanCo2'] = res.meanCo2[m] || 0;
    res[m + '_meanTracks'] = res.meanTracks[m] || 0;
  });
  delete res.meanScore;
  delete res.limitedMeanScore;
  delete res.meanDistance;
  delete res.meanDuration;
  delete res.meanCo2;
  delete res.meanTracks;
  return res;
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
    configuration.campaign.id, 
    configuration.company.id, 
    null, 
    null, 
    configuration.timeUnit.apiField, 
    'location', 
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
    getFields(configuration),
    true,
    configuration.csv
    );
}

function getAllEmployeesStats(configuration) {
  console.log('getAllEmployeesStat', configuration);
  return callStatsAPI(
    configuration.campaign.id, 
    configuration.company.id, 
    null, 
    null, 
    configuration.timeUnit.apiField, 
    'employee', 
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
    getFields(configuration),
    true,
    configuration.csv
    );
}

function getCompanyStats(configuration) {
  console.log('getCompanyStats', configuration);
  return callStatsAPI(
    configuration.campaign.id, 
    configuration.company.id, 
    null, 
    null, 
    configuration.timeUnit.apiField, 
    'company', 
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
    getFields(configuration),
    false,
    configuration.csv
    );

}

function aggregateByEmployeeStat(configuration) {
  console.log('aggregateByEmployeeStat', configuration);
  return callStatsAPI(
    configuration.campaign.id, 
    configuration.company.id, 
    null, 
    configuration.puntualAggregationItems.map(i => i.id).join(','), 
    configuration.timeUnit.apiField, 
    'employee', 
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
    getFields(configuration),
    false,
    configuration.csv
  );  
}
function aggregateByLocationStat(configuration) {
  console.log('aggregateByLocationStat', configuration);
  return callStatsAPI(
    configuration.campaign.id, 
    configuration.company.id, 
    null, 
    null, 
    configuration.timeUnit.apiField, 
    'location', 
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
    getFields(configuration),
    true,
    configuration.csv
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
    null, 
    configuration.timeUnit.apiField, 
    'company', 
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
    getFields(configuration),
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
    null, 
    configuration.timeUnit.apiField, 
    'campaign', 
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
    configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
    getFields(configuration),
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
      null, 
      configuration.timeUnit.apiField, 
      'company', 
      configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
      configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
      getFields(configuration),
      false,
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
        console.log('RES', res);
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
  // //check configuration and ask the right csv
  // switch (configuration.dataLevel.api) {
  //   case "getCampaignCompanyStats":
  //     return getCampaignCsv({
  //       campaignId: campaign.id,
  //       from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
  //       to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
  //     });

  //   case "getEmployeesStats":
  //     return getCompanyCsv({
  //       campaignId: campaign.id,
  //       companyId: configuration.company.id,
  //       from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
  //       to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
  //     });
  //   case "getLocationsStats":
  //     return getLocationCsv({
  //       campaignId: campaign.id,
  //       companyId: configuration.company.id,
  //       from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
  //       to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
  //     });

  //   default:
  //     break;
  // }
}

// function getCampaignCsv({campaignId, from, to}) {
//   return axios
//     .get(
//       process.env.VUE_APP_BASE_URL +
//       process.env.VUE_APP_CAMPAIGNS_API +
//       "/" +
//       campaignId +
//       "/stats/csv",
//       {
//         params: {
//           ...(from ? { from: from } : {}),
//           ...(to ? { to: to } : {}),
//         },
//       }
//     )
//     .then(
//       (res) => {
//         if (res && res.data) {
//           return Promise.resolve(res.data);
//         } else return Promise.reject(null);
//       },
//       (err) => {
//         return Promise.reject(err);
//       }
//     );
// }

// function getCompanyCsv({ campaignId, companyId, from, to }) {
//   var fromParam = from ? "?from=" + from : "";
//   var toParam = "";
//   if (fromParam) toParam = to ? "&to=" + to : "";
//   return axios
//     .get(
//       process.env.VUE_APP_BASE_URL +
//       process.env.VUE_APP_CAMPAIGNS_API +
//       "/" +
//       campaignId +
//       "/stats/csv/employee/" +
//       companyId +
//       fromParam +
//       toParam
//     )
//     .then(
//       (res) => {
//         if (res && res.data) {
//           return Promise.resolve(res.data);
//         } else return Promise.reject(null);
//       },
//       (err) => {
//         return Promise.reject(err);
//       }
//     );
// }
// function getLocationCsv({ campaignId, companyId, from, to }) {
//   var fromParam = from ? "?from=" + from : "";
//   var toParam = "";
//   if (fromParam) toParam = to ? "&to=" + to : "";
//   return axios
//     .get(
//       process.env.VUE_APP_BASE_URL +
//       process.env.VUE_APP_CAMPAIGNS_API +
//       "/" +
//       campaignId +
//       "/stats/csv/location/" +
//       companyId +
//       fromParam +
//       toParam
//     )
//     .then(
//       (res) => {
//         if (res && res.data) {
//           return Promise.resolve(res.data);
//         } else return Promise.reject(null);
//       },
//       (err) => {
//         return Promise.reject(err);
//       }
//     );
// }
