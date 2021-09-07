<template>
    <div>
        <!-- MODALI utilizzabili per tutti i dati-->
        <delete-modal v-if="modalType == 'delete'" :object="modalObject"/>

        <!-- MODALI per l' UtenteLoggato -->
        <profile-setting-modal v-if="modalType == 'profileSetting'" :profile="modalObject"/>
        
        <!-- MODALI per le Aziende -->
        <azienda-form-modal v-if="modalType == 'aziendaFormAdd'" :azienda="modalObject" typeCall="add"/>
        <azienda-form-modal v-if="modalType == 'aziendaFormEdit'" :azienda="modalObject" typeCall="edit"/>
        
        <!-- MODALI per l' User -->
        <user-form-modal v-if="modalType == 'userFormAdd'" :user="modalObject" typeCall="add"/>
        <user-form-modal v-if="modalType == 'userFormEdit'" :user="modalObject" typeCall="edit"/>

        <!-- MODALI per le Campaign -->
        <campaign-form-modal v-if="modalType == 'campaignFormAdd'" :campaign="modalObject" typeCall="add"/>
        <campaign-form-modal v-if="modalType == 'campaignFormEdit'" :campaign="modalObject" typeCall="edit"/>
        <associate-form-modal v-if="modalType == 'associateForm'" :campaign="modalObject"/>

        <!-- MODALI per gli Employee -->
        <employee-form-modal v-if="modalType == 'employeeFormAdd'" :employee="modalObject" typeCall="add"/>
        <employee-form-modal v-if="modalType == 'employeeFormEdit'" :employee="modalObject" typeCall="edit"/>

        <!-- MODALI per le Location -->
        <location-form-modal v-if="modalType == 'locationFormAdd'" :location="modalObject" typeCall="add"/>
        <location-form-modal v-if="modalType == 'locationFormEdit'" :location="modalObject" typeCall="edit"/>
    </div>
</template>

<script>
import DeleteModal from "@/Modal/DeleteModal.vue";
import ProfileSettingModal from "@/Modal/ProfileSettingModal.vue";
import AziendaFormModal from "@/Modal/AziendaFormModal.vue";
import UserFormModal from "@/Modal/UserFormModal.vue";
import CampaignFormModal from "@/Modal/CampaignFormModal.vue";
import AssociateFormModal from "@/Modal/AssociateFormModal.vue";
import EmployeeFormModal from "@/Modal/EmployeeFormModal.vue";
import LocationFormModal from "@/Modal/LocationFormModal.vue";

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