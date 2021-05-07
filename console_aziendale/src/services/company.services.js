import axios from "axios";

export const companyService = {
    getAllCompanies,
    getCompanyById,
    addCompany,
    updateCompany,
    deleteCompany,
    getUsers,
    addUser,
    updateUser,
    deleteUser
};
//get all the companies
function getAllCompanies() {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API,{
        params: {
          size: 1200
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

//get company byid
function getCompanyById(companyId) {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+companyId).then(
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
function addCompany(company){
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API,company).then(
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
function updateCompany(company){
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+company.id,company).then(
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
function deleteCompany(company){
    return axios.delete(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+company.id).then(
        res => {
            if (res) {
                return Promise.resolve(company);
            }
        }, err => {
            return Promise.reject(err);
        }

    )
}
//get administrators
function getUsers(company){
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+company.id+'/'+process.env.VUE_APP_USERS_API).then(
        res => {
            if (res) {
                if (res && res.data ) {
                return Promise.resolve(res.data);
                }
                 else return Promise.reject(null);

            }
        }, err => {
            return Promise.reject(err);
        }

    )
}

function addUser(companyId, user) {
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_USERS_API, user).then(
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
function updateUser(companyId, user) {
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_USERS_API , user).then(
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
function deleteUser(companyId, user) {
    return axios.delete(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_USERS_API + '/' + user.username).then(
        res => {
            if (res) {
                return Promise.resolve(user.id);
            }
        }, err => {
            return Promise.reject(err);
        }

    )
}






