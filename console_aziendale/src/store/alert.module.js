const state = {
    type: null,
    message: null
};
const HttpErrors ={
        'INVALID_ROLES':'Attenzione, Ruoli non esistenti!',
        'INVALID_USER_DATA':'Attenzione, dati utenti non validi!',
        'NO_COMPANY'	:'Attenzione, l\'azienda specificata non esiste!',
        'INVALID_COMPANY_DATA':'Attenzione, dati aziendali non validi!',
        'NO_APP'	:'Attenzione, l\'applicazione selezionata non esiste!',
        'NO_CAMPAIGN':'Attenzione, la campagna di mobilitá specificata non esiste!',
        'INVALID_CAMPAIGN_DATA':'Attenzione, i dati inseriti per la campagna non sono validi!',
        'NO_START_DATE'	:'Attenzione, la data di inizio della campagna non é specificata!',
        'INVALID_APP_DATA':'Attenzione, dati dell\'app non validi!',
        'NO_USER'	:'Attenzione, utente non esistente!',
        'NO_CODE'	:'Attenzione, codice utente non esistente!',
        'CODE_IN_USE'	:'Attenzione, il codice utente inserito é giá stato utilizzato!',
        'NO_LOCATION'	:'Attenzione, la sede specificata non esiste!',
        'INVALID_CSV'	:'Attenzione, il formato del file CSV caricato non é valido!',
        'INVALID_GROUPING_DATA':'Attenzione, parametro non valido!',
        'EMPTY_ROLES'	:'Attenzione, nessun ruolo specificato per l\'utente!',
        'NO_EMPLOYEE'	:'Attenzione, il dipendente specificato non esiste!',
        'NO_SUBSCRIPTION'	:'Attenzione, nessuna registrazione attiva per il codice specificato!',
        'INVALID_PASSWORD'	:'Attenzione, il formato della password non é valido!',
        'LOGIN_IN_USE'	:'Attenzione, il nome utente utilizzato é giá in uso!',
        'BAD_REQUEST_DATA'	:'Attenzione, dati della richiesta non validi!',
        'INVALID_IMPORT_DATA':'Attenzione, formato dati per l\'import non valido!',
      'BAD_CREDENTIALS'	:'Attenzione, le credenziali per accedere non sono valide!',
        'INSUFFICIENT_RIGHTS':'Attenzione, permessi non validi per eseguire l\'operazione!',
        'GENERIC_ERROR'	:'Attenzione, errori di sistema, riprovare piú tardi!',
        'USER_ANOTHER_COMPANY'	:'Attenzione, l\'utente specificato é giá iscritto per un\'altra azienda!',
        'REPEATING_SUBSCRIPTION'	:'Attenzione, utente giá presente!'
    }


const actions = {
    success({ commit }, message) {
        commit('success', message);
    },
    error({ commit }, message) {
        commit('error', message);
    },
    clear({ commit }) {
        commit('clear');
    }
};

const mutations = {
    success(state, message) {
        state.type = 'alert-success';
        state.message = message;
    },
    error(state, message) {
        state.type = 'alert-danger';
        state.message = HttpErrors[message];
    },
    clear(state) {
        state.type = null;
        state.message = null;
    }
};

export const alert = {
    namespaced: true,
    state,
    actions,
    mutations
};