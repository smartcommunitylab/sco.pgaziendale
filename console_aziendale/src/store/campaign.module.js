import { campaignService } from '../services';

const state = {
    allCampaigns: null,
    actualCampaign:null
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
    }, 
    getCampaign({ commit }, campaign) {
        if (campaign){
        commit('getCampaign', campaign)
        }
        else {
            commit('removeActualCampaign'); 
        }
    },
    addCampaign({ commit, dispatch },campaign) {
        commit('addCampaign');
        campaignService.addCampaign(campaign).then(
            campaign => commit('addCampaignSuccess', campaign),
            error => {
                commit('addCampaignFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    updateCampaign({ commit, dispatch },campaign) {
        commit('updateCampaign');
        campaignService.updateCampaign(campaign).then(
            campaign => {
            commit('updateCampaignSuccess', campaign);
            dispatch('alert/success', "Dipendente modificato con successo", { root: true });
        },
            error => {
                commit('updateCampaignFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    deleteCampaign({ commit, dispatch }, campaignId) {
        commit('deleteCampaign');
        campaignService.deleteCampaign(campaignId).then(
            campaignId => {
                commit('deleteCampaignSuccess', campaignId);
                dispatch('alert/success', "Dipendente cancellato con successo", { root: true });

            },
            error => {
                commit('deleteCampaignFailure', error);
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
    },

    removeActualCampaign(state) {
        state.actualCampaign=null;
    },

    getCampaign(state, campaign) {
        state.actualCampaign = { item: campaign };
    },

    addCampaign(state) {
        state.actualCampaign = { loading: true };
    },
    addCampaignSuccess(state, campaign) {
        state.actualCampaign = { item: campaign };
        if (!state.allCampaigns.items)
            state.allCampaigns = { items: [] }
        state.allCampaigns.items.push(campaign)
    },
    addCampaignFailure(state, error) {
        state.actualCampaign = { error };
    },
    updateCampaign(state) {
        state.actualCampaign = { loading: true };
    },
    updateCampaignSuccess(state, campaign) {
        state.actualCampaign = { item: campaign };
        //update allCampaigns
        if (state.allCampaigns.items)
        state.allCampaigns.items= state.allCampaigns.items.map(function(element){
              return campaign.id==element.id?  campaign : element
        })
    },
    updateCampaignFailure(state, error) {
        state.actualCampaign = { error };
    },
    deleteCampaign(state) {
        state.actualCampaign = { loading: true };
    },
    deleteCampaignSuccess(state, campaignId) {
        state.actualCampaign = null;
        if (state.allCampaigns.items)
        state.allCampaigns.items= state.allCampaigns.items.filter(function(element){
            return campaignId!=element.id
        })
    },
    deleteCampaignFailure(state, error) {
        state.actualCampaign = { error };
    },
};

export const campaign = {
    namespaced: true,
    state,
    actions,
    mutations
};