import { campaignService } from '../services';

const state = {
    allCampaigns: {}
};

const actions = {
    getAll({ commit,dispatch },id) {
        commit('getAllCampaigns');

        campaignService.getAllCampaigns(id)
            .then(
                campaigns => commit('getAllSuccess', campaigns),
                error => {
                    commit('getAllFailure', error);
                    dispatch('alert/error', error, { root: true });
                }
            );
    }
};

const mutations = {
    getAllCampaigns(state) {
        state.allCampaigns = { loading: true };
    },
    getAllSuccess(state, campaigns) {
        state.allCampaigns = { items: campaigns };
    },
    getAllFailure(state, error) {
        state.allCampaigns = { error };
    }
};

export const campaign = {
    namespaced: true,
    state,
    actions,
    mutations
};