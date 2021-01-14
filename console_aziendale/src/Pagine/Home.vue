<template>
    <div>
       {{user}}
       {{status}}
       {{status.loggedIn}}
    </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'

export default {
    data () {
        return {
            username: '',
            password: '',
            submitted: false
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