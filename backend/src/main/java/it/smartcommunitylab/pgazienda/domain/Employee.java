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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;

/**
 * @author raman
 *
 */
public class Employee {

	@Id
	private String id;
	
	private String name, surname, companyEmail, companyId, code;
	private String location;
	private List<String> campaigns;

	private Map<String, TrackingRecord> trackingRecord = new HashMap<>();

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the surname
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * @param surname the surname to set
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * @return the companyEmail
	 */
	public String getCompanyEmail() {
		return companyEmail;
	}

	/**
	 * @param companyEmail the companyEmail to set
	 */
	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}

	/**
	 * @return the companyId
	 */
	public String getCompanyId() {
		return companyId;
	}

	/**
	 * @param companyId the companyId to set
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the campaigns
	 */
	public List<String> getCampaigns() {
		if (campaigns == null) campaigns = new LinkedList<>();
		return campaigns;
	}

	/**
	 * @param campaigns the campaigns to set
	 */
	public void setCampaigns(List<String> campaigns) {
		this.campaigns = campaigns;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	public Map<String, TrackingRecord> getTrackingRecord() {
		return trackingRecord;
	}

	public void setTrackingRecord(Map<String, TrackingRecord> trackingRecord) {
		this.trackingRecord = trackingRecord;
	}



	public static class TrackingRecord {

		public Long getRegistration() {
			return registration;
		}

		public void setRegistration(Long registration) {
			this.registration = registration;
		}

		public Long getLeave() {
			return leave;
		}

		public void setLeave(Long leave) {
			this.leave = leave;
		}

		public Long getTracking() {
			return tracking;
		}

		public void setTracking(Long tracking) {
			this.tracking = tracking;
		}

		public Long registration, leave, tracking;

	}
}
