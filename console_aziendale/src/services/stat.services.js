import axios from "axios";
import { statsConfigurations } from "../pages/stats/statsConfigurations";
import { VARIABLES } from "../variables";
import { campaignService } from '.';
import { companyService } from ".";
import moment from "moment";

export const statService = {
  setActiveViewType,
  getActiveConfiguration,
  getConfigurationByRole,
  getStat,
  getCsv,
  getItemsAggregation,
  fillTheViewWithValues
  // getCampaignCsv,
  // getCompanyCsv,
  // getLocationCsv,
  // getCampaignStat,
  // getCompanyStat,
  // getLocationStat,
  // getEmployeeStat,
};

function setActiveViewType(activeViewType) {
  return Promise.resolve(activeViewType);
}

function getActiveConfiguration(configurationId) {
  return Promise.resolve(configurationId);
}

function getConfigurationByRole(ROLE, temporaryAdmin) {
  let array = [];
  if (temporaryAdmin) ROLE = 'ROLE_COMPANY_ADMIN';
  statsConfigurations.forEach((config) => {
    if (config.profile === ROLE) {
      array.push(config);
    }
  });

  return Promise.resolve(array);
}
function getItemsAggregation(itemAggregationValue, campaignId) {
  switch (itemAggregationValue) {
    case "EMPLOYEES":
      return;
    case "LOCATIONS":
      return;
    case "COMPANIES":
      return campaignService.getAllCompaniesOfCampaign(campaignId);
    default:
      return Promise.resolve(null);

  }

}
function buildAggregateData(configuration,responses){
  let aggregateArray=[];
  for (let companyId=0;companyId<configuration.puntualAggregationItems.length;companyId++){
    aggregateArray.push({ company: configuration.puntualAggregationItems[companyId].id, values: responses[companyId] })
  }
  return aggregateArray;
}
function aggregateCompanyStat(configuration) {
  let arrayRequest=[];
  for (let companyId=0;companyId<configuration.puntualAggregationItems.length;companyId++){
    arrayRequest.push(getCompanyStat({
      campaignId: configuration.campaign.id,
      companyId: configuration.puntualAggregationItems[companyId].id,
      from: configuration.timePeriod.value!='ALL'?(configuration.selectedDateFrom ? configuration.selectedDateFrom : null):null,
      to: configuration.timePeriod.value!='ALL'?(configuration.selectedDateTo ? configuration.selectedDateTo : null):null,
      groupBy: configuration.timeUnit.value,
      noLimits: configuration.nolimitsKm ? true : false,
    }))
  }
  return axios.all(arrayRequest).then(axios.spread((...responses) => {
    console.log(responses);
    return Promise.resolve(buildAggregateData(configuration,responses));
  })).catch(errors => {
    console.log(errors)
    return Promise.reject();

  })

}
function getStat(configuration, campaign) {
  //check configuration and ask the right stat
  let apiToCall = configuration.dataLevel.api
  if (configuration.puntualAggregationSelected.function)
    apiToCall = configuration.puntualAggregationSelected.function
  switch (apiToCall) {
    //admin that check all comopanies of a campaign
    case "getCampaignCompanyStats":
      //based on limits or not get all marge data
      return getAllCompaniesCampaignStat({
        campaignId: campaign.id,
        from: configuration.timePeriod.value!='ALL'?(configuration.selectedDateFrom ? configuration.selectedDateFrom : null):null,
        to: configuration.timePeriod.value!='ALL'?(configuration.selectedDateTo ? configuration.selectedDateTo : null):null,
        groupBy: configuration.timeUnit.value
      });
    case "getCampaignStats":
      return getCampaignStat({
        campaignId: campaign.id,
        from: configuration.timePeriod.value!='ALL'?(configuration.selectedDateFrom ? configuration.selectedDateFrom : null):null,
        to: configuration.timePeriod.value!='ALL'?(configuration.selectedDateTo ? configuration.selectedDateTo : null):null,
        groupBy: configuration.timeUnit.value
      });
    case "aggregateBycompany":
      return aggregateCompanyStat(configuration);
    case VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY:
      return getCompanyStat({
        campaignId: configuration.campaign.id,
        companyId: configuration.company.id,
        from: configuration.timePeriod.value!='ALL'?(configuration.selectedDateFrom ? configuration.selectedDateFrom : null):null,
      to: configuration.timePeriod.value!='ALL'?(configuration.selectedDateTo ? configuration.selectedDateTo : null):null,
        groupBy: configuration.timeUnit.value,
        noLimits: configuration.nolimitsKm ? true : false,
      });

    case VARIABLES.STATS.VIEWS.DATALEVEL.EMPLOYEES:

      return getEmployeeStat({
        campaignId: campaign.id,
        employeeId: configuration.employeeId,
        from: configuration.timePeriod.value!='ALL'?(configuration.selectedDateFrom ? configuration.selectedDateFrom : null):null,
        to: configuration.timePeriod.value!='ALL'?(configuration.selectedDateTo ? configuration.selectedDateTo : null):null,
        groupBy: configuration.timeUnit.value,
        noLimits: configuration.nolimitsKm ? true : false,
      });
    case VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS:
      return getLocationStat({
        campaignId: campaign.id,
        companyId: configuration.company.id,
        locationId: configuration.locationId,
        from: configuration.timePeriod.value!='ALL'?(configuration.selectedDateFrom ? configuration.selectedDateFrom : null):null,
        to: configuration.timePeriod.value!='ALL'?(configuration.selectedDateTo ? configuration.selectedDateTo : null):null,
        groupBy: configuration.timeUnit.value,
        noLimits: configuration.nolimitsKm ? true : false,
      });


    default:
      break;
  }
}
function getCsv(configuration, campaign) {
  //check configuration and ask the right csv
  switch (configuration.dataLevel) {
    case VARIABLES.STATS.VIEWS.DATALEVEL.CAMPAIGN:
      return getCampaignCsv({
        campaignId: campaign.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
      });

    case VARIABLES.STATS.VIEWS.DATALEVEL.EMPLOYEES:
      return getCompanyCsv({
        campaignId: campaign.id,
        companyId: configuration.company.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
      });
    case VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS:
      return getLocationCsv({
        campaignId: campaign.id,
        companyId: configuration.company.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
      });

    default:
      break;
  }
}
function combineMultipleLimits(limitedArray,unlimitedArray,key){
  let combinedArray=[]
  if (limitedArray[0].values){
    limitedArray.map( (el,index) => {
      if (el[key]===unlimitedArray[index][key]){
        let combinedItem={};
        Object.defineProperty(combinedItem, key, {
          value: el[key],
          writable: true
        });
        Object.defineProperty(combinedItem, 'values', {
          value: [],
          writable: true
        });
        // let combinedItem={company:el[key],values:[]}
        el.values.map((val,subindex) => {
          let combinedElement=JSON.parse(JSON.stringify(val));
          combinedElement['distancesNolimits']=unlimitedArray[index].values[subindex]['distances'];
        combinedElement['trackCountNolimits']=unlimitedArray[index].values[subindex]['trackCount'];
        combinedItem.values.push(combinedElement);
        })
        combinedArray.push(combinedItem);
      }
    })
  }
  else {
    limitedArray.map((val,index) => {
      let combinedElement=JSON.parse(JSON.stringify(val));
      combinedElement['distancesNolimits']=unlimitedArray[index]['distances'];
    combinedElement['trackCountNolimits']=unlimitedArray[index]['trackCount'];
    //combinedItem.values.push(combinedElement);
    combinedArray.push(combinedElement);
    })
    
  }
    return combinedArray;

  }
  function dynamicSort(property) {
    var sortOrder = 1;
    if(property[0] === "-") {
        sortOrder = -1;
        property = property.substr(1);
    }
    return function (a,b) {
        var result = (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
        return result * sortOrder;
    }
}
function getAllCompaniesCampaignStat({ campaignId, from, to, groupBy }) {
  let multiple=[];
  // noLimits
  multiple.push(axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/agg/company",
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...({ noLimits: true } ),
        },
      }
    ))
  // limits
  multiple.push(axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/agg/company",
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...( { noLimits: false }),
        },
      }
    ))
    return axios.all(multiple).then(axios.spread((...responses) => {
      //combine limits and not limits values
      let companiesStateLimited = aggregateByCompany(responses[0].data);
      let companiesStateUnlimited = aggregateByCompany(responses[1].data);
      let returnData=combineMultipleLimits(companiesStateLimited.sort(dynamicSort('company')),companiesStateUnlimited.sort(dynamicSort('company')),'company');
      return Promise.resolve(returnData);
    })).catch(errors => {
      console.log(errors)
      return Promise.reject();
    })
}
function aggregateByCompany(allStat) {
  var companiesStat = []
  allStat.forEach(stat => {
    //se la mappa contiene l'azienda, aggiungi la stat al suo array
    let index = companiesStat.findIndex(el => el.company == stat.company)
    if (index != -1) {
      companiesStat[index].values.push(stat)
    }
    //altrimenti aggiungi nuova azienda e array con un elemento
    else {
      companiesStat.push({ company: stat.company, values: [stat] })
    }
  })
  return companiesStat;
}
function getHeadersNumner(selection){
  return selection.dataColumns.length;
}
async function fillTheViewWithValues(values, view, selection, currentCampaign) {
  let viewData = {};
  switch (view.item) {
    case 'Tabella':
      viewData.headers = getHeadersTable(values, selection, currentCampaign)
      viewData.headerNumber=getHeadersNumner(selection);
      viewData.subheaders = getSubHeaders(viewData.headers, selection)
      viewData.data = await getData(currentCampaign, viewData.headers, viewData.subheaders, selection, values)
      break;

    default:
      break;
  }
  return viewData;
}
function getPeriodBetweenDates(startDate, endDate, type) {
  let timeValues = [];
  switch (type) {
    case 'month':
      while (endDate > startDate || startDate.format('M') === endDate.format('M')) {
        timeValues.push(startDate.format('YYYY-MM'));
        startDate.add(1, 'month');
      }
      break;
    case 'day':
      while (endDate > startDate || startDate.format('YYYY-MM-DD') === endDate.format('YYYY-MM-DD')) {
        timeValues.push(startDate.format('YYYY-MM-DD'));
        startDate.add(1, 'days');
      }
      break;
    default:
      break;

  }
  return timeValues;
}

