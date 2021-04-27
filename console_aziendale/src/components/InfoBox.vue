<template lang="html">
  <div class="tooltip" v-on:mouseover="isActive = true" v-on:mouseleave="isActive = false" >
    <help-icon />
    <transition name="fade">
      <div v-if="isActive">
        <span class="triangle"></span>
        <span class="tooltiptext">{{ msg }}</span>
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: "Box",
  data() {
    return {
      isActive: false,
    };
  },
  props: {
    msg: {
      type: String,
      required: true,
      default: "",
    },
  },
  created() {},
  methods: {
    cahngeVisibility() {
      this.isActive = !this.isActive;
    },
  },
  computed: {},
};
</script>

<style lang="css">
.tooltip {
  position: absolute;
  display: inline-block;
  transform: translateY(8px);
  z-index: 9;
}
.tooltip span {
  position: fixed;
}
.tooltip .tooltiptext {
  /* visibility: hidden; */
  width: 120px;
  background-color: black !important;
  color: #fff;
  text-align: center;
  border-radius: 6px;
  padding: 8px;
  position: absolute;
  z-index: 1;
  left: 50px;
  overflow: visible;
  transform: translateY(-30%);
}
.triangle {
  border-width: 6px 6px 6px 0px;
  border-color: transparent;
  border-bottom-color: #59c7f9;
  position: absolute;
  /* top: -6px; */
  left: 41px;
  transform: translate-x(-50%);
  width: 0;
  height: 0;
  border-top: 10px solid transparent;
  border-bottom: 10px solid transparent;
  border-right: 10px solid black;
}

.fade-enter-active,
.fade-leave-active {
  opacity: 1;
  transition: opacity 0.5s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>



