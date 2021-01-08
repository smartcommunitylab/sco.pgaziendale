<template>
  <div class="group inline-block  ">
    <button
      class="outline-none focus:outline-none border px-3 py-1 bg-white border-primary  text-primary rounded-sm flex items-center min-w w-full "
    >
      <span class="pr-1 font-semibold flex-1">{{
        getCurrentOption().view_name
      }}</span>
      <span>
        <svg
          class="fill-current h-4 w-4 transform group-hover:-rotate-180
        transition duration-150 ease-in-out"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 20 20"
        >
          <path
            d="M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z"
          />
        </svg>
      </span>
    </button>
    <div>
      <ul
        class="bg-white  text-primary border rounded-sm transform scale-0 group-hover:scale-100 relative 
       transition duration-150 ease-in-out origin-top min-w w-full"
      >
        <template v-for="element in getOtherOptions()">
          <li
            :key="element.name"
            @click="changeCurrentOption(element)"
            class=" select-none cursor-pointer text-center  rounded-sm px-3 py-1 hover:bg-gray-100 "
          >
            {{ element.view_name }}
          </li>
        </template>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: "ContextMenu",
  props: { _options: Array },
  data: function() {
    return {
      //{name:"CO2",view_name:"CO2 Salvata (KG)",defaut:true}
      options: this._options,
      currentOption: {},
    };
  },
  mounted: function() {
    let tmp = false;
    let i = 0;
    console.log(this.options.length);
    while (!tmp && i < this.options.length) {
      if (this.options[i].default) {
        tmp = true;
        this.currentOption = this.options[i];
      } else {
        i++;
      }
    }
  },

  methods: {
    getCurrentOption: function() {
      return this.currentOption;
    },
    getOtherOptions: function() {
      let toRtn = [];
      this.options.forEach((e) => {
        if (e.name !== this.currentOption.name) {
          toRtn.push(e);
        }
      });
      return toRtn;
    },
    changeCurrentOption: function(e) {
      this.currentOption = e;
    },
  },
};
</script>

<style></style>