// based on values and selection, return the right headers for the table (day, month or Campaign)
function getHeadersTable(values, selection, currentCampaign) {
  console.log(values, selection, currentCampaign)
  let from = selection.timePeriod.value == 'SPECIFIC' ? selection.selectedDateFrom : currentCampaign.from
  let to = selection.timePeriod.value == 'SPECIFIC' ? selection.selectedDateTo : currentCampaign.to
  let headers = [];
  switch (selection.timeUnit.value) {
    case 'month':
      //get all month from selection.company.from to selection.company.to 
      headers.push(...getPeriodBetweenDates(moment(from), moment(to), 'month'));
      break;
    case 'date':
      //get all month from selection.company.from to selection.company.to 
      headers.push(...getPeriodBetweenDates(moment(from), moment(to), 'day'));
      break;
    case 'campaign':
      headers.push('Campagna');
      break;
    default:
      headers.push('Campagna');
      break;
  }
  return headers;
}
//based on configuration, return the subheader=header.length*selection.dataColumns
function getSubHeaders(headers, selection) {
  let subheaders = [{ text: 'Nome', value: 'name' }];
  for (let i = 0; i < headers.length; i++) {
    for (let k = 0; k < selection.dataColumns.length; k++) {
      subheaders.push({ text: selection.dataColumns[k].label, value: selection.dataColumns[k].value + headers[i] })
    }
  }
  return subheaders;
}


