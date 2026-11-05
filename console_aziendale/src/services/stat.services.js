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
  getDateRange,
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
          l.label = l.name || l.id;
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
  companies,
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
    const hasSpecificFilters = (locations?.length > 0) || (employeeCodes?.length > 0) || (companies?.length > 0);
    const finalAll = hasSpecificFilters ? false : all;

  let params = {
    ...(companyId ? { companyId } : {}),
    ...(companies ? { companies } : {}),
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
  companies,
  locations, 
  timeGroupBy, 
  dataGroupBy, 
  from, 
  to, 
  all, 
  csv) {
    const hasSpecificFilters = (locations?.length > 0) || (companies?.length > 0);
    const finalAll = hasSpecificFilters ? false : all;
  
  let params = {
    ...(companyId ? { companyId } : {}),
    ...(companies ? { companies } : {}), 
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
  companies, 
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
    ...(companies ? { companies } : {}),
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
function dateOnly(value) {
  if (!value) return null;
  if (value instanceof Date) {
    return [
      value.getFullYear(),
      String(value.getMonth() + 1).padStart(2, "0"),
      String(value.getDate()).padStart(2, "0"),
    ].join("-");
  }
  return String(value).slice(0, 10);
}

function parseLocalDate(value) {
  const [year, month, day] = value.split("-").map(Number);
  return new Date(year, month - 1, day);
}

function formatLocalDate(date) {
  return [
    date.getFullYear(),
    String(date.getMonth() + 1).padStart(2, "0"),
    String(date.getDate()).padStart(2, "0"),
  ].join("-");
}

function getDateRange(configuration) {
  const period = configuration.timePeriod?.value || configuration.timePeriod;
  const unit =
    configuration.timeUnit?.apiField ||
    configuration.timeUnit?.value ||
    configuration.timeUnit;
  const campaign = configuration.campaign || {};
  const today = formatLocalDate(new Date());
  const campaignFrom = dateOnly(campaign.from);
  const campaignTo = dateOnly(campaign.to);
  const maxCampaignDate =
    campaignTo && campaignTo < today ? campaignTo : today;
  const warnings = [];

  if (period === "ALL" && unit === "total") {
    return {
      from: campaignFrom,
      to: maxCampaignDate,
      warning: null,
      valid: !campaignFrom || campaignFrom <= maxCampaignDate,
    };
  }


const isCalendarAggregation = ["month", "week"].includes(unit);

if (isCalendarAggregation) {
  let from = dateOnly(configuration.selectedDateFrom) || campaignFrom;
  let to = dateOnly(configuration.selectedDateTo) || maxCampaignDate;

  if (unit === "month") {
    const start = parseLocalDate(from);
    const daysInStartMonth = new Date(
      start.getFullYear(),
      start.getMonth() + 1,
      0
    ).getDate();

    // se giorno(da) > giorni_mese - 7: passa il primo del mese successivo
    if (start.getDate() > daysInStartMonth - 7) {
      start.setDate(1);
      start.setMonth(start.getMonth() + 1);
      from = formatLocalDate(start);
      warnings.push("Il mese iniziale parziale è stato escluso per motivi di privacy.");
    }

    // se giorno(a) < 7: passa l'ultimo del mese precedente
    const end = parseLocalDate(to);
    if (end.getDate() < 7) {
      to = formatLocalDate(new Date(end.getFullYear(), end.getMonth(), 0));
      warnings.push("Il mese finale parziale è stato escluso per motivi di privacy.");
    }
  }

  if (unit === "week") {
    const start = parseLocalDate(from);
    if (start.getDay() !== 1) {
      start.setDate(start.getDate() + ((8 - start.getDay()) % 7));
      from = formatLocalDate(start);
      warnings.push("La settimana iniziale parziale è stata esclusa per motivi di privacy.");
    }

    const end = parseLocalDate(to);
    if (end.getDay() !== 0) {
      end.setDate(end.getDate() - end.getDay());
      to = formatLocalDate(end);
      warnings.push("La settimana finale parziale è stata esclusa per motivi di privacy.");
    }
  }

  return {
    from,
    to,
    warning: warnings.join(" "),
    valid: !!from && !!to && from <= to,
  };


  }

  return {
    from: period && period !== "ALL" ? dateOnly(configuration.selectedDateFrom) : null,
    to: period && period !== "ALL" ? dateOnly(configuration.selectedDateTo) : null,
    warning: null,
    valid: true,
  };
}


function getStat(configuration) {
  if (!configuration || !configuration.campaign?.id) return Promise.resolve([]);

  // FIX: evita TypeError se dataLevel è null o stringa
  const agg = configuration.dataLevel?.value || configuration.dataLevel;
  if (!agg) return Promise.resolve([]);

const dateRange = getDateRange(configuration);
if (!dateRange.valid) return Promise.resolve([]);

const { from, to } = dateRange;
  const companyId = (configuration.company || {}).id;
  const filterType = configuration.puntualAggregationSelected?.value || configuration.puntualAggregationSelected;

  const locations = (filterType === 'LOCATIONS' && configuration.puntualAggregationItems?.length)
    ? configuration.puntualAggregationItems.map(i => i.id).join(',')
    : null;

  const employeeCodes = (filterType === 'EMPLOYEES' && configuration.puntualAggregationItems?.length)
    ? configuration.puntualAggregationItems.map(i => i.code || i.id).join(',')
    : null;
    
  const companies = (!companyId && filterType === 'COMPANIES' && configuration.puntualAggregationItems?.length)
    ? configuration.puntualAggregationItems.map(i => i.id).join(',')
    : null;

  const timeUnitApi = configuration.timeUnit?.apiField || configuration.timeUnit?.value || configuration.timeUnit;

  if (configuration.source === 'tracks') {
    return callTrackStatsAPI(
      configuration.campaign.id,
      companyId,
      companies,
      locations,
      configuration.means,
      employeeCodes, 
      configuration.direction,
      timeUnitApi,
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
      companies,
      locations,
      timeUnitApi,
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
      companies,
      locations,
      employeeCodes,
      timeUnitApi,
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
