import { locationService } from '../services';

const state = {
    allLocations: {}
};

const actions = {
    getAll({ commit },companyId) {
        commit('getAllLocations');

        locationService.getAllLocations(companyId)
            .then(
                locations => commit('getAllSuccess', locations),
                error => commit('getAllFailure', error)
            );
    }
};

const mutations = {
    getAllLocations(state) {
        state.allLocations = { loading: true };
    },
    getAllSuccess(state, locations) {
        state.allLocations = { items: locations };
    },
    getAllFailure(state, error) {
        state.allLocations = { error };
    }
};

export const location = {
    namespaced: true,
    state,
    actions,
    mutations
};