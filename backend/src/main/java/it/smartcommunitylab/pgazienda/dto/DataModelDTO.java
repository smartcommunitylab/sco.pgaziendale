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

import java.util.LinkedList;
import java.util.List;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.PGApp;
import it.smartcommunitylab.pgazienda.domain.User;

/**
 * @author raman
 *
 */
public class DataModelDTO {

	private List<PGApp> apps = new LinkedList<>();
	private List<Company> companies = new LinkedList<>();
	private List<Campaign> campaigns = new LinkedList<>();
	private List<UserDTO> companyUsers = new LinkedList<>();
	/**
	 * @return the apps
	 */
	public List<PGApp> getApps() {
		return apps;
	}
	/**
	 * @param apps the apps to set
	 */
	public void setApps(List<PGApp> apps) {
		this.apps = apps;
	}
	/**
	 * @return the companies
	 */
	public List<Company> getCompanies() {
		return companies;
	}
	/**
	 * @param companies the companies to set
	 */
	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}
	/**
	 * @return the campaigns
	 */
	public List<Campaign> getCampaigns() {
		return campaigns;
	}
	/**
	 * @param campaigns the campaigns to set
	 */
	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}
	/**
	 * @return the companyUsers
	 */
	public List<UserDTO> getCompanyUsers() {
		return companyUsers;
	}
	/**
	 * @param companyUsers the companyUsers to set
	 */
	public void setCompanyUsers(List<UserDTO> companyUsers) {
		this.companyUsers = companyUsers;
	}
	
}
