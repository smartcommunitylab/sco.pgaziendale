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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Collections;

import org.apache.commons.lang3.RandomStringUtils;
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
import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.Subscription;
import it.smartcommunitylab.pgazienda.domain.Territory;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import it.smartcommunitylab.pgazienda.service.PGAppService;

/**
 * @author raman
 *
 */
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = Constants.ROLE_ADMIN)
@SpringBootTest(classes = PGAziendaApp.class)
public class CampaignResourceITest {

    /**
	 * 
	 */
	private static final String T_ID = "TAA";

	static final String ADMIN = "admin";

	private static final String CAMPAIGN_ID = "campaignId";    
    @Autowired
    private CampaignRepository campaignRepo;
    @Autowired
    private CompanyRepository companyRepo;
    @Autowired
    private EmployeeRepository employeeRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PGAppService service;

    @Autowired
    private MockMvc restMockMvc;

/**
 * Sets up the test environment by clearing all existing campaigns, companies,
 * employees, and users with the username "login@example.com" from the database.
 * It also initializes the application with a default test territory.
 * This ensures that each test starts with a clean state.
 */
    @BeforeEach
    public void setup() {
    	Territory t = testTerritory();
        service.setTerritories(Collections.singletonList(t));
        campaignRepo.deleteAll();
    	companyRepo.deleteAll();
    	employeeRepo.deleteAll();
    	userRepository.findAll().stream().filter(u -> u.getUsername().equalsIgnoreCase("login@example.com")).forEach(u -> {
        	userRepository.delete(u);
    	});
    }

    
    /**
     * Tests the read operation by retrieving the list of campaigns and
     * asserting that the result is an empty list (i.e., there are no campaigns
     * in the database). It then creates a campaign and asserts that the read
     * operation returns a list with one element.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testRead() throws Exception {
        restMockMvc.perform(
                get("/api/campaigns"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value("0"));

    	Campaign obj = testCampaign();
    	obj = campaignRepo.save(obj);
    	
        restMockMvc.perform(
                get("/api/campaigns"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value("1"));
    }

    /**
     * Test case for verifying the behavior of the resetCampaign endpoint.
     *
     * The test creates a campaign, saves it in the database, and then calls
     * the resetCampaign endpoint with the campaign's id. It then asserts that
     * the response status is 200 (OK).
     *
     * @throws Exception if an error occurs during the mock request
     */
	@Test
	public void testResetCampaign() throws Exception {
		Campaign obj = testCampaign();
		obj = campaignRepo.save(obj);

		restMockMvc.perform(
				put("/api/campaigns/{campaignId}/reset", obj.getId()))
				.andExpect(status().isOk());
	}
    
    /**
     * Test case for verifying the behavior of the campaigns associated with a company.
     *
     * The test first creates and saves a campaign and a company in the database.
     * It checks that the company initially has no associated campaigns.
     * Then, it associates the campaign with the company and verifies that the
     * campaign is successfully linked. After that, it removes the campaign from
     * the company and ensures that the company no longer has any associated campaigns.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testCompanyCampaign() throws Exception {
    	Campaign obj = testCampaign();
    	obj = campaignRepo.save(obj);
    	
    	Company company = testCompany();
    	company =companyRepo.save(company);
    	
        restMockMvc.perform(
                get("/api/companies/{companyId}/campaigns", company.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    	
        restMockMvc.perform(
                put("/api/companies/{companyId}/campaigns/{campaignId}", company.getId(), obj.getId()))
                .andExpect(status().isOk());
        restMockMvc.perform(
                get("/api/companies/{companyId}/campaigns", company.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));

        restMockMvc.perform(
                delete("/api/companies/{companyId}/campaigns/{campaignId}", company.getId(), obj.getId()))
                .andExpect(status().isOk());
        restMockMvc.perform(
                get("/api/companies/{companyId}/campaigns", company.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    	
	}
    
    /**
     * Test case for verifying the behavior of the subscribe and unsubscribe endpoints
     * when a user is associated with a company and a campaign.
     *
     * The test first creates a campaign and a company and associates the campaign
     * with the company. After that, it creates an employee and associates the employee
     * with the company. It also creates a user and associates the user with the company.
     *
     * Then, it subscribes the user to the campaign and verifies that the user is
     * successfully subscribed. After that, it unsubscribes the user from the campaign
     * and ensures that the user is no longer subscribed to the campaign.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithMockUser(username = "login@example.com")
    public void testSubscription() throws Exception {
    	Campaign obj = testCampaign();
    	obj = campaignRepo.save(obj);
    
    	Company company = testCompany();
    	company.setCampaigns(Collections.singletonList(obj.getId()));
    	company = companyRepo.save(company);
    	
    	Employee e = testEmployee(company);
    	e = employeeRepo.save(e);
    	
    	User user = testUser();
    	userRepository.save(user);

        restMockMvc.perform(
                put("/api/campaigns/{campaignId}/subscribe/{companyCode}/{key}", obj.getId(), company.getCode(), e.getCode()))
                .andExpect(status().isOk());

        user = userRepository.findById(user.getId()).orElse(null);
        assertThat(user).isNotNull();
        UserRole role = user.findRole(Constants.ROLE_APP_USER).orElse(null);
		assertThat(role).isNotNull();
		assertThat(role.activeSubscriptions().size()).isEqualTo(1);
		Subscription s = role.activeSubscriptions().get(0);
		assertThat(s.getCampaign()).isEqualTo(obj.getId());
		assertThat(s.getKey()).isEqualTo(e.getCode());
		assertThat(s.getCompanyCode()).isEqualTo(company.getCode());
		e = employeeRepo.findById(e.getId()).orElse(null);
		assertThat(e).isNotNull();
		assertThat(e.getCampaigns()).contains(obj.getId());
		
        restMockMvc.perform(
                delete("/api/campaigns/{campaignId}/unsubscribe", obj.getId()))
                .andExpect(status().isOk());
        
        user = userRepository.findById(user.getId()).orElse(null);
        assertThat(user).isNotNull();
        role = user.findRole(Constants.ROLE_APP_USER).orElse(null);
		assertThat(role).isNotNull();
		assertThat(role.activeSubscriptions().size()).isEqualTo(0);
		e = employeeRepo.findById(e.getId()).orElse(null);
		assertThat(e).isNotNull();
		assertThat(e.getCampaigns()).doesNotContain(obj.getId());
	
    }

	/**
	 * @param company
	 * @return
	 */
	private Employee testEmployee(Company company) {
		Employee e = new Employee();
    	e.setCode("1234");
    	e.setCompanyId(company.getId());
		return e;
	}
    
