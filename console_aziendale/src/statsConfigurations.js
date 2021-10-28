import VARIABLES from "./variables";

export const statsConfigurations = [
  /*
  / Configurazione - Km Fatti e Utili
  / Specifica per il COMPANY ADMIN -> Amministratore Aziendale
  */
  {
    id: 0,
    name: VARIABLES.STATS.NAME.KM_COUNTED,
    views: [
      {
        profile: VARIABLES.ROLE.COMPANY_ADMIN,
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
          VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
          VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
        ],
        selectAggregazionePuntuale:
          "si per il dipendente (nome dipendente); no per sedi; no per azienda",
        selectRangeTime: true,
        exportCSV: true,
      },
      {
        profile: VARIABLES.ROLE.COMPANY_ADMIN,
        type: VARIABLES.STATS.VIEWS.TYPE.BAR_CHART,
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_KM,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.DISCARDED_KM,
        ],
        dataLevel: [VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY],
        timeUnit: [
          VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
          VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
        ],
        selectAggregazionePuntuale: "nessuna",
        selectRangeTime: false,
        exportCSV: false,
      },
      {
        profile: VARIABLES.ROLE.COMPANY_ADMIN,
        type: VARIABLES.STATS.VIEWS.TYPE.LINE_CHART,
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_KM,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_COUNTING_TRAVEL,
        ],
        dataLevel: [VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY],
        timeUnit: [
          VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.DAY,
        ],
        selectAggregazionePuntuale: "nessuna",
        selectRangeTime: true,
        exportCSV: false,
      },
      {
        profile: VARIABLES.ROLE.ADMIN,
        type: VARIABLES.STATS.VIEWS.TYPE.TABLE,
        dataColumns: [
          VARIABLES.STATS.VIEWS.DATACOLUMNS.KM_TRAVELED,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.COUNTING_KM,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_TRAVEL,
          VARIABLES.STATS.VIEWS.DATACOLUMNS.N_COUNTING_TRAVEL,
        ],
        dataLevel: [
          VARIABLES.STATS.VIEWS.DATALEVEL.CAMPAIGN,
          VARIABLES.STATS.VIEWS.DATALEVEL.COMPANIES,
        ],
        timeUnit: [
          VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
          VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
        ],
        selectAggregazionePuntuale:
          "si per azienda (nome azienda); no per campagna",
        selectRangeTime: true,
        exportCSV: true,
      },
    ],
  },

  /*
  / Configurazione - Partecipazione Dipendenti
  */
  {
    id: 1,
    name: VARIABLES.STATS.NAME.EMPLOYEES_PARTECIPATION,
    views: [
      {
        profile: VARIABLES.ROLE.COMPANY_ADMIN,
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
          VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
          VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
        ],
        selectAggregazionePuntuale: "nessuna",
        selectRangeTime: true,
        exportCSV: true,
      },
      {
        profile: VARIABLES.ROLE.COMPANY_ADMIN,
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
    id: 2,
    name: VARIABLES.STATS.NAME.ENVIRONMENTAL_IMPACT,
    views: [
      {
        profile: VARIABLES.ROLE.COMPANY_ADMIN,
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
          VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.MONTH,
          VARIABLES.STATS.VIEWS.TIMEUNIT.CAMPAIGN,
        ],
        selectAggregazionePuntuale: "nessuna",
        selectRangeTime: true,
        exportCSV: true,
      },
      {
        profile: VARIABLES.ROLE.COMPANY_ADMIN,
        type: VARIABLES.STATS.VIEWS.TYPE.LINE_CHART,
        dataColumns: [VARIABLES.STATS.VIEWS.DATACOLUMNS.CO2_SAVED],
        dataLevel: [
          VARIABLES.STATS.VIEWS.DATALEVEL.LOCATIONS,
          VARIABLES.STATS.VIEWS.DATALEVEL.COMPANY,
        ],
        timeUnit: [
          VARIABLES.STATS.VIEWS.TIMEUNIT.WEEK,
          VARIABLES.STATS.VIEWS.TIMEUNIT.DAY,
        ],
        selectAggregazionePuntuale: "nessuna",
        selectRangeTime: true,
        exportCSV: true,
      },
    ],
  },
];
