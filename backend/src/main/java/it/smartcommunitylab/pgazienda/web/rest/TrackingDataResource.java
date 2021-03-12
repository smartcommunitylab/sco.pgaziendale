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
import java.util.List;
import java.util.Optional;

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
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.DayStat;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.service.TrackingDataService;
import it.smartcommunitylab.pgazienda.service.UserService;

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

	@Autowired
	private EmployeeRepository employeeRepo;
	@Autowired
	private CompanyRepository companyRepo;
	
    /**
     * Read all company locations
     * @param companyId
     * @return
     */
    @GetMapping("/campaigns/{campaignId}/stats/me")
	public ResponseEntity<List<DayStat>> getMyStats(@PathVariable String campaignId, @RequestParam(required=false) String from, @RequestParam(required=false) String to, @RequestParam(required=false, defaultValue = "day") String groupBy, @RequestParam(required=false, defaultValue = "false") Boolean withTracks) {
    	log.debug("Read proper stats {}", campaignId);
    	if (from == null) from = LocalDate.now().toString();
    	if (to == null) to = LocalDate.now().toString();
    	
    	return ResponseEntity.ok(dataService.getUserCampaignData(userService.getUserWithAuthorities().get().getPlayerId(), campaignId, LocalDate.parse(from), LocalDate.parse(to), groupBy, withTracks));
	}


    /**
     * Read all company locations
     * @param companyId
     * @return
     */
    @GetMapping("/campaigns/{campaignId}/stats/{employeeId:.*}")
	public ResponseEntity<List<DayStat>> getEmployeeStats(@PathVariable String campaignId, @PathVariable String employeeId, @RequestParam(required=false) String from, @RequestParam(required=false) String to, @RequestParam(required=false, defaultValue = "day") String groupBy, @RequestParam(required=false, defaultValue = "false") Boolean withTracks) {
    	log.debug("Read user stats {} / {}", campaignId, employeeId);
    	if (from == null) from = LocalDate.now().toString();
    	if (to == null) to = LocalDate.now().toString();
    	
    	Employee employee = employeeRepo.findById(employeeId).orElse( null);
    	if (employee == null) throw new IllegalArgumentException("Invalid employee: "+ employeeId);
    	
    	if (!userService.isInCompanyRole(employee.getCompanyId(), Constants.ROLE_COMPANY_ADMIN, Constants.ROLE_MOBILITY_MANAGER)) throw new SecurityException("Insufficient rights");
    	
    	Optional<Company> company = companyRepo.findById(employee.getCompanyId());
    	if (company.isEmpty())  throw new IllegalArgumentException("Invalid company: "+ employee.getCompanyId());
    	
    	List<User> users = userService.getUserByEmployeeCode(campaignId, company.get().getCode(), employee.getCode());
    	if (users == null || users.isEmpty()) throw new IllegalArgumentException("Invalid employee - no subscription: "+ employeeId);
    	
    	return ResponseEntity.ok(dataService.getUserCampaignData(users.get(0).getPlayerId(), campaignId, LocalDate.parse(from), LocalDate.parse(to), groupBy, withTracks));
	}

    /**
     * Read all company locations
     * @param companyId
     * @return
     */
    @GetMapping("/campaigns/{campaignId}/stats/{employeeId:.*}/exist")
	public ResponseEntity<Boolean> hasEmployeeStats(@PathVariable String campaignId, @PathVariable String employeeId) {
    	log.debug("Read user stats {} / {}", campaignId, employeeId);
    	
    	Employee employee = employeeRepo.findById(employeeId).orElse( null);
    	if (employee == null) throw new IllegalArgumentException("Invalid employee: "+ employeeId);
    	
    	if (!userService.isInCompanyRole(employee.getCompanyId(), Constants.ROLE_COMPANY_ADMIN, Constants.ROLE_MOBILITY_MANAGER)) throw new SecurityException("Insufficient rights");
    	
    	Optional<Company> company = companyRepo.findById(employee.getCompanyId());
    	if (company.isEmpty())  throw new IllegalArgumentException("Invalid company: "+ employee.getCompanyId());
    	
    	List<User> users = userService.getUserByEmployeeCode(campaignId, company.get().getCode(), employee.getCode());
    	if (users == null || users.isEmpty()) throw new IllegalArgumentException("Invalid employee - no subscription: "+ employeeId);
    	
    	return ResponseEntity.ok(dataService.hasCampaignData(users.get(0).getPlayerId(), campaignId));
	}

}
