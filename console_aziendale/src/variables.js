//global variables for the application

export const VARIABLES = {
  ROLE: {
    ADMIN: "ROLE_ADMIN",
    COMPANY_ADMIN: "ROLE_COMPANY_ADMIN",
    MOBILITY_MANAGER: "ROLE_MOBILITY_MANAGER",
    APP_USER: "ROLE_APP_USER",
  },

  STATS: {
    NAME: {
      EMPLOYEES_PARTECIPATION: "Partecipazione Dipendenti",
      ENVIRONMENTAL_IMPACT: "Impatto Ambientale",
      KM_COUNTED: "Km fatti e utili",
    },
    VIEWS: {
      TYPE: {
        TABLE: "Tabella",
        HORIZONTAL_STACKED_BARS: "Grafico a Barre Orizzontali",
        LINE_CHART: "Grafico a Linee",
        BAR_CHART: "Grafico a Barre",
      },
      DATACOLUMNS: {
        REGISTERED_EMPLOYEES: "dipendenti registrati",
        SUBSCRIBED_EMPLOYEES: "dipendenti iscritti",
        ACTIVE_EMPLOYEES: "dipendenti attivi",
        ACTIVE_SUBSCRIBED_EMPLOYEES: "% dipendenti iscritti attivi",
        INACTIVE_SUBSCRIBED_EMPLOYEES: "% dipendenti iscritti inattivi",
        NOT_SUBSCRIBED_EMPLOYEES: "% dipendenti non iscritti",
        PERCENTAGE_EMPLOYEES: "% dipendenti",

        TOTAL_TRAVEL: "Totale viaggi",
        KM_TRAVELED: "Km percorsi",
        COUNTING_KM: "Km utili",
        DISCARDED_KM: "Km scartati",
        N_TRAVEL: "Numero viaggi",
        N_COUNTING_TRAVEL: "Numero viaggi utili",
        CO2_SAVED: "CO2 salvata",
      },
      DATALEVEL: {
        LOCATIONS: "Sedi",
        COMPANY: "Azienda",
        EMPLOYEES: "Dipendenti",
        CAMPAIGN: "Campagna",
        COMPANIES: "Aziende",
      },
      TIMEUNIT: {
        DAY: {label:"Giorno",value:"day"},
        WEEK: {label:"Settimana",value:"week"},
        MONTH: {label:"Mese",value:"month"},
        CAMPAIGN: {label:"Campagna",value:"all"},
      },
      TIMEPERIOD: {
        ALL: { label: "Intera durata campagna", value: "ALL" },
        SPECIFIC: { label: "Periodo specifico", value: "SPECIFIC" }
      },
      PUNTUALAGGREGATION: {
        NONE: { label: "Nessuna", value: "NONE" },
        EMPLOYEES: { label: "Dipendente", value: "EMPLOYEES" },
        LOCATIONS:{ label: "Sedi", value: "LOCATIONS" },
        COMPANIES: { label: "Aziende", value: "COMPANIES" }
    },
  },
  },
};
