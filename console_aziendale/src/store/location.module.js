import { locationService } from '../services';

const state = {
    allLocations: {},
    actualLocation:null
};

const actions = {
    getAllLocations({ commit },companyId) {
        commit('getAllLocations');

        locationService.getAllLocations(companyId)
            .then(
                locations => commit('getAllSuccess', locations),
                error => commit('getAllFailure', error)
            );
    },
    selectActualLocation({commit},location){
        commit('selectActualLocation',location)
    },
    changeActualLocation({commit},location){
        locationService.changeLocation(location)
        .then(
            location => commit('selectActualLocation', location),
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
    },
    selectActualLocation(state, location) {
        state.actualLocation = location ;
    }
};

export const location = {
    namespaced: true,
    state,
    actions,
    mutations
};