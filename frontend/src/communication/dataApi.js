import axios from "axios";
import { BASE_URL, USER_API,PUBLIC_CAMPAIGNS_API,MY_CAMPAIGNS_API, COMPANIES_IN_CAMPAIGN_API, SUBSCRIBE_CAMPAIGN_API, SUBSCRIBE } from '../variables.js'
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
        return axios.get(BASE_URL+COMPANIES_IN_CAMPAIGN_API+campaignId)
    .then(result => { console.log(result); return result; })
    .catch(error => { console.error(error); return Promise.reject(error); });
          
    },
    subscribeCampaing(campaignId,companyCode,key) {
        return axios.put(BASE_URL+SUBSCRIBE_CAMPAIGN_API+campaignId+SUBSCRIBE+companyCode+'/'+key)
        .then(result => { console.log(result); return result; })
        .catch(error => { console.error(error); return Promise.reject(error); });
    }
  }