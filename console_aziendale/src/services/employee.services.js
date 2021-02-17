import axios from "axios";

export const employeeService = {
    importEmployees,
    getAllEmployees,
    getEmployeeById,
    addEmployee,
    updateEmployee,
    deleteEmployee
};

function importEmployees() {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API).then(
        res => {
            if (res && res.data && res.data.content) {
                return Promise.resolve(res.data.content);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}

//get all the employee
function getAllEmployees() {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API).then(
        res => {
            if (res && res.data && res.data.content) {
                return Promise.resolve(res.data.content);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}

//get company byid
function getEmployeeById(employeId) {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+employeId).then(
        res => {
            if (res && res.data && res.data) {
                return Promise.resolve(res.data);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}
//create a new company
function addEmployee(employee){
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API,employee).then(
        res => {
            if (res && res.data && res.data) {
                return Promise.resolve(res.data);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}
// update an old company
function updateEmployee(employee){
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+employee.id,employee).then(
        res => {
            if (res && res.data && res.data) {
                return Promise.resolve(res.data);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}
// update an old company
function deleteEmployee(employee){
    return axios.delete(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+employee.id).then(
        res => {
            if (res) {
                return Promise.resolve(employee);
            }
        }, err => {
            return Promise.reject(err);
        }

    )
}





