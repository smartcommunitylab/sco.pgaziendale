import { companyService } from '../services';

const state = {
    allCompanies: null,
    actualCompany: null
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
        if (companyId){
        commit('getCompanyById');
        companyService.getCompanyById(companyId).then(
            company => commit('getCompanyByIdSuccess', company),
            error => {
                commit('getAllFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
        }
        else {
            commit('removeActualCompany'); 
        }
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
        companyService.updateCompany(company).then(
            company => {
            commit('updateCompanySuccess', company);
            dispatch('alert/success', "Azienda modificata con successo", { root: true });
        },
            error => {
                commit('getAllFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    deleteCompany({ commit, dispatch }, company) {
        commit('deleteCompany');
        companyService.deleteCompany(company).then(
            company => {
                commit('deleteCompanySuccess', company);
                dispatch('alert/success', "Azienda cancellata con successo", { root: true });

            },
            error => {
                commit('deleteCompanyFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    }
};

const mutations = {
    removeActualCompany(state) {
        state.actualCompany=null;
    },
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
        //update allCompanies
        if (state.allCompanies.items)
        state.allCompanies.items= state.allCompanies.items.map(function(element){
              return company.id==element.id?  company : element
        })
    },
    updateCompanyFailure(state, error) {
        state.actualCompany = { error };
    },
    deleteCompany(state) {
        state.actualCompany = { loading: true };
    },
    deleteCompanySuccess(state, company) {
        state.actualCompany = null;
        if (state.allCompanies.items)
        state.allCompanies.items= state.allCompanies.items.filter(function(element){
            return company.id!=element.id
        })
    },
    deleteCompanyFailure(state, error) {
        state.actualCompany = { error };
    },

};

export const company = {
    namespaced: true,
    state,
    actions,
    mutations
};