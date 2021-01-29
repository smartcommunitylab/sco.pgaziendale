import { companyService } from '../services';

const state = {
    allCompanies: {},
    actualCompany:{}
};

const actions = {
    getAll({ commit }) {
        commit('getAllCompanies');

        companyService.getAllCompanies()
            .then(
                companies => commit('getAllSuccess', companies),
                error => commit('getAllFailure', error)
            );
    },
    getActualCompany({commit},companyId)  {
        commit('getActualCompany');
        companyService.getCompanyById(companyId).then(
            company => commit('getCompanySuccess', company),
            error => commit('getCompanyFailure', error)
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
    getActualCompany(state) {
        state.actualCompany = { loading: true };
    },
    getCompanySuccess(state, company) {
        state.actualCompany = { item: company };
    },
    getCompanyFailure(state, error) {
        state.actualCompany = { error };
    }
};

export const company = {
    namespaced: true,
    state,
    actions,
    mutations
};