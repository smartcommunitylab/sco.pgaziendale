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
      return employeeService.getAllEmployees(companyId).then((res) => {
        const content = res.content || res;
        if (!Array.isArray(content)) return [];
        
        return content.map(e => {
          const isEnrolled = !campaignId || (e.campaigns && e.campaigns.indexOf(campaignId) >= 0);
          e.label = (e.surname && e.surname != '-') 
                  ? `${e.surname} ${e.name}` 
                  : (e.name && e.name != '-') 
                  ? `${e.name}`
                  : (e.code)
                  ? `- - (${e.code})`
                  : `${e.id}`;
          e.disabled = !isEnrolled;
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
  locations, 
  means,
  employeeCodes, 
  way,
  timeGroupBy, 
  dataGroupBy, 
  from, 
  to, 
  fields,
  groupByMean, 
  all, 
  csv) {
    const hasSpecificFilters = (locations?.length > 0) || (employeeCodes?.length > 0);
  const finalAll = hasSpecificFilters ? false : all;

  let params = {
    ...(companyId ? { companyId } : {}),
    ...(locations ? { locations } : {}),
    ...(employeeCodes ? { employeeCodes } : {}),
    ...(means ? { means: means.join(',') } : {}),
    ...(way ? { way: Array.isArray(way) ? way.join(',') : way } : {}),
    ...(groupByMean ? { groupByMean } : {}),
    ...(from ? { from } : {}),
    ...(to ? { to } : {}),
    ...(timeGroupBy ? { timeGroupBy } : {}),
    ...(dataGroupBy ? { dataGroupBy } : {}),
    ...(fields ? { fields } : {fields: 'score'}),
    ...(finalAll !== undefined ? { allDataGroupBy: finalAll } : {}),
  }

  if (csv) {
    return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/track/csv/flat",
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
  locations, 
  timeGroupBy, 
  dataGroupBy, 
  from, 
  to, 
  all, 
  csv) {
    const hasSpecificFilters = (locations?.length > 0) ;
    const finalAll = hasSpecificFilters ? false : all;
  
  let params = {
    ...(companyId ? { companyId } : {}),
    ...(locations ? { locations } : {}),             
    ...(from ? { from } : {}),
    ...(to ? { to } : {}),
    ...(timeGroupBy ? { timeGroupBy } : {}),
    ...(dataGroupBy ? { dataGroupBy } : {}),
    ...({fields: 'tripCount,distance,duration'}),
    ...(finalAll !== undefined ? { allDataGroupBy: finalAll } : {}),
  }

  if (csv) {
    return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/multimodal/csv/flat",
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
  locations, 
  employeeCodes,
  timeGroupBy, 
  dataGroupBy, 
  from, 
  to, 
  fields,
  csv) {
  let params = {
    ...(companyId ? { companyId } : {}),
    ...(locations?.length ? { locations } : {}),
    ...(employeeCodes?.length ? { employeeCodes } : {}),
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
      "/stats/employee/csv/flat",
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

function filterByPuntualAggregation(res, configuration) {
  console.log(configuration);
  // if (!configuration.puntualAggregationItems?.length) return res;
  
  // const arr = configuration.puntualAggregationItems.map(e => {
  //   if (typeof e === 'string') return e;
  //   return e.label || e.name || e.id;
  // });
  
  // if (!arr.length) return res;
  // return res.filter(e => arr.includes(e.key));
  return res.filter(e => e.key && e.key !== 'null' && e.key.trim() !== '');

}

// FIX: funzione helper per le date
function getDateRange(configuration) {
  return {
    from: configuration.timePeriod.value !== 'ALL' ? (configuration.selectedDateFrom || null) : null,
    to:   configuration.timePeriod.value !== 'ALL' ? (configuration.selectedDateTo   || null) : null,
  };
}

function getStat(configuration) {
  if (!configuration.campaign?.id) return Promise.resolve([]);

  const agg   = configuration.dataLevel.value;
  const { from, to } = getDateRange(configuration);
  const companyId = (configuration.company || {}).id;
  // Intercetta il tipo di filtro ('LOCATIONS', 'EMPLOYEES', 'COMPANIES', ecc.)
  const filterType = configuration.puntualAggregationSelected?.value;

  // Estrai gli ID delle location come stringa separata da virgole
  const locations = (filterType === 'LOCATIONS' && configuration.puntualAggregationItems?.length)
    ? configuration.puntualAggregationItems.map(i => i.id).join(',')
    : null;

  // Estrai i CODICI dei dipendenti (uso code con fallback su id se non presente)
  const employeeCodes = (filterType === 'EMPLOYEES' && configuration.puntualAggregationItems?.length)
    ? configuration.puntualAggregationItems.map(i => i.code || i.id).join(',')
    : null;
    
  if (configuration.source === 'tracks') {

    return callTrackStatsAPI(
      configuration.campaign.id,
      companyId,
      locations,
      configuration.means,
      employeeCodes, 
      configuration.direction,
      configuration.timeUnit.apiField,
      agg,
      from, to,
      getFields(configuration),
      configuration.groupByMean,
      true,
      configuration.csv
    ).then(res => {
      if (['location', 'company', 'employee'].includes(agg)) {
        return filterByPuntualAggregation(res, configuration);
      }
      return res;
    });

  } else if (configuration.source === 'multimodal') {
    return callTrackStatsMultiAPI(
      configuration.campaign.id,
      companyId,
      locations,
      employeeCodes, 
      configuration.timeUnit.apiField,
      agg,
      from, to,
      true,
      configuration.csv
    ).then(res => {
      res = filterByPuntualAggregation(res, configuration);
      res = expandMulti(res, configuration);
      return res;
    });

  } else if (configuration.source === 'employee') {
    return callEmployeeStatsAPI(
      configuration.campaign.id,
      companyId,
      locations,
      employeeCodes,
      configuration.timeUnit.apiField,
      agg,
      from, to,
      getFields(configuration),
      configuration.csv
    ).then(res => filterByPuntualAggregation(res, configuration));
  }

  return Promise.resolve([]);
}

function expandMulti(res, configuration) {
  if (res.length == 0 || res[0].values.length == 0) return;
  const means = [];
  const obj = res[0].values[0];
  for (let key in obj) {
    for (let f of ['count', 'distance', 'duration', 'distance_avg', 'duration_avg', 'prcCount', 'prcDistance', 'prcDuration']) {
      let idx = key.indexOf('_' + f);
      if (idx > 0) {
        let mean = key.substring(0, idx);
        if (means.indexOf(mean) < 0) {
          means.push(mean);
        }
      }
    }
  }

  const subheaders = configuration.dataColumns.map(dc => {
    return {text: dc.label, value: dc.value};
  });
  const headers = ['totale'];
  means.forEach(m => {
    configuration.dataColumns.forEach(dc => {
      subheaders.push({text: dc.label, value: m + '_' + dc.value});
    });
    headers.push(m.split('_').join(' / '));
  });

  // res.forEach(r => {
  // });
  console.log('means', means);
  let result = {
    values: res,
    headers: headers,
    subheaders: subheaders
  };
  return result;
}

function getCsv(configuration) {
  configuration.csv = true;
  return getStat(configuration).finally(() => configuration.csv = false);
}
