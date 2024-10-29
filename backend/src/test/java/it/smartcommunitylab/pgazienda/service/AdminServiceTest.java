package it.smartcommunitylab.pgazienda.service;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.*;
import it.smartcommunitylab.pgazienda.dto.DataModelDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO;
import it.smartcommunitylab.pgazienda.dto.TrackValidityDTO;
import it.smartcommunitylab.pgazienda.dto.UserDTO;
import it.smartcommunitylab.pgazienda.repository.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = PGAziendaApp.class)
public class AdminServiceTest {

    private static final String T_ID = "TAA";
    private static final String CAMPAIGN_ID = "ferrara_bike2work";
    private static final String COMPANY_CODE = "companyCode";
    private static final String EMPLOYEE_CODE = "employeeCode";

    private static final String DEFAULT_LOGIN = "johndoe@example.com";
    private static final String DEFAULT_FIRSTNAME = "john";
    private static final String DEFAULT_LASTNAME = "doe";

    @Autowired
    private AdminService adminService;

    @Autowired
    private CampaignRepository campaignRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CompanyRepository companyRepo;
    @Autowired
    private EmployeeRepository employeeRepo;
    @Autowired
    private LegacyPlayerMappingRepository legacyRepo;

    private Territory territory;
    private Campaign campaign;
    private Company company;
    private UserDTO userDTO;
    private LegacyPlayerMapping legacy;
    private Employee employee;
    private Subscription subscription;
    private CompanyLocation loc;

    /**
     * Set up the test environment by clearing the CampaignRepository, CompanyRepository, UserRepository and LegacyPlayerMappingRepository.
     * Then create a new campaign, a new company, a new employee and a new user with the specified login.
     * Create a new location and add it to the company.
     * Create a new legacy mapping and add it to the employee.
     * Create a new subscription and add it to the user.
     */
    @BeforeEach
    public void setup(){
        campaignRepo.findAll().stream()
                .filter(u -> u.getId().equalsIgnoreCase(CAMPAIGN_ID))
                .forEach(campaignRepo::delete);

        companyRepo.findAll().stream()
                .filter(u -> u.getCode().equalsIgnoreCase(COMPANY_CODE))
                .forEach(companyRepo::delete);

        userRepo.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(DEFAULT_LOGIN))
                .forEach(userRepo::delete);


        territory = new Territory();
        territory.setName(Collections.singletonMap("en", "Trentino"));
        territory.setTerritoryId(T_ID);

        campaign = new Campaign();
        campaign.setTitle("campaign");
        campaign.setId(CAMPAIGN_ID);
        campaign.setTerritoryId(T_ID);
        campaign.setDescription("description");
        campaign.setFrom(LocalDate.now().minusDays(10));
        campaign.setTo(LocalDate.now().plusDays(10));
        campaign.setMeans(Collections.singletonList("bike"));
        campaign.setUseMultiLocation(true);
        campaignRepo.save(campaign);

        loc = new CompanyLocation();
        loc.setId("Sede 8");
        loc.setName("NameTestLoc");
        loc.setAddress("Via Marconi");
        loc.setStreetNumber("35");
        loc.setZip("44122");
        loc.setCity("Ferrara");
        loc.setProvince("Emilia-Romagna");
        loc.setCountry("Italy");
        loc.setLatitude(45.5);
        loc.setLongitude(11.5);
        loc.setRadius(10.0);

        List<CompanyLocation> locations = new ArrayList<>();
        locations.add(loc);

        company = new Company();
        company.setCode(COMPANY_CODE);
        company.setName("Test Company");
        company.setTerritoryId(T_ID);
        company.setLocations(locations);
        companyRepo.save(company);

        userDTO = new UserDTO();
        userDTO.setUsername(DEFAULT_LOGIN);
        userDTO.setPassword(RandomStringUtils.random(60));
        userDTO.setName(DEFAULT_FIRSTNAME);
        userDTO.setSurname(DEFAULT_LASTNAME);
        userDTO.setRole(Constants.ROLE_MOBILITY_MANAGER);
        userDTO.setCompanyCode(COMPANY_CODE);

        employee = new Employee();
        employee.setCode(EMPLOYEE_CODE);
        employee.setCompanyId(company.getId());
        employee.setName(DEFAULT_FIRSTNAME);
        employee.setSurname(DEFAULT_LASTNAME);
        employee.setCampaigns(Collections.singletonList(campaign.getId()));
        employee.setLocation(loc.getId());
        employeeRepo.save(employee);

