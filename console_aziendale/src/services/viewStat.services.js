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

function meanLabel(label, mean) {
    switch (mean) {
        case 'bike':
            return label.replace('_mezzo_', 'bici');
        case 'car':
            return label.replace('_mezzo_', 'auto');
        case 'train':
            return label.replace('_mezzo_', 'treno');
        case 'walk':
            return label.replace('_mezzo_', 'piedi');
        case 'bus':
            return label.replace('_mezzo_', 'autobus');
        case 'boat':
            return label.replace('_mezzo_', 'barca');
        default:
            return label;
    }
}

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
      viewData.subheaders = getSubHeaders(viewData.headers, selection)
      viewData.headerNumber = Math.floor(viewData.subheaders.length / viewData.headers.length);
      for (let i = 0; i < viewData.subheaders.length; i++) {
        let s = viewData.subheaders[i];
        s.class = (i == 0 || (i % viewData.headerNumber == 0)) ? 'cell-agg': '';
      }
      viewData.data = await getData(viewData.headers, selection, values)
      break;

    default:
      break;
  }
  return viewData;
}

// based on values and selection, return the right headers for the table (day, month or Campaign)
function getHeadersTable(values, selection, currentCampaign) {
  let from = selection.timePeriod.value == 'SPECIFIC' ? selection.selectedDateFrom : currentCampaign.from
  let to = selection.timePeriod.value == 'SPECIFIC' ? selection.selectedDateTo : currentCampaign.to
  let headers = [];
  switch (selection.timeUnit.value) {
    case 'month':
      //get all month from selection.company.from to selection.company.to 
      headers.push(...getPeriodBetweenDates(moment(from), moment(to), 'month'));
      break;
    case 'year':
      //get all month from selection.company.from to selection.company.to 
      headers.push(...getPeriodBetweenDates(moment(from), moment(to), 'year'));
      break;
    case 'date':
      //get all month from selection.company.from to selection.company.to 
      headers.push(...getPeriodBetweenDates(moment(from), moment(to), 'day'));
      break;
    case 'week':
      //get all month from selection.company.from to selection.company.to 
      headers.push(...getPeriodBetweenDates(moment(from), moment(to), 'week'));
      break;
    case 'dayOfWeek':
      //get all month from selection.company.from to selection.company.to 
      headers.push(...["Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"]);
      break;
    case 'hour':
      //get all month from selection.company.from to selection.company.to 
      headers.push(...["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"]);
      break;
    case 'campaign':
      headers.push('');
      break;
    default:
      headers.push('');
      break;
  }
  return headers;
}
//based on configuration, return the subheader=header.length*selection.dataColumns
function getSubHeaders(headers, selection) {
  let subheaders = [{ text: 'Nome', value: 'name', class: 'cell-agg'}];
  for (let i = 0; i < headers.length; i++) {
    for (let k = 0; k < selection.dataColumns.length; k++) {
      let dc = selection.dataColumns[k];
      subheaders.push({ text: dc.label, value: dc.value + headers[i]})
    }
    if (selection.groupByMean) {
      for (let k = 0; k < selection.dataColumns.length; k++) {
        let dc = selection.dataColumns[k];
        selection.means.forEach(m => {
          subheaders.push({ text: meanLabel(dc.meanLabel, m), value: m + '_mean_' + dc.value + headers[i]})
        });
      }
    }
  }
  return subheaders;
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
    case 'year':
      while (endDate > startDate || startDate.format('YYYY') === endDate.format('YYYY')) {
        timeValues.push(startDate.format('YYYY'));
        startDate.add(1, 'year');
      }
      break;
    case 'week':
      while (endDate > startDate || startDate.format('YYYY-WW') === endDate.format('YYYY-WW')) {
        timeValues.push(startDate.format('YYYY-WW'));
        startDate.add(1, 'week');
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
async function getData(headers, selection, values) {
  let data = []
  if (values) {
    console.log('values', values);
    for (let rowIndex = 0; rowIndex < values.length; rowIndex++) {
      //set detail name (it should be done more generic getting a function that could be company or locations or employees)
      let name = values[rowIndex].key;
      if (name.indexOf(';') > -1) {
        let code = name.substring(name.indexOf(';') + 1, name.length);
        name = name.substring(0, name.indexOf(';'));
        name += '<br><small>(' + code + ')</small>';
      }
      let row = { name: name };
      for (let columnIndex = 0; columnIndex < headers.length; columnIndex++) {
        // entity corresponding the row and the time aggregation element
        let found = findElementInValues(values, rowIndex, columnIndex, selection, headers);
        //set values for that row for every dataColumns
        for (let dataColumnsIndex = 0; dataColumnsIndex < selection.dataColumns.length; dataColumnsIndex++) {
          let dc = selection.dataColumns[dataColumnsIndex];
          row[dc.value + headers[columnIndex]] = (found ? parseInt(found[dc.apiField]) : 0)
          if (selection.groupByMean) {
            selection.means.forEach(m => {
              row[m + '_mean_' + dc.value + headers[columnIndex]] = (found ? parseInt(found[m+ '_mean_'+dc.value ]) : 0)
            });
          }
        }
      }
      data.push(row)
    }
  }
  return data;
}

//find the element of the row depending if the data has one, 2 dimension (1 or multiple row) or is an aggregation
function findElementInValues(values, rowIndex, columnIndex, selection, headers) {
  if (!headers[columnIndex]) {
    return values[rowIndex].values.length > 0 ? values[rowIndex].values[0] : null;
  }
  return values[rowIndex].values.find(el => el[selection.timeUnit.value] === headers[columnIndex])
}
