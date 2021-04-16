import { companyService } from '../services';

const state = {
    allCompanies: null,
    actualCompany: null,
    adminCompany: null,
    adminCompanyUsers: null
};

const actions = {
    logout({ commit }) {
        commit('removeActualCompany');
        commit('resetCompanyAdmin');
        commit('removeAllCompany');
        commit('resetadminCompanyUsers');
    },
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
        if (companyId) {
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
    initCompanyAdmin({ commit, dispatch }, companyId) {
        if (companyId) {
            // commit('chooseCompanyAdmin');
            companyService.getCompanyById(companyId).then(
                company => {
                    commit('choooseCompanyAdmin', { item: company });
                    dispatch('getUsers', company);
                    dispatch('alert/success', "Azienda selezionata", { root: true });
                },
                error => {
                    // commit('chooseCompanyAdminFailure', error);
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
    chooseCompanyAdmin({ commit, dispatch }, company) {
        commit('choooseCompanyAdmin', company);
        dispatch('getUsers', company.item);
        dispatch('campaign/removeActualCampaign', null, { root: true });
        dispatch('alert/success', "Azienda selezionata. Ora sei Amministratore", { root: true });
    },
    resetCompanyAdmin({ commit, dispatch }) {
        commit('resetCompanyAdmin');
        //dispatch('campaign/removeActualCampaign', null, { root: true });
        // dispatch('company/logout', null, { root: true });
        dispatch('campaign/logout', null, { root: true });
        dispatch('employee/logout', null, { root: true });
        dispatch('location/logout', null, { root: true });
        dispatch('stat/logout', null, { root: true });
        dispatch('alert/success', "Azienda deselezionata. Non sei piú amministratore", { root: true });
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
    },
    getUsers({ commit, dispatch }, company) {
        commit('users');
        companyService.getUsers(company).then(
            users => {
                commit('usersSuccess', users);
                // dispatch('alert/success', "Azienda cancellata con successo", { root: true });

            },
            error => {
                commit('usersFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    addUser({ commit, dispatch }, { companyId, user }) {
        commit('addUser');
        companyService.addUser(companyId, user).then(
            user => {
                commit('addUserSuccess', user)
                dispatch('alert/success', "Utente creato con successo. É stata inviata un'email di conferma all'indirizzo indicato.", { root: true });
            },
            error => {
                commit('addUserFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    updateUser({ commit, dispatch }, { companyId, user }) {
        commit('updateUser');
        companyService.updateUser(companyId, user).then(
            user => {
                commit('updateUserSuccess', user);
                dispatch('alert/success', "Utente modificato con successo", { root: true });
            },
            error => {
                commit('updateUserFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    deleteUser({ commit, dispatch }, { companyId, user }) {
        commit('deleteUser');
        companyService.deleteUser(companyId, user).then(
            userId => {
                commit('deleteUserSuccess', userId);
                dispatch('alert/success', "Utente cancellato con successo", { root: true });

            },
            error => {
                commit('deleteUserFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
};

const mutations = {
    choooseCompanyAdmin(state, company) {
        console.log("choooseCompanyAdmin" + company);
        state.adminCompany = company;
    },
    resetCompanyAdmin(state) {
        state.adminCompany = null;
        state.actualCompany = null;
    },
    removeActualCompany(state) {
        state.actualCompany = null;
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
    addCompanyFailure() {
        // state.actualCompany = { error };
    },
    updateCompany(state) {
        state.actualCompany = { loading: true };
    },
    updateCompanySuccess(state, company) {
        state.actualCompany = { item: company };
        //update allCompanies
        if (state.allCompanies.items)
            state.allCompanies.items = state.allCompanies.items.map(function (element) {
                return company.id == element.id ? company : element
            })
    },
    updateCompanyFailure() {
        // state.actualCompany = { error };
    },
    deleteCompany(state) {
        state.actualCompany = { loading: true };
    },
    deleteCompanySuccess(state, company) {
        state.actualCompany = null;
        if (state.allCompanies.items)
            state.allCompanies.items = state.allCompanies.items.filter(function (element) {
                return company.id != element.id
            })
    },
    deleteCompanyFailure(state, error) {
        state.actualCompany = { error };
    },
    users(state) {
        state.adminCompanyUsers = { loading: true };
    },
    usersSuccess(state, users) {

        state.adminCompanyUsers = { items: users };

    },
    usersFailure(state, error) {
        state.adminCompanyUsers.items = { error };
    },
    addUser() {
        // state.adminCompanyUsers.items = { loading: true };
    },
    addUserSuccess(state, user) {
        if (!state.adminCompanyUsers.items)
            state.adminCompanyUsers.items = []
        state.adminCompanyUsers.items.push(user);
        console.log(state.adminCompanyUsers.items);

    },
    addUserFailure() {
        // state.adminCompanyUsers.items = { error };
    },
    updateUser() {
        // state.adminCompanyUsers.items = { loading: true };
    },
    updateUserSuccess(state, user) {
        //update allEmployees
        if (state.adminCompanyUsers.items)
            state.adminCompanyUsers.items = state.adminCompanyUsers.items.map(function (element) {
                return user.id == element.id ? user : element
            })
    },
    updateUserFailure() {
        // state.adminCompanyUsers.items = { error };
    },

    deleteUser() {
        // state.adminCompanyUsers.items = { loading: true };
    },
    deleteUserSuccess(state, userId) {
        // state.actualEmployee = null;
        if (state.adminCompanyUsers.items)
            state.adminCompanyUsers.items = state.adminCompanyUsers.items.filter(function (element) {
                return userId != element.id
            })
        console.log(state.adminCompanyUsers.items);
    },
    deleteUserFailure() {
        // state.adminCompanyUsers.items = { error };
    },
    removeAllCompany(state){
        state.allCompanies = null;
    },
   resetadminCompanyUsers(state){
    state.adminCompanyUsers=null;
   }


};

export const company = {
    namespaced: true,
    state,
    actions,
    mutations
};