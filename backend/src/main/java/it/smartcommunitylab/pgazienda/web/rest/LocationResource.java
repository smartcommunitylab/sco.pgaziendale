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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.service.CompanyService;
import it.smartcommunitylab.pgazienda.service.UserService;

/**
 * @author raman
 *
 */
@RestController
@RequestMapping("/api")
public class LocationResource {

	private static final Logger log = LoggerFactory.getLogger(LocationResource.class);
	
	@Autowired
	private CompanyService companyService;
	@Autowired
	private UserService userService;
    /**
     * Create a new location
     * @param company
     * @return
     */
    @PostMapping("/companies/{companyId}/locations")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN+"\")")
	public ResponseEntity<CompanyLocation> createLocation(@PathVariable String companyId, @Valid @RequestBody CompanyLocation location) {
    	log.debug("Creating a location {} / {}", companyId, location);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
    	return ResponseEntity.ok(companyService.createLocation(companyId, location));
	}

    /**
     * Update basic location info
     * @param companyId
     * @param company
     * @return
     */
    @PutMapping("/companies/{companyId}/locations/{locationId:.*}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN+"\")")
	public ResponseEntity<CompanyLocation> updateLocation(@PathVariable String companyId, @PathVariable String locationId, @Valid @RequestBody CompanyLocation location) {
    	log.debug("Updating a location {} / {}", companyId, locationId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
    	return ResponseEntity.ok(companyService.updateLocation(companyId, location));
	}
    /**
     * Delete a location
     * @param companyId
     * @param locationId
     * @return
     */
    @DeleteMapping("/companies/{companyId}/locations/{locationId:.*}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN  +"\")")
	public ResponseEntity<Void> deleteLocation(@PathVariable String companyId, @PathVariable String locationId) {
    	log.debug("Deleting a location {} / {}", companyId, locationId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
    	companyService.deleteLocation(companyId, locationId);
    	return ResponseEntity.ok(null);
	}

    /**
     * Read all company locations
     * @param companyId
     * @return
     */
    @GetMapping("/companies/{companyId}/locations")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN  + "\", \""+Constants.ROLE_MOBILITY_MANAGER +"\")")
	public ResponseEntity<List<CompanyLocation>> getLocations(@PathVariable String companyId) {
    	log.debug("Read locations {}", companyId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN, Constants.ROLE_MOBILITY_MANAGER)) throw new SecurityException("Insufficient rights");
    	return ResponseEntity.ok(companyService.readlocations(companyId));
	}

    @PostMapping("/companies/{companyId}/locations/csv")
    public ResponseEntity<Void> uploadLocations(@PathVariable String companyId, @RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
    	log.debug("import csv locations {}", companyId);
    	companyService.importLocations(companyId, file.getInputStream());
    	return ResponseEntity.ok(null);
    }

    
}
