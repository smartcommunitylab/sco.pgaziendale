<template>
  <div>
    <div class="flex flex-col lg:flex-row bg-blue-600">
      <div class=" pb-6 lg:w-7/12 lg:my-2 text-white text-center">
        <h1 class="text-6xl">Contattaci</h1>
        <h2 class="text-2xl pb-2">
          Saremo felici di rispondere alle tue domande.
        </h2>
        <p>
          Contattaci via e-mail all'indirizzo
          <a href="mailto: email@fbk.eu">email@fbk.eu</a>.
        </p>
        <p>
          Altrimenti compila il form di richiesta e ci mettere noi in contatto
          con te.
        </p>
      </div>
      <div class="lg:w-4/12 lg:my-2">
        <form
          action=""
          class="form flex flex-col bg-white p-6 relative lg:rounded-xl justify-center "
        >
          <div
            class="flex flex-col md:flex-row  mt-3 justify-stretch lg:flex-col"
          >
            <input
              type="text"
              name=""
              id=""
              required
              placeholder="Nome e Cognome * "
              class=" focus:border-blue-600  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0"
            />
            <input
              type="email"
              name=""
              id=""
              required
              placeholder="Email * "
              class="focus:border-blue-600  border-2  p-2 flex-1 md:ml-2 lg:ml-0"
            />
          </div>

          <textarea
            name=""
            id=""
            rows="3"
            required
            placeholder="Inserisci qua il tuo messaggio * "
            class="focus:border-blue-600  border-2  p-2 mt-3 w-full"
          ></textarea>
          <p class="font-bold text-sm mt-3">Privacy Policy *</p>
          <div class="flex items-baseline space-x-2 mt-2">
            <input
              type="checkbox"
              name=""
              id=""
              required
              class="inline-block"
            />
            <p class="text-gray-600 text-xs ">
              Ai sensi dell'art. 13 del Regolamento EU n. 2016/679 (GDPR), i
              dati personali forniti saranno trattati per poter dare riscontro
              alla sua richiesta tramite strumenti manuali, informatici e
              telematici, comunque ideonei a garantire la sicurezza e la
              riservatezza dei dati stessi. <br />
              L'informativa Privacy completa è disponibile al seguente link
              {LINK}. Dichiaro di aver letto e compreso l'informativa sul
              trattamento dei dati personali.
            </p>
          </div>
          <input
            type="submit"
            value="Invia Email"
            class=" mt-6 bg-blue-600 hover:bg-blue-500 text-white font-semibold p-3 md:self-end flex-1"
          />
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Contatti",
};
</script>

<style>
/* prova



.nav-link{
    @apply px-4 py-2 mt-2 text-2xl text-white bg-blue-600 transition ease-in-out duration-150
}

.nav-link:hover{
    @apply bg-blue-500
}


*/
</style>
