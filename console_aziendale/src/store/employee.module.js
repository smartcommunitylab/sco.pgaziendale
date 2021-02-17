import { employeeService } from '../services';

const state = {
    allEmployees: null,
    actualEmployee: null
};

const actions = {
    getAll({ commit, dispatch }) {
        commit('getAllEmployees');
        employeeService.getAllEmployees()
            .then(
                employees => commit('getAllSuccess', employees),
                error => {
                    commit('getAllFailure', error);
                    dispatch('alert/error', error, { root: true });
                }
            );
    },
    getEmployeeById({ commit, dispatch }, employeeId) {
        if (employeeId){
        commit('getEmployeeById');
        employeeService.getEmployeeById(employeeId).then(
            employee => commit('getEmployeeByIdSuccess', employee),
            error => {
                commit('getAllFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
        }
        else {
            commit('removeActualEmployee'); 
        }
    },
    addEmployee({ commit, dispatch }, employee) {
        commit('addEmployee');
        employeeService.addEmployee(employee).then(
            employee => commit('addEmployeeSuccess', employee),
            error => {
                commit('getAllFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    updateEmployee({ commit, dispatch }, employee) {
        commit('updateEmployee');
        employeeService.updateEmployee(employee).then(
            employee => {
            commit('updateEmployeeSuccess', employee);
            dispatch('alert/success', "Dipendente modificato con successo", { root: true });
        },
            error => {
                commit('getAllFailure', error);
                dispatch('alert/error', error, { root: true });
            }
        );
    },
    deleteEmployee({ commit, dispatch }, employee) {
        commit('deleteEmployee');
        employeeService.deleteEmployee(employee).then(
            employee => {
                commit('deleteEmployeeSuccess', employee);
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
    getEmployeeById(state) {
        state.actualEmployee = { loading: true };
    },
    getEmployeeByIdSuccess(state, employee) {
        state.actualEmployee = { item: employee };
    },
    getEmployeeByIdFailure(state, error) {
        state.actualEmployee = { error };
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
    deleteEmployeeSuccess(state, employee) {
        state.actualEmployee = null;
        if (state.allEmployees.items)
        state.allEmployees.items= state.allEmployees.items.filter(function(element){
            return employee.id!=element.id
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