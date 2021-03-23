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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.PGApp;
import it.smartcommunitylab.pgazienda.domain.Subscription;
import it.smartcommunitylab.pgazienda.domain.TrackingData;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.DayStatRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.repository.PGAppRepository;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import it.smartcommunitylab.pgazienda.service.TrackingDataService;

/**
 * @author raman
 *
 */
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = Constants.ROLE_ADMIN)
@SpringBootTest(classes = PGAziendaApp.class)
public class TrackingDataResourceITest {

    /**
	 * 
	 */
	private static final String APP_ID = "externalAppId";

	static final String ADMIN = "admin";

	private static final String CAMPAIGN_ID = "campaignId";

    @Autowired
    private PGAppRepository appRepo;
    
    @Autowired
    private CampaignRepository campaignRepo;
    @Autowired
    private CompanyRepository companyRepo;
    @Autowired
    private EmployeeRepository employeeRepo;
    @Autowired
    private UserRepository userRepository;
	@Autowired
	private DayStatRepository dayStatRepo;

    @Autowired
    private TrackingDataService trackingService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private MockRestServiceServer mockServer;
    
    @Autowired
    private MockMvc restMockMvc;

    @BeforeEach
    public void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        appRepo.deleteAll();
    	PGApp app = testApp();
    	appRepo.save(app);
    	campaignRepo.deleteAll();
    	companyRepo.deleteAll();
    	employeeRepo.deleteAll();
    	dayStatRepo.deleteAll();
    	userRepository.findAll().stream().filter(u -> u.getUsername().equalsIgnoreCase("login@example.com")).forEach(u -> {
        	userRepository.delete(u);
    	});
    }
  
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
		assertThat(role.getSubscriptions().size()).isEqualTo(1);
		Subscription s = role.getSubscriptions().get(0);
		assertThat(s.getCampaign()).isEqualTo(obj.getId());
		assertThat(s.getKey()).isEqualTo(e.getCode());
		assertThat(s.getCompanyCode()).isEqualTo(company.getCode());
		e = employeeRepo.findById(e.getId()).orElse(null);
		assertThat(e).isNotNull();
		assertThat(e.getCampaigns()).contains(obj.getId());
		
		List<TrackingData> trackingData = new LinkedList<>();
		TrackingData td = new TrackingData();
		td.setDistance(100d);
		td.setMode("bike");
		td.setStartedAt(LocalDate.now().toString());
		td.setPlayerId(user.getPlayerId());
		td.setTrackId("12345667789");
		trackingData.add(td);
		
    	mockServer.expect(requestTo(new URI("http://endpoint")))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ObjectMapper().writeValueAsString(trackingData)));
    	
    	
    	trackingService.synchronizeApps();
    	
        restMockMvc.perform(
                get("/api/campaigns/{campaignId}/stats/me", obj.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        restMockMvc.perform(
                get("/api/campaigns/{campaignId}/stats/me?groupBy=month", obj.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

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
    
    private PGApp testApp() {
    	PGApp app = new PGApp();
    	app.setName("test app");
    	app.setId(APP_ID);
    	app.setEndpoint("http://endpoint");
    	app.setPassword("password");
    	return app;
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
    

    private Campaign testCampaign() {
    	Campaign c = new Campaign();
    	c.setTitle("campaign");
    	c.setId(CAMPAIGN_ID);
    	c.setApplication(APP_ID);
    	c.setDescription("description");
    	c.setFrom(LocalDate.now().minusDays(10));
    	c.setTo(LocalDate.now().plusDays(10));
    	c.setMeans(Collections.singletonList("bike"));
    	return c;
    }
    
    public User testUser() {
        User user = new User();
        user.setUsername("login@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setName("name");
        user.setSurname("surname");
        user.setPlayerId("1");
        return user;
    }
}
