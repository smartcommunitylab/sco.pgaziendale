import axios from "axios";
import { BASE_URL, USER_API,PUBLIC_CAMPAIGNS_API,MY_CAMPAIGNS_API, MY_STATS_API,COMPANIES_IN_CAMPAIGN_API, SUBSCRIBE_CAMPAIGN_API, SUBSCRIBE,UNSUBSCRIBE } from '../variables.js'
export default   {

    getUser() {
        return  axios.get(BASE_URL+USER_API)
    },
    getPublicCampaigns() {
        return  axios.get(BASE_URL+PUBLIC_CAMPAIGNS_API)
    },
    getMyCampaigns() {
        return  axios.get(BASE_URL+MY_CAMPAIGNS_API)
    },
    getCompaniesOfCampaign(campaignId) {
        return axios.get(BASE_URL+COMPANIES_IN_CAMPAIGN_API+campaignId);
          
    },
    subscribeCampaign(campaignId,companyCode,key) {
        return axios.put(BASE_URL+SUBSCRIBE_CAMPAIGN_API+campaignId+SUBSCRIBE+companyCode+'/'+key);
    },
    unsubrscribeCampaign(campaignId,companyCode,key) {
        return axios.delete(BASE_URL+SUBSCRIBE_CAMPAIGN_API+campaignId+UNSUBSCRIBE+companyCode+'/'+key);
    },
    getStats(campaignId, from, to, groupBy, withTracks) {
        
        return  axios.get(BASE_URL+SUBSCRIBE_CAMPAIGN_API+campaignId+MY_STATS_API,
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