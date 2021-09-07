import Vue from 'vue';
import Vuetify from 'vuetify/lib/framework';

Vue.use(Vuetify);

export default new Vuetify({
    theme: {
        themes: {
            light: {
                primary: '#0f70b7',
                secondary: '#5ab45e',
                accent: '#8c9eff',
                error: '#b71c1c',
            },
        },
    },
});
