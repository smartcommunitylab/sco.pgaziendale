import { companyService } from '../services';

const state = {
    allCompanies: null,
    actualCompany: null,
    adminCompany:null,
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
    initCompanyAdmin({ commit, dispatch }, companyId) {
        if (companyId){
            commit('initCompanyAdmin');
            companyService.getCompanyById(companyId).then(
                company => {
                    commit('chooseCompanyAdmin', company);
                    dispatch('getUsers',company.item);
                    dispatch('alert/success', "Azienda selezionata", { root: true });
                },
                error => {
                    commit('initCompanyAdminFailure', error);
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
    chooseCompanyAdmin({ commit, dispatch }, company){
        commit('choooseCompanyAdmin', company);
        dispatch('getUsers',company.item);
        dispatch('alert/success', "Azienda selezionata", { root: true });
    },
    resetCompanyAdmin({ commit, dispatch }){
        commit('resetCompanyAdmin');
        dispatch('alert/success', "Azienda deselezionata", { root: true });
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
    getUsers({ commit, dispatch },company){
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
    addUser({ commit, dispatch },{companyId,user}){
        commit('addUser');
        companyService.addUser(companyId,user).then(
            user => {
                commit('addUserSuccess', user)
                dispatch('alert/success', "Utente creato con successo", { root: true });
            },
            error => {
                commit('addUserFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    updateUser({ commit, dispatch },{companyId,user}){
        commit('updateUser');
        companyService.updateUser(companyId,user).then(
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
    deleteUser({ commit, dispatch },{companyId,user}){
        commit('deleteUser');
        companyService.deleteUser(companyId,user).then(
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
    choooseCompanyAdmin(state,company) {
        state.adminCompany=company;
    },
    resetCompanyAdmin(state) {
        state.adminCompany=null;
        state.actualCompany=null;
            },
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
    users(state) {
        state.adminCompany.item.users = { loading: true };
    },
    usersSuccess(state, users) {
        if (state.adminCompany.item)
        {
            //check types and add to admin company
            // build arrays with
            state.adminCompany.item.users=users;
            // for (var i=0; i<users.length; i++){
            //     if (users[i].roles.filter(function(role) { return role.role === 'ROLE_COMPANY_ADMIN'; }).length > 0) {
            //         state.adminCompany.item.administrators.push(users[i])
            //     }
            //     if (users[i].roles.filter(function(role) { return role.role === 'ROLE_MOBILITY_MANAGER'; }).length > 0) {
            //         state.adminCompany.item.managers.push(users[i])
            //     }
            // }
        }
    },
    usersFailure(state, error) {
        state.adminCompany.item.users = { error };
    },
    addUser(state) {
        state.adminCompany.item.users = { loading: true };
    },
    addUserSuccess(state, user) {
        if (!state.adminCompany.item.users)
        state.adminCompany.item.users = []
        state.adminCompany.item.users.push(user)
    },
    addUserFailure(state, error) {
        state.adminCompany.item.users = { error };
    },
    updateUser(state) {
        state.adminCompany.item.users = { loading: true };
    },
    updateUserSuccess(state, user) {
        // state.actualEmployee = { item: employee };
        //update allEmployees
        if (state.adminCompany.item.users)
        state.adminCompany.item.users= state.adminCompany.item.users.map(function(element){
              return user.id==element.id?  user : element
        })
    },
    updateEmployeeFailure(state, error) {
        state.adminCompany.item.users = { error };
    },
    deleteEmployee(state) {
        state.adminCompany.item.users = { loading: true };
    },
    deleteEmployeeSuccess(state, userId) {
        // state.actualEmployee = null;
        if (state.adminCompany.item.users)
        state.adminCompany.item.users= state.adminCompany.item.users.filter(function(element){
            return userId!=element.id
        })
    },
    deleteEmployeeFailure(state, error) {
        state.adminCompany.item.users = { error };
    },
  

};

export const company = {
    namespaced: true,
    state,
    actions,
    mutations
};