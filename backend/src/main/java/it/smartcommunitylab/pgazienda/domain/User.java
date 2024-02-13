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

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.smartcommunitylab.pgazienda.Constants;

/**
 * @author raman
 *
 */
public class User extends AbstractAuditingEntity {
	private static final long serialVersionUID = -247252786808033224L;

	@Id
	private String id;
	
    @Email
    @Size(min = 5, max = 254)
	private String username;
    
    private String playerId;
    
	private String name, surname, phone;

	private List<UserRole> roles = new LinkedList<>();
	
    @JsonIgnore
    private String password;
    private boolean activated = false;
    @JsonIgnore
    private String activationKey;
    @JsonIgnore
    private String resetKey;
    private Instant resetDate = null;
    
    private boolean deleted;

	
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @param surname the surname to set
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}
	/**
	 * @return the roles
	 */
	public List<UserRole> getRoles() {
		return roles;
	}
	/**
	 * @param roles the roles to set
	 */
	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the activated
	 */
	public boolean isActivated() {
		return activated;
	}
	/**
	 * @param activated the activated to set
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	/**
	 * @return the activationKey
	 */
	public String getActivationKey() {
		return activationKey;
	}
	/**
	 * @param activationKey the activationKey to set
	 */
	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}
	/**
	 * @return the resetKey
	 */
	public String getResetKey() {
		return resetKey;
	}
	/**
	 * @param resetKey the resetKey to set
	 */
	public void setResetKey(String resetKey) {
		this.resetKey = resetKey;
	}
	/**
	 * @return the resetDate
	 */
	public Instant getResetDate() {
		return resetDate;
	}
	/**
	 * @param resetDate the resetDate to set
	 */
	public void setResetDate(Instant resetDate) {
		this.resetDate = resetDate;
	}
	
	/**
	 * @return the playerId
	 */
	public String getPlayerId() {
		return playerId;
	}
	/**
	 * @param playerId the playerId to set
	 */
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public Optional<UserRole> findRole(String role) {
		if (roles == null) return Optional.empty();
		return roles.stream().filter(r -> r.getRole().equals(role)).findFirst();
	}
	public boolean hasCompanyRole(String companyId) {
		if (roles == null) return false;
		return roles.stream().anyMatch(r -> companyId.equalsIgnoreCase(r.getCompanyId()));
	}
	public boolean hasTerritoryRole(String territoryId) {
		if (roles == null) return false;
		return roles.stream().anyMatch(r -> Constants.ROLE_TERRITORY_MANAGER.equals(r.getRole()) && r.getTerritoryId().equals(territoryId));
	}
	public boolean hasCampaignRole(String campaignId) {
		if (roles == null) return false;
		return roles.stream().anyMatch(r -> Constants.ROLE_CAMPAIGN_MANAGER.equals(r.getRole()) && r.getCampaignId().equals(campaignId));
	}
	public List<UserRole> companyRoles() {
		if (roles == null) return Collections.emptyList();
		return roles.stream().filter(r -> !StringUtils.isEmpty(r.getCompanyId())).collect(Collectors.toList());
	}
	public boolean isAdmin() {
		return roles.stream().anyMatch(r -> Constants.ROLE_ADMIN.equals(r.getRole()));
	}
	public Optional<Subscription> findActiveSubscription(String campaign, String companyCode, String key) {
		for(UserRole r : roles) {
			if(Constants.ROLE_APP_USER.equals(r.getRole())) {
				for(Subscription s : r.getSubscriptions()) {
					if(s.getCampaign().equals(campaign) && s.getCompanyCode().equalsIgnoreCase(companyCode)
							&& s.getKey().equalsIgnoreCase(key) && !s.isAbandoned()) {
						return Optional.of(s);
					}
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	
}
