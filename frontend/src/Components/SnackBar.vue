<template>
  <div class="snackbar border-l-4 p-4">
    <p class="font-bold">{{ dTitle }}</p>
    <p>{{ dDescription }}</p>
  </div>
</template>

<script>
export default {
  name: "SnackBar",
  props: {
    title: String,
    description: String,
    color: String,
  },

  data: function() {
    return {
      dTitle: this.title,
      dDescription: this.description,
      dColor: this.color,
    };
  },
};
</script>

<style>
.snackbar {
  border-color: #f6993f;
  background-color: #fff5eb;
  color: #f6993f;
  font: bold;
}
</style>
