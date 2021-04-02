import axios from "axios";

export const statService = {
    getCampaignCsv,
    getCompanyCsv,
    getLocationCsv

};
function getCampaignCsv(campaignId, from, to){
    var fromParam= from?('?from='+from):''
    var toParam=''
    if (fromParam)
     toParam=to?('&to='+to):''
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_CAMPAIGNS_API+'/'+campaignId+'/stats/csv'+fromParam+toParam).then(
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

function getCompanyCsv(campaignId, companyId, from, to){
    var fromParam= from?('?from='+from):''
    var toParam=''
    if (fromParam)
     toParam=to?('&to='+to):''
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_CAMPAIGNS_API+'/'+campaignId+'/stats/csv/employee/'+companyId+fromParam+toParam).then(
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
function getLocationCsv(campaignId, companyId, from, to){
    var fromParam= from?('?from='+from):''
    var toParam=''
    if (fromParam)
     toParam=to?('&to='+to):''
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_CAMPAIGNS_API+'/'+campaignId+'/stats/csv/location/'+companyId+fromParam+toParam).then(
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





