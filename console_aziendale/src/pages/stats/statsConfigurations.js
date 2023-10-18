import { VARIABLES } from "../../variables";

export const statsConfigurations = [
  /*
  / Configurazione - Punti Fatti e Utili
  / Specifica per l'ADMIN -> Amministratore di Sistema
  */
  {
    id: 0,
    name: VARIABLES.STATS.NAME.KM_COUNTED,
    profile: VARIABLES.ROLE.ADMIN,
    views: [
      {
        type: VARIABLES.STATS.VIEWS.TYPE.TABLE,
        dataLevel: [
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.CAMPAIGN,
            value: "campaign",
            api: "getCampaignStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANIES,
            value: "companies",
            api: "getCampaignCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              function:''
            },{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.COMPANIES,
              value: 'COMPANIES',
              function:'aggregateBycompany'
            }]
          },
        ],
        dataColumns: [ 
          VARIABLES.STATS.VIEWS.DATACOLUMNS.POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_COUNTING_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_COUNTING_POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_DISTANCE,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_DURATION,

        ],
        timeUnit: [
          // VARIABLES.STATS.VIEWS.TIMEUNIT.DAY,
          // VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
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
        
        default: {
          dataColumns: [
            VARIABLES.STATS.VIEWS.DATACOLUMNS.POINTS,
            VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_POINTS,
            VARIABLES.STATS.VIEWS.DATACOLUMNS.N_COUNTING_TRAVEL,
            VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL
          ],
          puntualAggregationSelected:{
            label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
            value: 'NONE',
            function:''
          },
          dataLevel: {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANIES,
            value: "companies",
            api: "getCampaignCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              function:''
            },{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.COMPANIES,
              value: 'COMPANIES',
              function:'aggregateBycompany'
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
    profile: VARIABLES.ROLE.COMPANY_ADMIN,
    views: [
      {
        type: VARIABLES.STATS.VIEWS.TYPE.TABLE,
        dataLevel: [
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
            value: "company",
            api: "getCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
            value: "locations",
            api: "getLocationsStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              function:''
            }
            // ,{
            //   label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.LOCATIONS,
            //   value: 'LOCATIONS',
            //   function:''
            // }
            ]
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.EMPLOYEES,
            value: "employees",
            api: "getEmployeesStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              function:''
            },{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.EMPLOYEES,
              value: 'EMPLOYEES',
              function:'aggregateByemployee'
            }]
          }
        ],
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_COUNTING_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_COUNTING_POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_DISTANCE,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_DURATION,
        ],
        timeUnit: [
          {
            label:VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
            value: "campaign",
            apiField: "total"
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
          dataColumns: [
            VARIABLES.STATS.VIEWS.DATACOLUMNS.POINTS,
            VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_POINTS,
            ],
          puntualAggregationSelected:{
            label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
            value: 'NONE',
            function:''
          },
          dataLevel: {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.EMPLOYEES,
            value: "employees",
            api: "getEmployeesStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              function:''
            },{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.EMPLOYEES,
              value: 'EMPLOYEES',
              function:'aggregateByemployee'
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
    profile: VARIABLES.ROLE.COMPANY_ADMIN,
    views: [
      {
        type: VARIABLES.STATS.VIEWS.TYPE.TABLE,
        dataLevel: [
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
            value: "company",
            api: "getCompanyStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
            value: "locations",
            api: "getLocationsStats",
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              function:''
            }]
          }
        ],
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.CO2_SAVED,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_POINTS,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.MEAN_CO2_SAVED,
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
          dataColumns: [
            VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
            VARIABLES.STATS.VIEWS.DATACOLUMNS.POINTS,
            VARIABLES.STATS.VIEWS.DATACOLUMNS.CO2_SAVED
  
          ],
          puntualAggregationSelected:{
            label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
            value: 'NONE',
            function:''
          },
          dataLevel: {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
            value: "company",
            api: "getCompanyStats",
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
