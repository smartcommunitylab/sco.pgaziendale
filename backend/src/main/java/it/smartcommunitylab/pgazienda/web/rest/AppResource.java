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
import it.smartcommunitylab.pgazienda.domain.PGApp;
import it.smartcommunitylab.pgazienda.service.PGAppService;

/**
 * @author raman
 *
 */
@RestController
@RequestMapping("/api")
public class AppResource {

    private final Logger log = LoggerFactory.getLogger(AppResource.class);

    @Autowired
    private PGAppService service;
    
	/**
	 * List all companies
	 * @param pageable
	 * @return
	 */
    @GetMapping("/apps")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN+"\")")
	public ResponseEntity<List<PGApp>> getApps() {
    	log.debug("List apps");
		return ResponseEntity.ok(service.getApps());
	}
    
    
	/**
	 * List all companies
	 * @param pageable
	 * @return
	 */
    @GetMapping("/public/apps")
	public ResponseEntity<List<PGApp>> getPublicApps() {
    	log.debug("List apps");
		return ResponseEntity.ok(service.getApps());
	}
    /**
     * Create a new company
     * @param company
     * @return
     */
    @PostMapping("/apps")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN+"\")")
	public ResponseEntity<PGApp> createApp(@Valid @RequestBody PGApp app) {
    	log.debug("Creating app {}", app);
		return ResponseEntity.ok(service.saveApp(app));
	}
    /**
     * Update basic company info
     * @param companyId
     * @param company
     * @return
     */
    @PutMapping("/apps/{appId:.*}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN+"\")")
	public ResponseEntity<PGApp> updateCompany(@PathVariable String appId, @RequestBody PGApp app) {
    	log.debug("Updating app {}", appId);
    	app.setId(appId);
		return ResponseEntity.ok(service.saveApp(app));
	}
    /**
     * Delete a company
     * @param companyId
     * @return
     */
    @DeleteMapping("/apps/{appId:.*}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN+"\")")
	public ResponseEntity<Void> deleteCompany(@PathVariable String appId) {
    	log.debug("Deleting app {}", appId);
		service.deleteApp(appId);
		return ResponseEntity.ok(null);
	}
}
