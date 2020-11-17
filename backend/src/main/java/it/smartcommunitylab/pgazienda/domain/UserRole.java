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

import it.smartcommunitylab.pgazienda.Constants;

/**
 * @author raman
 *
 */
public class UserRole {

	private String role;
	private String companyId;
	private List<String> locations;
	private List<Subscription> subscriptions;
	
	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
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
	 * @return the locations
	 */
	public List<String> getLocations() {
		return locations;
	}
	/**
	 * @param locations the locations to set
	 */
	public void setLocations(List<String> locations) {
		this.locations = locations;
	}
	
	/**
	 * @return the subscriptions
	 */
	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}
	/**
	 * @param subscriptions the subscriptions to set
	 */
	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}
	public static UserRole createAdminRole() {
		UserRole role = new UserRole();
		role.setRole(Constants.ROLE_ADMIN);
		return role;
	}
	public static UserRole createCompanyAdminRole(String companyId) {
		UserRole role = new UserRole();
		role.setRole(Constants.ROLE_COMPANY_ADMIN);
		role.setCompanyId(companyId);
		return role;
	}
	public static UserRole createMobilityManager(String companyId, List<String> locations) {
		UserRole role = new UserRole();
		role.setRole(Constants.ROLE_MOBILITY_MANAGER);
		role.setCompanyId(companyId);
		role.setLocations(locations);
		return role;
	}
	
	public static UserRole createAppUserRole(Subscription ... subs) {
		UserRole role = new UserRole();
		role.setRole(Constants.ROLE_APP_USER);
		role.setSubscriptions(new LinkedList<>());
		if (subs != null) {
			for (Subscription sub : subs) role.subscriptions.add(sub);
		}
		return role;
	}

	public String key() {
		return role + (companyId != null ? ("@" + companyId) : "");
	}
}