//get the first element of the table (name) based on the selection: if companies get the name of the single company
//if campaign return the current campaign and so on
async function getRowName(obj, selectionValue, currentCampaign) {
  let returnValue = ""
  switch (selectionValue) {
    case ("companies"):
      returnValue = (await companyService.getCompanyById(obj['company'])).name
      break;
    case ("campaign"):
      returnValue = currentCampaign.title;
      break;
    default:
      break;
  }
  return returnValue;
}

//return the value of the stat or the aggregation in case of distances (array with multuple values)
function getValueByField(value) {
  if (value) {
    if (!isNaN(value)) {
      return value
    }
    else return Object.values(value).reduce((a, b) => a + b);
  }
  return value;
}
//sum all the properties
function sumProp(items, prop) {
  return items.reduce(function (a, b) {
    return a + getValueByField(b[prop]);
  }, 0);
}

//find the element of the row depending if the data has one, 2 dimension (1 or multiple row) or is an aggregation
function findElementInValues(values, rowIndex, selection, headers, columnIndex) {
  if (Object.prototype.hasOwnProperty.call(values[rowIndex], 'values')) {
    if (selection.timeUnit.value == 'campaign') {
      let newObj = {};
      if (values[rowIndex].values[0])
        for (let key in values[rowIndex].values[0]) {
          if (Object.prototype.hasOwnProperty.call(values[rowIndex].values[0], key)) {
            //var val = obj[key];
            newObj[key] = sumProp(values[rowIndex].values, key)
          }
        }
      return newObj
    }
    return (values[rowIndex].values.find(el => el[selection.timeUnit.value] === headers[columnIndex]))
  }
  else {
    if (selection.timeUnit.value == 'campaign') {
      let newObj = {};
      if (values[0])
        for (let key in values[0]) {
          if (Object.prototype.hasOwnProperty.call(values[0], key)) {
            //var val = obj[key];
            newObj[key] = sumProp(values, key)
          }
        }
      return newObj
    }
    return (values.find(el => el[selection.timeUnit.value] === headers[columnIndex]))
  }
}

