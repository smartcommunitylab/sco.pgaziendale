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
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
            value: "Km percorsi",
            apiOption: "noLimits=true",
            apiField: "distances",
            apiFunction:""
          },
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_KM, 
            value:  "Km utili",
            apiOption: "noLimits=true",
            apiField: "distances",
            apiFunction:""
          },
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL, 
            value: "Viaggi totali",
            apiOption: "noLimits=true",
            apiField: "trackCount",
            apiFunction:""
          },
          {
            label: VARIABLES.STATS.VIEWS.DATACOLUMNS.N_COUNTING_TRAVEL,
             value: "Viaggi utili",
            apiOption: "noLimits=true",
            apiField: "trackCount",
            apiFunction:""
          },
        ],
        timeUnit: [
          // VARIABLES.STATS.VIEWS.TIMEUNIT.DAY,
          // VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiOption: "groupBy=month",
            apiField: "month",
            apiFunction:""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
            value: "date",
            apiOption: "groupBy=date",
            apiField: "date",
            apiFunction:""
          }
        ],
        timePeriod: [
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiOption: "",
            apiField: "",
            apiFunction:""
          },
          {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.SPECIFIC,
            value: "SPECIFIC",
            apiOptions: ["from=","to="],
            apiField: "month",
            apiFunction:""
          },
        ],
        
        default: {
          dataColumns: [
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
              value: "Km percorsi",
              apiOption: "noLimits=true",
              apiField: "distances",
              apiFunction:""
            },
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_KM, 
              value:  "Km utili",
              apiOption: "noLimits=true",
              apiField: "distances",
              apiFunction:""
            },
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL, 
              value: "Viaggi totali",
              apiOption: "noLimits=true",
              apiField: "trackCount",
              apiFunction:""
            },
            {
              label: VARIABLES.STATS.VIEWS.DATACOLUMNS.N_COUNTING_TRAVEL,
               value: "Viaggi utili",
              apiOption: "noLimits=true",
              apiField: "trackCount",
              apiFunction:""
            },
          ],
          dataLevel: {
            label: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANIES,
            value: "companies",
            api: "getCampaignCompanyStats",
            puntualAggregation: {
              label:  VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
              value: 'NONE',
              function:''
            }
          },
          timeUnit:  {
            label: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
            value: "month",
            apiOption: "groupBy=month",
            apiField: "month",
            apiFunction:""
          },
          timePeriod: {
            label:VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
            value: "ALL",
            apiOption: "",
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
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_KM,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_COUNTING_TRAVEL,
        ],
        dataLevel: [
          VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
          VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
          VARIABLES.STATS.VIEWS.DATALEVEL.EMPLOYEES,
        ],
        timeUnit: [
          VARIABLES.STATS.VIEWS.TIMEUNIT.DAY,
          // VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
          VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
        ],
        timePeriod: [
          VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
          VARIABLES.STATS.VIEWS.TIMEPERIOD.SPECIFIC
        ],
        puntualAggregation: [
          VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
          VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.EMPLOYEES
        ],
        default: {
          dataColumns: VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
          dataLevel: VARIABLES.STATS.VIEWS.DATALEVEL.CAMPAIGN,
          timeUnit: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
          timePeriod: VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
          puntualAggregation: VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
        },
        exportCSV: true,
      },
      {
        type: VARIABLES.STATS.VIEWS.TYPE.BAR_CHART,
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_KM,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.DISCARDED_KM,
        ],
        dataLevel: [VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY],
        timeUnit: [
          VARIABLES.STATS.VIEWS.TIMEUNIT.DAY,
          // VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
          VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
        ],
        timePeriod: [
          VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
          VARIABLES.STATS.VIEWS.TIMEPERIOD.SPECIFIC
        ],
        puntualAggregation: [
          VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
          VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.EMPLOYEES
        ],
        default: {
          dataColumns: VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
          dataLevel: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
          timeUnit: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
          timePeriod: VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
          puntualAggregation: VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
        },
        exportCSV: false,
      },
      {
        type: VARIABLES.STATS.VIEWS.TYPE.LINE_CHART,
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_KM,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_COUNTING_TRAVEL,
        ],
        dataLevel: [VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY],
        timeUnit: [
          //VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.DAY,
        ],
        timePeriod: [
          VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
          VARIABLES.STATS.VIEWS.TIMEPERIOD.SPECIFIC
        ],
        puntualAggregation: [
          VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
          VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.EMPLOYEES
        ],
        default: {
          dataColumns: VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
          dataLevel: VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
          timeUnit: VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
          timePeriod: VARIABLES.STATS.VIEWS.TIMEPERIOD.ALL,
          puntualAggregation: VARIABLES.STATS.VIEWS.PUNTUALAGGREGATION.NONE,
        },
        exportCSV: false,
      },
    ],
  },



  /*
  / Configurazione - Partecipazione Dipendenti
  */
  {
    id: 2,
    name: VARIABLES.STATS.NAME.EMPLOYEES_PARTECIPATION,
    profile: VARIABLES.ROLE.COMPANY_ADMIN,
    views: [
      {
        type: VARIABLES.STATS.VIEWS.TYPE.TABLE,
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.REGISTER_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.SUBSCRIBED_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.ACTIVE_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.ACTIVE_SUBSCRIBED_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.INACTIVE_SUBSCRIBED_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.PERCENTAGE_EMPLOYEES,
        ],
        dataLevel: [
          VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
          VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
        ],
        timeUnit: [
          //VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
          VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
        ],
        selectAggregazionePuntuale: "nessuna",
        selectRangeTime: true,
        exportCSV: true,
      },
      {
        type: VARIABLES.STATS.VIEWS.TYPE.HORIZONTAL_STACKED_BARS,
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.ACTIVE_SUBSCRIBED_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.INACTIVE_SUBSCRIBED_EMPLOYEES,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.NOT_SUBSCRIBED_EMPLOYEES,
        ],
        dataLevel: [
          VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
          VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
        ],
        timeUnit: [VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN],
        selectAggregazionePuntuale: "si per sedi",
        selectRangeTime: true,
        exportCSV: false,
      },
    ],
  },

  /*
  / Configurazione - Impatto Ambientale
  */
  {
    id: 3,
    name: VARIABLES.STATS.NAME.ENVIRONMENTAL_IMPACT,
    profile: VARIABLES.ROLE.COMPANY_ADMIN,
    views: [
      {
        type: VARIABLES.STATS.VIEWS.TYPE.TABLE,
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.TOTAL_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.CO2_SAVED,
        ],
        dataLevel: [
          VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
          VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
        ],
        timeUnit: [
          //VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
          VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
        ],
        selectAggregazionePuntuale: "nessuna",
        selectRangeTime: true,
        exportCSV: true,
      },
      {
        type: VARIABLES.STATS.VIEWS.TYPE.LINE_CHART,
        dataColumns: [VARIABLES.STATS.VIEWS.DATACOLUMNS.CO2_SAVED],
        dataLevel: [
          VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
          VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
        ],
        timeUnit: [
          //VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.DAY,
        ],
        selectAggregazionePuntuale: "nessuna",
        selectRangeTime: true,
        exportCSV: true,
      },
    ],
  },
];
