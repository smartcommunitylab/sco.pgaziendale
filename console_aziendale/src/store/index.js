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
        loader
    }
});