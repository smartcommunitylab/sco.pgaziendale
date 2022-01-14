import axios from "axios";
import { statsConfigurations } from "../pages/stats/statsConfigurations";
import { VARIABLES } from "../variables";
import { campaignService } from '.';

export const statService = {
  setActiveViewType,
  getActiveConfiguration,
  getConfigurationByRole,
  getStat,
  getCsv,
  getItemsAggregation,
  fillTheViewWithValues
  // getCampaignCsv,
  // getCompanyCsv,
  // getLocationCsv,
  // getCampaignStat,
  // getCompanyStat,
  // getLocationStat,
  // getEmployeeStat,
};

function setActiveViewType(activeViewType) {
  return Promise.resolve(activeViewType);
}

function getActiveConfiguration(configurationId) {
  return Promise.resolve(configurationId);
}

function getConfigurationByRole(ROLE, temporaryAdmin) {
  let array = [];
  if (temporaryAdmin) ROLE = 'ROLE_COMPANY_ADMIN';
  statsConfigurations.forEach((config) => {
    if (config.profile === ROLE) {
      array.push(config);
    }
  });

  return Promise.resolve(array);
}
function getItemsAggregation(itemAggregationValue, campaignId) {
  switch (itemAggregationValue) {
    case VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.EMPLOYEES.value:
      return;
    case VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.LOCATIONS.value:
      return;
    case VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.COMPANIES.value:
      return campaignService.getAllCompaniesOfCampaign(campaignId);
    default:
      return Promise.resolve(null);

  }

}
function getStat(configuration) {
  //check configuration and ask the right stat
  switch (configuration.dataLevel) {
    //admin that check all comopanies of a campaign
    case VARIABLES.STATS.VIEWS.DATALEVEL.COMPANIES:
      return getAllCompaniesCampaignStat({
        campaignId: configuration.campaign.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
        groupBy: configuration.timeUnit.value,
        noLimits: configuration.nolimitsKm ? true : false,
      });
    case VARIABLES.STATS.VIEWS.DATALEVEL.CAMPAIGN:
      return getCampaignStat({
        campaignId: configuration.campaign.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
        groupBy: configuration.timeUnit.value,
        noLimits: configuration.nolimitsKm ? true : false,
      });

    case VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY:
      return getCompanyStat({
        campaignId: configuration.campaign.id,
        companyId: configuration.company.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
        groupBy: configuration.timeUnit.value,
        noLimits: configuration.nolimitsKm ? true : false,
      });

    case VARIABLES.STATS.VIEWS.DATALEVEL.EMPLOYEES:

      return getEmployeeStat({
        campaignId: configuration.campaign.id,
        employeeId: configuration.employeeId,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
        groupBy: configuration.timeUnit.value,
        noLimits: configuration.nolimitsKm ? true : false,
      });
    case VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS:
      return getLocationStat({
        campaignId: configuration.campaign.id,
        companyId: configuration.company.id,
        locationId: configuration.locationId,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
        groupBy: configuration.timeUnit.value,
        noLimits: configuration.nolimitsKm ? true : false,
      });


    default:
      break;
  }
}
function getCsv(configuration) {
  //check configuration and ask the right csv
  switch (configuration.dataLevel) {
    case VARIABLES.STATS.VIEWS.DATALEVEL.CAMPAIGN:
      return getCampaignCsv({
        campaignId: configuration.campaign.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
      });

    case VARIABLES.STATS.VIEWS.DATALEVEL.EMPLOYEES:
      return getCompanyCsv({
        campaignId: configuration.campaign.id,
        companyId: configuration.company.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
      });
    case VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS:
      return getLocationCsv({
        campaignId: configuration.campaign.id,
        companyId: configuration.company.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
      });

    default:
      break;
  }
}
function getAllCompaniesCampaignStat({ campaignId, from, to, groupBy, noLimits = false }) {
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/agg/company",
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...(noLimits ? { noLimits: noLimits } : {}),
        },
      }
    )
    .then(
      (res) => {
        if (res && res.data) {
          let companiesState = aggregateByCompany(res.data)
          return Promise.resolve(companiesState);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}
function aggregateByCompany(allStat) {
  var companiesStat = []
  allStat.forEach(stat => {
    //se la mappa contiene l'azienda, aggiungi la stat al suo array
    let index = companiesStat.findIndex(el => el.company == stat.company)
    if (index != -1) {
      companiesStat[index].values.push(stat)
    }
        //altrimenti aggiungi nuova azienda e array con un elemento
    else {
      companiesStat.push({ company: stat.company, values: [stat] })
    }
  })
return companiesStat;
}
function fillTheViewWithValues(values,view,selection){
  let viewData={};
  switch (view.item) {
    case 'Tabella':
      viewData.header = getHeadersTable(values)
      viewData.subheaders = getSubHeaders(viewData.header,selection)
      viewData.data=getData(values)
      break;
  
    default:
      break;
  }
  return viewData;
}
function getHeadersTable(values){
  // based on values, return the right headers for the table
  console.log(values)

  return ['','Gennaio','Febbraio','Marzo','Aprile','Maggio','Giugno','Luglio']
}
function getSubHeaders(header,selection){
//based on configuration, return the subheader=header.length*selection.dataColumns
console.log(header+selection)
return [
  {
    text: 'Dessert (100g serving)',
    align: 'start',
    sortable: false,
    value: 'name',
  },
  { text: 'Calories', value: 'calories',align: 'start',
    sortable: false,},
  { text: 'Fat (g)', value: 'fat' },
  { text: 'Carbs (g)', value: 'carbs' },
  { text: 'Protein (g)', value: 'protein' },
  { text: 'Iron (%)', value: 'iron' },
  { text: 'Iron (%)', value: 'iron' },
  { text: 'Iron (%)', value: 'iron' },
  { text: 'Iron (%)', value: 'iron' },
  { text: 'Iron (%)', value: 'iron' },
  { text: 'Iron (%)', value: 'iron' },
  { text: 'Iron (%)', value: 'iron' },
  { text: 'Iron (%)', value: 'iron' },
  { text: 'Iron (%)', value: 'iron' },
]
}
function getData(values){
  console.log(values);
return [
      {
        name: 'Frozen Yogurt',
        calories: 159,
        fat: 6.0,
        carbs: 24,
        protein: 4.0,
        iron: '1%',
      },
      {
        name: 'Ice cream sandwich',
        calories: 237,
        fat: 9.0,
        carbs: 37,
        protein: 4.3,
        iron: '1%',
      },
      {
        name: 'Eclair',
        calories: 262,
        fat: 16.0,
        carbs: 23,
        protein: 6.0,
        iron: '7%',
      },
      {
        name: 'Cupcake',
        calories: 305,
        fat: 3.7,
        carbs: 67,
        protein: 4.3,
        iron: '8%',
      },
      {
        name: 'Gingerbread',
        calories: 356,
        fat: 16.0,
        carbs: 49,
        protein: 3.9,
        iron: '16%',
      },
      {
        name: 'Jelly bean',
        calories: 375,
        fat: 0.0,
        carbs: 94,
        protein: 0.0,
        iron: '0%',
      },
      {
        name: 'Lollipop',
        calories: 392,
        fat: 0.2,
        carbs: 98,
        protein: 0,
        iron: '2%',
      },
      {
        name: 'Honeycomb',
        calories: 408,
        fat: 3.2,
        carbs: 87,
        protein: 6.5,
        iron: '45%',
      },
      {
        name: 'Donut',
        calories: 452,
        fat: 25.0,
        carbs: 51,
        protein: 4.9,
        iron: '22%',
      },
      {
        name: 'KitKat',
        calories: 518,
        fat: 26.0,
        carbs: 65,
        protein: 7,
        iron: '6%',
      },
    ]
}
function getCampaignStat({ campaignId, from, to, groupBy, noLimits = false }) {
  // var fromParam= from?('?from='+from):''
  // var toParam=''
  // if (fromParam)
  //  toParam=to?('&to='+to):''
  // if (fromParam)
  //   groupBy="&groupBy="+groupBy;
  // else groupBy="?groupBy="+groupBy;
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/agg",
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...(noLimits ? { noLimits: noLimits } : {}),
        },
      }
    )
    .then(
      (res) => {
        if (res && res.data) {
          return Promise.resolve(res.data);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}

function getCompanyStat(
  { campaignId,
    companyId,
    from,
    to,
    groupBy,
    noLimits = false }
) {
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/agg/" +
      companyId,
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...(noLimits ? { noLimits: noLimits } : {}),
        },
      }
    )
    .then(
      (res) => {
        if (res && res.data) {
          return Promise.resolve(res.data);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}
function getLocationStat(
  { campaignId,
    companyId,
    locationId,
    from,
    to,
    groupBy,
    noLimits = false }
) {
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/agg/" +
      companyId +
      "/" +
      locationId,
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...(noLimits ? { noLimits: noLimits } : {}),
        },
      }
    )
    .then(
      (res) => {
        if (res && res.data) {
          return Promise.resolve(res.data);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}
function getEmployeeStat(
  campaignId,
  employeeId,
  from,
  to,
  groupBy,
  withTracks = false,
  noLimits = false
) {
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/" +
      employeeId,
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...(withTracks ? { withTracks: withTracks } : {}),
          ...(noLimits ? { noLimits: noLimits } : {}),
        },
      }
    )
    .then(
      (res) => {
        if (res && res.data) {
          return Promise.resolve(res.data);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}

function getCampaignCsv(campaignId, from, to) {
  // var fromParam= from?('?from='+from):''
  // var toParam=''
  // if (fromParam)
  //  toParam=to?('&to='+to):''
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/csv",
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
        },
      }
    )
    .then(
      (res) => {
        if (res && res.data) {
          return Promise.resolve(res.data);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}

function getCompanyCsv({ campaignId, companyId, from, to }) {
  var fromParam = from ? "?from=" + from : "";
  var toParam = "";
  if (fromParam) toParam = to ? "&to=" + to : "";
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/csv/employee/" +
      companyId +
      fromParam +
      toParam
    )
    .then(
      (res) => {
        if (res && res.data) {
          return Promise.resolve(res.data);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}
function getLocationCsv({ campaignId, companyId, from, to }) {
  var fromParam = from ? "?from=" + from : "";
  var toParam = "";
  if (fromParam) toParam = to ? "&to=" + to : "";
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/csv/location/" +
      companyId +
      fromParam +
      toParam
    )
    .then(
      (res) => {
        if (res && res.data) {
          return Promise.resolve(res.data);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}
