<template>
  <div class="px-8 bg-blue-600">
    <h1
      class="justify-self-center text-center text-white text-4xl pt-2 md:text-6xl font-semibold pb-6"
    >
      Le Mie Performance
    </h1>
    <div class="flex flex-col justify-center">
      <div class="group inline-block mx-auto">
        <button
          class="outline-none focus:outline-none border px-3 py-1 bg-white rounded-sm flex items-center min-w "
        >
          <span class="pr-1 font-semibold flex-1">{{
            getCurrentOption()
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
        <ul
          class="bg-white border rounded-sm transform scale-0 group-hover:scale-100 absolute 
  transition duration-150 ease-in-out origin-top min-w "
        >
          <template v-for="element in getOtherOptions()">
            <li
              :key="element.id"
              @click="changeCurrentOption(element.id)"
              class=" select-none cursor-pointer text-center  rounded-sm px-3 py-1 hover:bg-gray-100 "
            >
              {{ element.text }}
            </li>
          </template>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MyPerformance",
  data: function() {
    return {
      stats: [],
      currentOption: "KM",
    };
  },
  methods: {
    getCurrentOption() {
      let toRtn = "Km Totali";
      if (this.currentOption == "CO2") toRtn = "CO2 Salvata";
      else if (this.currentOption == "TRIPS") toRtn = "Viaggi Validi";

      return toRtn;
    },
    changeCurrentOption(id) {
      this.currentOption = id;
    },
    getOtherOptions() {
      let options = [
        { id: "KM", text: "Km Totali" },
        { id: "CO2", text: "CO2 Salvata" },
        { id: "TRIPS", text: "Viaggi Validi" },
      ];

      let toRtn = [];

      options.forEach((element) => {
        if (this.currentOption != element.id) {
          toRtn.push(element);
        }
      });

      return toRtn;
    },
  },
  mounted: function() {
    let months = [
      "Gennaio",
      "Febbraio",
      "Marzo",
      "Aprile",
      "Maggio",
      "Giugno",
      "Luglio",
      "Agosto",
    ];
    let kms = [23, 53, 22, 64, 34, 53, 23, 74];
    let co2 = [2, 5, 3, 6, 4, 6, 1, 7];
    let trips = [1, 3, 1, 5, 2, 3, 1, 5];

    for (let i = 0; i < kms.length; i++) {
      this.stats.push({
        month: months[i],
        kms: kms[i],
        co2: co2[i],
        trips: trips[i],
      });
    }
  },
};
</script>

<style>
.group:hover .group-hover\:scale-100 {
  transform: scale(1);
}
.group:hover .group-hover\:-rotate-180 {
  transform: rotate(180deg);
}
.scale-0 {
  transform: scale(0);
}
.min-w {
  min-width: 10rem;
}
</style>
