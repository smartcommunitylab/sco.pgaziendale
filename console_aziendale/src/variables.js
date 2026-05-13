//global variables for the application

export const VARIABLES = {
  ROLE: {
    ADMIN: "ROLE_ADMIN",
    MOBILITY_MANAGER: "ROLE_MOBILITY_MANAGER",
    APP_USER: "ROLE_APP_USER",
  },

  STATS: {
    NAME: {
      EMPLOYEES_PARTECIPATION: "Partecipazione dipendenti",
      ENVIRONMENTAL_IMPACT: "Impatto ambientale",
      POINTS_COUNTED: "Punteggio campagna",
      PERFORMANCE_KM:"Performance KM totali",
      PERFORMANCE_HOURS:"Performance ore totali",
      TRIPS: "Viaggi",
      MULTI: "Multimodalità",
    },
    VIEWS: {
      DATACOLUMNS: {
        REGISTERED_EMPLOYEES: { 
          label: "Dipendenti registrati", 
          value: "registration", 
          source: 'employee' 
        },
        ACTIVE_EMPLOYEES: { 
          label: "Dipendenti attivi", 
          value: "activeUsers", 
          source: 'employee' 
        },
        UNSUBSCRIBED_EMPLOYEES: { 
          label: "Dipendenti rimossi", 
          value: "dropout", 
          source: 'employee' 
        },
        REGISTERED_EMPLOYEES_PERCENTAGE: {
          label: "Percentuale dipendenti registrati",
          value: "registration__prcTot",
          baseValue: "registration",
          source: "employee"
        },
        ACTIVE_EMPLOYEES_PERCENTAGE: {
          label: "Percentuale dipendenti attivi",
          value: "activeUsers__prcTot",
          baseValue: "activeUsers",
          source: "employee"
        },
        UNSUBSCRIBED_EMPLOYEES_PERCENTAGE: {
          label: "Percentuale dipendenti rimossi",
          value: "dropout__prcTot",
          baseValue: "dropout",
          source: "employee"
        },
        ACTIVE_EMPLOYEES_PERCENTAGE_REGISTRATION: {
          label: "Percentuale dipendenti attivi su registrati",
          value: "activeUsers__prcRegistered",
          baseValue: "activeUsers",
          source: "employee"
        },
         UNSUBSCRIBED_EMPLOYEES_PERCENTAGE_REGISTRATION: {
          label: "Percentuale dipendenti rimossi su registrati",
          value: "dropout__prcRegistered",
          baseValue: "dropout",
          source: "employee"
        },
        POINTS: {
          label: "Punti potenziali",
          meanLabel: "Potenziali per mezzo (_mezzo_)",
          value: "score",
          source: "tracks"
        },
        COUNTING_POINTS: {
          label:  "Punti utili", 
          meanLabel: "Utili per mezzo (_mezzo_)",
          value:  "limitedScore",
          source: "tracks"
        },
        N_COUNTING_TRAVEL: {
          label:  "Viaggi utili", 
          value: "limitedTripCount",
          source: "tracks"
        },
        N_TRAVEL: {
          label: "Viaggi totali",
          meanLabel: "Tratte per mezzo (_mezzo_)",
          value: "tripCount",
          source: "tracks"
        },
        N_TRAVEL_SINGLE: {
          label: "Viaggi singoli",
          value: "singleCount",
          baseValue: "tripCount",
          source: "tracks"
        },
        N_TRAVEL_MULTI: {
          label: "Viaggi multimodali",
          value: "multimodalCount",
          baseValue: "tripCount",
          source: "tracks"
        },
        DISTANCE: {
          label: "Distanca totale (km)",
          meanLabel: "KM per mezzo (_mezzo_)",
          value: "distance",
          source: "tracks"
        },
        DISTANCE_AVG_LEG: {
          label: "Distanca media per tratta (km)",
          meanLabel: "KM per mezzo (_mezzo_) per tratta",
          value: "distance__avgTrack",
          baseValue: "distance",
          source: "tracks"
        },
        DISTANCE_AVG_TRIP: {
          label: "Distanca media per viaggio (km)",
          meanLabel: "KM per mezzo (_mezzo_) per viaggio",
          value: "distance__avgTrip",
          baseValue: "distance",
          source: "tracks"
        },
        DISTANCE_PERCENTAGE: {
          label: "Percentuale distanza tracciata",
          meanLabel: "Percentuale distanza tracciata per mezzo (_mezzo_)",
          value: "distance__prcValue",
          baseValue: "distance",
          source: "tracks"
        },
        DISTANCE_PERCENTAGE_AVG_TRIP: {
          label: "Percentuale distanza media per viaggio",
          meanLabel: "Percentuale distanza media per viaggio per mezzo (_mezzo_)",
          value: "distance__prcAvgTrip",
          baseValue: "distance",
          source: "tracks"
        },
        DISTANCE_PERCENTAGE_AVG_LEG: {
          label: "Percentuale distanza media per tratta",
          meanLabel: "Percentuale distanza media per tratta per mezzo (_mezzo_)",
          value: "distance__prcAvgTrack",
          baseValue: "distance",
          source: "tracks"
        },
        DURATION: {
          label: "Durata totale (ore)",
          meanLabel: "Ore per mezzo (_mezzo_)",
          value: "duration",
          source: "tracks"
        },
        DURATION_AVG_LEG: {
          label: "Durata media per tratta (ore)",
          meanLabel: "Ore per mezzo (_mezzo_) per tratta",
          value: "duration__avgTrack",
          baseValue: "duration",
          source: "tracks"
        },
        DURATION_AVG_TRIP: {
          label: "Durata media per viaggio (ore)",
          meanLabel: "Ore per mezzo (_mezzo_) per viaggio",
          value: "duration__avgTrip",
          baseValue: "duration",
          source: "tracks"
        },
        DURATION_PERCENTAGE: {
          label: "Percentuale durata tracciata",
          meanLabel: "Percentuale durata tracciata per mezzo (_mezzo_)",
          value: "duration__prcValue",
          baseValue: "duration",
          source: "tracks"
        },
        DURATION_PERCENTAGE_AVG_TRIP: {
          label: "Percentuale durata media per viaggio",
          meanLabel: "Percentuale durata media per viaggio per mezzo (_mezzo_)",
          value: "duration__prcAvgTrip",
          baseValue: "duration",
          source: "tracks"
        },
        DURATION_PERCENTAGE_AVG_LEG: {
          label: "Percentuale durata media per tratta",
          meanLabel: "Percentuale durata media per tratta per mezzo (_mezzo_)",
          value: "duration__prcAvgTrack",
          baseValue: "duration",
          source: "tracks"
        },
        CO2_SAVED: {
          label: "kg CO2 risparmiata",
          meanLabel: "CO2 per mezzo (_mezzo_)",
          value: "co2",
          source: "tracks"
        },
        CO2_SAVED_AVG_LEG: {
          label: "kg CO2 media per tratta",
          meanLabel: "CO2 per mezzo (_mezzo_) per tratta",
          value: "co2__avgTrack",
          baseValue: "co2",
          source: "tracks"
        },
        CO2_SAVED_AVG_TRIP: {
          label: "kg CO2 media per viaggio",
          meanLabel: "CO2 per mezzo (_mezzo_) per viaggio",
          value: "co2__avgTrip",
          baseValue: "co2",
          source: "tracks"
        },
        CO2_SAVED_PERCENTAGE: {
          label: "Percentuale CO2 risparmiata",
          meanLabel: "Percentuale CO2 risparmiata per mezzo (_mezzo_)",
          value: "co2__prcValue",
          baseValue: "co2",
          source: "tracks"
        },
        CO2_SAVED_PERCENTAGE_AVG_TRIP: {
          label: "Percentuale CO2 media per viaggio",
          meanLabel: "Percentuale CO2 media per viaggio per mezzo (_mezzo_)",
          value: "co2__prcAvgTrip",
          baseValue: "co2",
          source: "tracks"
        },
        CO2_SAVED_PERCENTAGE_AVG_LEG: {
          label: "Percentuale CO2 media per tratta",
          meanLabel: "Percentuale CO2 media per tratta per mezzo (_mezzo_)",
          value: "co2__prcAvgTrack",
          baseValue: "co2",
          source: "tracks"
        }, 
        MM_TRAVEL: {
          label: "Viaggi",
          value: "count",
          source: "multimodal"
        },
        MM_DURATION: {
          label: "Durata (ore)",
          value: "duration",
          source: "multimodal"
        },
        MM_DISTANCE: {
          label: "Distanza (km)",
          value: "distance",
          source: "multimodal"
        },
        MM_DURATION_AVG: {
          label: "Durata media (ore)",
          value: "duration_avg",
          source: "multimodal"
        },
        MM_DISTANCE_AVG: {
          label: "Distanza media (km)",
          value: "distance_avg",
          source: "multimodal"
        },
        MM_COUNT_PRC: {
          label: "Percentuale viaggi",
          value: "prcCount",
          source: "multimodal"
        },
        MM_DURATION_PRC: {
          label: "Percentuale durata",
          value: "prcDuration",
          source: "multimodal"
        },
        MM_DISTANCE_PRC: {
          label: "Percentuale distanza",
          value: "prcDistance",
          source: "multimodal"
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
