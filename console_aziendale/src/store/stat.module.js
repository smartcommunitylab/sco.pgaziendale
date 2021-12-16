import { statService } from "../services";

const state = {
  stat: null,
  configurations: null,
  activeConfiguration: null,
  activeViewType: null,
  activeSelection:null
};

function saveFile(filename, stats) {
  var pom = document.createElement("a");
  pom.setAttribute(
    "href",
    "data:text/plain;charset=utf-8," + encodeURIComponent(stats)
  );
  pom.setAttribute("download", filename);

  if (document.createEvent) {
    var event = document.createEvent("MouseEvents");
    event.initEvent("click", true, true);
    pom.dispatchEvent(event);
  } else {
    pom.click();
  }
}
const actions = {
  resetStat({ commit }) {
    commit("resetStat");
  },
  logout({ commit }) {
    commit("removeStat");
  },
  getCampaignStat(
    { commit, dispatch },
    { campaignId, from, to, groupBy, noLimits }
  ) {
    commit("getCampaignStat");

    statService.getCampaignStat(campaignId, from, to, groupBy, noLimits).then(
      (statistics) => {
        commit("getCampaignStatSuccess", statistics);
        dispatch("alert/success", "Recuperate le statistiche della campagna.", {
          root: true,
        });
      },
      (error) => {
        commit("getCampaignStatFailure", error);
        dispatch("alert/error", "Errore nel recupero delle statistiche.", {
          root: true,
        });
      }
    );
  },

  getCompanyStat(
    { commit, dispatch },
    { campaignId, companyId, from, to, groupBy, noLimits }
  ) {
    commit("getCompanyStat");

    statService
      .getCompanyStat(campaignId, companyId, from, to, groupBy, noLimits)
      .then(
        (statistics) => {
          commit("getCompanyStatSuccess", statistics);
          dispatch(
            "alert/success",
            "Recuperate le statistiche della azienda.",
            { root: true }
          );
        },
        (error) => {
          commit("getCompanyStatFailure", error);
          dispatch("alert/error", "Errore nel recupero delle statistiche.", {
            root: true,
          });
        }
      );
  },
  getEmployeeStat(
    { commit, dispatch },
    { campaignId, employeeId, from, to, groupBy, withTracks, noLimits }
  ) {
    commit("getCompanyStat");

    statService
      .getEmployeeStat(
        campaignId,
        employeeId,
        from,
        to,
        groupBy,
        withTracks,
        noLimits
      )
      .then(
        (statistics) => {
          commit("getEmployeeStatSuccess", statistics);
          dispatch(
            "alert/success",
            "Recuperate le statistiche del dipendente.",
            { root: true }
          );
        },
        (error) => {
          commit("getEmployeeStatFailure", error);
          dispatch("alert/error", "Errore nel recupero delle statistiche.", {
            root: true,
          });
        }
      );
  },
  getLocationStat(
    { commit, dispatch },
    { campaignId, companyId, locationId, from, to, groupBy, noLimits }
  ) {
    commit("getLocationStat");

    statService
      .getLocationStat(
        campaignId,
        companyId,
        locationId,
        from,
        to,
        groupBy,
        noLimits
      )
      .then(
        (statistics) => {
          commit("getLocationStatSuccess", statistics);
          dispatch("alert/success", "Recuperate le statistiche della sede.", {
            root: true,
          });
        },
        (error) => {
          commit("getLocationStatFailure", error);
          dispatch("alert/error", "Errore nel recupero delle statistiche.", {
            root: true,
          });
        }
      );
  },
  getCampaignCsv({ commit, dispatch }, { campaignId, from, to }) {
    commit("getCampaignCsv");

    statService.getCampaignCsv(campaignId, from, to).then(
      (stats) => {
        commit("getCampaignCsvSuccess", stats);
        saveFile("campaign.csv", stats);
        dispatch("alert/success", "Recuperate le statistiche della campagna.", {
          root: true,
        });
      },
      (error) => {
        commit("getCampaignCsvFailure", error);
        dispatch("alert/error", "Errore nel recupero delle statistiche.", {
          root: true,
        });
      }
    );
  },
  getCompanyCsv({ commit, dispatch }, { campaignId, companyId, from, to }) {
    commit("getCompanyCsv");

    statService.getCompanyCsv(campaignId, companyId, from, to).then(
      (stats) => {
        commit("getCompanyCsvSuccess", stats);
        saveFile("company.csv", stats);
        dispatch("alert/success", "Recuperate le statistiche della campagna.", {
          root: true,
        });
      },
      (error) => {
        commit("getCompanyCsvFailure", error);
        dispatch("alert/error", "Errore nel recupero delle statistiche.", {
          root: true,
        });
      }
    );
  },
  getLocationCsv({ commit, dispatch }, { campaignId, companyId, from, to }) {
    commit("getLocationCsv");

    statService.getLocationCsv(campaignId, companyId, from, to).then(
      (stats) => {
        commit("getLocationCsvSuccess", stats);
        saveFile("location.csv", stats);
        dispatch("alert/success", "Recuperate le statistiche della campagna.", {
          root: true,
        });
      },
      (error) => {
        commit("getLocationCsvFailure", error);
        dispatch("alert/error", "Errore nel recupero delle statistiche.", {
          root: true,
        });
      }
    );
  },
  initConfigurationByRole({ commit, dispatch }, { role }) {
    commit("getConfigurationByRole");
    statService.getConfigurationByRole(role).then(
      (configurations) => {
        commit("getConfigurationByRoleSuccess", configurations);
        dispatch("setActiveConfiguration", {
          configurationId: configurations[0].id,
        });
        if (configurations[0]?.views[0]?.default)
        dispatch("setActiveSelection", {
          selection: configurations[0].views[0].default,
        });
      },
      (error) => {
        commit("getConfigurationByRoleFailure", error),
          dispatch("alert/error", error, { root: true });
      }
    );
  },
  getConfigurationByRole({ commit, dispatch }, { role }) {
    commit("getConfigurationByRole");
    statService.getConfigurationByRole(role).then(
      (configurations) => {
        commit("getConfigurationByRoleSuccess", configurations);
        dispatch("setActiveConfiguration", {
          configurationId: configurations[0].id,
        });
      },
      (error) => {
        commit("getConfigurationByRoleFailure", error),
          dispatch("alert/error", error, { root: true });
      }
    );
  },
  setActiveSelection({ commit }, { selection }) {
    console.log("active Selection:");
    console.log(selection);
    commit("setActiveSelection",selection);
    //call and get stats?
  },
  setActiveConfiguration({ commit, dispatch, state }, { configurationId }) {
    console.log("configurationID:");
    console.log(configurationId);
    commit("setActiveConfiguration");
    statService.getActiveConfiguration(configurationId).then(
      (configurationId) => {
        commit("setActiveConfigurationSuccess", configurationId);

        let configuration = state.configurations.items.find(
          (i) => i.id === configurationId
        );

        dispatch("setActiveViewType", {
          activeViewType: configuration.views[0].type,
        });
      },
      (error) => {
        commit("setActiveConfigurationFailure", error),
          dispatch("alert/error", error, { root: true });
      }
    );
  },

  setActiveViewType({ commit, dispatch }, { activeViewType }) {
    console.log("activeViewType:");
    console.log(activeViewType);
    commit("setActiveViewType");
    statService.setActiveViewType(activeViewType).then(
      (activeViewType) => {
        commit("setActiveViewTypeSuccess", activeViewType);
      },
      (error) => {
        commit("setActiveViewTypeFailure", error),
          dispatch("alert/error", error, { root: true });
      }
    );
  },
};

