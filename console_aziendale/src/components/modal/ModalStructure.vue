<!-- 
DESCRIZIONE:
Il "Modal.vue" è un componente che serve per dare lo "scheletro" a tutti i modali, così da
facilitarne la creazione. Divide i modali in 3 aree: header, body, footer. L'header contiene
il titolo del modale, il body contiene tutti i campi per la compilazione che il modale richiede
mentre il footer contiene i bottoni per gestire lo stato dei modali e l'invio dei dati in esso.
-->
<template>
  <div class="modal-backdrop" v-init="show" @click.self="closeModal">
    <div class="modal w-2/3 z-50 m-0 absolute "
      role="dialog"
      aria-labelledby="modalTitle"
      aria-describedby="modalDescription"
    >
      <header
        class="modal-header"
        id="modalTitle"
      >
        <slot name="header">
          Titolo

          <button
            type="button"
            class="py-8 ml-8"
            @click="close"
            aria-label="Close modal"
          >
            x
          </button>
        </slot>
      </header>
      <section
        class="modal-body"
        id="modalDescription"
      >
        <slot name="body">
          Body
        </slot>
      </section>
      <div class="modal-footer">
        <slot name="footer">
          Footer

          <v-btn
            text
            @click="close"
            class="py-8 ml-8"
            aria-label="Close modal"
          >
            Chiudi
          </v-btn>
        </slot>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex';

export default {
  name: 'modal',

  methods: {
    ...mapActions("modal", {closeModal:"closeModal"}),

    toggleBodyClass(addRemoveClass, className) {
      const el = document.body;
      if (addRemoveClass === 'addClass') {
        el.classList.add(className);
      } else {
        el.classList.remove(className);
      }
    },
    show() {
      console.log('modal mounted');
      this.toggleBodyClass('addClass', 'modal-show');
    },
    close() {
      console.log('modal detroyed');
      this.toggleBodyClass('removeClass', 'modal-hide');
    },
  },
};
</script>

<style>
.modal-fade-enter,
.modal-fade-leave-active {
  opacity: 0;
}
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity .5s ease
}
.modal-backdrop {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal {
  background: #FFFFFF;
  box-shadow: 2px 2px 20px 1px;
  overflow-x: visible;
  display: flex;
  flex-direction: column;
  /* text-align: center; */
}
.modal-header {
  justify-content: space-between;
  height: 50px;
  line-height: 2rem;
  font-family: Roboto,sans-serif !important;
  font-size: 1.5rem !important;
  font-weight: 400;
  letter-spacing: normal !important;
  text-align: left;
  padding: 18px;
  margin: 20px;
}
.modal-footer {
  justify-content: flex-end;
  text-align: right;
  padding: 16px 24px;
}
.modal-body {
  position: relative;
  margin-left: 20px;
  padding: 20px;
  max-height: calc(100vh - 210px);
  overflow-y: scroll;
  overflow-x: visible;
  scrollbar-width: thin;
}
</style>