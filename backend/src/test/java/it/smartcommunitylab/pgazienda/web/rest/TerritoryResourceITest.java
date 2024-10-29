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

import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.Territory;
import it.smartcommunitylab.pgazienda.service.PGAppService;
import org.springframework.web.client.RestTemplate;

/**
 * @author raman
 *
 */
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = Constants.ROLE_ADMIN)
@SpringBootTest(classes = PGAziendaApp.class)
public class TerritoryResourceITest {

    /**
	 *
	 */

    @Value("${spring.data.mongodb.uri}")
    private String mongoDbUri;

	private static final String T_ID = "TAA";

	static final String ADMIN = "admin";

    @Autowired
    private MockMvc restMockMvc;
    @Autowired
    private PGAppService service;

    MockRestServiceServer mockServer;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Set up the test environment by creating a mock server to intercept
     * requests made to the MongoDB API.
     *
     * The mock server is configured to expect requests to the territory endpoint
     * and respond with a 200 response and a JSON content type.
     */
    @BeforeEach
    public void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);

        mockServer.expect(manyTimes(), requestTo(mongoDbUri + "/publicapi/territory"))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Test read territories
     *
     * Verifies that the read endpoint returns a list of territories.
     * Initially checks that the list is empty after resetting territories.
     * Then adds a test territory and verifies that the list contains one territory.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testReadTerritories() throws Exception {
        service.resetTerritories();
        restMockMvc.perform(
                get("/api/territories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

    	Territory t = testTerritory();
        service.setTerritories(Collections.singletonList(t));

        restMockMvc.perform(
                get("/api/territories"))
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
}
