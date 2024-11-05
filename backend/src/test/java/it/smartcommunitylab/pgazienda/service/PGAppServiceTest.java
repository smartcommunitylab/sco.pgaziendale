package it.smartcommunitylab.pgazienda.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class PGAppServiceTest {

    private static final String T_ID = "TAA";
    private static final String CAMPAIGN_ID = "ferrara_bike2work";

    @InjectMocks
    private PGAppService pgAppService;

    @Mock
    private RestTemplate restTemplate;

    /**
     * This method is called before each test, and is used to set up any mocks or other test data.
     */
    @BeforeEach
    void setUp() {

    }

    /**
     * This test checks that the {@link PGAppService#retrieveExternalCampaigns()} method
     * correctly converts the JSON response from the external campaign service into
     * a list of {@link Campaign} objects.
     *
     * The test mocks the JSON response to contain a single campaign with some example
     * values for the campaign's properties. It then checks that the resulting list
     * contains a single campaign with the expected values.
     *
     * Note that this test does not check the actual values of the campaign's properties,
     * as these are tested in other tests. Instead, it checks that the method correctly
     * handles the JSON response and creates a list of campaigns with the correct
     * properties.
     */
    @Test
    void testRetrieveExternalCampaigns() {

        Map<String, Object> description = new HashMap<>();
        description.put("it", "Test Campaign");
        description.put("en", "Test Campaign");

        Map<String, Object> logo = new HashMap<>();
        logo.put("url", "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");

        Map<String, Object> name = new HashMap<>();
        name.put("it", "Test Campaign");
        name.put("en", "Test Campaign");

        Map<String, Object> virtualScore = new HashMap<>();
        virtualScore.put("trackLimits", 50);
        virtualScore.put("scoreLimits", 1000);

        Map<String, Object> specificData = new HashMap<>();
        specificData.put("virtualScore", virtualScore);
        specificData.put("useEmployeeLocation", false);
        specificData.put("useMultiLocation", false);


        Map<String, Object>[] mockCampaigns = new Map[1];
        mockCampaigns[0] = new HashMap<>();
        mockCampaigns[0].put("name", name);
        mockCampaigns[0].put("campaignId", CAMPAIGN_ID);
        mockCampaigns[0].put("title", "campaign");
        mockCampaigns[0].put("territoryId", T_ID);
        mockCampaigns[0].put("description", description);
        mockCampaigns[0].put("dateFrom", 20200101L);
        mockCampaigns[0].put("dateTo", 20210101L);
        mockCampaigns[0].put("logo", logo);
        mockCampaigns[0].put("means", Collections.singletonList("bike"));
        mockCampaigns[0].put("useMultiLocation", true);
        mockCampaigns[0].put("specificData", specificData);

        when(restTemplate.getForObject(anyString(), eq(Map[].class))).thenReturn(mockCampaigns);


        List<Campaign> campaigns = pgAppService.retrieveExternalCampaigns();


        assertNotNull(campaigns);
        assertEquals(1, campaigns.size());
        assertEquals(CAMPAIGN_ID, campaigns.get(0).getId());
        assertEquals("Test Campaign", campaigns.get(0).getDescription());
    }
}
