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
      DATACOLUMNS: {
        REGISTERED_EMPLOYEES: { label: "Dipendenti registrati", value: "registration", source: 'employee' },
        ACTIVE_EMPLOYEES: { label: "Dipendenti attivi", value: "activeUsers", source: 'employee' },
        UNSUBSCRIBED_EMPLOYEES: { label: "Dipendenti rimossi", value: "dropout", source: 'employee' },
        CO2_SAVED: {
          label: "kg CO2 risparmiata",
          meanLabel: "CO2 per mezzo (_mezzo_)",
          value: "co2",
          source: "tracks"
        }, 
        POINTS: {
          label: "Punti",
          meanLabel: "Punti per mezzo (_mezzo_)",
          value: "score",
          source: "tracks"
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
          source: "tracks"
        },
        DISTANCE: {
          label: "Distanca totale (km)",
          meanLabel: "KM per mezzo (_mezzo_)",
          value: "distance",
          source: "tracks"
        },
        DURATION: {
          label: "Durata totale (ore)",
          meanLabel: "Ore per mezzo (_mezzo_)",
          value: "duration",
          source: "tracks"
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
