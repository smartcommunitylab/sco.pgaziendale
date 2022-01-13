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
  getItemsAggregation
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
      return getCampaignStat({
        campaignId: configuration.campaign.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
        groupBy: configuration.timeUnit,
        noLimits: configuration.nolimitsKm ? true : false,
      });
    case VARIABLES.STATS.VIEWS.DATALEVEL.CAMPAIGN:
      return getCampaignStat({
        campaignId: configuration.campaign.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
        groupBy: configuration.timeUnit,
        noLimits: configuration.nolimitsKm ? true : false,
      });

    case VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY:
      return getCompanyStat({
        campaignId: configuration.campaign.id,
        companyId: configuration.company.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
        groupBy: configuration.timeUnit,
        noLimits: configuration.nolimitsKm ? true : false,
      });

    case VARIABLES.STATS.VIEWS.DATALEVEL.EMPLOYEES:

      return getEmployeeStat({
        campaignId: configuration.campaign.id,
        employeeId: configuration.employeeId,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
        groupBy: configuration.timeUnit,
        noLimits: configuration.nolimitsKm ? true : false,
      });
    case VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS:
      return getLocationStat({
        campaignId: configuration.campaign.id,
        companyId: configuration.company.id,
        locationId: configuration.locationId,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
        groupBy: configuration.timeUnit,
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
