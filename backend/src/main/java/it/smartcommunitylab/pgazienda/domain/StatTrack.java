package it.smartcommunitylab.pgazienda.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class StatTrack {
	public static final String EMPLOYEE_KEY_DIV = "__";
	
	@Id
	private String id;
	@Indexed
	private String campaign; 
	private String playerId;
	@Indexed
	private String date;
	private String year;
	private String month;
	private String week; 
	private String dayOfWeek; 
	private String hour; 
	@Indexed
	private String company; 
	private String location; 
	private String employeeCode;
	private String employeeKey;
	private String trackId;
	private String multimodalId; 
	private String startedAt; 
	private String mode;
	private double distance, co2, score, limitedScore;
	private long duration;
	private boolean wayBack = false;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCampaign() {
		return campaign;
	}
	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getMultimodalId() {
		return multimodalId;
	}
	public void setMultimodalId(String multimodalId) {
		this.multimodalId = multimodalId;
	}
	public String getStartedAt() {
		return startedAt;
	}
	public void setStartedAt(String startedAt) {
		this.startedAt = startedAt;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double getCo2() {
		return co2;
	}
	public void setCo2(double co2) {
		this.co2 = co2;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getLimitedScore() {
		return limitedScore;
	}
	public void setLimitedScore(double limitedScore) {
		this.limitedScore = limitedScore;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public boolean isWayBack() {
		return wayBack;
	}
	public void setWayBack(boolean wayBack) {
		this.wayBack = wayBack;
	}
	public String getEmployeeKey() {
		return employeeKey;
	}
	public void setEmployeeKey(String employeeKey) {
		this.employeeKey = employeeKey;
	}
	
}
