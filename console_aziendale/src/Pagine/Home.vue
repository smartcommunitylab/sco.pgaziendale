<template>
    <div>
Home Page
    </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'

export default {
    data () {
        return {

        }
    },
    computed: {
        ...mapState('account', ['status','user'])
    },

    methods: {
        ...mapActions('account', ['login', 'logout']),

    }
};
</script>