    /**
     * Test case for verifying the behavior of the read endpoint
     * when accessed by an unauthenticated user.
     *
     * The test expects the endpoint to return an HTTP 200 OK status
     * with an empty list of campaigns.
     *
     * Additionally, the test checks that if a campaign is activated,
     * it is included in the response.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithUnauthenticatedMockUser
    public void testReadPublic() throws Exception {
    	Campaign obj = testCampaign();
    	obj = campaignRepo.save(obj);
    	
        restMockMvc.perform(
                get("/api/public/campaigns"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value("0"));

        obj.setActive(true);
    	obj = campaignRepo.save(obj);
    	
        restMockMvc.perform(
                get("/api/public/campaigns"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value("1"));
    }

    /**
     * Test case for verifying the behavior of the read endpoint when accessed by an authenticated user.
     *
     * The test first creates a campaign, associates it with a company, and saves an employee.
     * It then subscribes the user to the campaign and verifies that the endpoint returns the
     * correct list of subscribed campaigns for the user.
     *
     * The test expects the endpoint to return an HTTP 200 OK status with a response body containing
     * the list of campaigns the user is subscribed to.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithMockUser(username = "login@example.com")
    public void testReadUser() throws Exception {
    	Campaign obj = testCampaign();
    	obj = campaignRepo.save(obj);
    
    	Company company = testCompany();
    	company.setCampaigns(Collections.singletonList(obj.getId()));
    	company = companyRepo.save(company);
    	
    	Employee e = testEmployee(company);
    	e = employeeRepo.save(e);
    	
    	User user = testUser();
    	userRepository.save(user);
    	
        restMockMvc.perform(
                put("/api/campaigns/{campaignId}/subscribe/{companyCode}/{key}", obj.getId(), company.getCode(), e.getCode()))
                .andExpect(status().isOk());


        restMockMvc.perform(
                get("/api/campaigns/user/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }
    
    /**
     * Returns a test Territory object.
     *
     * The test Territory has English name "Trentino" and territoryId {@link #T_ID}.
     *
     * @return a test Territory object
     */
    private Territory testTerritory() {
    	Territory t = new Territory();
    	t.setName(Collections.singletonMap("en", "Trentino"));
    	t.setTerritoryId(T_ID);
    	return t;
    }

    /**
     * Returns a test Company object.
     *
     * The test Company has code "code", English name "company", address "address",
     * contact email "email", contact phone "123", logo "logo", and web page "web".
     * Its territoryId is {@link #T_ID}.
     *
     * @return a test Company object
     */
    private Company testCompany() {
    	Company c = new Company();
    	c.setCode("code");
    	c.setAddress("address");
    	c.setContactEmail("email");
    	c.setContactPhone("123");
    	c.setLogo("logo");
        c.setTerritoryId(T_ID);
    	c.setName("company");
    	c.setWeb("web");
    	return c;
    }


/**
 * Utility method for creating a test campaign object.
 *
 * This method initializes a Campaign instance with preset data for testing purposes.
 * It sets the campaign's title, id, territory id, description, start and end dates,
 * and means of transportation.
 *
 * @return a Campaign object for use in unit tests
 */
    private Campaign testCampaign() {
    	Campaign c = new Campaign();
    	c.setTitle("campaign");
    	c.setId(CAMPAIGN_ID);
    	c.setTerritoryId(T_ID);
    	c.setDescription("description");
    	c.setFrom(LocalDate.now().minusDays(10));
    	c.setTo(LocalDate.now().plusDays(10));
    	c.setMeans(Collections.singletonList("bike"));
    	return c;
    }
    
    /**
     * Utility method for creating a test user object.
     *
     * This method initializes a User instance with preset data for testing purposes.
     * It sets the user's username, password, activated flag, name, and surname.
     *
     * @return a User object for use in unit tests
     */
    public User testUser() {
        User user = new User();
        user.setUsername("login@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setName("name");
        user.setSurname("surname");
        return user;
    }
}
