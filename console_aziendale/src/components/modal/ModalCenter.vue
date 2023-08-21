<!-- 
DESCRIZIONE:
Il "ModalCenter.vue" è un componente dormiente in primo piano dell'applicativo contenente tutti i
componenti modali che l'applicazione necessita. Il suo compito è quello di attivarsi e disattivarsi
quando l'applicazione lo richiede per visualizzare o smettere di visualizzare un modale. Questo lo
fa in base alla variabile "modalType" che permette l'avvio del modale corretto.
-->
<template>
    <div class="container-modal">
        <!-- MODALI per l' UtenteLoggato -->
        <profile-setting-modal v-if="modalType == 'profileSetting'"/>
        <change-password-modal v-if="modalType == 'changePasswordModal'"/>
        
        <!-- MODALI per le Aziende -->
        <company-form-modal v-if="modalType == 'aziendaFormAdd'" typeCall="add"/>
        <company-form-modal v-if="modalType == 'aziendaFormEdit'" typeCall="edit"/>
        <delete-modal v-if="modalType == 'deleteAzienda'" typeCall="azienda"/>
        <state-toggle-modal v-if="modalType == 'updateCompanyState'"/>
        
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
import DeleteModal from "@/components/modal/specific-modals/DeleteModal.vue";
import ProfileSettingModal from "@/components/modal/specific-modals/ProfileSettingModal.vue";
import CompanyFormModal from "@/components/modal/specific-modals/CompanyFormModal.vue";
import UserFormModal from "@/components/modal/specific-modals/UserFormModal.vue";
import CampaignFormModal from "@/components/modal/specific-modals/CampaignFormModal.vue";
import AssociateFormModal from "@/components/modal/specific-modals/AssociateFormModal.vue";
import EmployeeFormModal from "@/components/modal/specific-modals/EmployeeFormModal.vue";
import LocationFormModal from "@/components/modal/specific-modals/LocationFormModal.vue";
import ChangePasswordModal from "@/components/modal/specific-modals/ChangePasswordModal.vue";
import DissociateFormModal from '@/components/modal/specific-modals/DissociateFormModal.vue';
import ImportModal from '@/components/modal/specific-modals/ImportModal.vue';
import StateToggleModal from '@/components/modal/specific-modals/StateToggleModal.vue';

export default {
    components: {
        "delete-modal": DeleteModal,
        "profile-setting-modal": ProfileSettingModal,
        "company-form-modal": CompanyFormModal,
        "user-form-modal": UserFormModal,
        "campaign-form-modal": CampaignFormModal,
        "associate-form-modal": AssociateFormModal,
        "employee-form-modal": EmployeeFormModal,
        "location-form-modal": LocationFormModal,
        "change-password-modal": ChangePasswordModal,
        "dissociate-form-modal": DissociateFormModal,
        "import-modal": ImportModal,
        "state-toggle-modal": StateToggleModal,
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