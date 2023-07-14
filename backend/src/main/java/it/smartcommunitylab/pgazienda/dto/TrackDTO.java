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

package it.smartcommunitylab.pgazienda.dto;

import java.util.List;

/**
 * @author raman
 *
 */
public class TrackDTO {

	private Long startTime;
	private String multimodalId;
	private List<TrackLegDTO> legs;
	
	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public List<TrackLegDTO> getLegs() {
		return legs;
	}


	public void setLegs(List<TrackLegDTO> legs) {
		this.legs = legs;
	}


	public String getMultimodalId() {
		return multimodalId;
	}

	public void setMultimodalId(String multimodalId) {
		this.multimodalId = multimodalId;
	}


	public static class TrackLegDTO {
		private String id;
		private String mean;
		private Double distance;
		private long duration;
		private double co2;
		
		private List<TrackPointDTO> points;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Double getDistance() {
			return distance;
		}
		public void setDistance(Double distance) {
			this.distance = distance;
		}
		public String getMean() {
			return mean;
		}
		public void setMean(String mean) {
			this.mean = mean;
		}
		public List<TrackPointDTO> getPoints() {
			return points;
		}
		public void setPoints(List<TrackPointDTO> points) {
			this.points = points;
		}
		public long getDuration() {
			return duration;
		}
		public void setDuration(long duration) {
			this.duration = duration;
		}
		public double getCo2() {
			return co2;
		}
		public void setCo2(double co2) {
			this.co2 = co2;
		}
		
	}
	
	
	public static class TrackPointDTO {
		private Double latitude;
		private Double longitude;
		private Long accuracy;
		private Double altitude;
		private Double speed;
		private Double heading;
		private String activity_type;
		private Long activity_confidence;
		private Double battery_level;
		private Boolean battery_is_charging;
		private Boolean is_moving;
		private Long recorded_at;
		private Long created_at;
		
		public Double getLatitude() {
			return latitude;
		}
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		public Double getLongitude() {
			return longitude;
		}
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}
		public Long getAccuracy() {
			return accuracy;
		}
		public void setAccuracy(Long accuracy) {
			this.accuracy = accuracy;
		}
		public Double getAltitude() {
			return altitude;
		}
		public void setAltitude(Double altitude) {
			this.altitude = altitude;
		}
		public Double getSpeed() {
			return speed;
		}
		public void setSpeed(Double speed) {
			this.speed = speed;
		}
		public Double getHeading() {
			return heading;
		}
		public void setHeading(Double heading) {
			this.heading = heading;
		}
		public String getActivity_type() {
			return activity_type;
		}
		public void setActivity_type(String activity_type) {
			this.activity_type = activity_type;
		}
		public Long getActivity_confidence() {
			return activity_confidence;
		}
		public void setActivity_confidence(Long activity_confidence) {
			this.activity_confidence = activity_confidence;
		}
		public Double getBattery_level() {
			return battery_level;
		}
		public void setBattery_level(Double battery_level) {
			this.battery_level = battery_level;
		}
		public Boolean getBattery_is_charging() {
			return battery_is_charging;
		}
		public void setBattery_is_charging(Boolean battery_is_charging) {
			this.battery_is_charging = battery_is_charging;
		}
		public Boolean getIs_moving() {
			return is_moving;
		}
		public void setIs_moving(Boolean is_moving) {
			this.is_moving = is_moving;
		}
		public Long getRecorded_at() {
			return recorded_at;
		}
		public void setRecorded_at(Long recorded_at) {
			this.recorded_at = recorded_at;
		}
		public Long getCreated_at() {
			return created_at;
		}
		public void setCreated_at(Long created_at) {
			this.created_at = created_at;
		}
	}
}
