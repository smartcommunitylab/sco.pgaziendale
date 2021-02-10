import axios from "axios";

export const companyService = {
    getAllCompanies,
    getCompanyById,
    addCompany,
    updateCompany
};
//get all the companies
function getAllCompanies() {
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
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+company.id).then(
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





