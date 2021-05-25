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

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.Subscription;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.web.rest.errors.RepeatingSubscriptionException;

/**
 * @author raman
 *
 */
@Service
public class CampaignService {

	private static final Logger logger = LoggerFactory.getLogger(CampaignService.class);

	@Autowired
	private CompanyRepository companyRepo;
	@Autowired
	private CampaignRepository campaignRepo;
	@Autowired
	private EmployeeRepository employeeRepo;
	@Autowired
	private UserService userService;
	@Autowired
	private TrackingDataService trackingDataService;
	
	/**
	 * List of all companies, paginated
	 * @param page
	 * @return
	 */
	public Page<Campaign> getCampaigns(Pageable page) {
		return campaignRepo.findAll(page);
	}

	/**
	 * Single company by ID
	 * @param id
	 * @return
	 */
	public Optional<Campaign> getCampaign(String id) {
		return campaignRepo.findById(id);
	}

	/**
	 * Publicly available campaign: active in the current period
	 * @return
	 */
	public Page<Campaign> getPublicCampaigns(Pageable pageable) {
		return campaignRepo.findActive(LocalDate.now(), pageable);
	}
	/**
	 * List of campaigns the current user is subscribed to
	 * @return
	 */
	public List<Campaign> getUserCampaigns() {
		User user = userService.getUserWithAuthorities().orElse(null);
		if (user != null) {
			UserRole role = user.findRole(Constants.ROLE_APP_USER).orElse(null);
			if (role != null) {
				return campaignRepo.findByIdIn(role.getSubscriptions().stream().map(s -> s.getCampaign()).collect(Collectors.toSet()));
			}
		}
		return Collections.emptyList();
	}
	
	/**
	 * Subscribe a campaign for the current user. The user should exist, the campaign should 
	 * exist, the company should exist and should adhere to the campaign 
	 * @param key
	 * @param companyCode
	 * @param campaignId
	 * @throws RepeatingSubscriptionException 
	 */
	public void subscribe(String key, String companyCode, String campaignId) throws RepeatingSubscriptionException {
		// find user
		User user = userService.getUserWithAuthorities().orElse(null);
		if (user == null) throw new IllegalArgumentException("Invalid user");
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new IllegalArgumentException("Invalid campaign");
		Company company = companyRepo.findOneByCode(companyCode).orElse(null);
		if (company == null || company.getCampaigns() == null || !company.getCampaigns().contains(campaignId)) throw new IllegalArgumentException("Invalid company");
		// app user role
		UserRole role = user.findRole(Constants.ROLE_APP_USER).orElse(null);

		// control key already used
		List<User> registered = userService.getUserByEmployeeCode(campaignId, companyCode, key);
		if (registered != null && registered.size() > 0 && !registered.get(0).getId().equals(user.getId())) {
			throw new IllegalArgumentException("User code already in use: " + campaignId +", " + companyCode + ", " + key);			
		}
		
		;// not yet subscribed
		if (role == null || role.getSubscriptions().stream().noneMatch(s -> s.getCampaign().equals(campaignId))) {
			Employee employee = employeeRepo.findOneByCompanyIdAndCode(company.getId(), key).orElse(null);
			if (employee == null ) throw new IllegalArgumentException("Invalid user key");
			if (employee.getCampaigns() == null) employee.setCampaigns(new LinkedList<>());
			
			// TODO check
			boolean hasCampaignData = trackingDataService.hasCampaignData(user.getPlayerId(), campaignId);
			if (hasCampaignData) {
				throw new RepeatingSubscriptionException("Previous subscription exists: " + campaignId +", "+user.getPlayerId());
			}
			
			if (!employee.getCampaigns().contains(campaignId)) {
				employee.getCampaigns().add(campaignId);
				employeeRepo.save(employee);
			}
			Subscription s = new Subscription();
			s.setCampaign(campaignId);
			s.setKey(key);
			s.setCompanyCode(companyCode);
			userService.addAppSubscription(user.getId(), s);
		}
	}
	
