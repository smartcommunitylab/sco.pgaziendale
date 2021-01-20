import axios from "axios";

export const campaignService = {
    getAllCampaigns
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



