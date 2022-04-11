import axios from "axios";
import { statsConfigurations } from "../pages/stats/statsConfigurations";
// import { VARIABLES } from "../variables";
import { campaignService, companyService, locationService } from '.';
// import { companyService } from ".";
import { employeeService } from ".";
import { viewStatService } from "./viewStat.services";


export const statService = {
  setActiveViewType,
  getActiveConfiguration,
  getConfigurationByRole,
  getStat,
  getCsv,
  getItemsAggregation,
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
function getItemsAggregation(itemAggregationValue, campaignId, companyId) {
  switch (itemAggregationValue) {
    case "EMPLOYEES":
      return employeeService.getAllEmployees(companyId);
    case "LOCATIONS":
      return;
    case "COMPANIES":
      return campaignService.getAllCompaniesOfCampaign(campaignId);
    default:
      return Promise.resolve(null);

  }

}
function getAllLocationsStat(configuration) {
  //getAll  employeesid of company and call aggregateByEmployeeStat
  return locationService.getAllLocations(configuration.company.id).then(values => {
    configuration.puntualAggregationItems = values;
    if (values)
        viewStatService.setMapLocation(values);
    return aggregateByLocationStat(configuration)
      })
}
function getAllEmployeesStat(configuration) {
  //getAll  employeesid of company and call aggregateByEmployeeStat
  return employeeService.getAllEmployees(configuration.company.id).then(values => {
    configuration.puntualAggregationItems = values;
    if (values)
        viewStatService.setMapEmployees(values);
    return aggregateByEmployeeStat(configuration)
  })
}

function aggregateByEmployeeStat(configuration) {
  let arrayRequest = [];
  for (let employeeId = 0; employeeId < configuration.puntualAggregationItems.length; employeeId++) {
    //don't put if the employee has no camapign
    if (configuration.puntualAggregationItems[employeeId].campaigns && configuration.puntualAggregationItems[employeeId].campaigns.length > 0)
      arrayRequest.push(getEmployeeStat({
        campaignId: configuration.campaign.id,
        //companyId: configuration.company.id,
        employeeId: configuration.puntualAggregationItems[employeeId].id,
        from: configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
        to: configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
        groupBy: configuration.timeUnit.apiField,
        noLimits: configuration.nolimitsKm ? true : false,
      }))
  }
 
  return axios.all(arrayRequest).then(axios.spread((...responses) => {
    console.log(responses);
    let returnArray = responses;
    return Promise.resolve(returnArray);
  })).catch(errors => {
    console.log(errors)
    return Promise.reject();

  })

}
function aggregateByLocationStat(configuration) {
  let arrayRequest = [];
  for (let locationId = 0; locationId < configuration.puntualAggregationItems.length; locationId++) {
          arrayRequest.push(getLocationStat({
        campaignId: configuration.campaign.id,
        companyId: configuration.company.id,
        locationId: configuration.puntualAggregationItems[locationId].id,
        from: configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
        to: configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
        groupBy: configuration.timeUnit.apiField,
        noLimits: configuration.nolimitsKm ? true : false,
      }))
  }

  return axios.all(arrayRequest).then(axios.spread((...responses) => {
    console.log(responses);
    let returnArray = responses;
    return Promise.resolve(returnArray);
  })).catch(errors => {
    console.log(errors)
    return Promise.reject();

  })

}
function aggregateCompanyStat(configuration) {
  let arrayRequest = [];
  for (let companyId = 0; companyId < configuration.puntualAggregationItems.length; companyId++) {
    arrayRequest.push(getCompanyStat({
      campaignId: configuration.campaign.id,
      companyId: configuration.puntualAggregationItems[companyId].id,
      from: configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
      to: configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
      groupBy: configuration.timeUnit.apiField,
      noLimits: configuration.nolimitsKm ? true : false,
    }))
  }
  return axios.all(arrayRequest).then(axios.spread((...responses) => {
    console.log(responses);
    return Promise.resolve(responses);
  })).catch(errors => {
    console.log(errors)
    return Promise.reject();

  })

}
function getStat(configuration, campaign) {
  //check configuration and ask the right stat
  let apiToCall = configuration.dataLevel.api
  if (configuration.puntualAggregationSelected && configuration.puntualAggregationSelected.function)
    apiToCall = configuration.puntualAggregationSelected.function
  switch (apiToCall) {
    //admin that check all comopanies of a campaign
    case "getCampaignCompanyStats":
      //based on limits or not get all marge data
      return getAllCompaniesCampaignStat({
        campaignId: campaign.id,
        from: configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
        to: configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
        groupBy: configuration.timeUnit.apiField
      });
    case "getCampaignStats":
      return getCampaignStat({
        campaignId: campaign.id,
        from: configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
        to: configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
        groupBy: configuration.timeUnit.apiField
      });
    case "aggregateBycompany":
      return aggregateCompanyStat(configuration);
    case "getEmployeesStats":
      return getAllEmployeesStat(configuration);
    case "aggregateByemployee":
      return aggregateByEmployeeStat(configuration);
    case "getLocationsStats":
      return getAllLocationsStat(configuration);
    case "getCompanyStats":
      //define single compamy in the map if not defined
      viewStatService.setMapCompanies([configuration.company])
      // Object.defineProperty(mapCompanies, configuration.company.id, {
      //   value: configuration.company,
      //   writable: true
      // });
      return getCompanyStat({
        campaignId: configuration.campaign.id,
        companyId: configuration.company.id,
        from: configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateFrom ? configuration.selectedDateFrom : null) : null,
        to: configuration.timePeriod.value != 'ALL' ? (configuration.selectedDateTo ? configuration.selectedDateTo : null) : null,
        groupBy: configuration.timeUnit.apiField,
      }).then(value=> {
        return Promise.resolve([value])
      })
    default:
      break;
  }
}
function getCsv(configuration, campaign) {
  //check configuration and ask the right csv
  switch (configuration.dataLevel.api) {
    case "getCampaignCompanyStats":
      return getCampaignCsv({
        campaignId: campaign.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
      });

    case "getEmployeesStats":
      return getCompanyCsv({
        campaignId: campaign.id,
        companyId: configuration.company.id,
        from: configuration.selectedDateFrom ? configuration.selectedDateFrom : null,
        to: configuration.selectedDateTo ? configuration.selectedDateTo : null,
      });
    case "getLocationsStats":
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

function combineSingleItem(limitsData,unlimitsData){
  let returnValue={};
        Object.defineProperty(returnValue, 'id', {
          value: limitsData.id,
          writable: true
        });
        Object.defineProperty(returnValue, 'values', {
          value: [],
          writable: true
        });
        limitsData.values.map((val, subindex) => {
          let combinedElement = JSON.parse(JSON.stringify(val));
          combinedElement['distancesNolimits'] = unlimitsData.values[subindex]['distances'];
          combinedElement['trackCountNolimits'] = unlimitsData.values[subindex]['trackCount'];
          returnValue.values.push(combinedElement);
                })
                return returnValue;
}
function combineMultipleLimits(limitedArray, unlimitedArray, key) {
  let combinedArray = []
  if (limitedArray[0].values) {
    limitedArray.map((el, index) => {
      if (el[key] === unlimitedArray[index][key]) {
        let combinedItem = {};
        Object.defineProperty(combinedItem, key, {
          value: el[key],
          writable: true
        });
        Object.defineProperty(combinedItem, 'values', {
          value: [],
          writable: true
        });
        // let combinedItem={company:el[key],values:[]}
        el.values.map((val, subindex) => {
          let combinedElement = JSON.parse(JSON.stringify(val));
          combinedElement['distancesNolimits'] = unlimitedArray[index].values[subindex]['distances'];
          combinedElement['trackCountNolimits'] = unlimitedArray[index].values[subindex]['trackCount'];
          combinedItem.values.push(combinedElement);
        })
        combinedArray.push(combinedItem);
      }
    })
  }
  else {
    limitedArray.map((val, index) => {
      let combinedElement = JSON.parse(JSON.stringify(val));
      combinedElement['distancesNolimits'] = unlimitedArray[index]['distances'];
      combinedElement['trackCountNolimits'] = unlimitedArray[index]['trackCount'];
      //combinedItem.values.push(combinedElement);
      combinedArray.push(combinedElement);
    })

  }
  return combinedArray;

}
function dynamicSort(property) {
  var sortOrder = 1;
  if (property[0] === "-") {
    sortOrder = -1;
    property = property.substr(1);
  }
  return function (a, b) {
    var result = (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
    return result * sortOrder;
  }
}
function getAllCompaniesCampaignStat({ campaignId, from, to, groupBy }) {
  let multiple = [];
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
          ...({ noLimits: true }),
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
          ...({ noLimits: false }),
        },
      }
    ))
  //get statinfo
  multiple.push(companyService.getAllCompanies())
  return axios.all(multiple).then(axios.spread((...responses) => {
    //combine limits and not limits values

    let companiesStateUnlimited = aggregateCompany(responses[0].data);
    let companiesStateLimited = aggregateCompany(responses[1].data);
    viewStatService.setMapCompanies(responses[2]);

    let returnData = combineMultipleLimits(companiesStateLimited.sort(dynamicSort('id')), companiesStateUnlimited.sort(dynamicSort('id')), 'id');
    return Promise.resolve(returnData);
  })).catch(errors => {
    console.log(errors)
    return Promise.reject();
  })
}
function aggregateCompany(allStat) {
  var stats = []
  allStat.forEach(stat => {
    //se la mappa contiene l'azienda, aggiungi la stat al suo array
    let index = stats.findIndex(el => el['company'] == stat['company'])
    if (index != -1) {
      stats[index].values.push(stat)
    }
    //altrimenti aggiungi nuova azienda e array con un elemento
    else {
      let newItem = {}
      Object.defineProperty(newItem, 'company', {
        value: stat['company'],
        writable: true
      });
      Object.defineProperty(newItem, 'id', {
        value: stat['company'],
        writable: true
      });
      Object.defineProperty(newItem, 'values', {
        value: [stat],
        writable: true
      });
      stats.push(newItem)
      // stats.push({ company: stat[field], values: [stat] })
    }
  })
  return stats;
}


