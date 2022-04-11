import { VARIABLES } from "../../variables";

export const statsConfigurations = [
  /*
  / Configurazione - Km Fatti e Utili
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
            exportCSV: false,
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANIES,
            value: "companies",
            api: "getCampaignCompanyStats",
            exportCSV: true,
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
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_KM,
            value: "Km utili",
            apiField: "distances",
            apiFunction:""
          },
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED, 
            value:  "Km percorsi",
            apiField: "distancesNolimits",
            apiFunction:""
          },
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.N_COUNTING_TRAVEL, 
            value: "Viaggi utili",
            apiField: "trackCount",
            apiFunction:""
          },
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
             value: "Viaggi totali",
            apiField: "trackCountNolimits",
            apiFunction:""
          },
        ],
        timeUnit: [
          // VARIABLES.STATS.VIEWS.TIMEUNIT.DAY,
          // VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month",
            apiFunction:""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
            value: "campaign",
            apiField: "month",
            apiFunction:"aggregate"
          }
        ],
        timePeriod: [
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: "",
            apiFunction:""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.SPECIFIC,
            value: "SPECIFIC",
            apiField: "month",
            apiFunction:""
          },
        ],
        
        default: {
          dataColumns: [
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
              value: "Km utili",
              apiField: "distances",
              apiFunction:""
            },
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_KM, 
              value:  "Km percorsi",
              apiField: "distancesNolimits",
              apiFunction:""
            },
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.N_COUNTING_TRAVEL, 
              value: "Viaggi utili",
              apiField: "trackCount",
              apiFunction:""
            },
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
               value: "Viaggi totali",
              apiField: "trackCountNolimits",
              apiFunction:""
            },
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
            exportCSV: true,
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
            apiField: "month",
            apiFunction:""
          },
          timePeriod: {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: "",
            apiFunction:""
          },
        
        exportCSV: true,
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
            exportCSV: false,
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
            value: "locations",
            api: "getLocationsStats",
            exportCSV: true,
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              function:''
            }]
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.EMPLOYEES,
            value: "employees",
            api: "getEmployeesStats",
            exportCSV: true,
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
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_KM,
            value: "Km utili",
            apiField: "distances",
            apiFunction:""
          },
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED, 
            value:  "Km percorsi",
            apiField: "distancesNolimits",
            apiFunction:""
          },
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.N_COUNTING_TRAVEL, 
            value: "Viaggi utili",
            apiField: "trackCount",
            apiFunction:""
          },
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
             value: "Viaggi totali",
            apiField: "trackCountNolimits",
            apiFunction:""
          },
        ],
        timeUnit: [
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month",
            apiFunction:""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEUNIT.DAY,
            value: "date",
            apiField: "day",
            apiFunction:""
          }
        ],
        timePeriod: [
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: "",
            apiFunction:""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.SPECIFIC,
            value: "SPECIFIC",
            apiField: "month",
            apiFunction:""
          },
        ],
        
        default: {
          dataColumns: [
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_KM,
              value: "Km utili",
              apiField: "distances",
              apiFunction:""
            },
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED, 
              value:  "Km percorsi",
              apiField: "distancesNolimits",
              apiFunction:""
            }
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
            exportCSV:true,
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
            apiField: "month",
            apiFunction:""
          },
          timePeriod: {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: "",
            apiFunction:""
          },
      }
    },

    ],
  },
  /*
  / Configurazione - Km Fatti e Utili
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
            exportCSV: false,
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
            value: "locations",
            api: "getLocationsStats",
            exportCSV: true,
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              function:''
            }]
          }
        ],
        dataColumns: [
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
             value: "Viaggi totali",
            apiField: "trackCountNolimits",
            apiFunction:""
          },
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED, 
            value:  "Km percorsi",
            apiField: "distancesNolimits",
            apiFunction:""
          },
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.CO2_SAVED,
             value: "CO2",
            apiField: "co2saved",
            apiFunction:""
          }
        ],
        timeUnit: [
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month",
            apiFunction:""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
            value: "campaign",
            apiField: "month",
            apiFunction:"aggregate"
          }

        ],
        timePeriod: [
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: "",
            apiFunction:""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.SPECIFIC,
            value: "SPECIFIC",
            apiField: "month",
            apiFunction:""
          },
        ],
        
        default: {
          dataColumns: [
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
               value: "Viaggi totali",
              apiField: "trackCountNolimits",
              apiFunction:""
            },
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED, 
              value:  "Km percorsi",
              apiField: "distancesNolimits",
              apiFunction:""
            },
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.CO2_SAVED,
               value: "CO2",
              apiField: "co2saved",
              apiFunction:""
            }
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
            exportCSV: false,
            puntualAggregation: [{
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value:  'NONE'
            }],
          },
          timeUnit:  {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiField: "month",
            apiFunction:""
          },
          timePeriod: {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiField: "",
            apiFunction:""
          },
      }
    },

    ],
  }
];
