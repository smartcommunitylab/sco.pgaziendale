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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;

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
    
    @BeforeEach
    public void setup() {
        repo.deleteAll();
        employeeRepo.deleteAll();
        company = repo.save(testCompany());
    }
    
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
    
    @Test
    public void testUpdate() throws Exception {
    	Employee e = testEmployee();
    	e = employeeRepo.save(e);
    	
    	e.setLocation("location1");

        restMockMvc.perform(
                put("/api/companies/{companyId}/employees/{id}", company.getId(), e.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(e)))
                .andExpect(status().isOk());

        assertThat(employeeRepo.findAll().size()).isEqualTo(1);
        Employee updated = employeeRepo.findAll().get(0);
        assertThat(updated.getLocation()).isEqualTo(e.getLocation());

    }
    
    @Test
    public void testDelete() throws Exception {
    	Employee e = testEmployee();
    	e = employeeRepo.save(e);

        restMockMvc.perform(
                delete("/api/companies/{companyId}/employees/{id}", company.getId(), e.getId()))
                .andExpect(status().isOk());
        assertThat(employeeRepo.findAll().size()).isEqualTo(0);

    }
    
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
                get("/api/companies/{companyId}/employees?location={location}", company.getId(), "location"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value(1));

        restMockMvc.perform(
                get("/api/companies/{companyId}/employees?location={location}", company.getId(), "location2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value(0));

    }


    private Company testCompany() {
    	Company c = new Company();
    	c.setCode("code");
    	c.setAddress("address");
    	c.setContactEmail("email");
    	c.setContactPhone("123");
    	c.setLogo("logo");
    	c.setName("company");
    	c.setWeb("web");
    	return c;
    }
	/**
	 * @param company
	 * @return
	 */
	private Employee testEmployee() {
		Employee e = new Employee();
    	e.setCode("1234");
    	e.setLocation("location");
    	e.setCompanyId(company.getId());
		return e;
	}
    
}
