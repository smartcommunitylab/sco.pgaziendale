import axios from "axios";
import { statsConfigurations } from "../pages/stats/statsConfigurations";
// import { VARIABLES } from "../variables";
import { campaignService, companyService, locationService } from '.';
// import { companyService } from ".";
import { employeeService } from ".";
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
let mapEmployees = {};
let mapCompanies = {};
 let mapLocations={};

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
// function buildAggregateData(configuration, responses, field) {
//   let aggregateArray = [];
//   for (let index = 0; index < configuration.puntualAggregationItems.length; index++) {
//     let newItem = {}
//     Object.defineProperty(newItem, field, {
//       value: configuration.puntualAggregationItems[index].id,
//       writable: true
//     });
//     Object.defineProperty(newItem, 'values', {
//       value: responses[index],
//       writable: true
//     });
//     aggregateArray.push(newItem)
//   }
//   return aggregateArray;
// }
function getAllLocationsStat(configuration) {
  //getAll  employeesid of company and call aggregateByEmployeeStat
  return locationService.getAllLocations(configuration.company.id).then(values => {
    configuration.puntualAggregationItems = values;
    if (values)
    values.map(el => {
          Object.defineProperty(mapLocations, el.id, {
            value: el,
            writable: true
          });
        })
    return aggregateByLocationStat(configuration)
      })
}
function getAllEmployeesStat(configuration) {
  //getAll  employeesid of company and call aggregateByEmployeeStat
  return employeeService.getAllEmployees(configuration.company.id).then(values => {
    configuration.puntualAggregationItems = values;
    if (values)
    values.map(el => {
          Object.defineProperty(mapEmployees, el.id, {
            value: el,
            writable: true
          });
        })
    // configuration.puntualAggregationItems = values.map(function (obj) {
    //   return obj.id;
    // });
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
  // if (!mapEmployees[Object.keys(mapEmployees)[0]] || mapEmployees[Object.keys(mapEmployees)[0]].companyId != companyId)
  //   arrayRequest.push(employeeService.getAllEmployees(companyId))
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
  // if (!mapEmployees[Object.keys(mapEmployees)[0]] || mapEmployees[Object.keys(mapEmployees)[0]].companyId != companyId)
  //   arrayRequest.push(employeeService.getAllEmployees(companyId))
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
      Object.defineProperty(mapCompanies, configuration.company.id, {
        value: configuration.company,
        writable: true
      });
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
    responses[2].map(el => {
      Object.defineProperty(mapCompanies, el.id, {
        value: el,
        writable: true
      });
    })
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
function getHeadersNumner(selection) {
  return selection.dataColumns.length;
}
async function fillTheViewWithValues(values, view, selection, currentCampaign) {
  let viewData = {};
  switch (view.item) {
    case 'Tabella':
      viewData.headers = getHeadersTable(values, selection, currentCampaign)
      viewData.headerNumber = getHeadersNumner(selection);
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

function getCompanyName(id) {
  if (mapCompanies[id])
    return Promise.resolve(mapCompanies[id].name);
  return Promise.resolve("");
}
function getEmployeeName(id) {
  if (mapEmployees[id])
    return Promise.resolve(mapEmployees[id].name + ' ' + mapEmployees[id].surname);
  return Promise.resolve("");
}
function getLocationName(id){
  if (mapLocations[id])
  return Promise.resolve(mapLocations[id].id);
  return Promise.resolve("");
}

//get the first element of the table (name) based on the selection: if companies get the name of the single company
//if campaign return the current campaign and so on
async function getRowName(obj, selectionValue, currentCampaign) {
  let returnValue = ""
  if (obj)
  switch (selectionValue) {
    case ("companies"):
      //map with companyID and detail
      returnValue = await getCompanyName([obj['id']])
      break;
   case ("company"):
        //map with companyID and detail
        returnValue = await getCompanyName([obj['id']])
        break;
    case ("campaign"):
      returnValue = currentCampaign.title;
      break;
      case ("employees"):
        //TODO add the name of employee
        //map with companyID and detail
        returnValue = await getEmployeeName([obj['id']])
        break;
      case ("locations"):
          //map with companyID and detail
          returnValue = await getLocationName(decodeURI([obj['id']]))
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
  if (values[rowIndex] && Object.prototype.hasOwnProperty.call(values[rowIndex], 'values')) {
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
    return (values[rowIndex].values ? values[rowIndex].values.find(el => {
      return el[selection.timeUnit.value] === headers[columnIndex]
    }) : null)
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
  if (values[0] && Object.prototype.hasOwnProperty.call(values[0], 'values'))
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
  // return axios
  //   .get(
  //     process.env.VUE_APP_BASE_URL +
  //     process.env.VUE_APP_CAMPAIGNS_API +
  //     "/" +
  //     campaignId +
  //     "/agg/" +
  //     companyId +
  //     "/" +
  //     locationId,
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
