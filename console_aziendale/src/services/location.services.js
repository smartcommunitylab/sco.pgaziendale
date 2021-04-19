import axios from "axios";

export const locationService = {
    getAllLocations,
    addLocation,
    updateLocation,
    deleteLocation,
    getArrayDays,
    getDayByInt,
    importLocations
};

const arrayDays= [
    { value: 1, text: "Lunedi" },
    { value: 2, text: "Martedì" },
    { value: 3, text: "Mercoledì" },
    { value: 4, text: "Giovedì" },
    { value: 5, text: "Venerdì" },
    { value: 6, text: "Sabato" },
    { value: 7, text: "Domenica" }
]

function getArrayDays() {
    return arrayDays;
}
function getDayByInt(number) {
    for (var i=0; i<arrayDays.length;i++){
        if (arrayDays[i].value == number)
            return arrayDays[i].text;
    }
}
function importLocations(companyId,file) {
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_LOCATIONS_API + '/' + process.env.VUE_APP_CSV_API, file, {
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
function updateLocation(companyId, location,oldLocation) {
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_LOCATIONS_API + '/' + oldLocation.id, location).then(
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


