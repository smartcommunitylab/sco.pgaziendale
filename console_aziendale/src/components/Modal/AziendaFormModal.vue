<template>
    <modal>
        <template v-slot:header> {{ popup.title }} </template>
        <template v-slot:body>
            <form action="" id="addAzienda">
                <div class="mb-20 flex flex-wrap justify-between">
                    <v-row>
                        <v-col
                        cols="6"
                        >
                            <v-text-field
                                label="Nome"
                                placeholder="Nome *"
                                type="text"
                                name="companyName"
                                :rules="[rules.required]"
                                id="companyName"
                                v-model.trim="$v.name.$model"
                                outlined
                            ></v-text-field>
                        </v-col>
                        <v-col
                        cols="6"
                        >
                            <v-text-field
                                label="Codice Azienda"
                                placeholder="Codice *"
                                type="text"
                                name="companyCode"
                                :rules="[rules.required]"
                                id="companyCode"
                                v-model.trim="$v.code.$model"
                                outlined
                            >
                                <template v-slot:append>
                                    <v-tooltip
                                        left
                                        nudge-bottom="50px"
                                    >
                                        <template v-slot:activator="{ on }">
                                        <v-icon v-on="on">
                                            mdi-help-circle-outline
                                        </v-icon>
                                        </template>
                                        Codice univoco dell'azienda con cui verrá poi identificata nelle campagne
                                    </v-tooltip>
                                </template>
                            </v-text-field>
                        </v-col>
                        <v-col
                        cols="6"
                        >
                            <v-text-field
                                label="Indirizzo"
                                placeholder="Indirizzo *"
                                type="text"
                                name="companyAddress"
                                :rules="[rules.required]"
                                id="companyAddress"
                                v-model.trim="$v.address.$model"
                                outlined
                            ></v-text-field>
                        </v-col>
                        <v-col
                        cols="2"
                        >
                            <v-text-field
                                label="Numero"
                                placeholder="Numero *"
                                type="text"
                                name="companyNumber"
                                :rules="[rules.required]"
                                id="companyNumber"
                                v-model.trim="$v.streetNumber.$model"
                                outlined
                            ></v-text-field>
                        </v-col>
                        <v-col
                        cols="4"
                        >
                            <v-text-field
                                label="CAP"
                                placeholder="CAP *"
                                type="text"
                                name="companyCap"
                                :rules="[rules.required]"
                                id="companyCap"
                                v-model.trim="$v.zip.$model"
                                outlined
                            ></v-text-field>
                        </v-col>
                        <v-col
                        cols="6"
                        >
                            <v-text-field
                                label="Città"
                                placeholder="Città *"
                                type="text"
                                name="companyCity"
                                :rules="[rules.required]"
                                id="companyCity"
                                v-model.trim="$v.city.$model"
                                outlined
                            ></v-text-field>
                        </v-col>
                        <v-col
                        cols="4"
                        >
                            <v-autocomplete
                                label="Provincia"
                                placeholder="Provincia *"
                                name="companyProvince"
                                :rules="provinceRules"
                                id="companyProvince"
                                v-model.trim="$v.province.$model"
                                :items="listaProvince"
                                outlined
                            ></v-autocomplete>
                        </v-col>
                        <v-col
                        cols="6"
                        > 
                            <v-autocomplete
                                label="Regione"
                                placeholder="Regione *"
                                name="companyRegion"
                                :rules="[rules.required]"
                                id="companyRegion"
                                v-model.trim="$v.region.$model"
                                :items="listaRegioni"
                                outlined
                            ></v-autocomplete>
                        </v-col>
                        <v-col
                        cols="6"
                        >
                            <v-text-field
                                label="Stato"
                                placeholder="Stato *"
                                type="text"
                                name="companyCountry"
                                :rules="[rules.required]"
                                id="companyCountry"
                                v-model.trim="$v.country.$model"
                                outlined
                            ></v-text-field>
                        </v-col>
                        <v-col
                        cols="6"
                        >
                            <v-text-field
                                label="Telefono"
                                placeholder="Telefono *"
                                type="text"
                                name="companyPhone"
                                :rules="[rules.required]"
                                id="companyPhone"
                                v-model.trim="$v.contactPhone.$model"
                                outlined
                            ></v-text-field>
                        </v-col>
                        <v-col
                        cols="6"
                        >
                            <v-text-field
                                label="E-mail"
                                placeholder="E-mail *"
                                type="text"
                                name="companyEmail"
                                :rules="contactEmailRules"
                                id="companyEmail"
                                v-model.trim="$v.contactEmail.$model"
                                outlined
                            ></v-text-field>
                        </v-col>
                        <v-col
                        cols="6"
                        >
                            <v-text-field
                                label="Web"
                                placeholder="Web *"
                                type="text"
                                name="companyWeb"
                                :rules="webRules"
                                id="companyWeb"
                                v-model.trim="$v.web.$model"
                                outlined
                            ></v-text-field>
                        </v-col>
                        <v-col
                        cols="6"
                        >
                            <v-text-field
                                label="Logo"
                                placeholder="Logo"
                                type="text"
                                name="companyLogo"
                                id="companyLogo"
                                v-model.trim="$v.logo.$model"
                                outlined
                            >
                                <template v-slot:append>
                                    <v-tooltip
                                        left
                                        nudge-bottom="50px"
                                    >
                                        <template v-slot:activator="{ on }">
                                            <v-icon v-on="on">
                                                mdi-help-circle-outline
                                            </v-icon>
                                        </template>
                                        Inserisci un url che contenga il logo dell'azienda
                                    </v-tooltip>
                                </template>
                            </v-text-field>
                        </v-col>
                    </v-row>
                </div>
            </form>
        </template>
        <template v-slot:footer>
            <v-btn
            text
            @click="closeModal()"
            class="py-8 ml-8"
            >
            Annulla
            </v-btn>
            <v-btn
            color="primary"
            text
            @click="saveCompany"
            class="py-8 ml-8"
            >
            Salva
            </v-btn>
        </template>
    </modal>
