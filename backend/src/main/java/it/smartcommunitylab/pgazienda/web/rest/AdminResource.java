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

import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.dto.DataModelDTO;
import it.smartcommunitylab.pgazienda.service.AdminService;
import it.smartcommunitylab.pgazienda.service.TrackingDataService;

/**
 * @author raman
 *
 */
@RestController
@RequestMapping("/api")
public class AdminResource {

	@Autowired
	private AdminService service;
	@Autowired
	private TrackingDataService trackingDataService;
	
	@PostMapping("/admin/load")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<Void> uploadModel(@Valid @RequestBody DataModelDTO model) {
		service.loadData(model);
		return ResponseEntity.ok(null);
	}

	@GetMapping("/admin/datasync")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<Void> syncTrackingData() {
		trackingDataService.synchronizeApps();
		return ResponseEntity.ok(null);
	}

	@GetMapping("/admin/datasync/{campaignId}/{companyId}/{from}/{to}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<Void> syncCompanyTrackingData(@PathVariable String campaignId, @PathVariable String companyId, @PathVariable String from, @PathVariable String to) {
		trackingDataService.syncCompanyData(campaignId, companyId, LocalDate.parse(from), LocalDate.parse(to));
		return ResponseEntity.ok(null);
	}

}
