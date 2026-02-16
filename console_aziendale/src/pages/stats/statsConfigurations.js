import { VARIABLES } from "../../variables";

const VC = VARIABLES.STATS.VIEWS;
const PA = VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION;
const DC = VARIABLES.STATS.VIEWS.DATACOLUMNS;
export const statsConfigurations = [
  /*
  / Configurazione - Punti Fatti e Utili
  / Specifica per l'ADMIN -> Amministratore di Sistema
  */
  {
    id: '1_punti_global',
    name: VARIABLES.STATS.NAME.POINTS_COUNTED,
    profile: 'global',
    views: [
      {
        dataLevel: [
          { label: VC.DATALEVEL.CAMPAIGN,value: "campaign", puntualAggregation: [{  label:  PA.NONE,  value:  'NONE'}] },
          { label: VC.DATALEVEL.COMPANIES,value: "company", puntualAggregation: [{  label:  PA.NONE,  value: 'NONE'}, { label:  PA.COMPANIES,  value: 'COMPANIES' }]},
        ],
        dataColumns: [  DC.POINTS, DC.COUNTING_POINTS ],
        timeUnit: [
          {label: VC.TIMEUNIT.DAY,value: "day",apiField: "day" },
          {label: VC.TIMEUNIT.WEEK,value: "week",apiField: "week" },
          {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          {label: VC.TIMEUNIT.YEAR,value: "year",apiField: "year" },
          {label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }
        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
          {label:VC.TIMEPERIOD.SPECIFIC,value: "SPECIFIC",apiField: "month" },
        ],
        source: "tracks",
        default: {
          source: "tracks",
          dataColumns: [DC.POINTS,DC.COUNTING_POINTS],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE'},
          dataLevel: {label: VC.DATALEVEL.COMPANIES,value: "company", puntualAggregation: [{  label:  PA.NONE,  value: 'NONE'},{  label:  PA.COMPANIES,  value: 'COMPANIES' }]
          },
          timeUnit:  {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },        
      }
    }
    ],
  },
  /*
  / Configurazione - Punti Fatti e Utili
  / Specifica per il COMPANY ADMIN -> Amministratore Aziendale
  */
  {
    id: '2_punti_company',
    name: VARIABLES.STATS.NAME.POINTS_COUNTED,
    profile: 'company',
    views: [
      {
        source: "tracks",
        dataLevel: [ 
          { label: VC.DATALEVEL.COMPANY,value: "company", puntualAggregation: [{  label:  PA.NONE,  value:  'NONE'}], },
          { label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{  label:  PA.NONE,  value: 'NONE'}, { label:  PA.LOCATIONS,  value: 'LOCATIONS' }] },
          { label: VC.DATALEVEL.EMPLOYEES,value: "employee", puntualAggregation: [{  label:  PA.NONE,  value: 'NONE'}, { label:  PA.EMPLOYEES,  value: 'EMPLOYEES'}] }
        ],
        dataColumns: [ DC.POINTS, DC.COUNTING_POINTS ],
        timeUnit: [
          {label:VC.TIMEUNIT.DAY,value: "day", apiField: "day" },
          {label: VC.TIMEUNIT.WEEK,value: "week",apiField: "week" },
          {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          {label: VC.TIMEUNIT.YEAR,value: "year",apiField: "year" },
          {label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" },
        ],
        timePeriod: [ {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" }, {label:VC.TIMEPERIOD.SPECIFIC,value: "SPECIFIC",apiField: "month" },],
        
        default: {
          source: "tracks",
          dataColumns: [DC.POINTS, DC.COUNTING_POINTS],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {label: VC.DATALEVEL.EMPLOYEES,value: "employee", puntualAggregation: [{  label:  PA.NONE,  value: 'NONE'}, {  label:  PA.EMPLOYEES,  value: 'EMPLOYEES' }] },
          timeUnit:  {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
      }
    },
    ],
  },
    /*
  / Configurazione - Utenti e registrazione
  / Specifica per il COMPANY ADMIN -> Amministratore Aziendale
  */
  {
    id: '3_utenti_global',
    name: VARIABLES.STATS.NAME.EMPLOYEES_PARTECIPATION,
    profile: 'global',
    views: [
      {
        source: "employee",
        dataLevel: [
          {label: VC.DATALEVEL.COMPANY,value: "company", puntualAggregation: [{  label:  PA.NONE,  value:  'NONE'}] },
          {label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{  label:  PA.NONE,  value: 'NONE'}, { label:  PA.LOCATIONS,  value: 'LOCATIONS' }] },
        ],
        dataColumns: [ DC.REGISTERED_EMPLOYEES, DC.ACTIVE_EMPLOYEES, DC.UNSUBSCRIBED_EMPLOYEES, DC.REGISTERED_EMPLOYEES_PERCENTAGE, DC.ACTIVE_EMPLOYEES_PERCENTAGE, DC.UNSUBSCRIBED_EMPLOYEES_PERCENTAGE, DC.ACTIVE_EMPLOYEES_PERCENTAGE_REGISTRATION, DC.UNSUBSCRIBED_EMPLOYEES_PERCENTAGE_REGISTRATION ],
        timeUnit: [
          {label: VC.TIMEUNIT.DAY,value: "day",apiField: "day" },
          {label: VC.TIMEUNIT.WEEK,value: "week",apiField: "week" },
          {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          {label: VC.TIMEUNIT.YEAR,value: "year",apiField: "year"},          
          {label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }

        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
          {label:VC.TIMEPERIOD.SPECIFIC,value: "SPECIFIC",apiField: "month" },
        ],
        
        default: {
          source: "employee",
          dataColumns: [DC.REGISTERED_EMPLOYEES,DC.ACTIVE_EMPLOYEES,DC.UNSUBSCRIBED_EMPLOYEES ],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {label: VC.DATALEVEL.COMPANY,value: "company", puntualAggregation: [{  label:  PA.NONE,  value:  'NONE'}], },
          timeUnit:  {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
      }
    },
    ],
  },
  
  /*
  / Configurazione - utenti
  / Specifica per il COMPANY ADMIN -> Amministratore Aziendale
  */
 /*
  / Configurazione - Utenti e registrazione
  / Specifica per il COMPANY ADMIN -> Amministratore Aziendale
  */
  {
    id: '4_utenti_company',
    name: VARIABLES.STATS.NAME.EMPLOYEES_PARTECIPATION,
    profile: 'company',
    views: [
      {
        source: "employee",
        dataLevel: [
          {label: VC.DATALEVEL.COMPANY,value: "company", puntualAggregation: [{  label:  PA.NONE,  value:  'NONE'}] },
          {label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{  label:  PA.NONE,  value: 'NONE'}, { label:  PA.LOCATIONS,  value: 'LOCATIONS' }] },
          // {label: VC.DATALEVEL.EMPLOYEES,value: "employee", puntualAggregation: [{  label:  PA.NONE,  value: 'NONE'}, { label:  PA.EMPLOYEES,  value: 'EMPLOYEES'}] }
        ],
        dataColumns: [ DC.REGISTERED_EMPLOYEES, DC.ACTIVE_EMPLOYEES, DC.UNSUBSCRIBED_EMPLOYEES, DC.REGISTERED_EMPLOYEES_PERCENTAGE, DC.ACTIVE_EMPLOYEES_PERCENTAGE, DC.UNSUBSCRIBED_EMPLOYEES_PERCENTAGE, DC.ACTIVE_EMPLOYEES_PERCENTAGE_REGISTRATION, DC.UNSUBSCRIBED_EMPLOYEES_PERCENTAGE_REGISTRATION ],
        timeUnit: [
          {label: VC.TIMEUNIT.DAY,value: "day",apiField: "day" },
          {label: VC.TIMEUNIT.WEEK,value: "week",apiField: "week" },
          {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          {label: VC.TIMEUNIT.YEAR,value: "year",apiField: "year"},          
          {label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }

        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
          {label:VC.TIMEPERIOD.SPECIFIC,value: "SPECIFIC",apiField: "month" },
        ],
        
        default: {
          source: "employee",
          dataColumns: [DC.REGISTERED_EMPLOYEES,DC.ACTIVE_EMPLOYEES,DC.UNSUBSCRIBED_EMPLOYEES, ],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {label: VC.DATALEVEL.COMPANY,value: "company", puntualAggregation: [{  label:  PA.NONE,  value:  'NONE'}], },
          timeUnit:  {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
      }
    },
    ],
  },
  
  {
    id: "11_performance_km_global",
    name: VARIABLES.STATS.NAME.PERFORMANCE_KM,
    profile: 'global',
    views: [
      {
        dataLevel: [
          { label: VC.DATALEVEL.CAMPAIGN,  value: "campaign",  puntualAggregation: [{ label:  PA.NONE, value:  'NONE' }], },
          { label: VC.DATALEVEL.COMPANIES, value: "company",  puntualAggregation: [{ label:  PA.NONE, value: 'NONE' },  { label:  PA.COMPANIES, value: 'COMPANIES' } ]},
          // { label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, {  label:  PA.LOCATIONS,  value: 'LOCATIONS' } ]},            
        ],
        dataColumns: [  DC.DISTANCE, DC.DISTANCE_AVG_TRIP, DC.DISTANCE_AVG_LEG, DC.DISTANCE_PERCENTAGE,/* DC.DISTANCE_PERCENTAGE_AVG_TRIP, DC.DISTANCE_PERCENTAGE_AVG_LEG,*/],
        timeUnit: [
          { label: VC.TIMEUNIT.HOUR, value: "hour", apiField: "hour" },
          { label: VC.TIMEUNIT.DOW, value: "dayOfWeek", apiField: "dayOfWeek" },
          { label: VC.TIMEUNIT.DAY, value: "day", apiField: "day" },
          { label: VC.TIMEUNIT.WEEK,value: "week",apiField: "week" },
          { label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          { label: VC.TIMEUNIT.YEAR,value: "year",apiField: "year" },
          { label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }
        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
          {label:VC.TIMEPERIOD.SPECIFIC,value: "SPECIFIC",apiField: "month" },
        ],
        source: "tracks",
        default: {
          source: "tracks",
          dataColumns: [DC.DISTANCE ],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {
            label: VC.DATALEVEL.COMPANIES,value: "company",
            puntualAggregation: [{  label:  PA.NONE,  value: 'NONE' }, {  label:  PA.COMPANIES,  value: 'COMPANIES'}]
          },
          timeUnit:  {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },        
      }
    }
    ],
  },
  {
    id: "12_performance_km_company",
    name: VARIABLES.STATS.NAME.PERFORMANCE_KM,
    profile: 'company',
    views: [
      {
        dataLevel: [
          { label: VC.DATALEVEL.COMPANIES, value: "company",  puntualAggregation: [{ label:  PA.NONE, value: 'NONE' },  { label:  PA.COMPANIES, value: 'COMPANIES' } ]},
          { label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, { label:  PA.LOCATIONS,  value: 'LOCATIONS' } ]},            
          { label: VC.DATALEVEL.EMPLOYEES,value: "employee", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, { label:  PA.EMPLOYEES,  value: 'EMPLOYEES',  } ]},
        ],
        dataColumns: [  DC.DISTANCE, DC.DISTANCE_AVG_TRIP, DC.DISTANCE_AVG_LEG, DC.DISTANCE_PERCENTAGE,/* DC.DISTANCE_PERCENTAGE_AVG_TRIP, DC.DISTANCE_PERCENTAGE_AVG_LEG,*/],
        timeUnit: [
          { label: VC.TIMEUNIT.HOUR, value: "hour", apiField: "hour" },
          { label: VC.TIMEUNIT.DOW, value: "dayOfWeek", apiField: "dayOfWeek" },
          { label: VC.TIMEUNIT.DAY, value: "day", apiField: "day" },
          { label: VC.TIMEUNIT.WEEK,value: "week",apiField: "week" },
          { label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          { label: VC.TIMEUNIT.YEAR,value: "year",apiField: "year" },
          { label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }
        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
          {label:VC.TIMEPERIOD.SPECIFIC,value: "SPECIFIC",apiField: "month" },
        ],
        source: "tracks",
        default: {
          source: "tracks",
          dataColumns: [DC.DISTANCE ],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {
            label: VC.DATALEVEL.EMPLOYEES,value: "employee",
            puntualAggregation: [{  label:  PA.NONE,  value: 'NONE'},{  label:  PA.EMPLOYEES,  value: 'EMPLOYEES'}]  
          },
          timeUnit:  {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },        
      }
    }
    ],
  },
  {
    id: "13_performance_ore_global",
    name: VARIABLES.STATS.NAME.PERFORMANCE_HOURS,
    profile: 'global',
    views: [
      {
        dataLevel: [
          { label: VC.DATALEVEL.CAMPAIGN,  value: "campaign",  puntualAggregation: [{ label:  PA.NONE, value:  'NONE' }], },
          { label: VC.DATALEVEL.COMPANIES, value: "company",  puntualAggregation: [{ label:  PA.NONE, value: 'NONE' },  { label:  PA.COMPANIES, value: 'COMPANIES' } ]},
          // { label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, {  label:  PA.LOCATIONS,  value: 'LOCATIONS' } ]},            
        ],
        dataColumns: [  DC.DURATION, DC.DURATION_AVG_TRIP, DC.DURATION_AVG_LEG, DC.DURATION_PERCENTAGE,/* DC.DURATION_PERCENTAGE_AVG_TRIP, DC.DURATION_PERCENTAGE_AVG_LEG,*/],
        timeUnit: [
          { label: VC.TIMEUNIT.HOUR, value: "hour", apiField: "hour" },
          { label: VC.TIMEUNIT.DOW, value: "dayOfWeek", apiField: "dayOfWeek" },
          { label: VC.TIMEUNIT.DAY, value: "day", apiField: "day" },
          { label: VC.TIMEUNIT.WEEK,value: "week",apiField: "week" },
          { label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          { label: VC.TIMEUNIT.YEAR,value: "year",apiField: "year" },
          { label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }
        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
          {label:VC.TIMEPERIOD.SPECIFIC,value: "SPECIFIC",apiField: "month" },
        ],
        source: "tracks",
        default: {
          source: "tracks",
          dataColumns: [DC.DURATION ],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {
            label: VC.DATALEVEL.COMPANIES,value: "company",
            puntualAggregation: [{  label:  PA.NONE,  value: 'NONE' }, {  label:  PA.COMPANIES,  value: 'COMPANIES'}]
          },
          timeUnit:  {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },        
      }
    }
    ],
  },
  {
    id: "14_performance_hours_company",
    name: VARIABLES.STATS.NAME.PERFORMANCE_HOURS,
    profile: 'company',
    views: [
      {
        dataLevel: [
          { label: VC.DATALEVEL.COMPANIES, value: "company",  puntualAggregation: [{ label:  PA.NONE, value: 'NONE' },  { label:  PA.COMPANIES, value: 'COMPANIES' } ]},
          { label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, { label:  PA.LOCATIONS,  value: 'LOCATIONS' } ]},            
          { label: VC.DATALEVEL.EMPLOYEES,value: "employee", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, { label:  PA.EMPLOYEES,  value: 'EMPLOYEES',  } ]},
        ],
        dataColumns: [  DC.DURATION, DC.DURATION_AVG_TRIP, DC.DURATION_AVG_LEG, DC.DURATION_PERCENTAGE,/* DC.DURATION_PERCENTAGE_AVG_TRIP, DC.DURATION_PERCENTAGE_AVG_LEG,*/],
        timeUnit: [
          { label: VC.TIMEUNIT.HOUR, value: "hour", apiField: "hour" },
          { label: VC.TIMEUNIT.DOW, value: "dayOfWeek", apiField: "dayOfWeek" },
          { label: VC.TIMEUNIT.DAY, value: "day", apiField: "day" },
          { label: VC.TIMEUNIT.WEEK,value: "week",apiField: "week" },
          { label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          { label: VC.TIMEUNIT.YEAR,value: "year",apiField: "year" },
          { label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }
        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
          {label:VC.TIMEPERIOD.SPECIFIC,value: "SPECIFIC",apiField: "month" },
        ],
        source: "tracks",
        default: {
          source: "tracks",
          dataColumns: [DC.DURATION ],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {
            label: VC.DATALEVEL.EMPLOYEES,value: "employee",
            puntualAggregation: [{  label:  PA.NONE,  value: 'NONE'},{  label:  PA.EMPLOYEES,  value: 'EMPLOYEES'}]  
          },
          timeUnit:  {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },        
      }
    }
    ],
  },
  {
    id: "15_co2_global",
    name: VARIABLES.STATS.NAME.ENVIRONMENTAL_IMPACT,
    profile: 'global',
    views: [
      {
        dataLevel: [
          { label: VC.DATALEVEL.CAMPAIGN,  value: "campaign",  puntualAggregation: [{ label:  PA.NONE, value:  'NONE' }], },
          { label: VC.DATALEVEL.COMPANIES, value: "company",  puntualAggregation: [{ label:  PA.NONE, value: 'NONE' },  { label:  PA.COMPANIES, value: 'COMPANIES' } ]},
          // { label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, {  label:  PA.LOCATIONS,  value: 'LOCATIONS' } ]},            
        ],
        dataColumns: [  DC.CO2_SAVED, DC.CO2_SAVED_AVG_TRIP, DC.CO2_SAVED_AVG_LEG, DC.CO2_SAVED_PERCENTAGE,/* DC.CO2_SAVED_PERCENTAGE_AVG_TRIP, DC.CO2_SAVED_PERCENTAGE_AVG_LEG,*/],
        timeUnit: [
          { label: VC.TIMEUNIT.WEEK,value: "week",apiField: "week" },
          { label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          { label: VC.TIMEUNIT.YEAR,value: "year",apiField: "year" },
          { label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }
        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
          {label:VC.TIMEPERIOD.SPECIFIC,value: "SPECIFIC",apiField: "month" },
        ],
        source: "tracks",
        default: {
          source: "tracks",
          dataColumns: [DC.CO2_SAVED ],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {
            label: VC.DATALEVEL.COMPANIES,value: "company",
            puntualAggregation: [{  label:  PA.NONE,  value: 'NONE' }, {  label:  PA.COMPANIES,  value: 'COMPANIES'}]
          },
          timeUnit:  {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },        
      }
    }
    ],
  },
  {
    id: "16_co2_company",
    name: VARIABLES.STATS.NAME.ENVIRONMENTAL_IMPACT,
    profile: 'company',
    views: [
      {
        dataLevel: [
          { label: VC.DATALEVEL.COMPANIES, value: "company",  puntualAggregation: [{ label:  PA.NONE, value: 'NONE' },  { label:  PA.COMPANIES, value: 'COMPANIES' } ]},
          { label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, { label:  PA.LOCATIONS,  value: 'LOCATIONS' } ]},            
        ],
        dataColumns: [  DC.CO2_SAVED, DC.CO2_SAVED_AVG_TRIP, DC.CO2_SAVED_AVG_LEG, DC.CO2_SAVED_PERCENTAGE,/* DC.CO2_SAVED_PERCENTAGE_AVG_TRIP, DC.CO2_SAVED_PERCENTAGE_AVG_LEG,*/],
        timeUnit: [
          { label: VC.TIMEUNIT.WEEK,value: "week",apiField: "week" },
          { label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          { label: VC.TIMEUNIT.YEAR,value: "year",apiField: "year" },
          { label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }
        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
          {label:VC.TIMEPERIOD.SPECIFIC,value: "SPECIFIC",apiField: "month" },
        ],
        source: "tracks",
        default: {
          source: "tracks",
          dataColumns: [DC.CO2_SAVED ],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {
            label: VC.DATALEVEL.EMPLOYEES,value: "location",
            puntualAggregation: [{  label:  PA.NONE,  value: 'NONE'},{  label:  PA.LOCATIONS,  value: 'LOCATIONS'}]  
          },
          timeUnit:  {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },        
      }
    }
    ],
  },
  {
    id: "17_trips_global",
    name: VARIABLES.STATS.NAME.TRIPS,
    profile: 'global',
    views: [
      {
        dataLevel: [
          { label: VC.DATALEVEL.CAMPAIGN,  value: "campaign",  puntualAggregation: [{ label:  PA.NONE, value:  'NONE' }], },
          { label: VC.DATALEVEL.COMPANIES, value: "company",  puntualAggregation: [{ label:  PA.NONE, value: 'NONE' },  { label:  PA.COMPANIES, value: 'COMPANIES' } ]},
          // { label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, {  label:  PA.LOCATIONS,  value: 'LOCATIONS' } ]},            
        ],
        dataColumns: [  DC.N_TRAVEL, DC.N_TRAVEL_SINGLE, DC.N_TRAVEL_MULTI, DC.N_COUNTING_TRAVEL,/* DC.DISTANCE_AVG_TRIP, DC.DISTANCE_AVG_LEG,*/],
        timeUnit: [
          { label: VC.TIMEUNIT.HOUR, value: "hour", apiField: "hour" },
          { label: VC.TIMEUNIT.DOW, value: "dayOfWeek", apiField: "dayOfWeek" },
          { label: VC.TIMEUNIT.WEEK,value: "week",apiField: "week" },
          { label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          { label: VC.TIMEUNIT.YEAR,value: "year",apiField: "year" },
          { label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }
        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
          {label:VC.TIMEPERIOD.SPECIFIC,value: "SPECIFIC",apiField: "month" },
        ],
        source: "tracks",
        noGroupByMean: false, 
        default: {
          source: "tracks",
          dataColumns: [DC.N_TRAVEL, DC.N_TRAVEL_SINGLE, DC.N_TRAVEL_MULTI ],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {
            label: VC.DATALEVEL.COMPANIES,value: "company",
            puntualAggregation: [{  label:  PA.NONE,  value: 'NONE' }, {  label:  PA.COMPANIES,  value: 'COMPANIES'}]
          },
          timeUnit:  {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },        
      }
    }
    ],
  },  
  {
    id: "18_trips_company",
    name: VARIABLES.STATS.NAME.TRIPS,
    profile: 'company',
    views: [
      {
        dataLevel: [
          { label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, { label:  PA.LOCATIONS,  value: 'LOCATIONS' } ]},            
          { label: VC.DATALEVEL.EMPLOYEES,value: "employee", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, { label:  PA.EMPLOYEES,  value: 'EMPLOYEES',  } ]},
        ],
        dataColumns: [  DC.N_TRAVEL, DC.N_TRAVEL_SINGLE, DC.N_TRAVEL_MULTI, DC.N_COUNTING_TRAVEL,/* DC.DISTANCE_AVG_TRIP, DC.DISTANCE_AVG_LEG,*/],
        timeUnit: [
          { label: VC.TIMEUNIT.HOUR, value: "hour", apiField: "hour" },
          { label: VC.TIMEUNIT.DOW, value: "dayOfWeek", apiField: "dayOfWeek" },
          { label: VC.TIMEUNIT.WEEK,value: "week",apiField: "week" },
          { label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          { label: VC.TIMEUNIT.YEAR,value: "year",apiField: "year" },
          { label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }
        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },
          {label:VC.TIMEPERIOD.SPECIFIC,value: "SPECIFIC",apiField: "month" },
        ],
        source: "tracks",
        noGroupByMean: false, 
        default: {
          source: "tracks",
          dataColumns: [DC.N_TRAVEL, DC.N_TRAVEL_SINGLE, DC.N_TRAVEL_MULTI ],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {
            label: VC.DATALEVEL.LOCATIONS,value: "location",
            puntualAggregation: [{  label:  PA.NONE,  value: 'NONE' }, {  label:  PA.LOCATIONS,  value: 'LOCATIONS'}]
          },
          timeUnit:  {label: VC.TIMEUNIT.MONTH,value: "month",apiField: "month" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },        
      }
    }
    ],
  },
  {
    id: "19_multi_global",
    name: VARIABLES.STATS.NAME.MULTI,
    profile: 'global',
    views: [
      {
        dataLevel: [
          { label: VC.DATALEVEL.CAMPAIGN,  value: "campaign",  puntualAggregation: [{ label:  PA.NONE, value:  'NONE' }], },
          { label: VC.DATALEVEL.COMPANIES, value: "company",  puntualAggregation: [{ label:  PA.NONE, value: 'NONE' },  { label:  PA.COMPANIES, value: 'COMPANIES' } ]},
          // { label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, {  label:  PA.LOCATIONS,  value: 'LOCATIONS' } ]},            
        ],
        dataColumns: [  DC.MM_TRAVEL, DC.MM_DURATION, DC.MM_DISTANCE, DC.MM_DISTANCE_AVG, DC.MM_DURATION_AVG],
        timeUnit: [
          { label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }
        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" }
        ],
        source: "multimodal",
        noGroupByMean: true, 
        default: {
          source: "multimodal",
          dataColumns: [DC.MM_TRAVEL, DC.MM_DURATION, DC.MM_DISTANCE],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {
            label: VC.DATALEVEL.COMPANIES,value: "company",
            puntualAggregation: [{  label:  PA.NONE,  value: 'NONE' }, {  label:  PA.COMPANIES,  value: 'COMPANIES'}]
          },
          timeUnit:   { label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },        
      }
    }
    ],
  },    
    {
    id: "20_multi_company",
    name: VARIABLES.STATS.NAME.MULTI,
    profile: 'company',
    views: [
      {
        dataLevel: [
          { label: VC.DATALEVEL.COMPANIES, value: "company",  puntualAggregation: [{ label:  PA.NONE, value: 'NONE' } ]},
          { label: VC.DATALEVEL.LOCATIONS,value: "location", puntualAggregation: [{ label:  PA.NONE,  value: 'NONE' }, {  label:  PA.LOCATIONS,  value: 'LOCATIONS' } ]},            
        ],
        dataColumns: [  DC.MM_TRAVEL, DC.MM_DURATION, DC.MM_DISTANCE, DC.MM_DISTANCE_AVG, DC.MM_DURATION_AVG],
        timeUnit: [
          { label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" }
        ],
        timePeriod: [
          {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" }
        ],
        source: "multimodal",
        noGroupByMean: true, 
        default: {
          source: "multimodal",
          dataColumns: [DC.MM_TRAVEL, DC.MM_DURATION, DC.MM_DISTANCE],
          puntualAggregationSelected:{label:  PA.NONE,value: 'NONE', },
          dataLevel: {
            label: VC.DATALEVEL.LOCATIONS,value: "location",
            puntualAggregation: [{  label:  PA.NONE,  value: 'NONE' }, {  label:  PA.LOCATIONS,  value: 'LOCATIONS'}]
          },
          timeUnit:   { label:VC.TIMEUNIT.CAMPAIGN,value: "campaign",apiField: "total" },
          timePeriod: {label:VC.TIMEPERIOD.ALL,value: "ALL",apiField: "" },        
      }
    }
    ],
  },    
];
