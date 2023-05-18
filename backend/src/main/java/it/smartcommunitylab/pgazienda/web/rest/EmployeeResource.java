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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.Employee;
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
public class EmployeeResource {

	private static final Logger log = LoggerFactory.getLogger(EmployeeResource.class);
	
	@Autowired
	private CompanyService companyService;
	@Autowired
	private UserService userService;
    /**
     * Create a new employee
     * @param company
     * @return
     * @throws BadRequestAlertException 
     * @throws InconsistentDataException 
     */
    @PostMapping("/companies/{companyId}/employees")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN + "\", \""+Constants.ROLE_MOBILITY_MANAGER+"\")")
	public ResponseEntity<Employee> createEmployee(@PathVariable String companyId, @Valid @RequestBody Employee employee) throws BadRequestAlertException, InconsistentDataException {
    	log.debug("Creating a employee {} / {}", companyId, employee);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
    	if (employee.getId() != null) {
    		throw new BadRequestAlertException("A new emplyee cannot already have an ID");
    	}
    	return ResponseEntity.ok(companyService.createEmployee(companyId, employee));
	}

    /**
     * Update basic employee info
     * @param companyId
     * @param company
     * @return
     */
    @PutMapping("/companies/{companyId}/employees/{employeeId:.*}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN + "\", \""+Constants.ROLE_MOBILITY_MANAGER+"\")")
	public ResponseEntity<Employee> updateEmployee(@PathVariable String companyId, @PathVariable String employeeId, @Valid @RequestBody Employee employee) {
    	log.debug("Updating a employee {} / {}", companyId, employeeId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
    	return ResponseEntity.ok(companyService.updateEmployee(companyId, employee));
	}
    /**
     * Delete a employee
     * @param companyId
     * @param employeeId
     * @return
     */
    @DeleteMapping("/companies/{companyId}/employees/{employeeId:.*}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN + "\", \""+Constants.ROLE_MOBILITY_MANAGER  +"\")")
	public ResponseEntity<Void> deleteEmployee(@PathVariable String companyId, @PathVariable String employeeId) {
    	log.debug("Deleting a employee {} / {}", companyId, employeeId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
    	companyService.deleteEmployee(companyId, employeeId);
    	return ResponseEntity.ok(null);
	}

    /**
     * Read all company employees
     * @param companyId
     * @return
     */
    @GetMapping("/companies/{companyId}/employees")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN  + "\", \""+Constants.ROLE_MOBILITY_MANAGER +"\")")
	public ResponseEntity<Page<Employee>> getEmployees(@PathVariable String companyId, @RequestParam(required=false) String location, Pageable pageable) {
    	log.debug("Read employees {}", companyId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN, Constants.ROLE_MOBILITY_MANAGER)) throw new SecurityException("Insufficient rights");
    	return ResponseEntity.ok(companyService.findEmployees(companyId, location, pageable));
	}

    @PostMapping("/companies/{companyId}/employees/csv")
    public ResponseEntity<Void> uploadEmployees(@PathVariable String companyId, @RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
    	log.debug("import csv empoloyees {}", companyId);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
    	companyService.importEmployees(companyId, file.getInputStream());
    	return ResponseEntity.ok(null);
    }

}
