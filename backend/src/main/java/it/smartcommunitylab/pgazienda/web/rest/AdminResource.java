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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
	private CampaignService campaignService;
	
	private static final Logger logger = LoggerFactory.getLogger(AdminResource.class); 
	
	@PostMapping("/admin/load")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<Void> uploadModel(@Valid @RequestBody DataModelDTO model) throws InconsistentDataException {
		service.loadData(model);
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
		logger.info("Validating track for campaign {} player {}", campaignId, playerId);
		return ResponseEntity.ok(service.validateTrack(playerId, campaignId, body));
	}

	@PutMapping("/admin/invalidate/{campaignId}/{playerId}/{trackId}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<TrackValidityDTO> invalidate(@PathVariable String campaignId, 
			@PathVariable String playerId, @PathVariable String trackId) 
	{
		return ResponseEntity.ok(service.invalidateTrack(playerId, campaignId, trackId));
	}

    @PostMapping("/admin/legacy/{campaignId}/csv")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
    public ResponseEntity<Void> uploadLegacy(@PathVariable String campaignId, @RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
    	service.loadLegacyData(campaignId, file.getInputStream());
    	return ResponseEntity.ok(null);
    }
    
	@PostMapping("/admin/unregister/player/{playerId}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN +"\")")
	public @ResponseBody ResponseEntity<Void> unregisterPlayer(
			@PathVariable String playerId) throws InconsistentDataException 
	{
		service.unregisterPlayer(playerId);
		return ResponseEntity.ok(null);
	}
    
}
