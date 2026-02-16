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
    ...(all ? { allDataGroupBy: all } : {}),
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
    "/stats/track/flat",
    { params }
  )
  .then(res => {
    let map = {};
    res.data.forEach(d => {
      // d = flattenStat(d, timeGroupBy, fields, means, groupByMean);
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
function callTrackStatsMultiAPI(
  campaignId, 
  companyId, 
  location, 
  timeGroupBy, 
  dataGroupBy, 
  from, 
  to, 
  all, 
  csv) {
  let params = {
    ...(companyId ? { companyId } : {}),
    ...(location ? { location } : {}),
    ...(from ? { from } : {}),
    ...(to ? { to } : {}),
    ...(timeGroupBy ? { timeGroupBy } : {}),
    ...(dataGroupBy ? { dataGroupBy } : {}),
    ...({fields: 'tripCount,distance,duration'}),
    ...(all ? { allDataGroupBy: all } : {}),
  }

  if (csv) {
    return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/multimodal/csv",
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
    "/stats/multimodal/flat",
    { params }
  )
  .then(res => {
    let map = {};
    res.data.forEach(d => {
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
  fields,
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
    "/stats/employee/flat",
    { params }
  )
  .then(res => {
    let map = {};
    res.data.forEach(d => {
      // d = flattenEmployeeStat(d, timeGroupBy);
      // d.name = d.dataGroupName || d.dataGroup;
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

// /**
//  * Convert the data structure received from service to a flat object with mode fields exploded
//  * @param {*} ds 
//  * @returns 
//  */
// function flattenStat(ds, timeGroupBy, fields, means, groupByMean) {
//   ds.stats = ds.stats || {};
//   let res = {campaign: ds.campaign, id: ds.dataGroup, name: ds.dataGroupName || ds.dataGroup};
//   res[timeGroupBy] = mapTimeLabel(timeGroupBy, ds.timeGroup);
//   ds.stats = adjustStat(ds.stats, ds.meanStatMap);
  
//   expandStats(res, ds, means, groupByMean);
//   return res;
// }

// function flattenEmployeeStat(ds, timeGroupBy) {
//   let res = {campaign: ds.campaign, id: ds.dataGroup, name: ds.dataGroupName || ds.dataGroup};
//   res[timeGroupBy] = mapTimeLabel(timeGroupBy, ds.timeGroup);
//   expandEmployeeData(res, ds);
//   return res;
// }
// /**
//  * Expand the data structure received from service to a flat object with mode fields exploded
//  * @param {*} res the object to be expanded
//  * @param {*} ds the data structure received from service
//  * @param {*} means the list of means to be expanded
//  * @param {*} groupByMean whether to group by mean
//  * @returns The expanded object
//  */
// function expandStats(res, ds, means, groupByMean) {
//   res.track = ds.stats.track || 0;
//   res.tripCount = ds.stats.tripCount || 0;
//   res.limitedTrackCount = ds.stats.limitedTrackCount || 0;
//   if (groupByMean && means && means.length > 0 && ds.meanStatMap) {
//     means.forEach(m => {
//           res[m + '_mean_tripCount'] = (ds.meanStatMap[m] || {}).tripCount || 0;
//           res[m + '_mean_limitedTrackCount'] = (ds.meanStatMap[m] || {}).limitedTrackCount || 0;
//           res[m + '_mean_track'] = (ds.meanStatMap[m] || {}).track || 0; 
//     });
//   }

//   for (let key of ['score', 'limitedScore', 'distance', 'duration', 'co2']) {
//     const stat = ds.stats[key];
//     if (stat) {
//       res[key] = stat.value || 0;
//       res[key + '__avgTrack'] = stat.avgTrack || 0;
//       res[key + '__avgTrip'] = stat.avgTrip || 0;
//     } else {
//       res[key] = 0;
//       res[key + '__avgTrack'] = 0;
//       res[key + '__avgTrip'] = 0;
//     }
//     if (groupByMean && means && means.length > 0 && ds.meanStatMap) {
//       means.forEach(m => {
//         const meanStat = (ds.meanStatMap[m] || {})[key];
//         if (meanStat) {
//           res[m + '_mean_' + key] = meanStat.value || 0;
//           res[m + '_mean_' + key + '__avgTrack'] = meanStat.avgTrack || 0;
//           res[m + '_mean_' + key + '__avgTrip'] = meanStat.avgTrip || 0;
//           res[m + '_mean_' + key + '__prcValue'] = meanStat.prcValue || 0;
//           res[m + '_mean_' + key + '__prcAvgTrack'] = meanStat.prcAvgTrack || 0;
//           res[m + '_mean_' + key + '__prcAvgTrip'] = meanStat.prcAvgTrip || 0;
//         } else {
//           res[m + '_mean_' + key] = 0;
//           res[m + '_mean_' + key + '__avgTrack'] = 0;
//           res[m + '_mean_' + key + '__avgTrip'] = 0;
//           res[m + '_mean_' + key + '__prcValue'] = 0;
//           res[m + '_mean_' + key + '__prcAvgTrack'] = 0;
//           res[m + '_mean_' + key + '__prcAvgTrip'] = 0;
//         }
//       });
//     }
//   }
//   for (let key of ['registration', 'activeUsers', 'dropout']) {
//     const stat = ds.stats[key];
//     if (stat) {
//       res[key] = stat.value || 0;
//       res[key + '__prcTot'] = stat.prcTot || 0;
//       res[key + '__prcRegistered'] = stat.prcRegistered || 0;
//     } else {
//       res[key] = 0;
//       res[key + '__prcTot'] = 0;
//       res[key + '__prcRegistered'] = 0;
//     }
//   }
// }

// function expandEmployeeData(res, ds) {
//   for (let key of ['registration', 'activeUsers', 'dropout']) {
//     const stat = ds[key];
//     if (stat) {
//       res[key] = stat.value || 0;
//       res[key + '__prcTot'] = stat.prcTot || 0;
//       res[key + '__prcRegistered'] = stat.prcRegistered || 0;
//     } else {
//       res[key] = 0;
//       res[key + '__prcTot'] = 0;
//       res[key + '__prcRegistered'] = 0;
//     }
//   }
// }

// /**
//  * Maps a time group value to a more human readable label
//  * 
//  * @param {string} timeGroupBy - the time group by, e.g. 'dayOfWeek'
//  * @param {string} timeGroup - the time group value, e.g. 'MON'
//  * @returns {string} the mapped time group label, e.g. 'Lunedì'
//  */
// function mapTimeLabel(timeGroupBy, timeGroup) {
//   if (timeGroupBy === 'dayOfWeek') {
//     const days = {"MONDAY": "Lunedì", "TUESDAY": "Martedì", "WEDNESDAY": "Mercoledì", "THURSDAY": "Giovedì", "FRIDAY": "Venerdì", "SATURDAY": "Sabato", "SUNDAY": "Domenica"};
//     return days[timeGroup];
//   }
//   return timeGroup;
// }

// /**
//  * Adjusts the given stat value and its mean stat value
//  * 
//  * @param {Object} m - the stat object
//  * @param {Object} meanStatMap - the mean stat map object
//  * @param {string} key - the stat key
//  * @param {Object} stat - the stat object
//  * 
//  * The adjustment is done by dividing the value by the track or trip count
//  * and by calculating the percentage value and the average track and trip percentage
//  */
// function adjustField(m, meanStatMap, key, stat) {
//     m.value = adjustValue(m.value, key);
//     if (stat.track && stat.track > 0) {
//       m.avgTrack = m.value / stat.track;
//     }
//     if (stat.tripCount && stat.tripCount > 0) {
//       m.avgTrip = m.value / stat.tripCount;
//     }

//     if (meanStatMap) {
//       Object.keys(meanStatMap).forEach(mean => {
//         const meanStat = meanStatMap[mean];
//         meanStat.track = meanStat.track || 0;
//         meanStat.tripCount = meanStat.tripCount || meanStat.track;
//         meanStat.limitedTrackCount = meanStat.limitedTrackCount || 0;
        
//         const mm = meanStat[key] || {};
//         mm.value = adjustValue(mm.value, key);
//         if (meanStat.track && meanStat.track > 0) {
//           mm.avgTrack = mm.value / meanStat.track;
//         } else {
//           mm.avgTrack = 0;
//         }
//         if (meanStat.tripCount && meanStat.tripCount > 0) {
//           mm.avgTrip = mm.value / meanStat.tripCount;
//         } else {
//           mm.avgTrip = 0;
//         }
//         mm.prcValue = m.value > 0 ? (mm.value / m.value) * 100 : 0;
//         mm.prcAvgTrack = (m.avgTrack && m.avgTrack > 0) ? (mm.avgTrack / m.avgTrack) * 100 : 0;
//         mm.prcAvgTrip = (m.avgTrip && m.avgTrip > 0) ? (mm.avgTrip / m.avgTrip) * 100 : 0;
//       });
//     }
// }

// /**
//  * Adjust the stat object by dividing the value by the track or trip count
//  * and by calculating the percentage value and the average track and trip percentage
//  * 
//  * @param {Object} stat - the stat object
//  * @param {Object} meanStatMap - the mean stat map
//  * @returns {Object} the adjusted stat object
//  */
// function adjustStat(stat, meanStatMap) {
//   stat = stat || {};
//   stat.track = stat.track || 0;
//   stat.tripCount = stat.tripCount || 0;
//   stat.limitedTrackCount = stat.limitedTrackCount || 0;

//   if (stat.score) {
//     adjustField(stat.score, meanStatMap, 'score', stat);
//   }
//   if (stat.limitedScore) {
//     adjustField(stat.limitedScore, meanStatMap, 'limitedScore', stat);
//   }
//   if (stat.distance) {
//     adjustField(stat.distance, meanStatMap, 'distance', stat);
//   }
//   if (stat.duration) {
//     adjustField(stat.duration, meanStatMap, 'duration', stat);
//   }
//   if (stat.co2) {
//     adjustField(stat.co2, meanStatMap, 'co2', stat);
//   }

//   return stat;
// }

// function adjustValue(value) {
//   if (!value) return 0;

//   /** Should be done server side
//   if (field === 'distance') {
//     return (value / 1000); //convert to km
//   }
//   if (field === 'duration') {
//     return (value / 3600); //convert to hours
//   }
//   */
//   return value;
// }
/**
 * Prepare fields parameter given the configuration
 * @param {*} configuration 
 * @returns 
 */
function getFields(configuration) {
  return configuration.dataColumns.map(c => {
    return c.baseValue || c.value;
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
        (configuration.company || {}).id, // companyId is null, filter later
        null, // locationId is null, filter later
        configuration.means,       // means
        agg === 'employee' ? configuration.puntualAggregationItems.map(i => i.id).join(',') : null, // employees
        configuration.direction, 
        configuration.timeUnit.apiField, 
        agg, 
        configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
        configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
        getFields(configuration), // fields
        configuration.groupByMean,
        true, 
        configuration.csv
      ).then(res => {
        if (agg === 'location' || agg === 'company') {
          const arr = configuration.puntualAggregationItems ? configuration.puntualAggregationItems.map(e => e.id) : [];
          if (arr.length == 0) return res;
          return res.filter(e => arr.indexOf(e.key) >= 0);
        }
        return res;
      });
  } else if (configuration.source === 'multimodal') {
    return callTrackStatsMultiAPI(
        configuration.campaign.id, 
        (configuration.company || {}).id, // companyId is null, filter later
        null, // locationId is null, filter later
        configuration.timeUnit.apiField, 
        agg, 
        configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
        configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
        true, 
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
        (configuration.company || {}).id, // companyId is null, filter later
        null, // locationId is null, filter later
        configuration.timeUnit.apiField, 
        agg, 
        configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
        configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
        getFields(configuration), // fields
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
