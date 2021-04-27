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

package it.smartcommunitylab.pgazienda.service;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.dto.DataModelDTO;

/**
 * Global data bootstrap helper service
 * @author raman
 *
 */
@Service
public class AdminService {
	
	@Autowired
	private PGAppService appService;
	@Autowired
	private UserService userService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CampaignService campaignService;
	
	
	public void loadData(DataModelDTO model) {
		validateApps(model);
		model.getApps().forEach(app -> appService.updateApp(app));
		
		validateCampaigns(model);
		model.getCampaigns().forEach(c -> campaignService.saveCampaign(c));
		
		validateCompanies(model);
		model.getCompanies().forEach(c -> {
			Company existing = companyService.findByCode(c.getCode()).orElse(null);
			if (existing == null) {
				companyService.createCompany(c);
			} else {
				c.setId(existing.getId());
				companyService.updateCompany(c);
				if (c.getLocations() != null) {
					c.getLocations().forEach(l -> {
						companyService.createLocation(c.getId(), l);
					});
				}
			}
		});
		
		validateUsers(model);
		model.getCompanyUsers().forEach(u -> {
			User user = userService.getUserWithAuthoritiesByUsername(u.getUsername()).orElse(null);
			if (user == null) {
				user = new User();
				user.setName(u.getName());
				user.setSurname(u.getSurname());
				user.setPhone(u.getPhone());
				user.setUsername(u.getUsername());
				user.setPassword(u.getPassword());
				String companyId = companyService.findByCode(u.getCompanyCode()).orElse(null).getId();
				UserRole role = (Constants.ROLE_COMPANY_ADMIN.equals(u.getRole())) ? UserRole.createCompanyAdminRole(companyId) : UserRole.createMobilityManager(companyId, u.getLocations());
				user.setRoles(Collections.singletonList(role));
				userService.createUser(user);
			}
		});
		
	}


	/**
	 * Check data consistency considering both stored and new data
	 * @param model
	 */
	private void validateUsers(DataModelDTO model) {
		if (model.getCompanyUsers().stream().anyMatch(u -> !Constants.ROLE_COMPANY_ADMIN.equals(u.getRole()) && !Constants.ROLE_MOBILITY_MANAGER.equals(u.getRole()))) throw new IllegalArgumentException("Invalid user roles");
		if (model.getCompanyUsers().stream().anyMatch(u -> StringUtils.isAnyEmpty(u.getUsername(), u.getName(), u.getSurname(), u.getPassword()))) throw new IllegalArgumentException("Invalid user definition");
		if (model.getCompanyUsers().stream().anyMatch(u -> companyService.findByCode(u.getCompanyCode()).isEmpty())) throw new IllegalArgumentException("Invalid user definition: non existing company");
		
	}


	/**
	 * @param model
	 */
	private void validateCompanies(DataModelDTO model) {
		if (model.getCompanies().stream().anyMatch(c -> StringUtils.isAnyEmpty(c.getCode(), c.getName()))) throw new IllegalArgumentException("Invalid company definition"); 
		if (model.getCompanies().stream().anyMatch(c -> c.getEnabledApps()== null || c.getEnabledApps().isEmpty())) throw new IllegalArgumentException("Invalid company definition: missing app");
		if (model.getCompanies().stream().anyMatch(c -> c.getEnabledApps().stream().anyMatch(a -> appService.getApp(a).isEmpty()))) throw new IllegalArgumentException("Invalid company definition: non existing app");
		if (model.getCompanies().stream().anyMatch(c -> c.getCampaigns() != null && c.getCampaigns().stream().anyMatch(campaign -> campaignService.getCampaign(campaign).isEmpty()))) throw new IllegalArgumentException("Invalid company definition: non existing campaign");
	}


	/**
	 * @param model
	 */
	private void validateCampaigns(DataModelDTO model) {
		if (model.getCampaigns().stream().anyMatch(c -> StringUtils.isAnyEmpty(c.getId(), c.getTitle()))) throw new IllegalArgumentException("Invalid campaign definition");
		if (model.getCampaigns().stream().anyMatch(c -> c.getFrom() == null)) throw new IllegalArgumentException("Invalid campaign definition: missing start date");
		if (model.getCampaigns().stream().anyMatch(c -> appService.getApp(c.getApplication()).isEmpty())) throw new IllegalArgumentException("Invalid campaign definition: non existing app");
	}


	/**
	 * @param model
	 */
	private void validateApps(DataModelDTO model) {
		if (model.getApps().stream().anyMatch(a -> StringUtils.isAnyEmpty(a.getId(), a.getName()))) throw new IllegalArgumentException("Invalid apps definition");
	}

}
