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

package it.smartcommunitylab.pgazienda.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.repository.PGAppRepository;
import it.smartcommunitylab.pgazienda.repository.UserRepository;

/**
 * @author raman
 *
 */
@Service
public class CompanyService {

	@Autowired
	private CompanyRepository companyRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private EmployeeRepository employeeRepo;
	@Autowired
	private PGAppRepository pgappRepo;
	
	/**
	 * List of all companies, paginated
	 * @param page
	 * @return
	 */
	public Page<Company> getCompanies(Pageable page) {
		return companyRepo.findAll(page);
	}

	/**
	 * Single company by ID
	 * @param id
	 * @return
	 */
	public Optional<Company> getCompany(String id) {
		return companyRepo.findById(id);
	}
	
	/**
	 * @param id
	 * @return list of all company user with management role (company admin or mobility manager)
	 */
	public List<User> getCompanyUsers(String id) {
		List<User> res = userRepo.findByCompanyId(id);
		res.forEach(u -> u.setRoles(
				u.getRoles().stream().filter(r -> id.equals(r.getCompanyId()))
				.collect(Collectors.toList())));
		return res;
	}
	/**
	 * Find list of company employees
	 * @param id
	 * @param locationId
	 * @return
	 */
	public List<Employee> findEmployees(String id, String locationId) {
		if (StringUtils.isEmpty(locationId)) return employeeRepo.findByCompanyId(id);
		else return employeeRepo.findByCompanyIdAndLocation(id, locationId);
	}
	
	/**
	 * @param company
	 * @return
	 */
	public Company createCompany(Company company) {
		return companyRepo.save(company);
	}

	/**
	 * @param companyId
	 * @return
	 */
	public void deleteCompany(String companyId) {
		companyRepo.deleteById(companyId);;
	}

	/**
	 * Update profile data only
	 * @param company
	 * @return
	 */
	public Optional<Company> updateCompany(Company company) {
		Company old = companyRepo.findById(company.getId()).orElse(null);
		if (old != null) {
			old.setAddress(company.getAddress());
			old.setContactEmail(company.getContactEmail());
			old.setContactPhone(company.getContactPhone());
			old.setEnabledApps(company.getEnabledApps());
			old.setLogo(company.getLogo());
			old.setName(company.getName());
			old.setWeb(company.getWeb());
			return Optional.of(companyRepo.save(old));
		}
		return Optional.empty();
	}

	/**
	 * @param companyId
	 * @param campaignId
	 * @return
	 */
	public Company assignCampaign(String companyId, String campaignId) {
		Company c = getCompany(companyId).orElse(null);
		if (c != null) {
			if (!c.getCampaigns().contains(campaignId)) {
				c.getCampaigns().add(campaignId);
				companyRepo.save(c);
			}
		}
		return c;
	}

	/**
	 * @param companyId
	 * @param campaignId
	 * @return
	 */
	public void deleteCampaign(String companyId, String campaignId) {
		Company c = getCompany(companyId).orElse(null);
		if (c != null) {
			if (c.getCampaigns().remove(campaignId)) {
				companyRepo.save(c);
			}
		}
	}


	/**
	 * @param companyId
	 * @param location
	 * @return
	 */
	public CompanyLocation createLocation(String companyId, CompanyLocation location) {
		companyRepo.findById(companyId).ifPresent(company -> {
			if (company.getLocations() == null) company.setLocations(Collections.singletonList(location));
			else {
				int idx = company.getLocations().indexOf(location);
				if (idx >= 0) company.getLocations().set(idx, location);
				else company.getLocations().add(location);
			}
			companyRepo.save(company);
		});
		return location;
	}

	/**
	 * @param companyId
	 * @param campaign
	 * @return
	 */
	public CompanyLocation updateLocation(String companyId, CompanyLocation location) {
		companyRepo.findById(companyId).ifPresent(company -> {
			if (company.getLocations() != null) {
				int idx = company.getLocations().indexOf(location);
				if (idx >= 0) {
					company.getLocations().set(idx, location);
					companyRepo.save(company);
				}
			}
		});
		return location;
	}

	/**
	 * @param companyId
	 * @param locationId
	 */
	public void deleteLocation(String companyId, String locationId) {
		companyRepo.findById(companyId).ifPresent(company -> {
			if (company.getLocations() != null) {
				company.getLocations().removeIf(l -> l.getId().equals(locationId));
			}
		});		
	}

	/**
	 * @param companyId
	 * @return
	 */
	public List<CompanyLocation> readlocations(String companyId) {
		Company company = companyRepo.findById(companyId).orElse(null);
		return (company != null) ? company.getLocations() : Collections.emptyList(); 
	}
}
