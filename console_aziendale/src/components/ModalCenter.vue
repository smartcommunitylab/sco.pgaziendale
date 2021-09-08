<template>
    <div>
        <!-- MODALI utilizzabili per tutti i dati-->
        <delete-modal v-if="modalType == 'delete'"/>

        <!-- MODALI per l' UtenteLoggato -->
        <profile-setting-modal v-if="modalType == 'profileSetting'"/>
        
        <!-- MODALI per le Aziende -->
        <azienda-form-modal v-if="modalType == 'aziendaFormAdd'" typeCall="add"/>
        <azienda-form-modal v-if="modalType == 'aziendaFormEdit'" typeCall="edit"/>
        
        <!-- MODALI per l' User -->
        <user-form-modal v-if="modalType == 'userFormAdd'" typeCall="add"/>
        <user-form-modal v-if="modalType == 'userFormEdit'" typeCall="edit"/>

        <!-- MODALI per le Campaign -->
        <campaign-form-modal v-if="modalType == 'campaignFormAdd'" typeCall="add"/>
        <campaign-form-modal v-if="modalType == 'campaignFormEdit'" typeCall="edit"/>
        <associate-form-modal v-if="modalType == 'associateForm'"/>

        <!-- MODALI per gli Employee -->
        <employee-form-modal v-if="modalType == 'employeeFormAdd'" typeCall="add"/>
        <employee-form-modal v-if="modalType == 'employeeFormEdit'" typeCall="edit"/>

        <!-- MODALI per le Location -->
        <location-form-modal v-if="modalType == 'locationFormAdd'" typeCall="add"/>
        <location-form-modal v-if="modalType == 'locationFormEdit'" typeCall="edit"/>
    </div>
</template>

<script>
import DeleteModal from "@/components/Modal/DeleteModal.vue";
import ProfileSettingModal from "@/components/Modal/ProfileSettingModal.vue";
import AziendaFormModal from "@/components/Modal/AziendaFormModal.vue";
import UserFormModal from "@/components/Modal/UserFormModal.vue";
import CampaignFormModal from "@/components/Modal/CampaignFormModal.vue";
import AssociateFormModal from "@/components/Modal/AssociateFormModal.vue";
import EmployeeFormModal from "@/components/Modal/EmployeeFormModal.vue";
import LocationFormModal from "@/components/Modal/LocationFormModal.vue";

export default {
    components: {
        "delete-modal": DeleteModal,
        "profile-setting-modal": ProfileSettingModal,
        "azienda-form-modal": AziendaFormModal,
        "user-form-modal": UserFormModal,
        "campaign-form-modal": CampaignFormModal,
        "associate-form-modal": AssociateFormModal,
        "employee-form-modal": EmployeeFormModal,
        "location-form-modal": LocationFormModal,
    },
    props: {
        modalType: String,
        modalObject: Object,
    },
}
</script>

<style>

</style>