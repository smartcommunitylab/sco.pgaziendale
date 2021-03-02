import axios from "axios";

export const campaignService = {
    getAllCampaigns,
    addCampaign,
    updateCampaign,
    deleteCampaign
};

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
function addCampaign(campaign) {
    return axios.post(process.env.VUE_APP_BASE_URL  + process.env.VUE_APP_CAMPAIGNS_API, campaign).then(
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
function updateCampaign( campaign) {
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_CAMPAIGNS_API + '/'+ campaign.id, campaign).then(
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
function deleteCampaign( campaignId) {
    return axios.delete(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/'+ campaignId).then(
        res => {
            if (res) {
                return Promise.resolve(campaignId);
            }
        }, err => {
            return Promise.reject(err);
        }

    )
}



