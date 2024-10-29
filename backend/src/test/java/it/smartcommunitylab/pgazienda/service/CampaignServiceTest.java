package it.smartcommunitylab.pgazienda.service;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.*;
import it.smartcommunitylab.pgazienda.repository.*;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = PGAziendaApp.class)
public class CampaignServiceTest {
    private static final String T_ID = "TAA";
    private static final String CAMPAIGN_ID = "ferrara_bike2work";
    private static final String COMPANY_CODE = "companyCode";
    private static final String EMPLOYEE_CODE = "employeeCode";

    private static final String DEFAULT_LOGIN = "johndoe@example.com";
    private static final String DEFAULT_FIRSTNAME = "john";
    private static final String DEFAULT_LASTNAME = "doe";

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CampaignRepository campaignRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CompanyRepository companyRepo;
    @Autowired
    private EmployeeRepository employeeRepo;

    private Territory territory;
    private Campaign campaign;
    private Company company;
    private Employee employee;
    private Subscription subscription;
    private CompanyLocation loc;
    private User user;


    @BeforeEach
    public void setup() {
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

        List<String> campaigns = new ArrayList<>();
        campaigns.add(campaign.getId());

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
        company.setCampaigns(campaigns);
        companyRepo.save(company);

        employee = new Employee();
        employee.setCode(EMPLOYEE_CODE);
        employee.setCompanyId(company.getId());
        employee.setName(DEFAULT_FIRSTNAME);
        employee.setSurname(DEFAULT_LASTNAME);
        employee.setCampaigns(Collections.singletonList(campaign.getId()));
        employee.setLocation(loc.getId());
        employeeRepo.save(employee);

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

        user = new User();
        user.setUsername(DEFAULT_LOGIN);
        user.setPlayerId("player");
        user.setRoles(roles);
        userRepo.save(user);

    }

    @Test
    public void testGetCampaignExceptions() throws Exception {

        campaignRepo.findAll().stream()
                .filter(u -> u.getId().equalsIgnoreCase(CAMPAIGN_ID))
                .forEach(campaignRepo::delete);

        assertThrows(InconsistentDataException.class, () -> {
            campaignService.subscribeUser(user, EMPLOYEE_CODE, COMPANY_CODE, CAMPAIGN_ID, false);
        });

        campaignRepo.save(campaign);

        companyRepo.findAll().stream()
                .filter(u -> u.getCode().equalsIgnoreCase(COMPANY_CODE))
                .forEach(companyRepo::delete);

        assertThrows(InconsistentDataException.class, () -> {
            campaignService.subscribeUser(user, EMPLOYEE_CODE, COMPANY_CODE, CAMPAIGN_ID, false);
        });

        companyRepo.save(company);

        Employee employee2 = new Employee();
        employee2.setCode(EMPLOYEE_CODE);
        employee2.setCompanyId(company.getId());
        employee2.setName(DEFAULT_FIRSTNAME);
        employee2.setSurname(DEFAULT_LASTNAME);
        employee2.setCampaigns(Collections.singletonList(campaign.getId()));
        employee2.setLocation(loc.getId());
        employeeRepo.save(employee2);

        Subscription subscription2 = new Subscription();
        subscription2.setCampaign(CAMPAIGN_ID);
        subscription2.setCompanyCode(COMPANY_CODE);
        subscription2.setKey(EMPLOYEE_CODE);
        subscription2.setAbandoned(false);

        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription2);

        UserRole role = new UserRole();
        role.setRole(Constants.ROLE_APP_USER);
        role.setSubscriptions(subscriptions);
        role.setCompanyId(company.getId());
        role.setCampaignId(campaign.getId());
        List<UserRole> roles = new ArrayList<>();
        roles.add(role);

        User user2 = new User();
        user2.setUsername(DEFAULT_LOGIN);
        user2.setPlayerId("player");
        user2.setRoles(roles);
        userRepo.save(user2);

        assertThrows(InconsistentDataException.class, () -> {
            campaignService.subscribeUser(user2, EMPLOYEE_CODE, COMPANY_CODE, CAMPAIGN_ID, false);
        });


    }

}
