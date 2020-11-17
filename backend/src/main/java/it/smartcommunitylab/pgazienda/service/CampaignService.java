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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;

/**
 * @author raman
 *
 */
@Service
public class CampaignService {

	@Autowired
	private CompanyRepository companyRepo;
	@Autowired
	private CampaignRepository campaignRepo;
	@Autowired
	private UserService userService;
	
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
	 * List of company campaigns
	 * @param id
	 * @return
	 */
	public List<Campaign> getCompanyCampaigns(String id) {
		Company company = companyRepo.findById(id).orElse(null);
		if (company != null) {
			campaignRepo.findByIdIn(company.getCampaigns());
		}
		return Collections.emptyList();
	}

	/**
	 * @param campaign
	 * @return
	 */
	public Campaign createCampaign(Campaign campaign) {
		return campaignRepo.save(campaign);
	}

	/**
	 * @param campaign
	 * @return
	 */
	public Campaign updateCampaign(Campaign campaign) {
		return campaignRepo.save(campaign);
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
}
