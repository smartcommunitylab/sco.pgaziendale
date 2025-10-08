import { VARIABLES } from "../../variables";

export const statsConfigurations = [
  /*
  / Configurazione - Punti Fatti e Utili
  / Specifica per l'ADMIN -> Amministratore di Sistema
  */
  {
    id: 0,
    name: VARIABLES.STATS.NAME.KM_COUNTED,
    profile: 'global',
    views: [
      {
        dataLevel: [
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.CAMPAIGN,
            value: "campaign",
            // api: "getCampaignStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANIES,
            value: "company",
            // api: "getCampaignCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              // function:''
            },{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.COMPANIES,
              value: 'COMPANIES',
              // function:'aggregateBycompany'
            }]
          },
        ],
        dataColumns: [ 
          VARIABLES.STATS.VIEWS.DATACOLUMNS.POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.DISTANCE,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.DURATION,
          

        ],
        timeUnit: [
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.HOUR,
            value: "hour",
            apiField: "hour"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.DOW,
            value: "dayOfWeek",
            apiField: "dayOfWeek"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
            value: "week",
            apiField: "week"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.YEAR,
            value: "year",
            apiField: "year"
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
            value: "campaign",
            apiField: "total"
          }
        ],
        timePeriod: [
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: ""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.SPECIFIC,
            value: "SPECIFIC",
            apiField: "month"
          },
        ],
        source: "tracks",
        default: {
          source: "tracks",
          dataColumns: [
            VARIABLES.STATS.VIEWS.DATACOLUMNS.POINTS,
            VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL
          ],
          puntualAggregationSelected:{
            label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
            value: 'NONE',
            // function:''
          },
          dataLevel: {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANIES,
            value: "company",
            // api: "getCampaignCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              // function:''
            },{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.COMPANIES,
              value: 'COMPANIES',
              // function:'aggregateBycompany'
            }]
          },
          timeUnit:  {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month"
          },
          timePeriod: {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: ""
          },        
      }
    }
    ],
  },
  /*
  / Configurazione - Km Fatti e Utili
  / Specifica per il COMPANY ADMIN -> Amministratore Aziendale
  */
  {
    id: 1,
    name: VARIABLES.STATS.NAME.KM_COUNTED,
    profile: 'company',
    views: [
      {
        source: "tracks",
        dataLevel: [
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
            value: "company",
            // api: "getCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
            value: "location",
            // api: "getLocationsStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              // function:''
            }
            ,{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.LOCATIONS,
              value: 'LOCATIONS',
              // function:'aggregateByLocation'
            }
            ]
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.EMPLOYEES,
            value: "employee",
            // api: "getEmployeesStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              // function:''
            },{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.EMPLOYEES,
              value: 'EMPLOYEES',
              // function:'aggregateByemployee'
            }]
          }
        ],
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.DISTANCE,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.DURATION,
        ],
        timeUnit: [
          {
            label:VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
            value: "campaign",
            apiField: "total"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.HOUR,
            value: "hour",
            apiField: "hour"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.DOW,
            value: "dayOfWeek",
            apiField: "dayOfWeek"
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEUNIT.DAY,
            value: "date",
            apiField: "day"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
            value: "week",
            apiField: "week"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.YEAR,
            value: "year",
            apiField: "year"
          },
        ],
        timePeriod: [
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: ""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.SPECIFIC,
            value: "SPECIFIC",
            apiField: "month"
          },
        ],
        
        default: {
          source: "tracks",
          dataColumns: [
            VARIABLES.STATS.VIEWS.DATACOLUMNS.POINTS,
            ],
          puntualAggregationSelected:{
            label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
            value: 'NONE',
            // function:''
          },
          dataLevel: {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.EMPLOYEES,
            value: "employee",
            // api: "getEmployeesStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              // function:''
            },{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.EMPLOYEES,
              value: 'EMPLOYEES',
              // function:'aggregateByemployee'
            }]
          },
          timeUnit:  {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month"
          },
          timePeriod: {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: ""
          },
      }
    },

    ],
  },
  /*
  / Configurazione - Punti Fatti e Utili
  / Specifica per il COMPANY ADMIN -> Amministratore Aziendale
  */
  {
    id: 2,
    name: VARIABLES.STATS.NAME.ENVIRONMENTAL_IMPACT,
    profile: 'company',
    views: [
      {
        source: "tracks",
        dataLevel: [
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
            value: "company",
            // api: "getCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
            value: "location",
            // api: "getLocationsStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              // function:''
            },{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.LOCATIONS,
              value: 'LOCATIONS',
              // function:'aggregateByLocation'
            }]
          }
        ],
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.CO2_SAVED,
        ],
        timeUnit: [
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.HOUR,
            value: "hour",
            apiField: "hour"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.DOW,
            value: "dayOfWeek",
            apiField: "dayOfWeek"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.YEAR,
            value: "year",
            apiField: "year"
          },          {
            label:VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
            value: "campaign",
            apiField: "total"
          }

        ],
        timePeriod: [
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: ""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.SPECIFIC,
            value: "SPECIFIC",
            apiField: "month"
          },
        ],
        
        default: {
          source: "tracks",
          dataColumns: [
            VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
            VARIABLES.STATS.VIEWS.DATACOLUMNS.POINTS,
            VARIABLES.STATS.VIEWS.DATACOLUMNS.CO2_SAVED
  
          ],
          puntualAggregationSelected:{
            label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
            value: 'NONE',
            // function:''
          },
          dataLevel: {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
            value: "company",
            // api: "getCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          timeUnit:  {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month"
          },
          timePeriod: {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: ""
          },
      }
    },

    ],
  },
    /*
  / Configurazione - Utenti e registrazione
  / Specifica per il COMPANY ADMIN -> Amministratore Aziendale
  */
  {
    id: 3,
    name: VARIABLES.STATS.NAME.EMPLOYEES_PARTECIPATION,
    profile: 'global',
    views: [
      {
        source: "employee",
        dataLevel: [
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
            value: "company",
            // api: "getCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
            value: "location",
            // api: "getLocationsStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              // function:''
            },{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.LOCATIONS,
              value: 'LOCATIONS',
              // function:'aggregateByLocation'
            }]
          }
        ],
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.REGISTERED_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.ACTIVE_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.UNSUBSCRIBED_EMPLOYEES,
        ],
        timeUnit: [
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.YEAR,
            value: "year",
            apiField: "year"
          },          {
            label:VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
            value: "campaign",
            apiField: "total"
          }

        ],
        timePeriod: [
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: ""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.SPECIFIC,
            value: "SPECIFIC",
            apiField: "month"
          },
        ],
        
        default: {
          source: "employee",
          dataColumns: [
            VARIABLES.STATS.VIEWS.DATACOLUMNS.REGISTERED_EMPLOYEES,
            VARIABLES.STATS.VIEWS.DATACOLUMNS.ACTIVE_EMPLOYEES,
            VARIABLES.STATS.VIEWS.DATACOLUMNS.UNSUBSCRIBED_EMPLOYEES,
          ],
          puntualAggregationSelected:{
            label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
            value: 'NONE',
            // function:''
          },
          dataLevel: {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
            value: "company",
            // api: "getCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          timeUnit:  {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month"
          },
          timePeriod: {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: ""
          },
      }
    },
    ],
  },
  /*
  / Configurazione - utenti
  / Specifica per il COMPANY ADMIN -> Amministratore Aziendale
  */
  {
    id: 4,
    name: VARIABLES.STATS.NAME.EMPLOYEES_PARTECIPATION,
    profile: 'company',
    views: [
      {
        source: "employee",
        dataLevel: [
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
            value: "company",
            // api: "getCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
            value: "location",
            // api: "getLocationsStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              // function:''
            },{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.LOCATIONS,
              value: 'LOCATIONS',
              // function:'aggregateByLocation'
            }]
          }
        ],
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.REGISTERED_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.ACTIVE_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.UNSUBSCRIBED_EMPLOYEES,
        ],
        timeUnit: [
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month"
          },
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.YEAR,
            value: "year",
            apiField: "year"
          },          {
            label:VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
            value: "campaign",
            apiField: "total"
          }

        ],
        timePeriod: [
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: ""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.SPECIFIC,
            value: "SPECIFIC",
            apiField: "month"
          },
        ],
        
        default: {
          source: "employee",
          dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.REGISTERED_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.ACTIVE_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.UNSUBSCRIBED_EMPLOYEES,
  
          ],
          puntualAggregationSelected:{
            label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
            value: 'NONE',
            // function:''
          },
          dataLevel: {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
            value: "company",
            // api: "getCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          timeUnit:  {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month"
          },
          timePeriod: {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: ""
          },
      }
    },
    ],
  }  
];
