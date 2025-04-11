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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    
    /**
     * Initializes the test environment. Deletes all existing companies and
     * creates a new test company.
     */
    @BeforeEach
    public void setup() {
        repo.deleteAll();
        company = repo.save(testCompany());
    }
    
    /**
     * Test create location
     *
     * Creates a new location, verifies correct status and location presence in the company.
     *
     * @throws Exception
     */
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
    
    /**
     * Test update location
     *
     * Updates an existing location's address and verifies that the update
     * is correctly reflected in the company's saved locations.
     *
     * @throws Exception if an error occurs during the mock request
     */
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
    
    /**
     * Test delete location
     *
     * Deletes an existing location and verifies that the location is correctly
     * removed from the company's saved locations.
     *
     * @throws Exception if an error occurs during the mock request
     */
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
    
    /**
     * Test read locations
     *
     * Verifies that the read endpoint returns a list of locations associated
     * with the specified company. The test first verifies that the list is
     * empty, then adds a location and verifies that the list contains the new
     * location.
     *
     * @throws Exception if an error occurs during the mock request
     */
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
    
    /**
     * Test import locations
     *
     * Verifies that the import endpoint correctly adds the locations from the
     * provided CSV file to the specified company.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testImportLocations() throws Exception {
    	
    	MockMultipartFile file = new MockMultipartFile("file", "sedi.csv", "text/plain", getClass().getResourceAsStream("/sedi.csv"));
        
    	restMockMvc.perform(
    			MockMvcRequestBuilders.multipart("/api/companies/{companyId}/locations/csv", company.getId())
                .file(file))
            .andExpect(status().is(200));
    }

    /**
     * Utility method for creating a test company object.
     *
     * The method creates an instance of the Company class and sets its code,
     * address, contact email, contact phone, logo, name, and web page fields.
     *
     * @return a Company object for use in unit tests
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
    	return c;
    }
    
    /**
     * Utility method for creating a test CompanyLocation object.
     *
     * The method creates an instance of the CompanyLocation class and sets its
     * id, address, city, country, latitude, longitude, province, radius,
     * region, street number, and zip fields.
     *
     * @return a CompanyLocation object for use in unit tests
     */
    private CompanyLocation testLocation() {
    	CompanyLocation loc = new CompanyLocation();
    	loc.setId("locationId");
    	loc.setAddress("address");
    	loc.setCity("city");
    	loc.setCountry("country");
    	loc.setLatitude(1d);
    	loc.setLongitude(1d);
    	loc.setProvince("province");
    	loc.setRadius(1d);
    	loc.setRegion("region");
    	loc.setStreetNumber("123");
    	loc.setZip("12345");
    	return loc;
    }
}
