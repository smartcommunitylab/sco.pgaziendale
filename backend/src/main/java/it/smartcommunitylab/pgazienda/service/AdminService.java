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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.LegacyPlayerMapping;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.dto.DataModelDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO;
import it.smartcommunitylab.pgazienda.dto.TrackValidityDTO;
import it.smartcommunitylab.pgazienda.repository.LegacyPlayerMappingRepository;
import it.smartcommunitylab.pgazienda.security.ExternalUserDetailsService;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import it.smartcommunitylab.pgazienda.service.errors.RepeatingSubscriptionException;

/**
 * Global data bootstrap helper service
 * @author raman
 *
 */
@Service
public class AdminService {
	
	private final static Logger log = LoggerFactory.getLogger(AdminService.class);
	
	@Autowired
	private PGAppService appService;
	@Autowired
	private UserService userService;
	@Autowired
	private ExternalUserDetailsService extUserService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CampaignService campaignService;
	@Autowired
	private TrackingDataService trackService;
	@Autowired
	private LegacyPlayerMappingRepository legacyRepo;
	
	private static Map<String, String> legacyIds = new HashMap<>();
	
	@Value("${app.legacyCampaign}")
	private String legacyCampaignId;

	@PostConstruct
	public void initLegacyData() {
		try {
			legacyIds = new HashMap<>();
			legacyRepo.findById(legacyCampaignId).ifPresent(lpm -> {
				legacyIds.putAll(lpm.getPlayers());
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
				c.setLocations(Collections.emptyList());
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


	/**
	 * Subscribe a user with the given campaign, player id, company key, and employee code
	 * @param campaignId
	 * @param playerId
	 * @param companyKey
	 * @param code
	 * @throws InconsistentDataException
	 * @throws RepeatingSubscriptionException
	 */
	public void subscribeCampaign(String campaignId, String playerId, String companyKey, String code) throws InconsistentDataException, RepeatingSubscriptionException {
		User user = extUserService.checkOrRegister(playerId);
		campaignService.subscribeUser(user, code, companyKey, campaignId, true);
	} 
	
	/**
	 * Unsubscribe specified user
	 * @param campaignId
	 * @param playerId
	 */
	public void unsubscribeCampaign(String campaignId, String playerId) {
		String legacyPlayerId = checkLegacyPlayer(playerId, campaignId);
		User user = userService.getUserByPlayerId(legacyPlayerId);
		if (user != null) {
			campaignService.unsubscribeUser(user, campaignId);
		}
	}
	
	public TrackValidityDTO validateTrack(String playerId, String campaignId, TrackDTO track) {
		try {
			String legacyPlayerId = checkLegacyPlayer(playerId, campaignId);
			return trackService.validate(campaignId, legacyPlayerId, track);
		} catch (InconsistentDataException e) {
			log.error("Error validating: " + e.getMessage());
			return new TrackValidityDTO(e.getDetails());
		}
	}


	/**
	 * @param playerId
	 * @param campaignId
	 * @return
	 */
	private String checkLegacyPlayer(String playerId, String campaignId) {
		if (legacyIds.containsKey(playerId)) {
			String legacyId =legacyIds.get(playerId); 
			userService.markAsUpgraded(legacyId, campaignId);
			return legacyId;
		}
		return playerId;
	}

	
	public String getLegacyPlayer(String playerId, String campaignId) {
		if (legacyIds.containsKey(playerId)) {
			String legacyId =legacyIds.get(playerId); 
			return legacyId;
		}
		return playerId;
	}

	/**
	 * @param playerId
	 * @param campaignId
	 * @param trackId
	 * @return
	 */
	public TrackValidityDTO invalidateTrack(String playerId, String campaignId, String trackId) {
		try {
			return trackService.invalidate(campaignId, playerId, trackId);
		} catch (InconsistentDataException e) {
			return new TrackValidityDTO(e.getDetails());
		}
	}


	/**
	 * @param playerId
	 * @param campaignId
	 * @param trackId
	 * @param inc
	 * @return
	 */
	public TrackValidityDTO update(String playerId, String campaignId, String trackId, Double inc) {
		try {
			return trackService.update(campaignId, playerId, trackId, inc);
		} catch (InconsistentDataException e) {
			return new TrackValidityDTO(e.getDetails());
		}
	}

	public void loadLegacyData(String campaignId, InputStream is) {
		LegacyPlayerMapping lpm = new LegacyPlayerMapping();
		lpm.setCampaignId(campaignId);
		lpm.setPlayers(new HashMap<>());
		new BufferedReader(new InputStreamReader(is)).lines().forEach(l -> {
			String[] arr = l.split(",");
			lpm.getPlayers().put(arr[0].trim(), arr[1].trim());
		});
		legacyRepo.save(lpm);
		initLegacyData();
	}

}
