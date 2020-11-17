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
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

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
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;

/**
 * @author raman
 *
 */
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = Constants.ROLE_ADMIN)
@SpringBootTest(classes = PGAziendaApp.class)
public class LocationResourceITest {

	static final String ADMIN = "admin";

    @Autowired
    private CompanyRepository repo;
    
    @Autowired
    private MockMvc restMockMvc;

    private Company company;
    
    @BeforeEach
    public void setup() {
        repo.deleteAll();
        company = repo.save(testCompany());
    }
    
    @Test
    public void testCreate() throws Exception {
    	CompanyLocation loc = testLocation();
    	
        restMockMvc.perform(
                post("/api/companies/{companyId}/locations", company.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(loc)))
                .andExpect(status().isOk());

        	company = repo.findById(company.getId()).orElse(null);
        	
            assertThat(company.getLocations().contains(loc)).isEqualTo(true);
    }
    
    @Test
    public void testUpdate() throws Exception {
    	CompanyLocation loc = testLocation();
    	company.setLocations(Collections.singletonList(loc));
    	company = repo.save(company);
    	
    	loc.setAddress("address2");

        restMockMvc.perform(
                put("/api/companies/{companyId}/locations/{locationId}", company.getId(), loc.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(loc)))
                .andExpect(status().isOk());

    	company = repo.findById(company.getId()).orElse(null);
        assertThat(company.getLocations().contains(loc)).isEqualTo(true);
        CompanyLocation updated = company.getLocations().get(0);
        
        assertThat(updated.getAddress()).isEqualTo(loc.getAddress());

    }
    
    @Test
    public void testDelete() throws Exception {
    	CompanyLocation loc = testLocation();
    	company.setLocations(Collections.singletonList(loc));
    	company = repo.save(company);

        restMockMvc.perform(
                delete("/api/companies/{companyId}/locations/{locationId}", company.getId(), loc.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(loc)))
                .andExpect(status().isOk());

    	company = repo.findById(company.getId()).orElse(null);
        assertThat(company.getLocations().contains(loc)).isEqualTo(false);
    }
    
    @Test
    public void testRead() throws Exception {
        restMockMvc.perform(
                get("/api/companies/{companyId}/locations", company.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

    	CompanyLocation loc = testLocation();
    	company.setLocations(Collections.singletonList(loc));
    	company = repo.save(company);
    	
        restMockMvc.perform(
                get("/api/companies/{companyId}/locations", company.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
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
    
    private CompanyLocation testLocation() {
    	CompanyLocation loc = new CompanyLocation();
    	loc.setId("locationId");
    	loc.setAddress("address");
    	loc.setCity("city");
    	loc.setCountry("country");
    	loc.setLatitute(1d);
    	loc.setLongitude(1d);
    	loc.setProvince("province");
    	loc.setRadius(1d);
    	loc.setRegion("region");
    	loc.setStreetNumber("123");
    	loc.setZip("12345");
    	return loc;
    }
}
