import Vue from 'vue';
import VueToast from 'vue-toast-notification';
import 'vue-toast-notification/dist/theme-sugar.css';
Vue.use(VueToast);
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
	'INVALID_IMPORT_DATA':'Attenzione, formato dati per l\'import non validi!',
  'BAD_CREDENTIALS'	:'Attenzione, le credenziali per accedere non sono validi!',
	'INSUFFICIENT_RIGHTS':'Attenzione, permessi non validi per eseguire l\'operazione!',
	'GENERIC_ERROR'	:'Attenzione, errori di sistema, riprovare piú tardi!',
	'USER_ANOTHER_COMPANY'	:'Attenzione, l\'utente specificato é giá iscritto per un\'altra azienda!',
	'REPEATING_SUBSCRIPTION'	:'Attenzione, utente giá presente!'
}
	

const toast = {
    error: (type) => {
        return Vue.$toast.open({
          message: HttpErrors[type],
          type: 'error',
          duration:3000,
          position:'bottom'
      });
    },
    success: (message) => {
      return Vue.$toast.open({
        message: message,
        type: 'success',
        duration:3000,
        position:'bottom'
    });
  }
};

export default toast;