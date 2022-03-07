import moment from "moment";
export const viewStatService = {
  fillTheViewWithValues,
  setMapEmployees,
  setMapCompanies,
  setMapLocations
};
let mapEmployees = {};
let mapCompanies = {};
let mapLocations = {};

function setMapEmployees(array) {
  mapEmployees = {};
  array.map(el => {
    Object.defineProperty(mapEmployees, el.id, {
      value: el,
      writable: true
    });
  })
}
function setMapCompanies(array) {
  mapCompanies = {};
  array.map(el => {
    Object.defineProperty(mapCompanies, el.id, {
      value: el,
      writable: true
    });
  })
}
function setMapLocations(array) {
  mapLocations = {};
  array.map(el => {
    Object.defineProperty(mapLocations, el.id, {
      value: el,
      writable: true
    });
  })
}
async function fillTheViewWithValues(values, view, selection, currentCampaign) {
  let viewData = {};
  switch (view.item) {
    case 'Tabella':
      viewData.headers = getHeadersTable(values, selection, currentCampaign)
      viewData.headerNumber = getHeadersNumber(selection);
      viewData.subheaders = getSubHeaders(viewData.headers, selection)
      viewData.data = await getData(currentCampaign, viewData.headers, viewData.subheaders, selection, values)
      break;

    default:
      break;
  }
  return viewData;
}

function getHeadersNumber(selection) {
  return selection.dataColumns.length;
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
// if values has more than 1 level it return the number of elements (every row of the table is an element)
// otherwise it is a table with just one row
function getRowByValues(values) {
  if (values[0] && Object.prototype.hasOwnProperty.call(values[0], 'values'))
    return values.length
  return 1;
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
          row[selection.dataColumns[dataColumnsIndex].value + headers[columnIndex]] = (found ? parseInt(getValueByField(found[selection.dataColumns[dataColumnsIndex].apiField])/(isKm(selection.dataColumns[dataColumnsIndex].apiField)?1000:1)) : 0)
        }
      }
      data.push(row)
      //  data.push(...Array(headers.length).fill(selection.dataColumns))
    }
  return data;

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
function getLocationName(id) {
  if (mapLocations[id])
    return Promise.resolve(mapLocations[id].id);
  return Promise.resolve("");
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
function isKm(field){
  return field.startsWith("distances")
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