	/**
	 * Unsubscribe a campaign for the current user. The user should exist
	 * @param campaignId
	 */
	public void unsubscribe(String campaignId) {
		// find user
		User user = userService.getUserWithAuthorities().orElse(null);
		if (user == null) throw new IllegalArgumentException("Invalid user");
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) return;
		// app user role
		UserRole role = user.findRole(Constants.ROLE_APP_USER).orElse(null);
		// not yet subscribed
		if (role != null && role.getSubscriptions().stream().anyMatch(s -> s.getCampaign().equals(campaignId))) {
			role.getSubscriptions().forEach(s -> {
				Company company = companyRepo.findOneByCode(s.getCompanyCode()).orElse(null);
				if (company != null) {
					Employee employee = employeeRepo.findOneByCompanyIdAndCode(company.getId(), s.getKey()).orElse(null);					
					if (employee != null && employee.getCampaigns().contains(campaignId)) {
						employee.getCampaigns().remove(campaignId);
						employeeRepo.save(employee);
					}
					userService.removeAppSubscription(user.getId(), s.getKey(), s.getCompanyCode(), campaignId);
				}
			});			
		}
	}
	
	/**
	 * List of company campaigns
	 * @param id
	 * @return
	 */
	public List<Campaign> getCompanyCampaigns(String id) {
		Company company = companyRepo.findById(id).orElse(null);
		if (company != null) {
			return campaignRepo.findByIdIn(company.getCampaigns());
		}
		return Collections.emptyList();
	}

	/**
	 * @param campaign
	 * @return
	 */
	public Campaign saveCampaign(Campaign campaign) {
		return campaignRepo.save(campaign);
	}
	
	/**
	 * @param campaignId
	 * @return
	 */
	public Campaign resetCampaign(String campaignId) {
		userService.cleanSubscriptions(campaignId);
		List<Employee> employees = employeeRepo.findByCampaigns(campaignId);
		employees.forEach(e -> e.getCampaigns().remove(campaignId));
		employeeRepo.saveAll(employees);
		trackingDataService.cleanCampaign(campaignId);
		return getCampaign(campaignId).orElse(null);
	}

	/**
	 * @param campaignId
	 * @return
	 */
	public void deleteCampaign(String campaignId) {
		campaignRepo.deleteById(campaignId);
	}
	
	/**
	 * Inverse active state of the campaign
	 * @param campaignId
	 * @param val 
	 * @return
	 */
	public Campaign toggleState(String campaignId, boolean val) {
		campaignRepo.findById(campaignId).ifPresent(campaign -> {
			campaign.setActive(val);
			campaignRepo.save(campaign);
		});
		return campaignRepo.findById(campaignId).orElse(null);
	}

	@Scheduled(fixedDelay=1000*60*60*24)
	public void syncSubscriptions() {
		List<Company> companies = companyRepo.findAll();
		companies.forEach(company -> {
			List<String> campaignIds = company.getCampaigns();
			Set<String> all = employeeRepo.findByCompanyId(company.getId()).stream().map(e -> e.getCode()).collect(Collectors.toSet());
			campaignIds.forEach(campaignId -> {
				Set<String> participating = employeeRepo.findByCompanyIdAndCampaigns(company.getId(), campaignId).stream().map(e -> e.getCode()).collect(Collectors.toSet());
				all.forEach(code -> {
					// employee potentially subscribed, but not having campaigns data filled 
					if (!participating.contains(code)) {
						List<User> user = userService.getUserByEmployeeCode(campaignId, company.getCode(), code);
						if (user != null && !user.isEmpty()) {
							logger.info("Non-sychronized employee found: {}, {}", company.getCode(), code);
							Optional<Employee> employee = employeeRepo.findOneByCompanyIdAndCode(company.getId(), code);
							if (employee.isPresent()) {
								Employee obj = employee.get();
								obj.getCampaigns().add(campaignId);
								employeeRepo.save(obj);
								logger.info("Non-sychronized employee added campaign: {}, {}: {}", company.getCode(), code, campaignId);
							}
						}
					}
				});
			});
		});
	}

}
