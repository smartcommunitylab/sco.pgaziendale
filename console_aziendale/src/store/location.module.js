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
    },
    addLocation({ commit, dispatch },{companyId,location}) {
        commit('addLocation');
        locationService.addLocation(companyId,location).then(
            location => {
                commit('addLocationSuccess', location);
                dispatch('alert/success', "Sede creata con successo", { root: true });
            },
            error => {
                commit('addLocationFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    updateLocation({ commit, dispatch },{companyId,location}) {
        commit('updateLocation');
        locationService.updateLocation(companyId,location).then(
            location => {
            commit('updateLocationSuccess', location);
            dispatch('alert/success', "Sede modificata con successo", { root: true });
        },
            error => {
                commit('updateLocationFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    deleteLocation({ commit, dispatch }, {companyId,locationId}) {
        commit('deleteLocation');
        locationService.deleteLocation(companyId,locationId).then(
            locationId => {
                commit('deleteLocationSuccess', locationId);
                dispatch('alert/success', "Sede cancellata con successo", { root: true });

            },
            error => {
                commit('deleteLocationFailure', error);
                dispatch('alert/error', error, { root: true });
            }
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