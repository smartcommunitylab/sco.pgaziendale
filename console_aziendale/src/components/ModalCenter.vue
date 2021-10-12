<template>
    <div class="container-modal">
        <!-- MODALI per l' UtenteLoggato -->
        <profile-setting-modal v-if="modalType == 'profileSetting'"/>
        <change-password-modal v-if="modalType == 'changePasswordModal'"/>
        
        <!-- MODALI per le Aziende -->
        <azienda-form-modal v-if="modalType == 'aziendaFormAdd'" typeCall="add"/>
        <azienda-form-modal v-if="modalType == 'aziendaFormEdit'" typeCall="edit"/>
        <delete-modal v-if="modalType == 'deleteAzienda'" typeCall="azienda"/>
        
        <!-- MODALI per l' User -->
        <user-form-modal v-if="modalType == 'userFormAdd'" typeCall="add"/>
        <user-form-modal v-if="modalType == 'userFormEdit'" typeCall="edit"/>
        <delete-modal v-if="modalType == 'deleteUser'" typeCall="user"/>

        <!-- MODALI per le Campaign -->
        <campaign-form-modal v-if="modalType == 'campaignFormAdd'" typeCall="add"/>
        <campaign-form-modal v-if="modalType == 'campaignFormEdit'" typeCall="edit"/>
        <delete-modal v-if="modalType == 'deleteCampaign'" typeCall="campaign"/>
        <associate-form-modal v-if="modalType == 'associateForm'"/>
        <dissociate-form-modal v-if="modalType == 'dissociateForm'"/>

        <!-- MODALI per gli Employee -->
        <employee-form-modal v-if="modalType == 'employeeFormAdd'" typeCall="add"/>
        <employee-form-modal v-if="modalType == 'employeeFormEdit'" typeCall="edit"/>
        <import-modal v-if="modalType == 'employeeImport'" typeCall='employee'/>
        <delete-modal v-if="modalType == 'deleteEmployee'" typeCall="employee"/>

        <!-- MODALI per le Location -->
        <location-form-modal v-if="modalType == 'locationFormAdd'" typeCall="add"/>
        <location-form-modal v-if="modalType == 'locationFormEdit'" typeCall="edit"/>
        <import-modal v-if="modalType == 'locationImport'" typeCall='location'/>
        <delete-modal v-if="modalType == 'deleteLocation'" typeCall="location"/>
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
import ChangePasswordModal from "@/components/Modal/ChangePasswordModal.vue";
import DissociateFormModal from '@/components/Modal/DissociateFormModal.vue';
import ImportModal from '@/components/Modal/ImportModal.vue';

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
        "change-password-modal": ChangePasswordModal,
        "dissociate-form-modal": DissociateFormModal,
        "import-modal": ImportModal,
    },
    props: {
        modalType: String,
    },
}
</script>

<style>
.container-modal{
    position:relative;
    z-index: 51;
}
</style>