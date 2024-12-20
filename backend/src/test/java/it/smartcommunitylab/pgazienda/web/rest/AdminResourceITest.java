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

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;


import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Campaign.Limit;
import it.smartcommunitylab.pgazienda.domain.Campaign.VirtualScoreValue;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.Employee;
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

	/**
	 * Sets up the test environment by removing all users, companies, employees,
	 * and campaigns from the database, and by creating a mock REST service
	 * server for the {@link RestTemplate} to use.
	 */
	@BeforeEach
	public void setup() {
		mockServer = MockRestServiceServer.createServer(restTemplate);
		campaignRepo.deleteAll();
		companyRepo.deleteAll();
		employeeRepo.deleteAll();
		statRepo.deleteAll();
		userRepo.findAll().stream().filter(u -> u.getUsername().startsWith("1234")).forEach(u -> {
			userRepo.delete(u);
		});
	}

	/**
	 * Tests the {@link AdminResource#load(DataModelDTO)} endpoint.
	 *
	 * The test expects the endpoint to return an HTTP 200 OK status and to
	 * successfully load the users, companies, employees, and campaigns from the
	 * provided DataModelDTO.
	 *
	 * @throws Exception if an error occurs during the mock request
	 */
	@Test
	public void testLoad() throws Exception {

		DataModelDTO dto = TestUtil.readObject(getClass().getResourceAsStream("/data.json"), DataModelDTO.class);

		restMockMvc.perform(
						post("/api/admin/load")
								.contentType(MediaType.APPLICATION_JSON)
								.content(TestUtil.convertObjectToJsonBytes(dto)))
				.andExpect(status().isOk());
	}

	/**
	 * Tests the {@link AdminResource#load(DataModelDTO)} endpoint.
	 *
	 * The test expects the endpoint to return an HTTP 200 OK status and to
	 * successfully load the users, companies, employees, and campaigns from the
	 * provided DataModelDTO.
	 *
	 * The test also checks that the endpoint does not add duplicate records when
	 * loading the same DataModelDTO twice.
	 *
	 * @throws Exception if an error occurs during the mock request
	 */
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

	/**
	 * Tests the {@link AdminResource#subscribe(Integer, String, String, String)}
	 * endpoint.
	 *
	 * The test expects the endpoint to return an HTTP 200 OK status and to
	 * successfully subscribe the user to the campaign.
	 *
	 * The test also checks that the endpoint does not add a subscription if the
	 * user is already subscribed to the campaign.
	 *
	 * Additionally, the test checks that the endpoint does not add a subscription
	 * if the campaign does not exist.
	 *
	 * @throws Exception if an error occurs during the mock request
	 */
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
		assertThat(role.activeSubscriptions().size()).isEqualTo(1);
		Subscription s = role.activeSubscriptions().get(0);
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
		assertThat(role.activeSubscriptions().size()).isEqualTo(0);
		e = employeeRepo.findById(e.getId()).orElse(null);
		assertThat(e).isNotNull();
		assertThat(e.getCampaigns()).doesNotContain(obj.getId());

	}

	/**
	 * Tests the {@link AdminResource#validate(Integer, String, TrackDTO)}
	 * endpoint.
	 *
	 * The test expects the endpoint to return an HTTP 200 OK status and to
	 * successfully validate the track.
	 *
	 * The test also checks that the endpoint correctly calculates the virtual
	 * score, which is just the distance of the track divided by 1000.
	 *
	 * @throws Exception if an error occurs during the mock request
	 */
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
		track.setStartTime(System.currentTimeMillis());
		track.setLegs(new LinkedList<>());
		TrackLegDTO leg = new TrackLegDTO();
		leg.setDistance(1000d);
		leg.setMean("bike");
		leg.setValid(true);
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
				.andExpect(jsonPath("$.valid").value("true"))
				.andExpect(jsonPath("$.legs[0].distance").value("1000.0"))
				.andExpect(jsonPath("$.legs[0].virtualScore").value("1.0"));

	}

	/**
	 * Test case for verifying the behavior of the invalidate endpoint
	 * when a valid track id is provided.
	 *
	 * The test expects the endpoint to return an HTTP 200 OK status and
	 * to update the track as invalid.
	 *
	 * @throws Exception if an error occurs during the mock request
	 */
	@Test
	public void testInvalidate() throws Exception {
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
		track.setStartTime(System.currentTimeMillis());
		track.setLegs(new LinkedList<>());
		TrackLegDTO leg = new TrackLegDTO();
		leg.setDistance(1000d);
		leg.setMean("bike");
		leg.setId("123456");
		leg.setValid(true);
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

		restMockMvc.perform(
						put("/api/admin/invalidate/{campaignId}/{playerId}/{trackId}", obj.getId(), "1234", "123456")
								.contentType(MediaType.APPLICATION_JSON)
								.content(TestUtil.convertObjectToJsonBytes(track)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.valid").value("false"))
				.andExpect(jsonPath("$.errorCode").value("ERR_INVALIDATED"));
	}

	/**
	 * Test case for verifying the behavior of the update endpoint
	 * when a valid track id is provided.
	 *
	 * The test expects the endpoint to return an HTTP 200 OK status and
	 * to update the track as valid.
	 *
	 * @throws Exception if an error occurs during the mock request
	 */
	@Test
	public void testUpdate() throws Exception {
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
		track.setStartTime(System.currentTimeMillis());
		track.setLegs(new LinkedList<>());
		TrackLegDTO leg = new TrackLegDTO();
		leg.setDistance(1000d);
		leg.setMean("bike");
		leg.setId("123456");
		leg.setValid(true);
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

		restMockMvc.perform(
						post("/api/admin/validate/{campaignId}/{playerId}", obj.getId(), "1234")
								.contentType(MediaType.APPLICATION_JSON)
								.content(TestUtil.convertObjectToJsonBytes(track)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.valid").value("true"));
	}

    /**
     * Test case for verifying the behavior of the legacy upload endpoint
     * when a valid CSV file is provided.
     *
     * The test expects the endpoint to return an HTTP 200 OK status after
     * successfully processing the uploaded CSV file containing legacy data
     * for a specific campaign.
     *
     * @throws Exception if an error occurs during the mock request
     */
	@Test
	public void testUploadLegacy() throws Exception {

		Campaign obj = testCampaign();

		MockMultipartFile file = new MockMultipartFile(
				"file",
				"legacy.csv",
				MediaType.TEXT_PLAIN_VALUE,
				("Title,Id,TerritoryId,Description,From,To,Means\n" +
						"campaign,externalCampaignId,TAA,description,2024-10-13,2024-11-02,bike").getBytes()
		);

		restMockMvc.perform(
						multipart("/api/admin/legacy/{campaignId}/csv", obj.getId())
								.file(file)
								.contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isOk());

	}

	/**
	 * Test case for verifying the behavior of the unregister player endpoint
	 * when a valid player id is provided.
	 *
	 * The test expects the endpoint to return an HTTP 200 OK status after
	 * successfully unregistering the user from the campaign.
	 *
	 * @throws Exception if an error occurs during the mock request
	 */
	@Test
	public void testUnregisterPlayer() throws Exception{

		Campaign obj = testCampaign();

		Subscription subscription = new Subscription();
		subscription.setCampaign(obj.getId());
		List<Subscription> subscriptions = new ArrayList<>();
		subscriptions.add(subscription);

		UserRole role = new UserRole();
		role.setRole(Constants.ROLE_APP_USER);
		role.setSubscriptions(subscriptions);
		List<UserRole> roles = new ArrayList<>();
		roles.add(role);

		User user = new User();
		user.setUsername("1234");
		user.setPlayerId("1234");
		user.setRoles(roles);

		userRepo.save(user);

		restMockMvc.perform(
						post("/api/admin/unregister/player/{playerId}", user.getPlayerId()))
				.andExpect(status().isOk());

	}


	/**
	 * Utility method for creating a test company object.
	 *
	 * <p>
	 * The method creates an instance of the Company class and sets its code,
	 * address, contact email, contact phone, logo, name, and web page fields.
	 * Additionally, it creates a CompanyLocation instance and adds it to the
	 * company's locations.
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

		c.setLocations(new LinkedList<>());
		CompanyLocation cl = new CompanyLocation();

		cl.setLatitude( 44.836158);
		cl.setLongitude(11.619019);
		cl.setRadius(200d);

		c.getLocations().add(cl);
		return c;
	}


	/**
	 * Utility method for creating a test campaign object.
	 *
	 * <p>
	 * The method creates an instance of the Campaign class and sets its title,
	 * id, territory id, description, from, to, and means fields. Additionally,
	 * it creates a list of Limit instances and adds it to the campaign's
	 * scoreLimits and trackLimits fields. Finally, it sets the campaign's
	 * virtualScore field.
	 *
	 * @return a Campaign object for use in unit tests
	 */
	private Campaign testCampaign() {
		Campaign c = new Campaign();
		c.setTitle("campaign");
		c.setId("externalCampaignId");
		c.setTerritoryId("TAA");
		c.setDescription("description");
		c.setFrom(LocalDate.now().minusDays(10));
		c.setTo(LocalDate.now().plusDays(10));
		c.setMeans(Collections.singletonList("bike"));

		LinkedList<Limit> limits = new LinkedList<>();
		limits.add(new Limit(it.smartcommunitylab.pgazienda.domain.Constants.AGG_DAY, it.smartcommunitylab.pgazienda.domain.Constants.MEAN.bike.toString(), 20d));
		limits.add(new Limit(it.smartcommunitylab.pgazienda.domain.Constants.AGG_MONTH, it.smartcommunitylab.pgazienda.domain.Constants.MEAN.bike.toString(), 250d));
		c.setScoreLimits(limits);
		limits = new LinkedList<>();
		limits.add(new Limit(it.smartcommunitylab.pgazienda.domain.Constants.AGG_DAY, it.smartcommunitylab.pgazienda.domain.Constants.MEAN.bike.toString(), 4d));
		c.setTrackLimits(limits);

		c.getVirtualScore().setBike(new VirtualScoreValue(it.smartcommunitylab.pgazienda.domain.Constants.METRIC_DISTANCE, 0.001));

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