// if values has more than 1 level it return the number of elements (every row of the table is an element)
// otherwise it is a table with just one row
function getRowByValues(values) {
  if (Object.prototype.hasOwnProperty.call(values[0], 'values'))
    return values.length
  return 1;
}

// return the data array with all the elements row for the table
async function getData(currentCampaign, headers, subheaders, selection, values) {
  let data = []
  // console.log(headers, selection, values);
  if (values)
    for (let rowIndex = 0; rowIndex < getRowByValues(values); rowIndex++) {
      //set detail name (it should be done more generic getting a function that could be company or locations or employees)
      // let currentCompany = await companyService.getCompanyById(values[rowIndex][selection.dataLevel.value])
      let name = await getRowName(values[rowIndex], selection.dataLevel.value, currentCampaign)
      let row = { name: name };
      for (let columnIndex = 0; columnIndex < headers.length; columnIndex++) {
        //set values for that row for every dataColumns
        for (let dataColumnsIndex = 0; dataColumnsIndex < selection.dataColumns.length; dataColumnsIndex++) {
          let found = findElementInValues(values, rowIndex, selection, headers, columnIndex);
          row[selection.dataColumns[dataColumnsIndex].value + headers[columnIndex]] = (found ? getValueByField(found[selection.dataColumns[dataColumnsIndex].apiField]) : 0)
        }
      }
      data.push(row)
      //  data.push(...Array(headers.length).fill(selection.dataColumns))
    }
  return data;

}
function getCampaignStat({ campaignId, from, to, groupBy}) {
  let multiple=[];
  // noLimits
  multiple.push(axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/agg",
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...({ noLimits: true } ),
        },
      }
    ))
  // limits
  multiple.push(axios
    .get(
      process.env.VUE_APP_BASE_URL +
          process.env.VUE_APP_CAMPAIGNS_API +
          "/" +
          campaignId +
          "/agg",
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...( { noLimits: false }),
        },
      }
    ))
    return axios.all(multiple).then(axios.spread((...responses) => {
      //combine limits and not limits values
      let campaignLimited = responses[0].data;
      let campaignUnLimited = responses[1].data;
      let returnData=combineMultipleLimits(campaignLimited,campaignUnLimited);
      return Promise.resolve(returnData);
    })).catch(errors => {
      console.log(errors)
      return Promise.reject();
    })
}

