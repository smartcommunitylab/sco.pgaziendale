<template>
  <div class="capagna flex flex-col">
    <div class="flex flex-col ml-8 mt-8">
      <div class="text-4xl mt-8 text-black text-center">Campagna</div>
    </div>
    <div class="flex flex-col">
      <img class="object-contain h-48 w-full" src="@/assets/images/bike.svg" />
    </div>
    <div class="flex flex-col">
      <div class="text-4xl mt-8 text-black text-center">Organizzata da</div>
    </div>
    <div class="flex flex-col">
      <img class="object-contain h-48 w-full" src="@/assets/images/bike.svg" />
    </div>
    <div class="flex flex-col m-8  text-justify">
      Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean congue mauris diam. Morbi commodo enim at turpis porta, id molestie magna vehicula. Morbi malesuada sapien nec augue ullamcorper, vitae consectetur velit ullamcorper. Nullam malesuada sodales neque, quis laoreet urna. Nullam interdum neque a vehicula
      feugiat. Integer non velit ut metus vulputate consequat. Cras vitae rutrum tellus, et accumsan dui. Vivamus dignissim maximus augue, sed sagittis lectus maximus et. Maecenas eu ligula urna. Nulla eros dolor, ornare eu metus non, imperdiet tempor leo. Nam nibh turpis, pretium in tincidunt et, euismod at purus.
      Pellentesque est leo, malesuada nec lobortis at, aliquet sit amet massa. Vivamus et dolor dui. Donec euismod tellus ut convallis ultrices. Phasellus eget eleifend ligula. Sed lobortis luctus feugiat. Morbi tristique dolor rutrum nulla tempus, id sagittis lorem cursus. Ut lacinia, orci a aliquam interdum, odio
      elit congue neque, eu rhoncus velit nisi a felis. Morbi porta, dui non facilisis luctus, ante turpis ultricies enim, sed molestie magna dolor in dui. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean a odio rhoncus, varius odio eu, aliquam lacus. Donec sagittis pulvinar felis, nec consequat libero
      ultrices vitae. Morbi aliquet ligula sit amet ipsum venenatis finibus. In hac habitasse platea dictumst. Interdum et malesuada fames ac ante ipsum primis in faucibus. Aliquam pharetra mi eu tellus semper, vitae porttitor urna iaculis.
    </div>
    <div class="flex flex-col">
      <div class="text-4xl mt-8 text-black text-center">A questa campagna partecipa anche:</div>
    </div>
    <div class="flex flex-col">
      <img class="object-contain h-48 w-full" src="@/assets/images/bike.svg" />
    </div>
  </div>
</template>

<script>
export default {
  name: "Campagna",
    computed: {
    campagna() {
      return this.$store.getters.campagna;
    }
  },
};
</script>

<style></style>
