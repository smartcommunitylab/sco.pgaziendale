import { statService } from '../services';

const state = {
    stat:null
};

const actions = {

    getStat({ commit,dispatch },companyId) {
        commit('getStat');

        statService.getAllStat(companyId)
            .then(
                stats => {
                    commit('getStatSuccess', stats);
                    dispatch('alert/success', "Statistiche aggiornate.", { root: true });

                },
                error => {
                    commit('getStatFailure', error);
                    dispatch('alert/error', "Errore nel recupero delle staistiche.", { root: true });
                }
            );
    },
    

};

const mutations = {
    getStat() {
        state.stat = { loading: true };

    },
    getStatSuccess(stats){
        state.stat = { items: stats };
    },
    getStatFailure(error) {
        state.stat = { error };

    },
   
};

export const stat = {
    namespaced: true,
    state,
    actions,
    mutations
};