        legacy = new LegacyPlayerMapping();
        legacy.setCampaignId(CAMPAIGN_ID);
        legacy.setPlayers(new HashMap<>());
        legacy.getPlayers().put("player", EMPLOYEE_CODE);
        legacyRepo.save(legacy);

        subscription = new Subscription();
        subscription.setCampaign(CAMPAIGN_ID);
        subscription.setCompanyCode(COMPANY_CODE);
        subscription.setKey(EMPLOYEE_CODE);
        subscription.setAbandoned(false);

        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription);

        UserRole role = new UserRole();
        role.setRole(Constants.ROLE_APP_USER);
        role.setSubscriptions(subscriptions);
        role.setCompanyId(company.getId());
        role.setCampaignId(campaign.getId());
        List<UserRole> roles = new ArrayList<>();
        roles.add(role);

        User user = new User();
        user.setUsername(DEFAULT_LOGIN);
        user.setPlayerId("player");
        user.setRoles(roles);
        userRepo.save(user);

    }

    /**
     * Test the {@link AdminService#loadData(DataModelDTO)} method.
     *
     * The test creates a new DataModelDTO with a territory, a campaign, a company,
     * and a user, and then calls the loadData method with the DataModelDTO.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testLoadData() throws Exception {
        List<Territory> territoryList = new ArrayList<>();
        territoryList.add(territory);

        List<Campaign> campaignList = new ArrayList<>();
        campaignList.add(campaign);

        List<Company> companies = new ArrayList<>();
        companies.add(company);

        List<UserDTO> users = new ArrayList<>();
        users.add(userDTO);

        DataModelDTO model = new DataModelDTO();
        model.setTerritories(territoryList);
        model.setCampaigns(campaignList);
        model.setCompanies(companies);
        model.setCompanyUsers(users);

        adminService.loadData(model);

    }

    /**
     * Tests the {@link AdminService#getLegacyPlayer(String, String)} method.
     *
     * The test creates a new LegacyPlayerMapping and then calls the getLegacyPlayer
     * method with a valid employee code and campaign id.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testCheckLegacyPlayer() throws Exception {

        adminService.getLegacyPlayer(EMPLOYEE_CODE, CAMPAIGN_ID);

    }

    /**
     * Tests the {@link AdminService#unsubscribeCampaign(String, String)} method.
     *
     * The test creates a subscription for the given campaign and player, and then
     * calls the unsubscribeCampaign method.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testUnsubscribeCampaign() throws Exception {

        adminService.unsubscribeCampaign(CAMPAIGN_ID, "player");

    }

/**
 * Tests the {@link AdminService#validateTrack(String, String, TrackDTO)}
 * method.
 *
 * This test verifies that the validateTrack method correctly evaluates
 * a track as valid when the track is well-formed and meets the required
 * criteria. It creates a TrackDTO with a single valid leg and point,
 * performs validation, and asserts that the track is considered valid.
 *
 * The test expects the method to return a TrackValidityDTO with the
 * isValid property set to true.
 *
 * @throws Exception if any error occurs during the validation process.
 */
    @Test
    public void testValidateTrack() throws Exception {

        TrackDTO track = new TrackDTO();
        track.setStartTime(System.currentTimeMillis());

        TrackDTO.TrackPointDTO point = new TrackDTO.TrackPointDTO();
        point.setLatitude(45.5);
        point.setLongitude(11.5);
        point.setAccuracy(15552451L);
        point.setAltitude(10.0);
        point.setSpeed(10.0);
        point.setHeading(10.0);
        point.setCreated_at(System.currentTimeMillis());
        point.setIs_moving(true);

        List<TrackDTO.TrackPointDTO> points = new ArrayList<>();
        points.add(point);

        TrackDTO.TrackLegDTO leg = new TrackDTO.TrackLegDTO();
        leg.setMean("bike");
        leg.setPoints(points);
        leg.setValid(true);
        leg.setDistance(10.0);

        List<TrackDTO.TrackLegDTO> legs = new ArrayList<>();
        legs.add(leg);
        track.setLegs(legs);

        TrackValidityDTO validity = adminService.validateTrack("player", CAMPAIGN_ID, track);
        assertTrue(validity.isValid());

    }

}
