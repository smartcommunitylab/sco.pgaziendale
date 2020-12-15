<template>
  <Transition name="fade">
    <div
      v-if="dShowing"
      class="snackbar  p-4 bg-danger absolute w-1/2 text-center text-white shadow-xl rounded-md"
    >
      <button
        type="button"
        class=" btn_close px-2 m-2 py-2 rounded-full"
        @click="close"
      >
        <img
          :src="require('../assets/images/math-multiplication.svg')"
          class="svg"
        />
      </button>
      <p class="font-bold  text-lg mb-2">{{ dTitle }}</p>
      <p>{{ dDescription }}</p>
    </div></Transition
  >
</template>

<script>
import EventBus from "../communication/eventBus";
export default {
  name: "SnackBar",
  props: {},

  data: function() {
    return {
      dTitle: this.title,
      dDescription: this.description,
      dShowing: this.showing,
    };
  },
  watch: {
    dShowing(value) {
      console.log(value);
      if (value) {
        this.close();
        return document.querySelector("body").classList.add("overflow-hidden");
      }
    },
  },
  created: function() {
    EventBus.$on("snack-open", (title, description) => {
      this.openSnackbar(title, description);
    });
  },
  methods: {
    close(e) {
      console.log(e);
      if (e == undefined) setTimeout(() => (this.dShowing = false), 3000);
      else this.dShowing = false;
    },
    openSnackbar(title, text) {
      this.dTitle = title;
      this.dDescription = text;
      this.dShowing = true;
    },
  },
};
</script>

<style>
.snackbar {
  bottom: 0%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 6;
}
.btn_close {
  position: fixed;
  top: 0;
  right: 0;
  height: 25px;
  width: 25px;
}
.st0 {
  fill: white;
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.4s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>
