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

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author raman
 *
 */
public class Company {

	@Id
	private String id;
	
	@Indexed(unique=true)
	@NotEmpty
	private String code;
	
	@NotNull
	private String name;
	private String logo;
	private String address;
	private String web;
	private String contactEmail;
	private String contactPhone;
	
	private List<CompanyLocation> locations = new LinkedList<>();
	
	private List<String> enabledApps = new LinkedList<>();
	private List<String> campaigns = new LinkedList<>();
	
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
	 * @return the logo
	 */
	public String getLogo() {
		return logo;
	}
	/**
	 * @param logo the logo to set
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the web
	 */
	public String getWeb() {
		return web;
	}
	/**
	 * @param web the web to set
	 */
	public void setWeb(String web) {
		this.web = web;
	}
	/**
	 * @return the contactEmail
	 */
	public String getContactEmail() {
		return contactEmail;
	}
	/**
	 * @param contactEmail the contactEmail to set
	 */
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	/**
	 * @return the contactPhone
	 */
	public String getContactPhone() {
		return contactPhone;
	}
	/**
	 * @param contactPhone the contactPhone to set
	 */
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	/**
	 * @return the locations
	 */
	public List<CompanyLocation> getLocations() {
		return locations;
	}
	/**
	 * @param locations the locations to set
	 */
	public void setLocations(List<CompanyLocation> locations) {
		this.locations = locations;
	}
	/**
	 * @return the enabledApps
	 */
	public List<String> getEnabledApps() {
		return enabledApps;
	}
	/**
	 * @param enabledApps the enabledApps to set
	 */
	public void setEnabledApps(List<String> enabledApps) {
		this.enabledApps = enabledApps;
	}
	/**
	 * @return the campaigns
	 */
	public List<String> getCampaigns() {
		return campaigns;
	}
	/**
	 * @param campaigns the campaigns to set
	 */
	public void setCampaigns(List<String> campaigns) {
		this.campaigns = campaigns;
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
}
