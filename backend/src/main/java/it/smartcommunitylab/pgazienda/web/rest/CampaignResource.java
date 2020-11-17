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

package it.smartcommunitylab.pgazienda.web.rest;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.service.CampaignService;
import it.smartcommunitylab.pgazienda.service.CompanyService;
import it.smartcommunitylab.pgazienda.service.UserService;

/**
 * @author raman
 *
 */
@RestController
@RequestMapping("/api")
public class CampaignResource {

	private static final Logger log = LoggerFactory.getLogger(CampaignResource.class);
	
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CampaignService campaignService;
	@Autowired
	private UserService userService;
    /**
     * Create a new campaign
     * @param company
     * @return
     */
    @PostMapping("/campaigns")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public ResponseEntity<Campaign> createCampaign(@Valid @RequestBody Campaign campaign) {
    	log.debug("Creating a campaign {} ", campaign);
    	return ResponseEntity.ok(campaignService.createCampaign(campaign));
	}

    /**
     * List campaigns
     * @param company
     * @return
     */
    @GetMapping("/campaigns")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public ResponseEntity<Page<Campaign>> listCampaign(Pageable pageable) {
    	log.debug("List all campaigns ");
    	return ResponseEntity.ok(campaignService.getCampaigns(pageable));
	}
    /**
     * Update basic campaign info
     * @param company
     * @return
     */
    @PutMapping("//campaigns/{campaignId:.*}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public ResponseEntity<Campaign> updateCampaign( @PathVariable String campaignId, @Valid @RequestBody Campaign campaign) {
    	log.debug("Updating a campaign {}", campaignId);
    	return ResponseEntity.ok(campaignService.updateCampaign(campaign));
	}
    /**
     * Delete a campaign
     * @param campaignId
     * @return
     */
    @DeleteMapping("/campaigns/{campaignId:.*}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public ResponseEntity<Void> deleteCampaign(@PathVariable String campaignId) {
    	log.debug("Updating a campaign {}", campaignId);
    	campaignService.deleteCampaign(campaignId);
    	return ResponseEntity.ok(null);
	}
    /**
     * Activate / deactivate a campaign
     * @param companyId
     * @param campaignId
     * @return
     */
    @PutMapping("/campaigns/{campaignId:.*}/active/{val}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN  +"\")")
	public ResponseEntity<Void> changeActiveState(@PathVariable String campaignId, @PathVariable boolean val) {
    	log.debug("Changing a campaign state {}", campaignId);
    	campaignService.toggleState(campaignId, val);
    	return ResponseEntity.ok(null);
	}
    /**
     * Read all company campaigns
     * @param companyId
     * @return
     */
    @GetMapping("/companies/{companyId}/campaigns")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN  + "\", \""+Constants.ROLE_MOBILITY_MANAGER +"\")")
	public ResponseEntity<List<Campaign>> getCampaigns(@PathVariable String companyId) {
    	log.debug("Read campaigns {}", companyId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN, Constants.ROLE_MOBILITY_MANAGER)) throw new SecurityException("Insufficient rights");
    	return ResponseEntity.ok(campaignService.getCompanyCampaigns(companyId));
	}

    /**
     * Read all active campaigns
     * @param companyId
     * @return
     */
    @GetMapping("/public/campaigns")
	public ResponseEntity<Page<Campaign>> getPublicCampaigns(Pageable pageable) {
    	log.debug("Read campaigns");
    	return ResponseEntity.ok(campaignService.getPublicCampaigns(pageable));
	}
    
    
    /**
     * Read all campaigns of the curent user
     * @param companyId
     * @return
     */
    @GetMapping("/campaigns/me")
	public ResponseEntity<List<Campaign>> getUserCampaigns() {
    	log.debug("Read campaigns");
    	return ResponseEntity.ok(campaignService.getUserCampaigns());
	}
    /**
     * Associate campaign to company
     * @param company
     * @return
     */
    @PutMapping("/companies/{companyId}/campaigns/{campaignId:.*}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN+"\")")
	public ResponseEntity<Company> createCompanyCampaign(@PathVariable String companyId, @PathVariable String campaignId) {
    	log.debug("Adding a campaign to company {} / {}", companyId, campaignId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
    	return ResponseEntity.ok(companyService.assignCampaign(companyId, campaignId));
	}

    /**
     * Delete a campaign
     * @param companyId
     * @param campaignId
     * @return
     */
    @DeleteMapping("/companies/{companyId}/campaigns/{campaignId:.*}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN +"\")")
	public ResponseEntity<Void> deleteCompanyCampaign(@PathVariable String companyId, @PathVariable String campaignId) {
    	log.debug("Deleting a campaign from company {} / {}", companyId, campaignId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
    	companyService.deleteCampaign(companyId, campaignId);
    	return ResponseEntity.ok(null);
	}
    
    /**
     * Subscribe to campaign 
     * @param company
     * @return
     */
    @PutMapping("/campaigns/{campaignId:.*}/subscribe/{companyCode}/{key}")
	public ResponseEntity<Void> subscribeCampaign(@PathVariable String campaignId, @PathVariable String companyCode, @PathVariable String key) {
    	log.debug("Subscriving to campaign {} / {} / {}", campaignId, companyCode, key);
    	campaignService.subscribe(key, companyCode, campaignId);
    	return ResponseEntity.ok(null);
	}
    
    /**
     * Subscribe to campaign 
     * @param company
     * @return
     */
    @DeleteMapping("/campaigns/{campaignId:.*}/unsubscribe/{companyCode}/{key}")
	public ResponseEntity<Void> unsubscribeCampaign(@PathVariable String campaignId, @PathVariable String companyCode, @PathVariable String key) {
    	log.debug("Subscriving to campaign {} / {} / {}", campaignId, companyCode, key);
    	campaignService.unsubscribe(key, companyCode, campaignId);
    	return ResponseEntity.ok(null);
	}
}
