import { employeeService } from '../services';

const state = {
    allEmployees: null,
    actualEmployee: null
};

const actions = {
    importEmployees({ commit, dispatch },{companyId,file}) {
        commit('importEmployees');
        employeeService.importEmployees(companyId,file)
            .then(
                () => {commit('importEmployeesSuccess');
                dispatch('alert/success', "Dipendenti importati  con successo", { root: true });
                dispatch('getAll',companyId);
                        },
                error => {
                    commit('importEmployeesFailure', error);
                    dispatch('alert/error', error, { root: true });
                }
            );
    },
    getAll({ commit, dispatch },companyId) {
        commit('getAllEmployees');
        employeeService.getAllEmployees(companyId)
            .then(
                employees => commit('getAllSuccess', employees),
                error => {
                    commit('getAllFailure', error);
                    dispatch('alert/error', error, { root: true });
                }
            );
    },
    getEmployee({ commit }, employee) {
        if (employee){
        commit('getEmployee', employee)
        }
        else {
            commit('removeActualEmployee'); 
        }
    },
    addEmployee({ commit, dispatch },{companyId,employee}) {
        commit('addEmployee');
        employeeService.addEmployee(companyId,employee).then(
            employee => commit('addEmployeeSuccess', employee),
            error => {
                commit('addEmployeeFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    updateEmployee({ commit, dispatch },{companyId,employee}) {
        commit('updateEmployee');
        employeeService.updateEmployee(companyId,employee).then(
            employee => {
            commit('updateEmployeeSuccess', employee);
            dispatch('alert/success', "Dipendente modificato con successo", { root: true });
        },
            error => {
                commit('updateEmployeeFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    deleteEmployee({ commit, dispatch }, {companyId,employeeId}) {
        commit('deleteEmployee');
        employeeService.deleteEmployee(companyId,employeeId).then(
            employeeId => {
                commit('deleteEmployeeSuccess', employeeId);
                dispatch('alert/success', "Dipendente cancellato con successo", { root: true });

            },
            error => {
                commit('deleteEmployeeFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    }
};

const mutations = {
    importEmployees() {

    },
    importEmployeesSuccess(){

    },
    importEmployeesFailure() {

    },
    removeActualEmployee(state) {
        state.actualEmployee=null;
    },
    getAllEmployees(state) {
        state.allEmployees = { loading: true };
    },
    getAllSuccess(state, employees) {
        state.allEmployees = { items: employees };
    },
    getAllFailure(state, error) {
        state.allEmployees = { error };
    },

    getEmployee(state, employee) {
        state.actualEmployee = { item: employee };
    },

    addEmployee(state) {
        state.actualEmployee = { loading: true };
    },
    addEmployeeSuccess(state, employee) {
        state.actualEmployee = { item: employee };
        if (!state.allEmployees.items)
            state.allEmployees = { items: [] }
        state.allEmployees.items.push(employee)
    },
    addEmployeeFailure(state, error) {
        state.actualEmployee = { error };
    },
    updateEmployee(state) {
        state.actualEmployee = { loading: true };
    },
    updateEmployeeSuccess(state, employee) {
        state.actualEmployee = { item: employee };
        //update allEmployees
        if (state.allEmployees.items)
        state.allEmployees.items= state.allEmployees.items.map(function(element){
              return employee.id==element.id?  employee : element
        })
    },
    updateEmployeeFailure(state, error) {
        state.actualEmployee = { error };
    },
    deleteEmployee(state) {
        state.actualEmployee = { loading: true };
    },
    deleteEmployeeSuccess(state, employeeId) {
        state.actualEmployee = null;
        if (state.allEmployees.items)
        state.allEmployees.items= state.allEmployees.items.filter(function(element){
            return employeeId!=element.id
        })
    },
    deleteEmployeeFailure(state, error) {
        state.actualEmployee = { error };
    },

};

export const employee = {
    namespaced: true,
    state,
    actions,
    mutations
};