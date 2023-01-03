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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.service.errors.ImportDataException;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;

/**
 * @author raman
 *
 */
@Service
public class CompanyService {

	@Autowired
	private CompanyRepository companyRepo;
	@Autowired
	private EmployeeRepository employeeRepo;
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
	 * Find list of company employees
	 * @param id
	 * @param locationId
	 * @return
	 */
	public Page<Employee> findEmployees(String id, String locationId, Pageable pageable) {
		if (StringUtils.isEmpty(locationId)) return employeeRepo.findByCompanyId(id, pageable);
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
	public void deleteLocation(String companyId, String locationId) {
		companyRepo.findById(companyId).ifPresent(company -> {
			if (company.getLocations() != null) {
				company.getLocations().removeIf(l -> l.getId() == null && locationId == null || l.getId().equals(locationId));
				companyRepo.save(company);
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
	public Employee updateEmployee(String companyId, Employee employee) {
		employeeRepo.findById(employee.getId()).ifPresent(e -> {
			if (e.getCompanyId().equals(companyId)) {
				e.setCompanyEmail(employee.getCompanyEmail());
				e.setCode(employee.getCode());
				e.setLocation(employee.getLocation());
				e.setName(employee.getName());
				e.setSurname(e.getSurname());
				employeeRepo.save(e);
			}
		});
		return employeeRepo.findById(employee.getId()).orElse(null);
	}

	/**
	 * @param companyId
	 * @param employeeId
	 */
	public void deleteEmployee(String companyId, String employeeId) {
		employeeRepo.findById(employeeId).ifPresent(e -> {
			employeeRepo.delete(e);
		});
		
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
		for (String[] l: lines) {
			String code = l[2];
			if (codes.contains(code)) {
				throw new InconsistentDataException("Duplicate employees", "INVALID_CSV_DUPLICATE_EMPLOYEES");				
			}
			Employee existing = employeeRepo.findByCompanyIdAndCodeIgnoreCase(companyId, code).stream().findAny().orElse(null);
			if (existing != null) {
				existing.setLocation(l[3]);
				existing.setName(l[0]);
				existing.setSurname(l[1]);
				employeeRepo.save(existing);
			} else {
				Employee e = new Employee();
				e.setCode(code);
				e.setName(l[0]);
				e.setSurname(l[1]);
				e.setCompanyId(companyId);
				e.setLocation(l[3]);
				employeeRepo.save(e);
			}
			codes.add(code);
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
		Set<String> locationIds = new HashSet<>();
		for (String[] l : lines) {
			CompanyLocation loc = new CompanyLocation();
			String id = stringValue(l[0], i+1, 0, true);
			if (locationIds.contains(id)) {
				throw new InconsistentDataException("Duplicate locations", "INVALID_CSV_DUPLICATE_LOCATIONS");				
			}
			locationIds.add(id);
			
			loc.setId(id);
			loc.setAddress(stringValue(l[1], i+1, 1, true));
			loc.setStreetNumber(stringValue(l[2], i+1, 2, false));
			loc.setZip(stringValue(l[3], i+1, 3, true));
			loc.setCity(stringValue(l[4], i+1, 4, true));
			loc.setProvince(stringValue(l[5], i+1, 5, true));
			loc.setRegion(stringValue(l[6], i+1, 6, true));
			loc.setCountry(stringValue(l[7], i+1, 7, false));

			Double radius = doubeValue(l[8], i+1, 8, false);
			if (radius == null) radius = 200d; 
			loc.setRadius(radius);
			loc.setLatitude(doubeValue(l[9], i+1, 9, true));
			loc.setLongitude(doubeValue(l[10], i+1, 10, true));

			// check non-working days
			String nwDoW = stringValue(l[11], i + 1, 11, false);
			if (nwDoW.length() > 0) {
				loc.setNonWorking(new LinkedList<>());
				String[] days = nwDoW.toLowerCase().split(",");
				for (String d: days) {
					if (DW.containsKey(d.trim())) {
						loc.getNonWorking().add(DW.get(d.trim()));
					} else {
						throw new ImportDataException(i + 1, 11);
					}
				}
			}
			// check exception days
			String nwDays = stringValue(l[12], i + 1, 12, false);
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
							throw new ImportDataException(i + 1, 11);
						}
					}
					loc.getNonWorkingDays().add(date.toString());
				}
			}
			locations.add(loc);
			i++;
		}
		if (locations.size() > 0) {
			Company c = companyRepo.findById(companyId).orElse(null);
			if (c != null) {
				c.setLocations(locations);
				companyRepo.save(c);
			}
		}
	}
	
	/**
	 * @param trim
	 * @param i
	 * @param j
	 * @param b
	 * @return
	 */
	private Double doubeValue(String v, int row, int col, boolean required) throws ImportDataException {
		if (StringUtils.isEmpty(v)) {
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
		if (StringUtils.isEmpty(v)) {
			if (required) throw new ImportDataException(row, col);
			else return "";
		} else return v.trim();
	}


}
