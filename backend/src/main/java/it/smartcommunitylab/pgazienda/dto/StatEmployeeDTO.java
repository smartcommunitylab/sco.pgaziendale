package it.smartcommunitylab.pgazienda.dto;

public class StatEmployeeDTO {
    private String campaign, timeGroup, dataGroup, dataGroupName;
    private long employee;
    private FieldEmployeeDTO registration, registered, activeUsers, dropout;

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getTimeGroup() {
        return timeGroup;
    }

    public void setTimeGroup(String timeGroup) {
        this.timeGroup = timeGroup;
    }

    public String getDataGroup() {
        return dataGroup;
    }

    public void setDataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
    }

    public String getDataGroupName() {
        return dataGroupName;
    }

    public void setDataGroupName(String dataGroupName) {
        this.dataGroupName = dataGroupName;
    }

    public long getEmployee() {
        return employee;
    }

    public void setEmployee(long employee) {
        this.employee = employee;
    }

    public FieldEmployeeDTO getRegistration() {
        return registration;
    }

    public void setRegistration(FieldEmployeeDTO registration) {
        this.registration = registration;
    }

    public FieldEmployeeDTO getRegistered() {
        return registered;
    }

    public void setRegistered(FieldEmployeeDTO registered) {
        this.registered = registered;
    }

    public FieldEmployeeDTO getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(FieldEmployeeDTO activeUsers) {
        this.activeUsers = activeUsers;
    }

    public FieldEmployeeDTO getDropout() {
        return dropout;
    }

    public void setDropout(FieldEmployeeDTO dropout) {
        this.dropout = dropout;
    }
}
