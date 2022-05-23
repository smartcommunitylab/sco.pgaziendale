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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.PGApp;
import it.smartcommunitylab.pgazienda.domain.Subscription;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.dto.DataModelDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO.TrackLegDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO.TrackPointDTO;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.DayStatRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.repository.PGAppRepository;
import it.smartcommunitylab.pgazienda.repository.UserRepository;

/**
 * @author raman
 *
 */
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = Constants.ROLE_ADMIN)
@SpringBootTest(classes = PGAziendaApp.class)
public class AdminResourceITest {

	static final String ADMIN = "admin";

    @Autowired
    private PGAppRepository appRepo;
	@Autowired
	private CampaignRepository campaignRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private CompanyRepository companyRepo;
	@Autowired
	private  EmployeeRepository employeeRepo;
    @Autowired
    private DayStatRepository statRepo;
	
    private MockRestServiceServer mockServer;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc restMockMvc;

    @BeforeEach
    public void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        appRepo.deleteAll();
    	campaignRepo.deleteAll();
    	companyRepo.deleteAll();
    	employeeRepo.deleteAll();
    	statRepo.deleteAll();
    	userRepo.findAll().stream().filter(u -> u.getUsername().startsWith("1234")).forEach(u -> {
        	userRepo.delete(u);
    	});
    }
    
    @Test
    public void testLoad() throws Exception {

    	DataModelDTO dto = TestUtil.readObject(getClass().getResourceAsStream("/data.json"), DataModelDTO.class);
    	
        restMockMvc.perform(
                post("/api/admin/load")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testLoadUpdate() throws Exception {

    	DataModelDTO dto = TestUtil.readObject(getClass().getResourceAsStream("/data.json"), DataModelDTO.class);
    	
        restMockMvc.perform(
                post("/api/admin/load")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dto)))
                .andExpect(status().isOk());
        
        restMockMvc.perform(
                post("/api/admin/load")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dto)))
                .andExpect(status().isOk());

    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void testCampaignSync() throws Exception {
    	PGApp app = testApp();
    	appRepo.save(app);

    	Map<String, Object> campaign = TestUtil.readObject(getClass().getResourceAsStream("/campaign.json"), Map.class);
    	campaign.put("dateFrom", LocalDate.now().minusDays(30).toString());
    	campaign.put("dateTo", LocalDate.now().plusDays(30).toString());
    	
    	mockServer.expect(requestTo(new URI("http://endpoint")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ObjectMapper().writeValueAsString(Collections.singletonList(campaign))));

    	
        restMockMvc.perform(
                post("/api/admin/campaignsync"))
                .andExpect(status().isOk());
        Optional<Campaign> campaignOpt = campaignRepo.findById((String) campaign.get("campaignId"));
        Assert.notNull(campaignOpt.get(), "Campaign is not saved");
    }
    
    @Test
    public void testSubscribe() throws Exception {
    	Campaign obj = testCampaign();
    	obj = campaignRepo.save(obj);
    
    	Company company = testCompany();
    	company.setCampaigns(Collections.singletonList(obj.getId()));
    	company = companyRepo.save(company);
    	
    	Employee e = testEmployee(company);
    	e = employeeRepo.save(e);
    	
        restMockMvc.perform(
                post("/api/admin/subscribe/{campaignId}/{playerId}/{companyKey}/{code}", obj.getId(), "1234", company.getCode(), e.getCode()))
                .andExpect(status().isOk());

        User user = userRepo.findByPlayerId("1234").orElse(null);
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

        restMockMvc.perform(
                post("/api/admin/unsubscribe/{campaignId}/{playerId}", obj.getId(), "1234", company.getCode(), e.getCode()))
                .andExpect(status().isOk());

        user = userRepo.findByPlayerId("1234").orElse(null);
        assertThat(user).isNotNull();
        role = user.findRole(Constants.ROLE_APP_USER).orElse(null);
		assertThat(role).isNotNull();
		assertThat(role.getSubscriptions().size()).isEqualTo(0);
		e = employeeRepo.findById(e.getId()).orElse(null);
		assertThat(e).isNotNull();
		assertThat(e.getCampaigns()).doesNotContain(obj.getId());

    } 
    
    @Test
    public void testValidate() throws Exception {
    	Campaign obj = testCampaign();
    	obj = campaignRepo.save(obj);
    
    	Company company = testCompany();
    	company.setCampaigns(Collections.singletonList(obj.getId()));
    	company = companyRepo.save(company);
    	
    	Employee e = testEmployee(company);
    	e = employeeRepo.save(e);
    	
        restMockMvc.perform(
                post("/api/admin/subscribe/{campaignId}/{playerId}/{companyKey}/{code}", obj.getId(), "1234", company.getCode(), e.getCode()))
                .andExpect(status().isOk());
        
        
    	TrackDTO track = new TrackDTO();
    	track.setDate(LocalDate.now().toString());
    	track.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    	track.setLegs(new LinkedList<>());
    	TrackLegDTO leg = new TrackLegDTO();
    	leg.setDistance(1000d);
    	leg.setMean("bike");
    	leg.setId("123456");
    	leg.setPoints(new LinkedList<>());
    	for (int i = 0; i < 10; i++) {
    		TrackPointDTO point = new TrackPointDTO();
    		point.setAccuracy(1l);
    		point.setRecorded_at(System.currentTimeMillis());
    		point.setLatitude(company.getLocations().get(0).getLatitude());
    		point.setLongitude(company.getLocations().get(0).getLongitude());
    		leg.getPoints().add(point);
    	}
    	track.getLegs().add(leg);
    	
        restMockMvc.perform(
                post("/api/admin/validate/{campaignId}/{playerId}", obj.getId(), "1234")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(track)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.valid").value("true"));
    	
    }

    
    private PGApp testApp() {
    	PGApp app = new PGApp();
    	app.setName("test app");
    	app.setId("externalAppId");
    	app.setEndpoint("http://endpoint");
    	app.setPassword("password");
    	app.setSupportCampaignMgmt(true);
    	app.setSupportPushValidation(true);
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
    	
    	c.setLocations(new LinkedList<>());
    	CompanyLocation cl = new CompanyLocation();
    	
    	cl.setLatitude( 44.836158);
    	cl.setLongitude(11.619019);
    	cl.setRadius(200d);
    	
    	c.getLocations().add(cl);
    	return c;
    }
    

    private Campaign testCampaign() {
    	Campaign c = new Campaign();
    	c.setTitle("campaign");
    	c.setId("externalCampaignId");
    	c.setApplication("externalAppId");
    	c.setDescription("description");
    	c.setFrom(LocalDate.now().minusDays(10));
    	c.setTo(LocalDate.now().plusDays(10));
    	c.setMeans(Collections.singletonList("bike"));
    	return c;
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
    
}
