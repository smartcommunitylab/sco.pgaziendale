import { statService } from "../services";

const state = {
  statValues: null,
  csvValues: null,
  configurations: null,
  activeConfiguration: null,
  activeViewType: null,
  activeSelection: null,
  currentCampaign: null
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
  getStat({ commit, dispatch,state }, configuration) {
    commit("getStat");
    if (state.currentCampaign)
      statService.getStat(configuration,state.currentCampaign.item).then(
      (statistics) => {
        commit("getStatSuccess", statistics);
        dispatch("alert/success", "Recuperate le statistiche con successo.", {
          root: true,
        });
      },
      (error) => {
        commit("getStatFailure", error);
        dispatch("alert/error", "Errore nel recupero delle statistiche.", {
          root: true,
        });
      }
    );
  },
  getCsv({ commit, dispatch,state }, configuration) {
    commit("getCsv");

    statService.getCsv(configuration,state.currentCampaign.item).then(
      (stats) => {
        commit("getCsvSuccess", stats);
        saveFile("campaign.csv", stats);
        dispatch("alert/success", "Recuperate le statistiche con successo.", {
          root: true,
        });
      },
      (error) => {
        commit("getCsvFailure", error);
        dispatch("alert/error", "Errore nel recupero delle statistiche.", {
          root: true,
        });
      }
    );
  },

  initConfigurationByRole({ commit, dispatch }, { role, temporaryAdmin }) {
     commit("resetStat");
    commit("getConfigurationByRole");
    statService.getConfigurationByRole(role, temporaryAdmin).then(
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
  getConfigurationByRole({ commit, dispatch }, { role, temporaryAdmin }) {
    commit("getConfigurationByRole");
    statService.getConfigurationByRole(role, temporaryAdmin).then(
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
  setCurrentCampaign({ commit }, { campaign }) {
    commit("setCurrentCampaign", campaign);
  },
  setActiveSelection({ commit }, { selection }) {
    console.log("active Selection:");
    console.log(selection);
    commit("setActiveSelection", selection);
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
        if (configuration?.views[0]?.default)
          dispatch("setActiveSelection", {
            selection: configuration.views[0].default,
          });
          if (configuration?.views[0]?.type)
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
  getStat(state) {
    state.statValues = { loading: true };
  },
  getStatSuccess(state, statistics) {
    state.statValues = { items: statistics };
  },
  getStatFailure(state, error) {
    state.statValues = { error };
  },
  setCurrentCampaign(state, campaign) {
    state.currentCampaign = { item: campaign };
  },
  removeStat(state) {
    state.statValues = null;
  },
  
  getCsv(state) {
    state.csvValues = { loading: true };
  },
  getCsvSuccess(state, stats) {
    state.csvValues = { items: stats };
  },
  getCsvFailure(state, error) {
    state.csvValues = { error };
  },
  
  resetStat(state) {
    state.statValues = null;
    state.configurations = null; 
    state.activeConfiguration = null;
    state.activeViewType =null;
    state.activeSelection=null;
  },
  getConfigurationByRole() {
    state.configurations = { loading: true };
  },
  getConfigurationByRoleSuccess(state, configurations) {
    // state.statValues = null;
    // state.activeConfiguration = null;
    // state.activeViewType =null;
    // state.activeSelection=null;
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
    state.activeSelection = selection;
  }
};

export const stat = {
  namespaced: true,
  state,
  actions,
  mutations,
};
