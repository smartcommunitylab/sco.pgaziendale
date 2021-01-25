import axios from "axios";

export const companyService = {
    getAllCompanies,
    getCompanyById
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




