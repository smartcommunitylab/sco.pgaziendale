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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.dto.DataModelDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO;
import it.smartcommunitylab.pgazienda.dto.TrackValidityDTO;
import it.smartcommunitylab.pgazienda.service.AdminService;
import it.smartcommunitylab.pgazienda.service.CampaignService;
import it.smartcommunitylab.pgazienda.service.TrackingDataService;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import it.smartcommunitylab.pgazienda.service.errors.RepeatingSubscriptionException;

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
	@Autowired
	private CampaignService campaignService;
	
	@PostMapping("/admin/load")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<Void> uploadModel(@Valid @RequestBody DataModelDTO model) throws InconsistentDataException {
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

	@PostMapping("/admin/subscribe/{campaignId}/{playerId}/{companyKey}/{code}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<Void> subscribeCampaign(
			@PathVariable String campaignId, 
			@PathVariable String playerId, 
			@PathVariable String companyKey,
			@PathVariable String code) throws InconsistentDataException, RepeatingSubscriptionException 
	{
		service.subscribeCampaign(campaignId, playerId, companyKey, code);
		return ResponseEntity.ok(null);
	}

	@PostMapping("/admin/campaignsync")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<Void> syncCampaigns() {
		campaignService.syncExternalCampaigns();
		return ResponseEntity.ok(null);
	}
	
	@PostMapping("/admin/unsubscribe/{campaignId}/{playerId}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<Void> unsubscribeCampaign(
			@PathVariable String campaignId, 
			@PathVariable String playerId) throws InconsistentDataException, RepeatingSubscriptionException 
	{
		service.unsubscribeCampaign(campaignId, playerId);
		return ResponseEntity.ok(null);
	}

	@PostMapping("/admin/validate/{campaignId}/{playerId}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<TrackValidityDTO> validate(@PathVariable String campaignId, 
			@PathVariable String playerId, @RequestBody TrackDTO body) 
	{
		return ResponseEntity.ok(service.validateTrack(playerId, campaignId, body));
	}

	@PutMapping("/admin/invalidate/{campaignId}/{playerId}/{trackId}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<TrackValidityDTO> invalidate(@PathVariable String campaignId, 
			@PathVariable String playerId, @PathVariable String trackId) 
	{
		return ResponseEntity.ok(service.invalidateTrack(playerId, campaignId, trackId));
	}

	@PutMapping("/admin/update/{campaignId}/{playerId}/{trackId}/{inc}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<TrackValidityDTO> update(@PathVariable String campaignId, 
			@PathVariable String playerId, @PathVariable String trackId, @PathVariable Double inc) 
	{
		return ResponseEntity.ok(service.update(playerId, campaignId, trackId, inc));
	}


    @PostMapping("/admin/legacy/{campaignId}/csv")
    public ResponseEntity<Void> uploadLegacy(@PathVariable String campaignId, @RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
    	service.loadLegacyData(campaignId, file.getInputStream());
    	return ResponseEntity.ok(null);
    }
}
