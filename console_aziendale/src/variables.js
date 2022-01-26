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
        REGISTERED_EMPLOYEES: { label: "dipendenti registrati", value: "dipendenti registrati" },
        SUBSCRIBED_EMPLOYEES: { label: "dipendenti iscritti", value: "dipendenti iscritti" },
        ACTIVE_EMPLOYEES: { label: "dipendenti attivi", value: "dipendenti attivi" },
        ACTIVE_SUBSCRIBED_EMPLOYEES: { label: "% dipendenti iscritti attivi", value: "% dipendenti iscritti attivi" },
        INACTIVE_SUBSCRIBED_EMPLOYEES: { label: "% dipendenti iscritti inattivi", value: "% dipendenti iscritti inattivi" },
        NOT_SUBSCRIBED_EMPLOYEES: { label: "% dipendenti non iscritti", value: "% dipendenti non iscritti" },
        PERCENTAGE_EMPLOYEES: { label: "% dipendenti", value: "% dipendenti" },
        TOTAL_TRAVEL: { label: "Totale viaggi", value: "totalTrackCount" },
        KM_TRAVELED: "Km percorsi",
        COUNTING_KM: "Km utili",
        DISCARDED_KM:  "Km scartati",
        N_TRAVEL:  "Numero viaggi",
        N_COUNTING_TRAVEL:  "Numero viaggi utili", 
        CO2_SAVED: "CO2 salvata", 
      },
      DATALEVEL: {
        LOCATIONS:  "Sedi",
        COMPANY:  "Azienda", 
        EMPLOYEES: "Dipendenti",
        CAMPAIGN: "Campagna",
        COMPANIES: "Aziende",
      },
      TIMEUNIT: {
        DAY: "Giorno", 
        // WEEK: { label: "Settimana", value: "week" },
        MONTH: "Mese", 
        CAMPAIGN:  "Campagna", 
      },
      TIMEPERIOD: {
        ALL:  "Intera durata campagna", 
        SPECIFIC: "Periodo specifico" 
      },
      PUNTUALAGGREGATION: {
        NONE:  "Nessuna", 
        EMPLOYEES:  "Dipendente", 
        LOCATIONS: "Sedi", 
        COMPANIES: "Aziende"
      },
    },
  },
};
