import axios from "axios";

export const locationService = {
    getAllLocations
};
//get all the companies
function getAllLocations(companyId) {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_ALL_COMPANIES_API+ '/' + companyId + '/' +process.env.VUE_APP_LOCATIONS_API ).then(
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