const mutations = {
  getCampaignStat(state) {
    state.stat = { loading: true };
  },
  getCampaignStatSuccess(state, statistics) {
    state.stat = { items: statistics };
  },
  getCampaignStatFailure(state, error) {
    state.stat = { error };
  },
  getCompanyStat(state) {
    state.stat = { loading: true };
  },
  removeStat(state) {
    state.stat = null;
  },
  getCompanyStatSuccess(state, statistics) {
    state.stat = { items: statistics };
  },
  getCompanyStatFailure(state, error) {
    state.stat = { error };
  },
  getEmployeeStat(state) {
    state.stat = { loading: true };
  },
  getEmployeeStatSuccess(state, statistics) {
    state.stat = { items: statistics };
  },
  getEmployeeStatFailure(state, error) {
    state.stat = { error };
  },
  getLocationStat(state) {
    state.stat = { loading: true };
  },
  getLocationStatSuccess(state, statistics) {
    state.stat = { items: statistics };
  },
  getLocationStatFailure(state, error) {
    state.stat = { error };
  },
  getCampaignCsv(state) {
    state.stat = { loading: true };
  },
  getCampaignCsvSuccess(state, stats) {
    state.stat = { items: stats };
  },
  getCampaignCsvFailure(state, error) {
    state.stat = { error };
  },
  getCompanyCsv(state) {
    state.stat = { loading: true };
  },
  getCompanyCsvSuccess(state, stats) {
    state.stat = { items: stats };
  },
  getCompanyCsvFailure(state, error) {
    state.stat = { error };
  },
  getLocationCsv(state) {
    state.stat = { loading: true };
  },
  getLocationCsvSuccess(state, stats) {
    state.stat = { items: stats };
  },
  getLocationCsvFailure(state, error) {
    state.stat = { error };
  },
  resetStat(state) {
    state.stat = null;
  },
  getConfigurationByRole() {
    state.configurations = { loading: true };
  },
  getConfigurationByRoleSuccess(state, configurations) {
    state.configurations = { items: configurations };
  },
  getConfigurationByRoleFailure(state, error) {
    state.configurations = { error };
  },
  setActiveConfiguration() {
    state.activeConfiguration = { loading: true };
  },
  setActiveConfigurationSuccess(state, configurationId) {
    state.activeConfiguration = { items: configurationId };
  },
  setActiveConfigurationFailure(state, error) {
    state.activeConfiguration = { error };
  },
  setActiveViewType() {
    state.activeViewType = { loading: true };
  },
  setActiveViewTypeSuccess(state, activeViewType) {
    state.activeViewType = { item: activeViewType };
  },
  setActiveViewTypeFailure(state, error) {
    state.activeViewType = { error };
  },
  setActiveSelection(state, selection) {
    state.activeSelection= selection;
  }
};

export const stat = {
  namespaced: true,
  state,
  actions,
  mutations,
};
