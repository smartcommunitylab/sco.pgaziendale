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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;

import java.util.*;

/**
 * @author raman
 *
 */
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = Constants.ROLE_ADMIN)
@SpringBootTest(classes = PGAziendaApp.class)
public class EmployeeResourceITest {

	static final String ADMIN = "admin";

    @Autowired
    private CompanyRepository repo;
    @Autowired
    private EmployeeRepository employeeRepo;
    
    @Autowired
    private MockMvc restMockMvc;

    private Company company;


    /**
     * Sets up the test case by deleting all existing companies and employees,
     * then saving a test company.
     */
    @BeforeEach
    public void setup() {
        repo.deleteAll();
        employeeRepo.deleteAll();
        company = repo.save(testCompany());
    }
    
    /**
     * Test that a new employee can be created with POST /api/companies/{companyId}/employees.
     * @throws Exception
     */
    @Test
    public void testCreate() throws Exception {
    	Employee e = testEmployee();
    	
        restMockMvc.perform(
                post("/api/companies/{companyId}/employees", company.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(e)))
                .andExpect(status().isOk());

        assertThat(employeeRepo.findAll().size()).isEqualTo(1);
        Employee created = employeeRepo.findAll().get(0);
        assertThat(created.getCode()).isEqualTo(e.getCode());
    }

    /**
     * Tests that an existing employee can be updated with PUT /api/companies/{companyId}/employees/{id}.
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {
        Employee e = testEmployee();
        e = employeeRepo.save(e);

        restMockMvc.perform(
                        put("/api/companies/{companyId}/employees/{id}", company.getId(), e.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(e)))
                .andExpect(status().isOk());

        assertThat(employeeRepo.findAll().size()).isEqualTo(1);
        Employee updated = employeeRepo.findAll().get(0);
        assertThat(updated.getLocation()).isEqualTo(e.getLocation());

    }
    
    /**
     * Test that an existing employee can be deleted with DELETE /api/companies/{companyId}/employees/{id}.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
    	Employee e = testEmployee();
    	e = employeeRepo.save(e);

        restMockMvc.perform(
                delete("/api/companies/{companyId}/employees/{id}", company.getId(), e.getId()))
                .andExpect(status().isOk());
        assertThat(employeeRepo.findAll().size()).isEqualTo(0);

    }
    
    /**
     * Test that a list of employees can be retrieved with GET /api/companies/{companyId}/employees.
     * The test also checks that the endpoint returns the correct number of employees
     * when filtered by location.
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testRead() throws Exception {
        restMockMvc.perform(
                get("/api/companies/{companyId}/employees", company.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value(0));

    	Employee e = testEmployee();
    	e = employeeRepo.save(e);
    	
        restMockMvc.perform(
                get("/api/companies/{companyId}/employees", company.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value(1));
        
        restMockMvc.perform(
                get("/api/companies/{companyId}/employees?location={location}", company.getId(), "Sede 8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value(1));

        restMockMvc.perform(
                get("/api/companies/{companyId}/employees?location={location}", company.getId(), "location2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value(0));

    }

    /**
     * Test that an employee can be blocked with PUT /api/companies/{companyId}/employees/{employeeId:.*}/blocked/{blocked}.
     * @throws Exception
     */
    @Test
    public void testBlockEmployee() throws Exception {
        Employee e = testEmployee();
        e = employeeRepo.save(e);

        restMockMvc.perform(
                put("/api/companies/{companyId}/employees/{employeeId:.*}/blocked/{blocked}", company.getId(), e.getId(),  true))
                .andExpect(status().isOk());
    }

    /**
     * Test that a list of employees can be imported with POST /api/companies/{companyId}/employees/csv.
     * The test also checks that the endpoint returns a 200 OK status.
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testUploadEmployeesCsv() throws Exception {

        String csvContent = "John,Doe,1234,Sede 8\nJane,Doe,5678,Sede 8";
        MockMultipartFile csvFile = new MockMultipartFile(
                "file",
                "employees.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes()
        );

        restMockMvc.perform(
                        multipart("/api/companies/{companyId}/employees/csv", company.getId())
                                .file(csvFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    /**
     * Test that a list of employees can be imported with POST /api/companies/{companyId}/employees/csv
     * and that the endpoint returns a 400 Bad Request status if the company does not exist.
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testUploadEmployeesCsv_ImportDataException() throws Exception {

        repo.delete(company);

        Company c = new Company();
        c.setCode("code");
        c.setAddress("address");
        c.setContactEmail("email");
        c.setContactPhone("123");
        c.setLogo("logo");
        c.setName("company");
        c.setWeb("web");
        c.setLocations(null);

        repo.save(c);


        String csvContent = "John,Doe,1234,Sede 8\nJane,Doe,5678,Sede 8";
        MockMultipartFile csvFile = new MockMultipartFile(
                "file",
                "employees.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes()
        );

        restMockMvc.perform(
                        multipart("/api/companies/{companyId}/employees/csv", c.getId())
                                .file(csvFile))
                .andExpect(status().isBadRequest());

    }

    /**
     * Creates a test company for testing purposes.
     *
     * The company created is a basic one with a single location.
     *
     * @return a Company object
     */
    private Company testCompany() {
    	Company c = new Company();
    	c.setCode("code");
    	c.setAddress("address");
    	c.setContactEmail("email");
    	c.setContactPhone("123");
    	c.setLogo("logo");
    	c.setName("company");
    	c.setWeb("web");

        CompanyLocation companyLocation = new CompanyLocation();
        companyLocation.setId("Sede 8");
        companyLocation.setName("NameTestLoc");
        companyLocation.setAddress("Via Marconi");
        companyLocation.setStreetNumber("35");
        companyLocation.setZip("44122");
        companyLocation.setCity("Ferrara");
        companyLocation.setProvince("Ferrara");
        companyLocation.setRegion("Emilia-Romagna");
        companyLocation.setCountry("Italy");
        companyLocation.setLatitude(44.85203);
        companyLocation.setLongitude(11.59929);
        companyLocation.setRadius(200.00);
        List<Integer> nonWorking = new ArrayList<>();
        nonWorking.add(50);
        companyLocation.setNonWorking(nonWorking);
        Set<String> nonWorkingDays = new HashSet<>(Arrays.asList("sab", "dom"));
        companyLocation.setNonWorkingDays(nonWorkingDays);

        List<CompanyLocation> companyLocations = new ArrayList<>();
        companyLocations.add(companyLocation);

        c.setLocations(companyLocations);

    	return c;
    }
	/**
	 * @param company
	 * @return
	 */
	private Employee testEmployee() {
		Employee e = new Employee();
    	e.setCode("1234");
    	e.setLocation("Sede 8");
    	e.setCompanyId(company.getId());
		return e;
	}
    
}
