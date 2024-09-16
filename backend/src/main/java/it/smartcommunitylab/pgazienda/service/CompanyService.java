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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.DayStatRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.service.errors.ImportDataException;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;

/**
 * @author raman
 *
 */
@Service
public class CompanyService {
	private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
	
	@Autowired
	private CompanyRepository companyRepo;
	@Autowired
	private EmployeeRepository employeeRepo;
	@Autowired
	private DayStatRepository statRepo;
	@Autowired
	private UserService userService;
	@Autowired
	private CampaignService campaignService;
	
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	private static final Map<String, Integer> DW = new LinkedHashMap<>();
	static {
		DW.put("lunedi", 1);
		DW.put("lunedì", 1);
		DW.put("lun", 1);
		DW.put("martedi", 2);
		DW.put("martedì", 2);
		DW.put("mar", 2);
		DW.put("mercoledi", 3);
		DW.put("mercoledì", 3);
		DW.put("mer", 3);
		DW.put("giovedi", 4);
		DW.put("giovedì", 4);
		DW.put("gio", 4);
		DW.put("venerdi", 5);
		DW.put("venerdì", 5);
		DW.put("gven", 5);
		DW.put("sabato", 6);
		DW.put("sab", 6);
		DW.put("domenica", 7);
		DW.put("dom", 7);
	}
	
	@PostConstruct
	public void init() {
		companyRepo.findAll().forEach(c -> {
			List<CompanyLocation> locs = c.getLocations();
			if (locs != null) {
				List<CompanyLocation> filtered = locs.stream().filter(l -> l.getId() != null).collect(Collectors.toList());
				if (filtered.size() != locs.size()) {
					c.setLocations(filtered);
					companyRepo.save(c);
				}
			} 
		});
	}
	
	
	/**
	 * List of all companies, paginated
	 * @param page
	 * @return
	 */
	public Page<Company> getCompanies(Pageable page) {
		List<Company> companies = companyRepo.findAll();
		List<Company> result = companies.stream().filter(c -> userService.isCompanyVisible(c)).collect(Collectors.toList());
		return new PageImpl<>(result, page, result.size());
	}

	/**
	 * Single company by ID
	 * @param id
	 * @return
	 */
	public Optional<Company> getCompany(String id) {
		Optional<Company> opt = companyRepo.findById(id);
		if(opt.isPresent()) {
			if(!userService.isCompanyVisible(opt.get()))
				return Optional.empty();
		}
		return opt;
	}
	
	/**
	 * Find list of company employees
	 * @param id
	 * @param locationId
	 * @return
	 */
	public Page<Employee> findEmployees(String id, String locationId, Pageable pageable) {
		if (!StringUtils.hasText(locationId)) return employeeRepo.findByCompanyId(id, pageable);
		else return employeeRepo.findByCompanyIdAndLocation(id, locationId, pageable);
	}
	
