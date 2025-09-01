//global variables for the application

export const VARIABLES = {
  ROLE: {
    ADMIN: "ROLE_ADMIN",
    MOBILITY_MANAGER: "ROLE_MOBILITY_MANAGER",
    APP_USER: "ROLE_APP_USER",
  },

  STATS: {
    NAME: {
      EMPLOYEES_PARTECIPATION: "Partecipazione Dipendenti",
      ENVIRONMENTAL_IMPACT: "Impatto Ambientale",
      KM_COUNTED: "Punteggio Campagna",
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
        CO2_SAVED: {
          label: "kg CO2 risparmiata",
          meanLabel: "CO2 per mezzo (_mezzo_)",
          value: "co2",
          apiField: "co2"
        }, 
        POINTS: {
          label: "Punti",
          meanLabel: "Punti per mezzo (_mezzo_)",
          value: "score",
          apiField: "score"
        },
        // COUNTING_POINTS: {
        //   label:  "Punti utili", 
        //   value:  "limitedScore",
        //   apiField: "limitedScore"
        // },
        // N_COUNTING_TRAVEL: {
        //   label:  "Viaggi utili", 
        //   value: "limitedTrackCount",
        //   apiField: "limitedTrackCount"
        // },
        N_TRAVEL: {
          label: "Viaggi totali",
          meanLabel: "Tratte per mezzo (_mezzo_)",
          value: "track",
          apiField: "track"
        },
        DISTANCE: {
          label: "Distanca totale (km)",
          meanLabel: "KM per mezzo (_mezzo_)",
          value: "distance",
          apiField: "distance"
        },
        DURATION: {
          label: "Durata totale (ore)",
          meanLabel: "Ore per mezzo (_mezzo_)",
          value: "duration",
          apiField: "duration"
        },
      },
      DATALEVEL: {
        LOCATIONS:  "Sedi",
        COMPANY:  "Azienda", 
        EMPLOYEES: "Dipendenti",
        CAMPAIGN: "Campagna",
        COMPANIES: "Aziende",
      },
      TIMEUNIT: {
        HOUR: "Fascia oraria",
        DOW: "Giorno della settimana",
        DAY: "Giorno", 
        WEEK: "Settimana",
        MONTH: "Mese", 
        YEAR: "Anno",
        CAMPAIGN:  "Campagna", 
      },
      TIMEPERIOD: {
        ALL:  "Intera durata campagna", 
        SPECIFIC: "Imposta periodo" 
      },
      PUNTUALAGGREGATION: {
        NONE:  "Nessuno", 
        EMPLOYEES:  "Selezione dipendente/i", 
        LOCATIONS: "Sedi", 
        COMPANIES: "Aziende",
      },
    },
  },
};
