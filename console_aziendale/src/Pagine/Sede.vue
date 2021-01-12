<template>
  <div class="flex flex-col lg:flex-row">
    <div class=" lg:w-4/6 mx-2 my-2 flex flex-col bg-white">
      <div class="flex flex-row justify-center py-4">
        <div class="px-2">
          <select
            class="focus:border-primary border-2 p-2 mb-2 md:mb-0 lg:mb-2  md:mr-2 lg:mr-0 appearance-none "
            v-model="selectedHq"
            required
          >
            <option value="0" selected disabled hidden
              >Seleziona una sede</option
            >
            <option value="1">
              Sede 1
            </option>
            <option value="2">
              Sede 2
            </option>
            <option value="3">
              Sede 3
            </option>
          </select>
        </div>

        <div class="px-2">
          <select
            class="focus:border-primary border-2 p-2 mb-2 md:mb-0 lg:mb-2  md:mr-2 lg:mr-0 appearance-none "
            v-model="selectedCampaign"
            required
          >
            <option value="0" selected disabled hidden
              >Seleziona una Campagna</option
            >
            <option value="1">
              Sede 1
            </option>
            <option value="2">
              Sede 2
            </option>
            <option value="3">
              Sede 3
            </option>
          </select>
        </div>

        <div class="px-2">
          <button
            class="inline-block px-6 py-2 text-xs font-medium leading-6 text-center text-white uppercase transition bg-primary rounded shadow ripple hover:shadow-lg hover:bg-primary_light focus:outline-none"
          >
            Importa CSV
          </button>
        </div>
      </div>
      <table class="table-auto rounded relative w-full">
        <thead class="text-center justify-between">
          <tr
            class="truncate px-2  flex border-b border-background text-center"
          >
            <th class="w-1/5">
              Nome
            </th>

            <th class=" w-1/5">
              Cognome
            </th>
            <th class="w-1/5">
              Matricola
            </th>
            <th class="w-1/5">
              Email
            </th>
            <th class="w-1/5"></th>
          </tr>
        </thead>
        <tbody class="bg-white text-center justify-between">
          <template v-for="employee in employees">
            <tr
              class=" text-center m-auto truncate px-2 select-none cursor-pointer flex items-center border-b border-background hover:bg-background transition ease-in duration-100 "
              :key="employee.id"
              tag="tr"
              :id="employee.id"
            >
              <td class=" w-1/5">
                <p class="text-gray-800 text-sm font-semibold text-center">
                  {{ employee.first_name }}
                </p>
              </td>
              <td class=" w-1/5">
                <p class="text-gray-800 text-sm font-semibold text-center">
                  {{ employee.last_name }}
                </p>
              </td>
              <td class=" w-1/5">
                <p class="text-gray-800 text-sm font-semibold text-center">
                  {{ employee.matricola }}
                </p>
              </td>
              <td class=" w-1/5">
                <p
                  class="text-gray-800 text-sm font-semibold text-center truncate"
                >
                  {{ employee.email }}
                </p>
              </td>
              <td class="flex  items-end w-1/5 pr-12">
                <svg
                  class="mr-3 md:mr-1 h-12 w-6 fill-current text-grey-dark m-auto "
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 20 20"
                >
                  <path
                    d="M4 12a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4zm6 0a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"
                  />
                </svg>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
      <div class="ml-auto pt-4 pr-4">
        <button
          class="p-0 w-12 h-12 bg-primary rounded-full hover:bg-primary_light active:shadow-lg mouse shadow transition ease-in duration-100 focus:outline-none"
        >
          <svg
            viewBox="0 0 20 20"
            enable-background="new 0 0 20 20"
            class="w-6 h-6 inline-block"
          >
            <path
              fill="#FFFFFF"
              d="M16,10c0,0.553-0.048,1-0.601,1H11v4.399C11,15.951,10.553,16,10,16c-0.553,0-1-0.049-1-0.601V11H4.601
                                    C4.049,11,4,10.553,4,10c0-0.553,0.049-1,0.601-1H9V4.601C9,4.048,9.447,4,10,4c0.553,0,1,0.048,1,0.601V9h4.399
                                    C15.952,9,16,9.447,16,10z"
            />
          </svg>
        </button>
      </div>
    </div>
    <infobox />
  </div>
</template>

<script>
import Infobox from "../components/Infobox.vue";
import { employees } from "../tmp-data/employees.js";
export default {
  components: { Infobox },
  name: "Sede",
  data: function() {
    return {
      employees: [],
      selectedHq: 0,
      selectedCampaign: 0,
    };
  },

  mounted: function() {
    this.employees = employees;
  },
};
</script>

<style></style>
