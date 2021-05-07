import axios from "axios";

export const employeeService = {
    importEmployees,
    getAllEmployees,
    addEmployee,
    updateEmployee,
    deleteEmployee
};

function importEmployees(companyId,file) {

    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_EMPLOYEES_API + '/' + process.env.VUE_APP_CSV_API, file, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    }).then(
        res => {
            if (res   ) {
                return Promise.resolve(res);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}

//get all the employee
function getAllEmployees(companyId) {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_EMPLOYEES_API, {
        params: {
          pageSize: 1200
        }
      }).then(
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

// //get company byid
// function getEmployeeById(companyId, employeeId) {
//     return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+companyId+'/'+process.env.VUE_APP_EMPLOYEES_API+'/'+employeeId).then(
//         res => {
//             if (res && res.data && res.data) {
//                 return Promise.resolve(res.data);
//             }
//             else return Promise.reject(null);
//         }, err => {
//             return Promise.reject(err);
//         }

//     )
// }
//create a new company
function addEmployee(companyId, employee) {
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_EMPLOYEES_API, employee).then(
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
function updateEmployee(companyId, employee) {
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_EMPLOYEES_API + '/' + employee.id, employee).then(
        res => {
            if (res && res.data) {
                return Promise.resolve(res.data);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}
// update an old company
function deleteEmployee(companyId, employeeId) {
    return axios.delete(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_EMPLOYEES_API + '/' + employeeId).then(
        res => {
            if (res) {
                return Promise.resolve(employeeId);
            }
        }, err => {
            return Promise.reject(err);
        }

    )
}





