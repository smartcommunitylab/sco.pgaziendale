import axios from "axios";

export const statService = {
    getCampaignCsv,
    getCompanyCsv,
    getLocationCsv,
    getCampaignStat,
    getCompanyStat,
    getLocationStat,
    getEmployeeStat

};
function getCampaignStat(campaignId, from, to,groupBy,noLimits){
    // var fromParam= from?('?from='+from):''
    // var toParam=''
    // if (fromParam)
    //  toParam=to?('&to='+to):''
    // if (fromParam)
    //   groupBy="&groupBy="+groupBy;
    // else groupBy="?groupBy="+groupBy;
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_CAMPAIGNS_API+'/'+campaignId+'/agg' ,{
        params: {
            ...(from ? { from: from} : {}),
            ...(to ? { to: to} : {}),
            ...(groupBy ? { groupBy: groupBy} : {}),
            ...(noLimits?{ noLimits: noLimits} : {})
        }
    }).then(
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

function getCompanyStat(campaignId,companyId, from, to,groupBy,noLimits){
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_CAMPAIGNS_API+'/'+campaignId+'/agg/'+ companyId,{
        params: {
            ...(from ? { from: from} : {}),
            ...(to ? { to: to} : {}),
            ...(groupBy ? { groupBy: groupBy} : {}),
            ...(noLimits?{ noLimits: noLimits} : {})
        }
    }).then(
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
function getLocationStat(campaignId,companyId,locationId, from, to,groupBy,noLimits){
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_CAMPAIGNS_API+'/'+campaignId+'/agg/'+companyId+'/'+locationId ,{
        params: {
            ...(from ? { from: from} : {}),
            ...(to ? { to: to} : {}),
            ...(groupBy ? { groupBy: groupBy} : {}),
            ...(noLimits?{ noLimits: noLimits} : {})
        }
    }).then(
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
function getEmployeeStat(campaignId, employeeId, from, to, groupBy, withTracks,noLimits) {
        
    return  axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_CAMPAIGNS_API+'/'+campaignId+'/stats/'+employeeId,
        {
            params: {
                ...(from ? { from: from} : {}),
                ...(to ? { to: to} : {}),
                ...(groupBy ? { groupBy: groupBy} : {}),
                ...(withTracks ? { withTracks: withTracks} : {}),
                ...(noLimits?{ noLimits: noLimits} : {})
            }
        })
}



function getCampaignCsv(campaignId, from, to){
    // var fromParam= from?('?from='+from):''
    // var toParam=''
    // if (fromParam)
    //  toParam=to?('&to='+to):''
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_CAMPAIGNS_API+'/'+campaignId+'/stats/csv',{
        params: {
            ...(from ? { from: from} : {}),
            ...(to ? { to: to} : {})
        }
    }).then(
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