function getCompanyStat(
  { campaignId,
    companyId,
    from,
    to,
    groupBy}
) {
  let multiple=[];
  // noLimits
  multiple.push(axios
    .get(
      process.env.VUE_APP_BASE_URL +
          process.env.VUE_APP_CAMPAIGNS_API +
          "/" +
          campaignId +
          "/agg/" +
          companyId,
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...({ noLimits: true } ),
        },
      }
    ))
  // limits
  multiple.push(axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/agg/" +
      companyId,
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...( { noLimits: false }),
        },
      }
    ))
    return axios.all(multiple).then(axios.spread((...responses) => {
      //combine limits and not limits values
      let companyStatLimited = responses[0].data;
      let companyStatUnLimited = responses[1].data;
      let returnData=combineMultipleLimits(companyStatLimited,companyStatUnLimited);
      return Promise.resolve(returnData);
    })).catch(errors => {
      console.log(errors)
      return Promise.reject();
    })
  // return axios
  //   .get(
  //     process.env.VUE_APP_BASE_URL +
  //     process.env.VUE_APP_CAMPAIGNS_API +
  //     "/" +
  //     campaignId +
  //     "/agg/" +
  //     companyId,
  //     {
  //       params: {
  //         ...(from ? { from: from } : {}),
  //         ...(to ? { to: to } : {}),
  //         ...(groupBy ? { groupBy: groupBy } : {}),
  //         ...(noLimits ? { noLimits: noLimits } : {}),
  //       },
  //     }
  //   )
  //   .then(
  //     (res) => {
  //       if (res && res.data) {
  //         return Promise.resolve(res.data);
  //       } else return Promise.reject(null);
  //     },
  //     (err) => {
  //       return Promise.reject(err);
  //     }
  //   );
}
function getLocationStat(
  { campaignId,
    companyId,
    locationId,
    from,
    to,
    groupBy,
    noLimits = false }
) {
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/agg/" +
      companyId +
      "/" +
      locationId,
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...(noLimits ? { noLimits: noLimits } : {}),
        },
      }
    )
    .then(
      (res) => {
        if (res && res.data) {
          return Promise.resolve(res.data);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}
function getEmployeeStat(
  campaignId,
  employeeId,
  from,
  to,
  groupBy,
  withTracks = false,
  noLimits = false
) {
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/" +
      employeeId,
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...(withTracks ? { withTracks: withTracks } : {}),
          ...(noLimits ? { noLimits: noLimits } : {}),
        },
      }
    )
    .then(
      (res) => {
        if (res && res.data) {
          return Promise.resolve(res.data);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}

function getCampaignCsv(campaignId, from, to) {
  // var fromParam= from?('?from='+from):''
  // var toParam=''
  // if (fromParam)
  //  toParam=to?('&to='+to):''
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/csv",
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
        },
      }
    )
    .then(
      (res) => {
        if (res && res.data) {
          return Promise.resolve(res.data);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}

function getCompanyCsv({ campaignId, companyId, from, to }) {
  var fromParam = from ? "?from=" + from : "";
  var toParam = "";
  if (fromParam) toParam = to ? "&to=" + to : "";
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/csv/employee/" +
      companyId +
      fromParam +
      toParam
    )
    .then(
      (res) => {
        if (res && res.data) {
          return Promise.resolve(res.data);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}
function getLocationCsv({ campaignId, companyId, from, to }) {
  var fromParam = from ? "?from=" + from : "";
  var toParam = "";
  if (fromParam) toParam = to ? "&to=" + to : "";
  return axios
    .get(
      process.env.VUE_APP_BASE_URL +
      process.env.VUE_APP_CAMPAIGNS_API +
      "/" +
      campaignId +
      "/stats/csv/location/" +
      companyId +
      fromParam +
      toParam
    )
    .then(
      (res) => {
        if (res && res.data) {
          return Promise.resolve(res.data);
        } else return Promise.reject(null);
      },
      (err) => {
        return Promise.reject(err);
      }
    );
}
