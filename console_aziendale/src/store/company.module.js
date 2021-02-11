import { companyService } from '../services';

const state = {
    allCompanies: {},
    actualCompany: {}
};

const actions = {
    getAll({ commit, dispatch }) {
        commit('getAllCompanies');
        companyService.getAllCompanies()
            .then(
                companies => commit('getAllSuccess', companies),
                error => {
                    commit('getAllFailure', error);
                    dispatch('alert/error', error, { root: true });
                }
            );
    },
    getCompanyById({ commit, dispatch }, companyId) {
        commit('getCompanyById');
        companyService.getCompanyById(companyId).then(
            company => commit('getCompanyByIdSuccess', company),
            error => {
                commit('getAllFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    addCompany({ commit, dispatch }, company) {
        commit('addCompany');
        companyService.addCompany(company).then(
            company => commit('addCompanySuccess', company),
            error => {
                commit('getAllFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    updateCompany({ commit, dispatch }, company) {
        commit('updateCompany');
        companyService.addCompany(company).then(
            company => commit('updateCompanySuccess', company),
            error => {
                commit('getAllFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    }
};

const mutations = {
    getAllCompanies(state) {
        state.allCompanies = { loading: true };
    },
    getAllSuccess(state, companies) {
        state.allCompanies = { items: companies };
    },
    getAllFailure(state, error) {
        state.allCompanies = { error };
    },
    getCompanyById(state) {
        state.actualCompany = { loading: true };
    },
    getCompanyByIdSuccess(state, company) {
        state.actualCompany = { item: company };
    },
    getCompanyByIdFailure(state, error) {
        state.actualCompany = { error };
    },
    addCompany(state) {
        state.actualCompany = { loading: true };
    },
    addCompanySuccess(state, company) {
        state.actualCompany = { item: company };
        if (!state.allCompanies.items)
            state.allCompanies = { items: [] }
        state.allCompanies.items.push(company)
    },
    addCompanyFailure(state, error) {
        state.actualCompany = { error };
    },
    updateCompany(state) {
        state.actualCompany = { loading: true };
    },
    updateCompanySuccess(state, company) {
        state.actualCompany = { item: company };
    },
    updateCompanyFailure(state, error) {
        state.actualCompany = { error };
    },

};

export const company = {
    namespaced: true,
    state,
    actions,
    mutations
};