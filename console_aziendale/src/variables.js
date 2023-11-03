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
          value: "co2saved",
          apiField: "co2saved"
        }, 
        POINTS: {
          label: "Punti totali",
          value: "score",
          apiField: "score"
        },
        COUNTING_POINTS: {
          label:  "Punti utili", 
          value:  "limitedScore",
          apiField: "limitedScore"
        },
        N_COUNTING_TRAVEL: {
          label:  "Viaggi utili", 
          value: "limitedTrackCount",
          apiField: "limitedTrackCount"
        },
        N_TRAVEL: {
          label: "Viaggi totali",
          value: "trackCount",
          apiField: "trackCount"
        },
        MEAN_POINTS: {
          label: "Punti totali per mezzo",
          value: "meanScore",
          apiField: "meanScore",
          mean: true
        },
        MEAN_COUNTING_POINTS: {
          label: "Punti utili per mezzo",
          value: "limitedMeanScore",
          apiField: "limitedMeanScore",
          mean: true
        },
        MEAN_DISTANCE: {
          label: "Km per mezzo",
          value: "meanDistance",
          apiField: "meanDistance",
          mean: true
        },
        MEAN_DURATION: {
          label: "Ore per mezzo",
          value: "meanDuration",
          apiField: "meanDuration",
          mean: true
        },
        MEAN_CO2_SAVED: {
          label: "CO2 salvata per mezzo",
          value: "meanCo2",
          apiField: "meanCo2",
          mean: true
        },
        MEAN_N_TRAVEL: {
          label: "Tratte per mezzo",
          value: "meanTracks",
          apiField: "meanTracks",
          mean: true
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
        COMPANIES: "Aziende"
      },
    },
  },
};
