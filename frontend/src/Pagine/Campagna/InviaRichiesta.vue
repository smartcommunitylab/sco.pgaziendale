<template>
  <div class="flex flex-col lg:flex-row bg-primary send_request">
    <div
      class="pb-6 lg:w-7/12 lg:my-2 text-white text-center lg:text-left md:pl-6 lg:pb-0 lg:pl-12"
    >
      <h1 class="text-6xl font-bold">Invia una richiesta</h1>
      <h2 class="text-2xl pb-2 ">
        Ti ri-contatteremo non appena possibile
      </h2>
    </div>
    <div class="lg:w-4/12 lg:my-2">
      <form
        name="send_request"
        action=""
        class="form flex flex-col bg-white p-6 relative lg:rounded-xl justify-center "
      >
        <div
          class="flex flex-col md:flex-row  mt-3 justify-stretch lg:flex-col"
        >
          <select
            class="focus:border-primary  border-2  p-2  mb-2 md:mb-0 lg:mb-2 flex-1 md:mr-2 lg:mr-0 appearance-none"
            name="request_type"
            id="cars"
            form="send_request"
          >
            <option value="invalid_trip">Viaggio non valido</option>
            <option value="invalid_values">Valori non validi</option>
            <option value="other">Altro</option>
          </select>
        </div>

        <textarea
          name=""
          id=""
          rows="3"
          required
          placeholder="Inserisci qua il tuo messaggio * "
          class="focus:border-primary  border-2  p-2 mt-3 w-full"
        ></textarea>
        <p class="font-bold text-sm mt-3">Privacy Policy *</p>
        <div class="flex items-baseline space-x-2 mt-2">
          <input type="checkbox" name="" id="" required class="inline-block" />
          <p class="text-gray-600 text-xs ">
            Ai sensi dell'art. 13 del Regolamento EU n. 2016/679 (GDPR), i dati
            personali forniti saranno trattati per poter dare riscontro alla sua
            richiesta tramite strumenti manuali, informatici e telematici,
            comunque ideonei a garantire la sicurezza e la riservatezza dei dati
            stessi. <br />
            L'informativa Privacy completa è disponibile al seguente link
            {LINK}. Dichiaro di aver letto e compreso l'informativa sul
            trattamento dei dati personali.
          </p>
        </div>
        <input
          type="submit"
          value="Invia Richiesta"
          class=" mt-6 bg-primary focus:border-primary hover:bg-primary_light text-white font-semibold p-3 md:self-end flex-1"
        />
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: "InviaRichiesta",
  created() {
        this.$store.dispatch("storePage", {title:"Invia Richiesta",back:true});

  },
};
</script>

<style>
.send_request {
  height: 100vh;
}
</style>
