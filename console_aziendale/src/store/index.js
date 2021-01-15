import Vue from 'vue';
import Vuex from 'vuex';

import { account } from './account.module';
import { navigation } from './navigation.module';

Vue.use(Vuex);

export const store = new Vuex.Store({
    modules: {
        account,
        navigation
    }
});