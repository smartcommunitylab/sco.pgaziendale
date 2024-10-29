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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
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
import it.smartcommunitylab.pgazienda.domain.Territory;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.service.PGAppService;

/**
 * @author raman
 *
 */
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = Constants.ROLE_ADMIN)
@SpringBootTest(classes = PGAziendaApp.class)
public class CompanyResourceITest {

    /**
	 * 
	 */
	private static final String T_ID = "TAA";

	static final String ADMIN = "admin";

    @Autowired
    private CompanyRepository companyRepo;
    @Autowired
    private CampaignRepository campaignRepo;
    @Autowired
    private PGAppService appService;
    
    @Autowired
    private MockMvc restMockMvc;

    /**
     * Sets up the test environment by clearing the CompanyRepository and setting a single test territory in the PGAppService.
     * This is done to ensure that the tests are isolated and start with a clean slate.
     */
    @BeforeEach
    public void setup() {
    	Territory t = testTerritory();
        appService.setTerritories(Collections.singletonList(t));

    	companyRepo.deleteAll();
    }
    
/**
 * Test case for verifying the creation of a new company.
 *
 * The test performs a POST request to create a new company and expects the
 * operation to return an HTTP 200 OK status. It then verifies that the company
 * has been correctly saved in the repository by checking the size of the
 * retrieved list and asserting that the stored company's details match the
 * ones provided.
 *
 * @throws Exception if an error occurs during the mock request
 */
    @Test
    public void testCreateCompany() throws Exception {
    	Company c = testCompany();
    	c.setTerritoryId(T_ID);

        restMockMvc.perform(
                post("/api/companies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(c)))
                .andExpect(status().isOk());

            List<Company> updated = companyRepo.findByName(c.getName(), null).getContent();
            assertThat(updated.size()).isEqualTo(1);
            Company updatedCompany = updated.get(0);
            
            assertThat(updatedCompany.getName()).isEqualTo(c.getName());
            assertThat(updatedCompany.getAddress()).isEqualTo(c.getAddress());
            assertThat(updatedCompany.getContactEmail()).isEqualTo(c.getContactEmail());
            assertThat(updatedCompany.getContactPhone()).isEqualTo(c.getContactPhone());
            assertThat(updatedCompany.getTerritoryId()).isEqualTo(c.getTerritoryId());
            assertThat(updatedCompany.getLogo()).isEqualTo(c.getLogo());
            assertThat(updatedCompany.getWeb()).isEqualTo(c.getWeb());

    }
    
    /**
     * Test case for verifying the update of a company.
     *
     * The test performs a PUT request to update a company and expects the
     * operation to return an HTTP 200 OK status. It then verifies that the
     * company has been correctly updated in the repository by checking the
     * updated company's details match the ones provided.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testUpdateCompany() throws Exception {
    	Company c = new Company();
    	c.setAddress("address1");
    	c.setContactEmail("email1");
    	c.setContactPhone("1231");
    	c.setLogo("logo1");
    	c.setName("company1");
    	c.setWeb("web1");
        c.setCode("code");
    	c.setTerritoryId(T_ID);
    	c = companyRepo.save(c);

        restMockMvc.perform(
                put("/api/companies/{companyId}", c.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(c)))
                .andExpect(status().isOk());

        restMockMvc.perform(
                put("/api/companies/{companyId}/{state}", c.getId(), true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(c)))
                .andExpect(status().isOk());

            Company updatedCompany = companyRepo.findById(c.getId()).orElse(null);
            assertThat(updatedCompany).isNotNull();
            assertThat(updatedCompany.getName()).isEqualTo(c.getName());
            assertThat(updatedCompany.getAddress()).isEqualTo(c.getAddress());
            assertThat(updatedCompany.getContactEmail()).isEqualTo(c.getContactEmail());
            assertThat(updatedCompany.getContactPhone()).isEqualTo(c.getContactPhone());
            assertThat(updatedCompany.getTerritoryId()).isEqualTo(c.getTerritoryId());
            assertThat(updatedCompany.getLogo()).isEqualTo(c.getLogo());
            assertThat(updatedCompany.getWeb()).isEqualTo(c.getWeb());
            assertThat(updatedCompany.getCode()).isEqualTo(c.getCode());

    }
    
    /**
     * Test case for verifying the behavior of the delete endpoint
     * when a valid company id is provided.
     *
     * The test expects the endpoint to return an HTTP 200 OK status
     * and to delete the company associated with the id.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testDeleteCompany() throws Exception {
    	Company c = testCompany();
    	c.setTerritoryId(T_ID);
    	c = companyRepo.save(c);
    	

        restMockMvc.perform(
                delete("/api/companies/{companyId}", c.getId()))
                .andExpect(status().isOk());

            List<Company> updated = companyRepo.findByName(c.getName(), null).getContent();
            assertThat(updated.size()).isEqualTo(0);
    }
    
    /**
     * Test case for verifying the behavior of the getCompany endpoint
     * when a valid company id is provided.
     *
     * The test expects the endpoint to return an HTTP 200 OK status
     * with a response body containing the company associated with the id.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testReadCompany() throws Exception {
    	Company c = testCompany();
    	c.setTerritoryId(T_ID);
    	c = companyRepo.save(c);
    	
        restMockMvc.perform(
                get("/api/companies/{companyId}", c.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(c.getName()));
    }

    /**
     * Test case for verifying the behavior of the getCompanies endpoint.
     *
     * The test performs a GET request to retrieve the list of companies and expects
     * the operation to return an HTTP 200 OK status. Initially, it checks that the list
     * is empty. Then, it saves a test company and performs the GET request again to
     * verify that the list contains one company.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testReadCompanies() throws Exception {
        restMockMvc.perform(
                get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value("0"));

        Company c = testCompany();
        c.setTerritoryId(T_ID);
    	c = companyRepo.save(c);
    	
        restMockMvc.perform(
                get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value("1"));
    }

    /**
     * Test case for verifying the behavior of the getCampaignCompanies endpoint.
     *
     * The test first saves a company with a campaign. It then performs a GET request
     * to retrieve the campaign companies and expects the operation to return an HTTP
     * 200 OK status. Then, it performs a GET request to retrieve the list of public
     * campaign companies and expects the operation to return an HTTP 200 OK status.
     * Finally, it performs a GET request to retrieve a public campaign company and
     * expects the operation to return an HTTP 200 OK status.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testGetCampaignCompanies() throws Exception {
        Company c = testCompany();
        c.setTerritoryId(T_ID);
        Campaign campaign = new Campaign();
        campaign.setId("12345");
        List<String> campaigns = new ArrayList<>();
        campaigns.add(campaign.getId());
        c.setCampaigns(campaigns);
        c = companyRepo.save(c);

        campaign = campaignRepo.save(campaign);

        restMockMvc.perform(
                        get("/api/companies/campaign/{campaignId}", c.getCampaigns().get(0)))
                .andExpect(status().isOk());

        restMockMvc.perform(
                get("/api/public/campaigns/{campaignId}/companies", campaign.getId()))
                .andExpect(status().isOk());

        restMockMvc.perform(
                        get("/api/public/campaigns/{campaignId}/companies/{code}", campaign.getId(), c.getCode()))
                .andExpect(status().isOk());

    }


    /**
     * Returns a test company object.
     *
     * <p>
     * The company created has the following properties:
     * <ul>
     * <li>code: code</li>
     * <li>address: address</li>
     * <li>contactEmail: email</li>
     * <li>contactPhone: 123</li>
     * <li>logo: logo</li>
     * <li>name: company</li>
     * <li>web: web</li>
     * </ul>
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
     * Returns a test territory object.
     *
     * <p>
     * The territory created has the following properties:
     * <ul>
     * <li>name: Trentino (in English)</li>
     * <li>territoryId: T_ID</li>
     * </ul>
     *
     * @return a Territory object for use in unit tests
     */
    private Territory testTerritory() {
    	Territory t = new Territory();
    	t.setName(Collections.singletonMap("en", "Trentino"));
    	t.setTerritoryId(T_ID);
    	return t;
    }

    
}
