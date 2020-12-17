import axios from "axios";
export default   {

    getUser() {
        return  axios.get(process.env.VUE_APP_BASE_URL+process.env.VUE_APP_USER_API)
    },
    getPublicCampaigns() {
        return  axios.get(process.env.VUE_APP_BASE_URL+process.env.VUE_APP_PUBLIC_CAMPAIGNS_API)
    },
    getMyCampaigns() {
        return  axios.get(process.env.VUE_APP_BASE_URL+process.env.VUE_APP_MY_CAMPAIGNS_API)
    },
    getCompaniesOfCampaign(campaignId) {
        return axios.get(process.env.VUE_APP_BASE_URL+process.env.VUE_APP_COMPANIES_IN_CAMPAIGN_API+campaignId);
          
    },
    subscribeCampaign(campaignId,companyCode,key) {
        return axios.put(process.env.VUE_APP_BASE_URL+process.env.VUE_APP_SUBSCRIBE_CAMPAIGN_API+campaignId+process.env.VUE_APP_SUBSCRIBE+companyCode+'/'+key);
    },
    unsubrscribeCampaign(campaignId) {
        return axios.delete(process.env.VUE_APP_BASE_URL+process.env.VUE_APP_SUBSCRIBE_CAMPAIGN_API+campaignId+process.env.VUE_APP_UNSUBSCRIBE);
    },
    getStats(campaignId, from, to, groupBy, withTracks) {
        
        return  axios.get(process.env.VUE_APP_BASE_URL+process.env.VUE_APP_SUBSCRIBE_CAMPAIGN_API+campaignId+process.env.VUE_APP_MY_STATS_API,
            {
                params: {
                    ...(from ? { from: from} : {}),
                    ...(to ? { to: to} : {}),
                    ...(groupBy ? { groupBy: groupBy} : {}),
                    ...(withTracks ? { withTracks: withTracks} : {})
                }
            })
    }
  }