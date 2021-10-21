import Vue from 'vue';
import Vuex from 'vuex';

import { account } from './account.module';
import { campaign } from './campaign.module';
import { navigation } from './navigation.module';
import { company } from './company.module';
import { location } from './location.module';
import { employee } from './employee.module';
import { alert } from './alert.module';
import { loader } from './loader.module';
import { stat } from './stat.module';
import { modal } from './modal.module';

import createPersistedState from "vuex-persistedstate";


Vue.use(Vuex);

export const store = new Vuex.Store({
    modules: {
        account,
        navigation,
        campaign,
        company,
        location,
        alert,
        employee,
        loader,
        stat,
        modal
    },
    plugins: [createPersistedState({
        storage: window.sessionStorage
    })]
    
});