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

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.DayStat;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_DATA;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;
import it.smartcommunitylab.pgazienda.domain.Constants.STAT_FIELD;
import it.smartcommunitylab.pgazienda.service.TrackingDataService;
import it.smartcommunitylab.pgazienda.service.UserService;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;

/**
 * @author raman
 *
 */
@RestController
@RequestMapping("/api")
public class TrackingDataResource {

	private static final Logger log = LoggerFactory.getLogger(TrackingDataResource.class);
	
	@Autowired
	private TrackingDataService dataService;
	@Autowired
	private UserService userService;

	/**
	 * Read all the statistics of the campaign with filters and aggregation
	 * @param campaignId
	 * @param companyId
	 * @param location
	 * @param employeeId
	 * @param timeGroupBy
	 * @param dataGroupBy
	 * @param fields
	 * @param from
	 * @param to
	 * @return List of records representing the stats
	 * @throws IOException
	 * @throws InconsistentDataException
	 */
    @GetMapping("/campaigns/{campaignId}/stats/all")
	public ResponseEntity<List<DayStat>> statistics(
		@PathVariable String campaignId, 
		@RequestParam(required=false) String companyId,
		@RequestParam(required=false) String location,
		@RequestParam(required=false) Set<String> employeeId,			
		@RequestParam(required=false, defaultValue = "month") GROUP_BY_TIME timeGroupBy, 
		@RequestParam(required=false) GROUP_BY_DATA dataGroupBy,
		@RequestParam(required=false, defaultValue = "score") List<STAT_FIELD> fields,
		@RequestParam(required=false, defaultValue = "false") boolean all,
		@RequestParam(required=false) String from, 
		@RequestParam(required=false) String to) throws IOException, InconsistentDataException 
	{
    	if(!userService.isInCampaignRole(campaignId)) {
    		if(StringUtils.isNotBlank(companyId)) {
    	    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_MOBILITY_MANAGER)) 
    	    		throw new SecurityException("Insufficient rights");        		
        	} else {
        		throw new SecurityException("Insufficient rights");	
        	}
    	}
        log.debug("REST request to get statistics");
    	LocalDate toDate = to == null ? LocalDate.now() : LocalDate.parse(to);
    	LocalDate fromDate = from == null ? null : LocalDate.parse(from);
    	return ResponseEntity.ok(dataService.statistics(campaignId, companyId, location, employeeId, timeGroupBy, dataGroupBy, fields, fromDate, toDate, all));
	}

	/**
	 * Read all the statistics of the campaign with filters and aggregation
	 * @param campaignId
	 * @param companyId
	 * @param location
	 * @param employeeId
	 * @param timeGroupBy
	 * @param dataGroupBy
	 * @param fields
	 * @param from
	 * @param to
	 * @return List of records representing the stats
	 * @throws IOException
	 * @throws InconsistentDataException
	 */
    @GetMapping("/campaigns/{campaignId}/stats/all/csv")
	public void statisticsCSV(
		@PathVariable String campaignId, 
		@RequestParam(required=false) String companyId,
		@RequestParam(required=false) String location,
		@RequestParam(required=false) Set<String> employeeId,			
		@RequestParam(required=false, defaultValue = "month") GROUP_BY_TIME timeGroupBy, 
		@RequestParam(required=false) GROUP_BY_DATA dataGroupBy,
		@RequestParam(required=false, defaultValue = "score") List<STAT_FIELD> fields,
		@RequestParam(required=false, defaultValue = "false") boolean all,
		@RequestParam(required=false) String from, 
		@RequestParam(required=false) String to,
		HttpServletResponse response) throws IOException, InconsistentDataException 
	{
    	if(!userService.isInCampaignRole(campaignId)) {
    		if(StringUtils.isNotBlank(companyId)) {
    	    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_MOBILITY_MANAGER)) 
    	    		throw new SecurityException("Insufficient rights");        		
        	} else {
        		throw new SecurityException("Insufficient rights");	
        	}
    	}    	
        log.debug("REST request to get statistics");
    	LocalDate toDate = to == null ? LocalDate.now() : LocalDate.parse(to);
    	LocalDate fromDate = from == null ? null : LocalDate.parse(from);
		dataService.csvStatistics(response.getWriter(), campaignId, companyId, location, employeeId, timeGroupBy, dataGroupBy, fields, fromDate, toDate, all);
	}
}
