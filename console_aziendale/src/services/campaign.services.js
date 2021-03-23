import axios from "axios";

export const campaignService = {
    getAllCampaigns,
    addCampaign,
    updateCampaign,
    deleteCampaign,
    getTextOfMeans,
    getArrayMeans,
    getApplications,
    getAllCompaniesOfCampaign,
    getPublicCampaigns
};
const arrayMeans= [
    { value: "bike", text: "Bici" },
    { value: "car", text: "Auto" },
    { value: "train", text: "Treno" },
    { value: "walk", text: "Piedi" },
    { value: "bus", text: "Autobus" },
    { value: "boat", text: "Barca" },
  ]
//get all campaigns of the company, if companyId null get all the campaigns
function getAllCampaigns(companyId = null) {
    console.log(process.env.VUE_APP_BASE_URL);
    var url = process.env.VUE_APP_BASE_URL;
    if (companyId)
        url = url + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_ALL_CAMPAIGNS_API
    else url = url + process.env.VUE_APP_ALL_CAMPAIGNS_API
    return axios.get(url).then(
        res => {
            if (res && res.data) {
                // console.log(res.data)
                if (companyId)
                return Promise.resolve(res.data);
                return Promise.resolve(res.data.content);
                
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}
function getPublicCampaigns() {
    return  axios.get(process.env.VUE_APP_BASE_URL+process.env.VUE_APP_PUBLIC_CAMPAIGNS_API).then(
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
function getAllCompaniesOfCampaign(campaignId) {
    return axios.get(process.env.VUE_APP_BASE_URL+ process.env.VUE_APP_COMPANIES_API + '/' +process.env.VUE_APP_COMPANIES_IN_CAMPAIGN_API+ '/' +campaignId ).then(
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
function addCampaign(companyId = null, campaign) {
    var url = process.env.VUE_APP_BASE_URL;
    if (companyId)
        url = url + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_ALL_CAMPAIGNS_API
    else url = url + process.env.VUE_APP_ALL_CAMPAIGNS_API
    return axios.post(url, campaign).then(
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
// update an old campaign
function updateCampaign( companyId = null, campaign) {
    var url = process.env.VUE_APP_BASE_URL;
    if (companyId)
        url = url + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_ALL_CAMPAIGNS_API + '/'+ campaign.id
    else url = url + process.env.VUE_APP_ALL_CAMPAIGNS_API + '/'+ campaign.id
    return axios.put(url, campaign).then(
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
// update an old campaign
function deleteCampaign( companyId = null, campaignId) {
    var url = process.env.VUE_APP_BASE_URL;
    if (companyId)
        url = url + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_ALL_CAMPAIGNS_API + '/'+ campaignId
    else url = url + process.env.VUE_APP_ALL_CAMPAIGNS_API + '/'+ campaignId
    return axios.delete(url).then(
        res => {
            if (res) {
                return Promise.resolve(campaignId);
            }
        }, err => {
            return Promise.reject(err);
        }

    )
}
// get all the applications present
function getApplications() {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_APPS_API ).then(
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
function getArrayMeans() {
    return arrayMeans;
}
//get list of means and return string of means using arraymeans
function getTextOfMeans(means) {
    var returnText="";
    means.forEach(element => {
        if (arrayMeans.filter(e=> {
            return e.value == element
        }).length>0)
        returnText+=" "+arrayMeans.filter(e=> {
            return e.value == element
        })[0].text
        
    });
    return returnText;
}



