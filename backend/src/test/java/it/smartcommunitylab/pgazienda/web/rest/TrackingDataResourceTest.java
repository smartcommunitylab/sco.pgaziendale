package it.smartcommunitylab.pgazienda.web.rest;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_DATA;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;
import it.smartcommunitylab.pgazienda.domain.Constants.STAT_FIELD;
import it.smartcommunitylab.pgazienda.service.TrackingDataService;
import it.smartcommunitylab.pgazienda.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = Constants.ROLE_ADMIN)
@SpringBootTest(classes = PGAziendaApp.class)
public class TrackingDataResourceTest {

    private static final String CAMPAIGN_ID = "testCampaign";
    private static final String COMPANY_ID = "testCompany";
    private static final String LOCATION = "testLocation";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrackingDataService trackingDataService;

    @MockBean
    private UserService userService;

    /**
     * Mocks the user service to always return true for the user being in the proper roles.
     * This is needed to allow the tests to run without needing to have a database set up.
     */
    @BeforeEach
    public void setup() {
        Mockito.when(userService.isInCampaignRole(Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.isInCompanyRole(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
    }

    /**
     * Test getting statistics for a campaign.
     *
     * This test verifies that the controller properly calls the tracking data service
     * and returns the statistics in the expected format.
     *
     * The test is parameterized with the following parameters:
     * - campaignId: the ID of the campaign
     * - companyId: the ID of the company
     * - location: the location of the campaign
     * - employeeId: a set of employee IDs
     * - timeGroupBy: the time group by parameter
     * - dataGroupBy: the data group by parameter
     * - fields: a list of fields to include in the statistics
     * - from: the start date of the statistics
     * - to: the end date of the statistics
     * - all: whether to include all employees or not
     */
    @Test
    public void testGetStatistics() throws Exception {
        String campaignId = "campaign123";
        String companyId = "companyABC";
        String location = "New York";
        Set<String> employeeId = new HashSet<>(Arrays.asList("emp1", "emp2", "emp3"));
        GROUP_BY_TIME timeGroupBy = GROUP_BY_TIME.month;
        GROUP_BY_DATA dataGroupBy = GROUP_BY_DATA.company;
        List<STAT_FIELD> fields = Arrays.asList(STAT_FIELD.score);
        boolean all = false;
        String from = "2024-01-01";
        String to = "2024-10-01";

        Mockito.when(trackingDataService.statistics(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anySet(),
                Mockito.any(GROUP_BY_TIME.class),
                Mockito.any(GROUP_BY_DATA.class),
                Mockito.anyList(),
                Mockito.any(),
                Mockito.any(),
                Mockito.anyBoolean()
        )).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/campaigns/{campaignId}/stats/all", campaignId)
                        .param("companyId", companyId)
                        .param("location", location)
                        .param("employeeId", String.join(",", employeeId))
                        .param("timeGroupBy", timeGroupBy.toString())
                        .param("dataGroupBy", dataGroupBy.toString())
                        .param("fields", fields.stream().map(Enum::name).collect(Collectors.joining(",")))
                        .param("from", from)
                        .param("to", to)
                        .param("all", String.valueOf(all)))
                .andExpect(status().isOk());
    }

    /**
     * Tests that the /campaigns/{campaignId}/stats/all/csv endpoint properly
     * calls the TrackingDataService#csvStatistics method and returns a 200 OK
     * response.
     */
    @Test
    public void testGetStatisticsCsv() throws Exception {
        Mockito.doNothing().when(trackingDataService).csvStatistics(
                Mockito.any(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anySet(),
                Mockito.any(GROUP_BY_TIME.class),
                Mockito.any(GROUP_BY_DATA.class),
                Mockito.anyList(),
                Mockito.any(),
                Mockito.any(),
                Mockito.anyBoolean()
        );

        mockMvc.perform(
                get("/api/campaigns/{campaignId}/stats/all/csv", CAMPAIGN_ID)
                        .param("from", "2023-01-01")
                        .param("to", "2023-12-31")
                        .param("timeGroupBy", "month")
                        .param("dataGroupBy", "company")
                        .param("fields", "score")
                        .param("all", "false")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    /**
     * Tests that the endpoints for retrieving campaign statistics return a
     * 403 Forbidden status when the user does not have the necessary campaign
     * role.
     *
     * This test verifies that the controller properly restricts access to the
     * statistics endpoints when the user is not in the campaign role, ensuring
     * that unauthorized users cannot access campaign data.
     *
     * The test performs requests to both the JSON and CSV endpoints and
     * expects a 403 Forbidden response for each.
     */
    @Test
    public void testNotInCampaignRole() throws Exception {
        Mockito.when(userService.isInCampaignRole(Mockito.anyString())).thenReturn(false);

        mockMvc.perform(
                get("/api/campaigns/{campaignId}/stats/all", CAMPAIGN_ID)
                        .param("from", "2023-01-01")
                        .param("to", "2023-12-31")
                        .param("timeGroupBy", "month")
                        .param("dataGroupBy", "company")
                        .param("fields", "score")
                        .param("all", "false")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());

        mockMvc.perform(
                get("/api/campaigns/{campaignId}/stats/all/csv", CAMPAIGN_ID)
                        .param("from", "2023-01-01")
                        .param("to", "2023-12-31")
                        .param("timeGroupBy", "month")
                        .param("dataGroupBy", "company")
                        .param("fields", "score")
                        .param("all", "false")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());

    }

    /**
     * Tests that the endpoints for retrieving campaign statistics return a
     * 403 Forbidden status when the user does not have the necessary company
     * role.
     *
     * This test ensures that the controller properly restricts access to the
     * statistics endpoints when the user is not in the company role, preventing
     * unauthorized users from accessing company-specific data.
     *
     * The test performs requests to both the JSON and CSV endpoints with a
     * specified company ID and expects a 403 Forbidden response for each.
     */
    @Test
    public void testNotInCompanyRole() throws Exception {
        Mockito.when(userService.isInCampaignRole(Mockito.anyString())).thenReturn(false);
        Mockito.when(userService.isInCompanyRole(Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        mockMvc.perform(
                get("/api/campaigns/{campaignId}/stats/all", CAMPAIGN_ID)
                        .param("companyId", "testCompany")
                        .param("from", "2023-01-01")
                        .param("to", "2023-12-31")
                        .param("timeGroupBy", "month")
                        .param("dataGroupBy", "company")
                        .param("fields", "score")
                        .param("all", "false")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());

        mockMvc.perform(
                get("/api/campaigns/{campaignId}/stats/all/csv", CAMPAIGN_ID)
                        .param("companyId", "testCompany")
                        .param("from", "2023-01-01")
                        .param("to", "2023-12-31")
                        .param("timeGroupBy", "month")
                        .param("dataGroupBy", "company")
                        .param("fields", "score")
                        .param("all", "false")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

}