	/**
	 * @param company
	 * @return
	 * @throws InconsistentDataException 
	 */
	public Company createCompany(Company company) throws InconsistentDataException {
		if (findByCode(company.getCode()).isPresent()) {
			throw new InconsistentDataException("Duplicate company creation", "INVALID_COMPANY_DATA_DUPLICATE_CODE");
		}
		
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
	public Optional<Company> updateCompany(Company company) throws InconsistentDataException {
		Company old = companyRepo.findById(company.getId()).orElse(null);
		if (old != null) {
			if(!old.getTerritoryId().equals(company.getTerritoryId()) || !old.getCode().equals(company.getCode())) {
				if(userService.countCompanySubscription(old.getCode()) > 0) 
					throw new InconsistentDataException("Company subscriptions", "INVALID_COMPANY_DATA_SUBSCRIPTION");
			}
			Company byCode = findByCode(company.getCode()).orElse(null);
			if (byCode != null && !byCode.getId().equals(company.getId())) {
				throw new InconsistentDataException("Duplicate company creation", "INVALID_COMPANY_DATA_DUPLICATE_CODE");
			}
			old.setAddress(company.getAddress());
			old.setTerritoryId(company.getTerritoryId());
			old.setCode(company.getCode());
			old.setContactEmail(company.getContactEmail());
			old.setContactPhone(company.getContactPhone());
			old.setLogo(company.getLogo());
			old.setName(company.getName());
			old.setWeb(company.getWeb());
			old.setCity(company.getCity());
			old.setCountry(company.getCountry());
			old.setProvince(company.getProvince());
			old.setRegion(company.getRegion());
			old.setStreetNumber(company.getStreetNumber());
			old.setZip(company.getZip());
			
			return Optional.of(companyRepo.save(old));
		}
		return Optional.empty();
	}


	/**
	 * Update state field
	 * @param companyId
	 * @param state
	 * @return
	 */
    public Company updateCompanyState(String companyId, Boolean state) {
		Company old = companyRepo.findById(companyId).orElse(null);
		if (old != null) {
			old.setState(state);
			return companyRepo.save(old);
		}
        return null;
    }

	/**
	 * List of companies assigned to a campaign
	 * @param id
	 * @return
	 */
	public List<Company> getCampaignCompanies(String id) {
		return companyRepo.findByCampaign(id);
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


	public CompanyLocation getLocation(String id, String location) {
		Company company = companyRepo.findById(id).orElse(null);
		if (company != null) {
			return company.getLocations().stream().filter(l -> l.getId().equals(location)).findAny().orElse(null);
		}
		return null;
	}
	

	/**
	 * @param companyId
	 * @param location
	 * @return
	 * @throws InconsistentDataException 
	 */
	public CompanyLocation createLocation(String companyId, CompanyLocation location) throws InconsistentDataException {
		if (location.getId() == null) {
			throw new InconsistentDataException("Empty location ID", "NO_LOCATION");
		}
		Company company = companyRepo.findById(companyId).orElse(null);
		if (company != null) {
			if (company.getLocations() == null) company.setLocations(Collections.singletonList(location));
			else {
				int idx = company.getLocations().indexOf(location);
				if (idx >= 0) throw new InconsistentDataException("Duplicate location", "INVALID_COMPANY_DATA_DUPLICATE_LOCATION");
				else company.getLocations().add(location);
			}
			companyRepo.save(company);
		}
		return location;
	}

	/**
	 * @param companyId
	 * @param campaign
	 * @return
	 * @throws InconsistentDataException 
	 */
	public CompanyLocation updateLocation(String companyId, CompanyLocation location) throws InconsistentDataException {
		if (location.getId() == null) {
			throw new InconsistentDataException("Empty location ID", "NO_LOCATION");
		}
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
	public void deleteLocation(String companyId, String locationId) throws InconsistentDataException {
		Company company = companyRepo.findById(companyId).orElse(null);
		if(company != null) {
			List<Employee> list = employeeRepo.findByCompanyIdAndLocationIgnoreCase(companyId, locationId);
			if(list.size() > 0) {
				throw new InconsistentDataException("Location has employees", "INVALID_LOCATION_DATA_EMPLOYEE");
			}
			if (company.getLocations() != null) {
				company.getLocations().removeIf(l -> l.getId() == null && locationId == null || l.getId().equalsIgnoreCase(locationId));
				companyRepo.save(company);
			}
		}		
	}
	/**
	 * @param companyId
	 * @return
	 */
	public List<CompanyLocation> readlocations(String companyId) {
		Company company = companyRepo.findById(companyId).orElse(null);
		return (company != null) ? company.getLocations() : Collections.emptyList(); 
	}

	/**
	 * @param companyId
	 * @param employee
	 * @return
	 * @throws InconsistentDataException 
	 */
	public Employee createEmployee(String companyId, @Valid Employee employee) throws InconsistentDataException {
		employee.setCompanyId(companyId);
		Employee existing = employeeRepo.findByCompanyIdAndCodeIgnoreCase(companyId, employee.getCode()).stream().findAny().orElse(null);
		if (existing != null) {
			throw new InconsistentDataException("Duplicate user creation", "INVALID_COMPANY_DATA_DUPLICATE_EMPLOYEE");
		}
		return employeeRepo.save(employee);
	}

	public Employee getEmployeeByCode(String companyId, String code) {
		List<Employee> employeeList = employeeRepo.findByCompanyIdAndCodeIgnoreCase(companyId, code);
		if (employeeList != null) return employeeList.get(0);
		return null;
	}
	
	/**
	 * @param companyId
	 * @param employee
	 * @return
	 */
	public Employee updateEmployee(String companyId, Employee employee) throws InconsistentDataException {
		Employee e = employeeRepo.findById(employee.getId()).orElse(null);
		if(e != null) {
			if(e.getCompanyId().equals(companyId)) {
				Company c = companyRepo.findById(companyId).orElse(null);
				if(c == null) {
					throw new InconsistentDataException("Company not found", "COMPANY_NOT_FOUND");
				}
				CompanyLocation loc = getCompanyLocation(c, employee.getLocation());
				if(loc == null) {
					throw new InconsistentDataException("Location not found", "LOCATION_NOT_FOUND");
				}
				e.setCompanyEmail(employee.getCompanyEmail());
				e.setCode(employee.getCode());
				e.setLocation(employee.getLocation());
				e.setName(employee.getName());
				e.setSurname(employee.getSurname());
				employeeRepo.save(e);
			}			
		}
		return employeeRepo.findById(employee.getId()).orElse(null);
	}

	/**
	 * @param companyId
	 * @param employeeId
	 */
	public void deleteEmployee(String companyId, String employeeId) throws InconsistentDataException {
		Employee employee = employeeRepo.findById(employeeId).orElse(null);
		if(employee != null) {
			if(employee.getCampaigns().size() > 0) {
				throw new InconsistentDataException("Employee has subscriptions", "INVALID_COMPANY_DATA_EMPLOYEE");
			}
			Company company = companyRepo.findById(companyId).orElse(null);
			Optional<User> user = userService.findOneByCompanyCodeAndEmployeeCodeAndActive(company.getCode(), employee.getCode());
			if(user.isPresent()) {
				long count = statRepo.countByPlayerIdAndCompany(user.get().getPlayerId(), companyId);
				if(count > 0) {
					throw new InconsistentDataException("Employee has tracks", "INVALID_COMPANY_DATA_EMPLOYEE");
				}
			}
			employeeRepo.delete(employee);
		}
	}

	/**
	 * @param code
	 * @return
	 */
	public Optional<Company> findByCode(String code) {
		return companyRepo.findByCode(code).stream().findFirst();
	}

	/**
	 * @param companyId
	 * @param inputStream
	 * @throws CsvException 
	 * @throws IOException 
	 */
	public void importEmployees(String companyId, InputStream inputStream) throws Exception {
		Company c = companyRepo.findById(companyId).orElse(null);
		if(c == null) {
			throw new InconsistentDataException("Company not found", "COMPANY_NOT_FOUND");
		}
		
		List<String[]> lines = null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(inputStream, baos);
		byte[] bytes = baos.toByteArray();
		try {
			lines = readCSV(new ByteArrayInputStream(bytes), ',', 4);
		} catch (Exception e) {
			lines = readCSV(new ByteArrayInputStream(bytes), ';', 4);
		}
		Set<String> codes = new HashSet<>();
		int i = 0;
		for (String[] l: lines) {
			String code = stringValue(l[2], i + 2, 3, true);
			if (codes.contains(code)) {
				throw new InconsistentDataException("Duplicate employees", "INVALID_CSV_DUPLICATE_EMPLOYEES");				
			}
			Employee existing = employeeRepo.findByCompanyIdAndCodeIgnoreCase(companyId, code).stream().findAny().orElse(null);
			String location = stringValue(l[3], i + 2, 4, true);
			String name = stringValue(l[0], i + 2, 1, true);
			String surname = stringValue(l[1], i + 2, 2, true);
			CompanyLocation loc = getCompanyLocation(c, location);
			if(loc == null) {
				throw new ImportDataException(i + 2, 4);
			}
			if (existing != null) {
				existing.setLocation(location);
				existing.setName(name);
				existing.setSurname(surname);
				employeeRepo.save(existing);
			} else {
				Employee e = new Employee();
				e.setCode(code);
				e.setName(name);
				e.setSurname(surname);
				e.setCompanyId(companyId);
				e.setLocation(location);
				employeeRepo.save(e);
			}
			codes.add(code);
			i++;
		}
	}

	private List<String[]> readCSV(InputStream is, char separator, int columns) throws Exception {
		CSVParser parser = new CSVParserBuilder()
			    .withSeparator(separator)
			    .withIgnoreLeadingWhiteSpace(true)
			    .build();
		
		CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(is))
			    .withSkipLines(1)
			    .withCSVParser(parser)
			    .build();
		
		List<String[]> list = csvReader.readAll();
		if (list.size() > 0 && list.get(0).length != columns) {
			throw new InconsistentDataException("Invalid CSV format", "INVALID_CSV");
		}
		
		return list;
	}
	
	public void importLocations(String companyId, InputStream inputStream) throws Exception {
		Company c = companyRepo.findById(companyId).orElse(null);
		if(c == null) {
			throw new InconsistentDataException("Company not found", "COMPANY_NOT_FOUND");
		}
		
		List<String[]> lines = null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(inputStream, baos);
		byte[] bytes = baos.toByteArray();
		try {
			lines = readCSV(new ByteArrayInputStream(bytes), ',', 13);
		} catch (Exception e) {
			lines = readCSV(new ByteArrayInputStream(bytes), ';', 13);
		}
		int i = 0;
		List<CompanyLocation> locations = new LinkedList<>();
		for (String[] l : lines) {
			String id = stringValue(l[0], i+2, 0, true);
			CompanyLocation loc = getCompanyLocation(c, id);
			if (loc == null) {
				loc = new CompanyLocation();
			}
			
			loc.setId(id);
			loc.setName(stringValue(l[1], i+2, 1, false));			
			loc.setAddress(stringValue(l[2], i+2, 2, true));
			loc.setStreetNumber(stringValue(l[3], i+2, 3, false));
			loc.setZip(stringValue(l[4], i+2, 4, true));
			loc.setCity(stringValue(l[5], i+2, 5, true));
			loc.setProvince(stringValue(l[6], i+2, 6, false));
			loc.setRegion(stringValue(l[7], i+2, 7, false));
			loc.setCountry(stringValue(l[8], i+2, 8, false));

			//Double radius = doubeValue(l[9], i+1, 8, false);
			//if (radius == null) radius = 200d; 
			loc.setRadius(200d);
			loc.setLatitude(doubeValue(l[9], i+2, 9, true));
			loc.setLongitude(doubeValue(l[10], i+2, 10, true));

			// check non-working days
			String nwDoW = stringValue(l[11], i + 2, 11, false);
			if (nwDoW.length() > 0) {
				loc.setNonWorking(new LinkedList<>());
				String[] days = nwDoW.toLowerCase().split(",");
				for (String d: days) {
					if (DW.containsKey(d.trim())) {
						loc.getNonWorking().add(DW.get(d.trim()));
					} else {
						throw new ImportDataException(i + 2, 11);
					}
				}
			}
			// check exception days
			String nwDays = stringValue(l[12], i + 2, 12, false);
			if (nwDays.length() > 0) {
				loc.setNonWorkingDays(new HashSet<>());
				String[] days = nwDays.toLowerCase().split(",");
				for (String d: days) {
					LocalDate date = null;
					try {
						date = LocalDate.parse(d.trim());
					} catch (Exception e) {
						try {
							date = LocalDate.parse(d.trim(), dateFormatter );
						} catch (Exception e1) {
							throw new ImportDataException(i + 2, 11);
						}
					}
					loc.getNonWorkingDays().add(date.toString());
				}
			}
			locations.add(loc);
			i++;
		}
		if (locations.size() > 0) {
			Map<String, CompanyLocation> map = locations.stream().collect(Collectors.toMap(l -> l.getId(), l -> l));
			if (c.getLocations() != null) {
				for (CompanyLocation l : c.getLocations()) {
					if (!map.containsKey(l.getId())) locations.add(l);
				}
			}
			c.setLocations(locations);
			companyRepo.save(c);
		}			
	}
	
	private CompanyLocation getCompanyLocation(Company c, String locationId) {
		for(CompanyLocation l : c.getLocations()) {
			if(l.getId().equalsIgnoreCase(locationId)) {
				return l;
			}
		}
		return null;
	}
	
	/**
	 * @param trim
	 * @param i
	 * @param j
	 * @param b
	 * @return
	 */
	private Double doubeValue(String v, int row, int col, boolean required) throws ImportDataException {
		if (!StringUtils.hasText(v)) {
			if (required) throw new ImportDataException(row, col);
			else return null;
		} else {
			try {
				return Double.parseDouble(v.trim());
			} catch (NumberFormatException e) {
				throw new ImportDataException(row, col);
			}
		}
	}

	private String stringValue(String v, int row, int col, boolean required) throws ImportDataException {
		if (!StringUtils.hasText(v)) {
			if (required) throw new ImportDataException(row, col);
			else return "";
		} else return v.trim();
	}


	public Employee setBlockedEmployee(String companyId, String employeeId, boolean blocked) throws InconsistentDataException {
		Employee employee = employeeRepo.findById(employeeId).orElse(null);
		if(employee == null) 
			throw new InconsistentDataException("Invalid employee", "NO_EMPLOYEE");
		Company company = companyRepo.findById(companyId).orElse(null);
		if(company == null)
			throw new InconsistentDataException("Invalid company", "NO_COMPANY");
		if(blocked) {
			List<String> campaigns = new ArrayList<>(employee.getCampaigns());
			for(String campaignId : campaigns) {
				List<User> users = userService.getUserByEmployeeCode(campaignId, company.getCode(), employee.getCode());
				if(!users.isEmpty()) {
					User user = users.get(0);
					try {
						campaignService.unsubscribePlayer(campaignId, user.getPlayerId());
						campaignService.unsubscribeUser(user, campaignId);
					} catch (Exception e) {
						logger.info("error unsubscibing employee to campaign: {}, {}, {}: {}", company.getCode(), employee.getCode(), campaignId, e.getMessage());
					}
				}
			}
		}
		employee = employeeRepo.findById(employeeId).orElse(null);
		employee.setBlocked(blocked);
		employeeRepo.save(employee);
		return employee;
	}



}
