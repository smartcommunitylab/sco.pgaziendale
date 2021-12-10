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
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.dto.DataModelDTO;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;

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
	
	
	public void loadData(DataModelDTO model) throws InconsistentDataException {
		validateApps(model);
		model.getApps().forEach(app -> appService.updateApp(app));
		
		validateCampaigns(model);
		model.getCampaigns().forEach(c -> campaignService.saveCampaign(c));
		
		validateCompanies(model);
		for (Company c:  model.getCompanies()) {
			Company existing = companyService.findByCode(c.getCode()).orElse(null);
			if (existing == null) {
				companyService.createCompany(c);
			} else {
				c.setId(existing.getId());
				companyService.updateCompany(c);
				if (c.getLocations() != null) {
					for (CompanyLocation l : c.getLocations()) {
						companyService.createLocation(c.getId(), l);
					}
				}
			}
		}
		
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
	 * @throws InconsistentDataException 
	 */
	private void validateUsers(DataModelDTO model) throws InconsistentDataException {
		if (model.getCompanyUsers().stream().anyMatch(u -> !Constants.ROLE_COMPANY_ADMIN.equals(u.getRole()) && !Constants.ROLE_MOBILITY_MANAGER.equals(u.getRole()))) throw new InconsistentDataException("Invalid user roles", "INVALID_ROLES");
		if (model.getCompanyUsers().stream().anyMatch(u -> StringUtils.isAnyEmpty(u.getUsername(), u.getName(), u.getSurname(), u.getPassword()))) throw new InconsistentDataException("Invalid user definition", "INVALID_USER_DATA");
		if (model.getCompanyUsers().stream().anyMatch(u -> companyService.findByCode(u.getCompanyCode()).isEmpty())) throw new InconsistentDataException("Invalid user definition: non existing company", "NO_COMPANY");
		
	}


	/**
	 * @param model
	 * @throws InconsistentDataException 
	 */
	private void validateCompanies(DataModelDTO model) throws InconsistentDataException {
		if (model.getCompanies().stream().anyMatch(c -> StringUtils.isAnyEmpty(c.getCode(), c.getName()))) throw new InconsistentDataException("Invalid company definition", "INVALID_COMPANY_DATA"); 
		if (model.getCompanies().stream().anyMatch(c -> c.getEnabledApps()== null || c.getEnabledApps().isEmpty())) throw new InconsistentDataException("Invalid company definition: missing app", "NO_APP");
		if (model.getCompanies().stream().anyMatch(c -> c.getEnabledApps().stream().anyMatch(a -> appService.getApp(a).isEmpty()))) throw new InconsistentDataException("Invalid company definition: non existing app", "NO_APP");
		if (model.getCompanies().stream().anyMatch(c -> c.getCampaigns() != null && c.getCampaigns().stream().anyMatch(campaign -> campaignService.getCampaign(campaign).isEmpty()))) throw new InconsistentDataException("Invalid company definition: non existing campaign", "NO_CAMPAIGN");
	}


	/**
	 * @param model
	 * @throws InconsistentDataException 
	 */
	private void validateCampaigns(DataModelDTO model) throws InconsistentDataException {
		if (model.getCampaigns().stream().anyMatch(c -> StringUtils.isAnyEmpty(c.getId(), c.getTitle()))) throw new InconsistentDataException("Invalid campaign definition", "INVALID_CAMPAIGN_DATA");
		if (model.getCampaigns().stream().anyMatch(c -> c.getFrom() == null)) throw new InconsistentDataException("Invalid campaign definition: missing start date", "NO_START_DATE");
		if (model.getCampaigns().stream().anyMatch(c -> appService.getApp(c.getApplication()).isEmpty())) throw new InconsistentDataException("Invalid campaign definition: non existing app", "NO_APP");
	}


	/**
	 * @param model
	 * @throws InconsistentDataException 
	 */
	private void validateApps(DataModelDTO model) throws InconsistentDataException {
		if (model.getApps().stream().anyMatch(a -> StringUtils.isAnyEmpty(a.getId(), a.getName()))) throw new InconsistentDataException("Invalid apps definition", "INVALID_APP_DATA");
	}

}
