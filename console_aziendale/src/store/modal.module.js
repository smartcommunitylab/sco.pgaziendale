const state = {
    active: null,
    type: null,
    object: null
};

const actions = {
    openModal({ commit }, {type, object}) {
        commit('setActive', true);
        commit('setType', type);
        commit('setObject', object);
    },
    closeModal({ commit }) {
        commit('setActive', false);
    },
    initModal({ commit }){
        commit('init');
    }
};

const mutations = {
    setActive(state, active) {
        state.active = active;
    },
    setType(state, type) {
        state.type = type;
    },
    setObject(state, object) {
        state.object = object;
    },
    init(state) {
        state.active = null;
        state.type = null;
        state.object = null;
    }
};

export const modal = {
    namespaced: true,
    state,
    actions,
    mutations
};