</template>

<script>
import { mapActions, mapState } from "vuex";

import { required, email,url } from "vuelidate/lib/validators";
import Modal from "@/components/Modal.vue";

export default {
    props: {
        typeCall: String,
    },
    components:{
        "modal": Modal,
    },
    data() {
        return {
            listaProvince: ['AG', 'AL', 'AN', 'AO', 'AR', 'AP', 'AT', 'AV', 'BA', 'BT',
            'BL', 'BN', 'BG', 'BI', 'BO', 'BZ', 'BS', 'BR', 'CA', 'CL',
            'CB', 'CE', 'CT', 'CZ', 'CH', 'CO', 'CS', 'CR', 'KR', 'CN',
            'EN', 'FM', 'FE', 'FI', 'FG', 'FC', 'FR', 'GE', 'GO', 'GR',
            'IM', 'IS', 'AQ', 'SP', 'LT', 'LE', 'LC', 'LI', 'LO', 'LU', 'MC',
            'MN', 'MS', 'MT', 'ME', 'MI', 'MO', 'MB', 'NA', 'NO', 'NU',
            'OR', 'PD', 'PA', 'PR', 'PV', 'PG', 'PU', 'PE', 'PC', 'PI', 'PT',
            'PN', 'PZ', 'PO', 'RG', 'RA', 'RC', 'RE', 'RI', 'RN', 'RM', 'RO',
            'SA', 'SS', 'SV', 'SI', 'SR', 'SO', 'SU', 'TA', 'TE', 'TR', 'TO', 'TP',
            'TN', 'TV', 'TS', 'UD', 'VA', 'VE', 'VB', 'VC', 'VR', 'VV', 'VI', 'VT'
            ],
            listaRegioni: ['Abruzzo', 'Basilicata', 'Calabria', 'Campania', 'Emilia-Romagna', 
            'Friuli-Venezia Giulia', 'Lazio', 'Liguria', 'Lombardia', 'Marche', 'Molise', 'Piemonte', 
            'Puglia', 'Sardegna', 'Sicilia', 'Toscana', 'Trentino-Alto Adige', 'Umbria', 'Valle d\'Aosta Veneto' 
            ],
            company: {},
            id: null,
            name: "",
            code: "",
            address: "",
            streetNumber: "",
            city: "",
            province: 1,
            region: "",
            country: "",
            zip: "",
            contactEmail: "",
            contactPhone: "",
            web: "",
            logo: "",
            popup: {
                title: "",
            },
            
            rules: {
                required: value => !!value || 'Campo richiesto.',
            },
            provinceRules: [
                value => this.isInListaProvince(value) || 'Campo richiesto.'
            ],
            contactEmailRules: [
                v => !!v || 'Campo richiesto.', 
                v => this.validateEmail(v) || 'E-mail non valida.'
            ],
            webRules: [ 
                (v) => this.isURL(v) || 'URL non valido. Inserire " https:// ". ',
            ], 
        };
    },
    validations: {
        name: {
            required,
        },
        code: {
            required,
        },
        address: {
            required,
        },
        streetNumber: {
            required,
        },
        city: {
            required,
        },
        province: {
            required,
        },
        region: {
            required,
        },
        country: {
            required,
        },
        zip: {
            required,
        },
        contactEmail: {
            required,
            email,
        },
        contactPhone: {
            required,
        },
        web: {
            url
        },
        logo: {
        },
    },
    methods: {
        ...mapActions("modal", { closeModal:"closeModal" }),
        copyFormValues(company) {
            console.log("Sono nel For");
            console.log("Questa è la company:");
            console.log(company);
            for (const [key] of Object.entries(company)) {
                this[key] = company[key];
            }
        },

        isURL(str) {
            let url;
            try {
                url = new URL(str);
            } catch (_) {
                return false;
            }
            return url.protocol === "http:" || url.protocol === "https:";
        },
        validateEmail(email){
            var re = /\S+@\S+\.\S+/;
            return re.test(email);
        },
        isInListaProvince(value){
            return this.listaProvince.includes(value)
        },
        initCompany() {
            this.company = {};
            this.id = null;
            this.name = "";
            this.code = "";
            this.address = "";
            this.streetNumber = "";
            this.city = "";
            this.province = "";
            this.region = "";
            this.country = "";
            this.zip = "";
            this.contactEmail = "";
            this.contactPhone = "";
            this.web = "";
            this.logo = "";
        },
        createCompany() {
            this.company = {
                id: this.id,
                name: this.name,
                code: this.code,
                address: this.address,
                streetNumber: this.streetNumber,
                city: this.city,
                province: this.province,
                region: this.region,
                country: this.country,
                zip: this.zip,
                contactEmail: this.contactEmail,
                contactPhone: this.contactPhone,
                web: this.web,
                logo: this.logo,
            };
        },
        ...mapActions("company", {addCompany:"addCompany", updateCompany:"updateCompany"}),
        saveCompany() {
            
            //EventBus.$emit("CHECK_COMPANY_FORM");
            this.createCompany();
            if(this.typeCall == "add"){
                this.addCompany(this.company);
                this.closeModal();
            }else if (this.typeCall == "edit") {
                this.updateCompany(this.company);
                this.closeModal();
            }
        },
        setModalData(){
            if(this.typeCall == "add"){
                this.initCompany();
                this.popup.title = "Aggiungi Azienda";
                console.log("Modalità AGGIUNGI");

            }else if (this.typeCall == "edit") {
                this.copyFormValues(this.actualCompany.item);
                this.popup.title = "Modifica Azienda";
                console.log("Modalità MODIFICA");
            }
        },
    },
    computed: {
        ...mapState("company", ["actualCompany"]),
    },
    created() {
        this.setModalData()
    },
    watch: {
        typeCall: function(){
            this.setModalData();
        },
        actualCompany: function(){
            this.setModalData();
        }
    },
}
</script>

<style>

</style>