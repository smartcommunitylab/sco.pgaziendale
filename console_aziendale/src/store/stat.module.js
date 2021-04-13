import { statService } from '../services';

const state = {
    stat:null
};
function saveFile(filename,stats){
    var pom = document.createElement('a');
    pom.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(stats));
    pom.setAttribute('download', filename);

    if (document.createEvent) {
        var event = document.createEvent('MouseEvents');
        event.initEvent('click', true, true);
        pom.dispatchEvent(event);
    }
    else {
        pom.click();
    }
}
const actions = {
    getCampaignStat({ commit,dispatch },{campaignId,from,to,groupBy,noLimits}) {
        commit('getCampaignStat');

        statService.getCampaignStat(campaignId,from,to,groupBy,noLimits)
            .then(
                statistics => {
                    commit('getCampaignStatSuccess', statistics);
                    dispatch('alert/success', "Recuperate le statistiche della campagna.", { root: true });

                },
                error => {
                    commit('getCampaignStatFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle statistiche.", { root: true });
                }
            );
    },


    getCompanyStat({ commit,dispatch },{campaignId,companyId,from,to,groupBy,noLimits}) {
        commit('getCompanyStat');

        statService.getCompanyStat(campaignId,companyId,from,to,groupBy,noLimits)
            .then(
                statistics => {
                    commit('getCompanyStatSuccess', statistics);
                    dispatch('alert/success', "Recuperate le statistiche della azienda.", { root: true });

                },
                error => {
                    commit('getCompanyStatFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle statistiche.", { root: true });
                }
            );
    },
    getEmployeeStat({ commit,dispatch },{campaignId,employeeId,from,to,groupBy, withTracks,noLimits}) {
        commit('getCompanyStat');

        statService.getEmployeeStat(campaignId,employeeId,from,to,groupBy,withTracks,noLimits)
            .then(
                statistics => {
                    commit('getEmployeeStatSuccess', statistics);
                    dispatch('alert/success', "Recuperate le statistiche del dipendente.", { root: true });

                },
                error => {
                    commit('getEmployeeStatFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle statistiche.", { root: true });
                }
            );
    },
    getLocationStat({ commit,dispatch },{campaignId,companyId,locationId,from,to,groupBy,noLimits}) {
        commit('getLocationStat');

        statService.getLocationStat(campaignId,companyId,locationId,from,to,groupBy,noLimits)
            .then(
                statistics => {
                    commit('getLocationStatSuccess', statistics);
                    dispatch('alert/success', "Recuperate le statistiche della sede.", { root: true });

                },
                error => {
                    commit('getLocationStatFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle statistiche.", { root: true });
                }
            );
    },
    getCampaignCsv({ commit,dispatch },{campaignId,from,to}) {
        commit('getCampaignCsv');

        statService.getCampaignCsv(campaignId,from,to)
            .then(
                stats => {
                    commit('getCampaignCsvSuccess', stats);
                    saveFile('campaign.csv',stats);
                    dispatch('alert/success', "Recuperate le statistiche della campagna.", { root: true });

                },
                error => {
                    commit('getCampaignCsvFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle statistiche.", { root: true });
                }
            );
    },
    getCompanyCsv({ commit,dispatch },{campaignId, companyId,from,to}) {
        commit('getCompanyCsv');

        statService.getCompanyCsv(campaignId, companyId,from,to)
            .then(
                stats => {
                    commit('getCompanyCsvSuccess', stats);
                    saveFile('company.csv',stats);
                    dispatch('alert/success', "Recuperate le statistiche della campagna.", { root: true });

                },
                error => {
                    commit('getCompanyCsvFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle statistiche.", { root: true });
                }
            );
    },
    getLocationCsv({ commit,dispatch },{campaignId, companyId,from,to}) {
        commit('getLocationCsv');

        statService.getLocationCsv(campaignId, companyId,from,to)
            .then(
                stats => {
                    commit('getLocationCsvSuccess', stats);
                    saveFile('location.csv',stats);
                    dispatch('alert/success', "Recuperate le statistiche della campagna.", { root: true });

                },
                error => {
                    commit('getLocationCsvFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle statistiche.", { root: true });
                }
            );
    }
    

};

const mutations = {
    getCampaignStat() {
        state.stat = { loading: true };
    },
    getCampaignStatSuccess(state,statistics){
        state.stat = { items: statistics };
    },
    getCampaignStatFailure(state,error) {
        state.stat = { error };
    },
    getCompanyStat() {
        state.stat = { loading: true };
    },
    getCompanyStatSuccess(state,statistics){
        state.stat = { items: statistics };
    },
    getCompanyStatFailure(state,error) {
        state.stat = { error };
    },
    getEmployeeStat() {
        state.stat = { loading: true };
    },
    getEmployeeStatSuccess(state,statistics){
        state.stat = { items: statistics };
    },
    getEmployeeStatFailure(state,error) {
        state.stat = { error };
    },
    getLocationStat() {
        state.stat = { loading: true };
    },
    getLocationStatSuccess(state,statistics){
        state.stat = { items: statistics };
    },
    getLocationStatFailure(state,error) {
        state.stat = { error };
    },
    getCampaignCsv() {
        state.stat = { loading: true };

    },
    getCampaignCsvSuccess(state,stats){
        state.stat = { items: stats };
    },
    getCampaignCsvFailure(state,error) {
        state.stat = { error };

    },
    getCompanyCsv() {
        state.stat = { loading: true };
    },
    getCompanyCsvSuccess(state,stats){
        state.stat = { items: stats };
    },
    getCompanyCsvFailure(state,error) {
        state.stat = { error };

    },
    getLocationCsv() {
        state.stat = { loading: true };

    },
    getLocationCsvSuccess(state,stats){
        state.stat = { items: stats };
    },
    getLocationCsvFailure(state,error) {
        state.stat = { error };

    },
   
};

export const stat = {
    namespaced: true,
    state,
    actions,
    mutations
};