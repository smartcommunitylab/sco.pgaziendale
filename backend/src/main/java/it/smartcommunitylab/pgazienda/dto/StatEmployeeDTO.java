package it.smartcommunitylab.pgazienda.dto;

public class StatEmployeeDTO {
	private String campaign, timeGroup, dataGroup;
	private int registration, activeUsers, dropout;
	
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
	public int getRegistration() {
		return registration;
	}
	public void setRegistration(int registration) {
		this.registration = registration;
	}
	public int getActiveUsers() {
		return activeUsers;
	}
	public void setActiveUsers(int activeUsers) {
		this.activeUsers = activeUsers;
	}
	public int getDropout() {
		return dropout;
	}
	public void setDropout(int dropout) {
		this.dropout = dropout;
	}
	
	public void addRegistration() {
		this.registration += 1;
	}
	
	public void addDropout() {
		this.dropout += 1;
	}
	
	public void addActiveUsers() {
		this.activeUsers += 1;
	}
	
}
