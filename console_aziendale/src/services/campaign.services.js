import axios from "axios";
import moment from "moment";
export const campaignService = {
    getAllCampaigns,
    addCampaign,
    updateCampaign,
    deleteCampaign,
    getTextOfMeans,
    getArrayMeans,
    getTerritories,
    getAllCompaniesOfCampaign,
    getPublicCampaigns,
    getMonthsForCampaign,
    getMeansForCampaign
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
function getMonthsForCampaign(campaign) {
    var dateStart = moment(campaign.from);
var dateEnd = moment(campaign.to);
var timeValues = [];

while (dateEnd > dateStart || dateStart.format('M') === dateEnd.format('M')) {
   timeValues.push({id:timeValues.length,name: dateStart.format('MMMM'), value:dateStart.startOf('month').format('YYYY-MM-DD') });
   dateStart.add(1,'month');
}
return timeValues;
}
function getMeansForCampaign(campaign) {
var means = [];
    for (var i=0;i<campaign.means.length;i++){
        //add ite
        means.push(arrayMeans.find(function(elem){return elem.value == campaign.means[i]}))
    }
return means;
}
function getAllCampaigns(companyId = null) { 
    console.log(process.env.VUE_APP_BASE_URL);
    var url = process.env.VUE_APP_BASE_URL;
    if (companyId)
        url = url + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_ALL_CAMPAIGNS_API
    else url = url + process.env.VUE_APP_ALL_CAMPAIGNS_API
    return axios.get(url,{
        params: {
          size: 1200
        }
      }).then(
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
            if (res && res.data ) {
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
// get all the territories present
function getTerritories() {
    return axios.get(process.env.VUE_APP_BASE_URL+ process.env.VUE_APP_TERRITORIES_API)
    .then(
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
function getArrayMeans() {
    return arrayMeans;
}
//get list of means and return string of means using arraymeans
function getTextOfMeans(means) {
    // var returnText="";

    return means
    
    .map(e => arrayMeans.find(ae => ae.value === e))
    .filter(e => e != null)
    .map(e => e.text)
    .sort()
    .join(', ');
}



