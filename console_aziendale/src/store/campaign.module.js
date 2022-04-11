import { campaignService } from '../services';

const state = {
    allCampaigns: null,
    actualCampaign: null,
    publicCampaigns: null
};

const actions = {
    logout({ commit }) {
        commit('removeActualCampaign');
        commit('removeallCampaign');
        commit('removepublicCampaigns');
    },
    removeActualCampaign({ commit }) {
        commit('removeActualCampaign');
    },
    getAll({ commit, dispatch }, id) {
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

    getPublicCampaigns({ commit, dispatch }) {
        commit('getPublicCampaigns');

        campaignService.getPublicCampaigns()
            .then(
                campaigns => commit('getPublicCampaignsSuccess', campaigns),
                error => {
                    commit('getPublicCampaignsFailure', error);
                    dispatch('alert/error', error, { root: true });
                }
            );
    },
    getAllCompaniesOfCampaign({ commit, dispatch }, campaign) {
        if (campaign) {
            commit('getAllCompaniesOfCampaign');
            campaignService.getAllCompaniesOfCampaign(campaign.id)
                .then(
                    companies => {
                        //add companies to campaign
                        campaign["companies"] = companies;
                        commit('getAllCompaniesOfCampaignSuccess', campaign)
                    },
                    error => {
                        commit('getAllCompaniesOfCampaignFailure', error);
                        dispatch('alert/error', error, { root: true });
                    }
                );
        }
        else {
            commit('removeActualCampaign');
        }
    },
    addCampaign({ commit, dispatch }, { companyId, campaign }) {
        commit('addCampaign');
        campaignService.addCampaign(companyId, campaign).then(
            campaign => commit('addCampaignSuccess', campaign),
            error => {
                commit('addCampaignFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    updateCampaign({ commit, dispatch }, { companyId, campaign }) {
        commit('updateCampaign');
        campaignService.updateCampaign(companyId, campaign).then(
            campaign => {
                commit('updateCampaignSuccess', campaign);
                dispatch('alert/success', "Campagna modificato con successo", { root: true });
            },
            error => {
                commit('updateCampaignFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    deleteCampaign({ commit, dispatch }, { companyId, campaignId }) {
        commit('deleteCampaign');
        campaignService.deleteCampaign(companyId, campaignId).then(
            campaignId => {
                commit('deleteCampaignSuccess', campaignId);
                dispatch('alert/success', "Campagna cancellata con successo", { root: true });

            },
            error => {
                commit('deleteCampaignFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    deleteCompanyCampaign({ commit, dispatch }, { companyId, campaign }) {
        commit('deleteCompanyCampaign');
        campaignService.deleteCampaign(companyId, campaign.id).then(
            campaignId => {
                commit('deleteCompanyCampaignSuccess', campaignId);
                dispatch('alert/success', "Campagna cancellata con successo", { root: true });

            },
            error => {
                commit('deleteCompanyCampaignFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    createCompanyCampaign({ commit, dispatch }, { companyId, campaign }) {
        commit('createCompanyCampaign');
        campaignService.updateCampaign(companyId, campaign).then(
            () => {
                commit('createCompanyCampaignSuccess', campaign);
                dispatch('alert/success', "Campagna cancellata con successo", { root: true });

            },
            error => {
                commit('createCompanyCampaignFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
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
    getPublicCampaigns(state) {

        state.publicCampaigns = { loading: true };
    },
    getPublicCampaignsSuccess(state, campaigns) {
        state.publicCampaigns = { items: campaigns };
    },
    getPublicCampaignsFailure(state, error) {
        state.publicCampaigns = { error };
    },
    removeActualCampaign(state) {
        state.actualCampaign = null;
    },
    getAllCompaniesOfCampaign(state) {
        state.actualCampaign = { loading: true };
    },
    getAllCompaniesOfCampaignSuccess(state, campaign) {
        state.actualCampaign = { item: campaign };
    },
    getAllCompaniesOfCampaignFailure(state, error) {
        state.actualCampaign = { error };
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
            state.allCampaigns.items = state.allCampaigns.items.map(function (element) {
                return campaign.id == element.id ? campaign : element
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
            state.allCampaigns.items = state.allCampaigns.items.filter(function (element) {
                return campaignId != element.id
            })
    },
    deleteCampaignFailure(state, error) {
        state.actualCampaign = { error };
    },

    deleteCompanyCampaign() {
    },
    deleteCompanyCampaignSuccess(state, campaignId) {
        if (state.allCampaigns.items)
            state.allCampaigns.items = state.allCampaigns.items.filter(function (element) {
                return campaignId != element.id
            })
    },
    deleteCompanyCampaignailure() {
        // state.actualCampaign = { error };
    },
    createCompanyCampaign() {
        // state.actualCampaign = { loading: true };
    },
    createCompanyCampaignSuccess(state, campaign) {
        if (!state.allCampaigns.items)
            state.allCampaigns = { items: [] }
        state.allCampaigns.items.push(campaign)

    },
    createCompanyCampaignFailure() {
        // state.actualCampaign = { error };
    },
    removeallCampaign(state) {
        state.allCampaigns = null;
    },
    removepublicCampaigns(state) {
        state.publicCampaigns = null;
    }
};

export const campaign = {
    namespaced: true,
    state,
    actions,
    mutations
};