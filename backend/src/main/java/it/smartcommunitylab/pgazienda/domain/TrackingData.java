/*******************************************************************************
 * Copyright 2015 Fondazione Bruno Kessler
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/

package it.smartcommunitylab.pgazienda.domain;

public class TrackingData {
	private String trackId;
	private String playerId;
	private String multimodalId;
	private String startedAt;
	private String mode;
	private double distance;
	private long duration;
	private Double co2 = Double.valueOf(0.0);
	private Double score = Double.valueOf(0.0);
	private Double limitedScore = Double.valueOf(0.0);
	private boolean wayBack = false;
	private String locationId;
	private String hour;
	
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
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
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public Double getCo2() {
		return co2;
	}
	public void setCo2(double co2) {
		this.co2 = co2;
	}
	public String getMultimodalId() {
		return multimodalId;
	}
	public void setMultimodalId(String multimodalId) {
		this.multimodalId = multimodalId;
	}
	public void setCo2(Double co2) {
		this.co2 = co2;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	public Double getLimitedScore() {
		return limitedScore;
	}
	public void setLimitedScore(Double limitedScore) {
		this.limitedScore = limitedScore;
	}
	public boolean isWayBack() {
		return wayBack;
	}
	public void setWayBack(boolean wayBack) {
		this.wayBack = wayBack;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
}