function getCampaignStat({ campaignId, from, to, groupBy }) {
  let multiple = [];
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
          ...({ noLimits: true }),
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
          ...({ noLimits: false }),
        },
      }
    ))
  return axios.all(multiple).then(axios.spread((...responses) => {
    //combine limits and not limits values
    let campaignUnLimited = responses[0].data;
    let campaignLimited = responses[1].data;
    let returnData = combineMultipleLimits(campaignLimited, campaignUnLimited);
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
    groupBy }
) {
  let multiple = [];
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
          ...({ noLimits: true }),
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
          ...({ noLimits: false }),
        },
      }
    ))
  return axios.all(multiple).then(axios.spread((...responses) => {
    //combine limits and not limits values
    
    let companyStatLimited = createEmployeeData(responses[0]);
    let companyStatUnLimited = createEmployeeData(responses[1]);
    let combinedItem = combineSingleItem(companyStatLimited,companyStatUnLimited);
    return Promise.resolve(combinedItem);
  })).catch(errors => {
    console.log(errors)
    return Promise.reject();
  })
  
}
function getLocationStat(
  { campaignId,
    companyId,
    locationId,
    from,
    to,
    groupBy}
) {
  let multiple = [];
  // noLimits
  multiple.push(axios
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
          // ...(withTracks ? { withTracks: withTracks } : {}),
          ...({ noLimits: true }),
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
          companyId +
          "/" +
          locationId,
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          // ...(withTracks ? { withTracks: withTracks } : {}),
          ...({ noLimits: false }),
        },
      }
    ))

  return axios.all(multiple).then(axios.spread((...responses) => {
    let unlimitsData=createEmployeeData(responses[0])
    let limitsData=createEmployeeData(responses[1])
    let combinedItem = combineSingleItem(limitsData,unlimitsData);
    return Promise.resolve(combinedItem);
  })).catch(errors => {
    console.log(errors)
    return Promise.reject();
  })
  
}
function getEmployeeStat({
  campaignId,
  //companyId,
  employeeId,
  from,
  to,
  groupBy,
  withTracks = false,

}) {
  let multiple = [];
  // noLimits
  multiple.push(axios
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
          ...({ noLimits: true }),
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
      "/stats/" +
      employeeId,
      {
        params: {
          ...(from ? { from: from } : {}),
          ...(to ? { to: to } : {}),
          ...(groupBy ? { groupBy: groupBy } : {}),
          ...(withTracks ? { withTracks: withTracks } : {}),
          ...({ noLimits: false }),
        },
      }
    ))

  return axios.all(multiple).then(axios.spread((...responses) => {
    //order responses.data
    
    let unlimitsData=createEmployeeData(responses[0]);
    unlimitsData.values=unlimitsData.values.sort(dynamicSort(groupBy=='day'?'date':'month'))
    let limitsData=createEmployeeData(responses[1])
    limitsData.values=limitsData.values.sort(dynamicSort(groupBy=='day'?'date':'month'))
    let combinedItem = {};
        Object.defineProperty(combinedItem, 'id', {
          value: limitsData.id,
          writable: true
        });
        Object.defineProperty(combinedItem, 'values', {
          value: [],
          writable: true
        });
        limitsData.values.map((val, subindex) => {
          let combinedElement = JSON.parse(JSON.stringify(val));
          combinedElement['distancesNolimits'] = unlimitsData.values[subindex]['distances'];
          combinedElement['trackCountNolimits'] = unlimitsData.values[subindex]['trackCount'];
          combinedItem.values.push(combinedElement);        })
    return Promise.resolve(combinedItem);
  })).catch(errors => {
    console.log(errors)
    return Promise.reject();
  })
}
function createEmployeeData(res){
//combine limits and not limits values
let req = res.request.responseURL.split("?")
req = req[0];
// Get the last path:
req = req.split("/");
let id = req[req.length - 1];
res.data = res.data.map(el => {
  return { ...el, id: id };
})
return {id:id,values:res.data};
}
function getCampaignCsv({campaignId, from, to}) {
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
