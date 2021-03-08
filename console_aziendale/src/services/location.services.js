import axios from "axios";

export const locationService = {
    getAllLocations,
    addLocation,
    updateLocation,
    deleteLocation
};
//get all the companies
function getAllLocations(companyId) {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+ '/' + companyId + '/' +process.env.VUE_APP_LOCATIONS_API ).then(
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
//create a new location
function addLocation(companyId, location) {
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_LOCATIONS_API, location).then(
        res => {
            if (res && res.data ) {
                return Promise.resolve(res.data);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}
// update an old company
function updateLocation(companyId, location) {
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_LOCATIONS_API + '/' + location.id, location).then(
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
function deleteLocation(companyId, locationId) {
    return axios.delete(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_LOCATIONS_API + '/' + locationId).then(
        res => {
            if (res) {
                return Promise.resolve(locationId);
            }
        }, err => {
            return Promise.reject(err);
        }

    )
}


