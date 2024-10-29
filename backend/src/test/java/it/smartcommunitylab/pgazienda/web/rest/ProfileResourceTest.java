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


import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.*;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(username = "testuser@test.com", authorities = Constants.ROLE_APP_USER)
@SpringBootTest(classes = PGAziendaApp.class)
public class ProfileResourceTest {

    private static final String T_ID = "TAA";
    private static final String CAMPAIGN_ID = "campaignId";
    private static final String COMPANY_CODE = "companyCode";
    private static final String EMPLOYEE_CODE = "employeeCode";

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private CampaignRepository campaignRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CompanyRepository companyRepo;
    @Autowired
    private EmployeeRepository employeeRepo;

    private Campaign campaign;
    private Company company;
    private Employee employee;
    private User user;

    /**
     * Sets up the test environment before each test is run.
     *
     * Deletes existing user and company with the specified company code, then
     * creates a new company, user, and campaign. The user is given the role of
     * app user and is assigned to the company. The campaign is saved. The user is
     * then saved with the new role and subscription. Finally, a new employee is
     * created and saved with the specified company and employee codes.
     */
    @BeforeEach
    public void setup() {

        companyRepo.findAll().stream()
                .filter(u -> u.getCode().equalsIgnoreCase(COMPANY_CODE))
                .forEach(companyRepo::delete);

        userRepo.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase("testuser@test.com"))
                .forEach(userRepo::delete);

        UserRole role = new UserRole();
        role.setRole(Constants.ROLE_APP_USER);
        List<UserRole> roles = new ArrayList<>();
        roles.add(role);

        user = new User();
        user.setUsername("testuser@test.com");
        user.setRoles(roles);

        campaign = testCampaign();
        campaignRepo.save(campaign);

        company = new Company();
        company.setCode(COMPANY_CODE);
        company.setName("Test Company");
        companyRepo.save(company);

        role.setCompanyId(company.getId());

        Subscription subscription = new Subscription();
        subscription.setCompanyCode(COMPANY_CODE);
        subscription.setKey(EMPLOYEE_CODE);
        subscription.setCampaign(campaign.getId());
        LinkedList<Subscription> subscriptions = new LinkedList<>();
        subscriptions.add(subscription);
        role.setSubscriptions(subscriptions);

        userRepo.save(user);

        employee = new Employee();
        employee.setCompanyEmail("test@company.com");
        employee.setName("John");
        employee.setSurname("Doe");
        employee.setCompanyId(company.getId());
        employee.setCode(EMPLOYEE_CODE);

        employeeRepo.save(employee);
    }

    /**
     * Test the {@link ProfileResource#getProfile(String)} method.
     *
     * This test verifies that the endpoint returns an HTTP 200 OK status
     * when the correct campaign id is specified.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testGetProfile() throws Exception {

        restMockMvc.perform(
                        get("/api/profile/campaign/{campaign}", campaign.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


    }

    /**
     * Test the {@link ProfileResource#getProfile(String)} method when the user is
     * not authenticated.
     *
     * This test verifies that the endpoint returns an HTTP 404 NOT FOUND status
     * when the correct campaign id is specified, but the user is not authenticated.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testUserAuthNotPresent() throws Exception {

        userRepo.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase("testuser@test.com"))
                .forEach(userRepo::delete);

        User userNotAuth = new User();
        userNotAuth.setUsername("testuser@test.com");

        restMockMvc.perform(
                        get("/api/profile/campaign/{campaign}", campaign.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
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

}
