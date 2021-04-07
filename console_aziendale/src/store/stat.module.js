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
    getCampaignCsv() {
        state.stat = { loading: true };

    },
    getCampaignCsvSuccess(stats){
        state.stat = { items: stats };
    },
    getCampaignCsvFailure(error) {
        state.stat = { error };

    },
    getCompanyCsv() {
        state.stat = { loading: true };

    },
    getCompanyCsvSuccess(stats){
        state.stat = { items: stats };
    },
    getCompanyCsvFailure(error) {
        state.stat = { error };

    },
    getLocationCsv() {
        state.stat = { loading: true };

    },
    getLocationCsvSuccess(stats){
        state.stat = { items: stats };
    },
    getLocationCsvFailure(error) {
        state.stat = { error };

    },
   
};

export const stat = {
    namespaced: true,
    state,
    actions,
    mutations
};