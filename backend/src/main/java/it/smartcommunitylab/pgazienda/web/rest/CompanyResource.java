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
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.service.CompanyService;
import it.smartcommunitylab.pgazienda.service.UserService;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import it.smartcommunitylab.pgazienda.web.rest.errors.BadRequestAlertException;

/**
 * @author raman
 *
 */
@RestController
@RequestMapping("/api")
public class CompanyResource {

    private final Logger log = LoggerFactory.getLogger(CompanyResource.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;
    
	/**
	 * List all companies
	 * @param pageable
	 * @return
	 */
    @GetMapping("/companies")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN+"\")")
	public ResponseEntity<Page<Company>> getCompanies(Pageable pageable) {
    	log.debug("List companies");
		return ResponseEntity.ok(companyService.getCompanies(pageable));
	}

    /**
     * List of companies assigned to the campaign
     * @param campaignId
     * @return
     */
    @GetMapping("/companies/campaign/{campaignId}")
    public ResponseEntity<List<Company>> getCampaignCompanies(@PathVariable String campaignId) {
    	return ResponseEntity.ok(companyService.getCampaignCompanies(campaignId));
    }
    
    /**
     * List of companies assigned to the campaign
     * @param campaignId
     * @return
     */
    @GetMapping("/public/campaigns/{campaignId}/companies")
    public ResponseEntity<List<Company>> getPublicCampaignCompanies(@PathVariable String campaignId) {
    	return ResponseEntity.ok(companyService.getCampaignCompanies(campaignId));
    }

    /**
     * Return specific company of the campaign
     * @param campaignId
     * @return
     */
    @GetMapping("/public/campaigns/{campaignId}/companies/{code}")
    public ResponseEntity<Company> getPublicCampaignCompany(@PathVariable String campaignId, @PathVariable String code) {
    	return ResponseEntity.ok(companyService.findByCode(code).orElse(null));
    }
    
    /**
     * Create a new company
     * @param company
     * @return
     * @throws BadRequestAlertException 
     * @throws InconsistentDataException 
     */
    @PostMapping("/companies")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN+"\")")
	public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) throws BadRequestAlertException, InconsistentDataException {
    	log.debug("Creating company {}", company);
    	if (company.getId() != null) {
    		throw new BadRequestAlertException("A new company cannot already have an ID");
    	}
		return ResponseEntity.ok(companyService.createCompany(company));
	}
    /**
     * Read a single company info
     * @param companyId
     * @return
     */
    @GetMapping("/companies/{companyId:.*}")
    //@PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN  + "\", \""+Constants.ROLE_MOBILITY_MANAGER +"\")")
	public ResponseEntity<Company> getCompany(@PathVariable String companyId) {
    	log.debug("Reading company {}", companyId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_TERRITORY_MANAGER, Constants.ROLE_COMPANY_ADMIN, Constants.ROLE_MOBILITY_MANAGER)) throw new SecurityException("Insufficient rights");
    	return ResponseEntity.of(companyService.getCompany(companyId));
	}

    /**
     * Update basic company info
     * @param companyId
     * @param company
     * @return
     */
    @PutMapping("/companies/{companyId:.*}")
    //@PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN+"\")")
	public ResponseEntity<Company> updateCompany(@PathVariable String companyId, @RequestBody Company company) {
    	log.debug("Updating company {}", companyId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_TERRITORY_MANAGER, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
    	company.setId(companyId);
		return ResponseEntity.of(companyService.updateCompany(company));
	}

    /**
     * Update company verification state
     * @param companyId
     * @param company
     * @return
     */
    @PutMapping("/companies/{companyId}/{state}")
    //@PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN+"\")")
	public ResponseEntity<Company> updateCompanyState(@PathVariable String companyId, @PathVariable Boolean state) {
    	log.debug("Updating company state {}, state {}", companyId, state);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_TERRITORY_MANAGER, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
		return ResponseEntity.ok(companyService.updateCompanyState(companyId, state));
	}
    /**
     * Delete a company
     * @param companyId
     * @return
     */
    @DeleteMapping("/companies/{companyId:.*}")
    //@PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN+"\")")
	public ResponseEntity<Void> deleteCompany(@PathVariable String companyId) {
    	log.debug("Deleting company {}", companyId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_TERRITORY_MANAGER, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
		companyService.deleteCompany(companyId);
		return ResponseEntity.ok(null);
	}
}
