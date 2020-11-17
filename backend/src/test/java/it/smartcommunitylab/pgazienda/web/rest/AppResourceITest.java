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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.*;

import java.util.List;

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
import it.smartcommunitylab.pgazienda.domain.PGApp;
import it.smartcommunitylab.pgazienda.repository.PGAppRepository;

/**
 * @author raman
 *
 */
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = Constants.ROLE_ADMIN)
@SpringBootTest(classes = PGAziendaApp.class)
public class AppResourceITest {

    /**
	 * 
	 */
	private static final String APP_ID = "externalAppId";

	static final String ADMIN = "admin";

    @Autowired
    private PGAppRepository appRepo;
    
    @Autowired
    private MockMvc restMockMvc;

    @BeforeEach
    public void setup() {
        appRepo.deleteAll();
    }
    
    @Test
    public void testCreate() throws Exception {
    	PGApp app = testApp();

        restMockMvc.perform(
                post("/api/apps")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(app)))
                .andExpect(status().isOk());

            PGApp updatedApp = appRepo.findById(app.getId()).orElse(null);
            assertThat(updatedApp).isNotNull();
            
            assertThat(updatedApp.getName()).isEqualTo(app.getName());
    }
    
    @Test
    public void testUpdate() throws Exception {
    	PGApp app = testApp();
    	app = appRepo.save(app);
    	

        restMockMvc.perform(
                put("/api/apps/{appId}", app.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(app)))
                .andExpect(status().isOk());

            PGApp updatedApp = appRepo.findById(app.getId()).orElse(null);
            assertThat(updatedApp).isNotNull();
            assertThat(updatedApp.getName()).isEqualTo(app.getName());
    }
    
    @Test
    public void testDelete() throws Exception {
    	PGApp app = testApp();
    	app = appRepo.save(app);

        restMockMvc.perform(
                delete("/api/apps/{appId}", app.getId()))
                .andExpect(status().isOk());

            List<PGApp> updated = appRepo.findAll();
            assertThat(updated.size()).isEqualTo(0);
    }
    
    @Test
    public void testReadApps() throws Exception {
        restMockMvc.perform(
                get("/api/apps"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));

    	PGApp app = testApp();
    	app = appRepo.save(app);
    	
        restMockMvc.perform(
                get("/api/apps"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }
    
    @Test
    @WithUnauthenticatedMockUser
    public void testReadAppsPublic() throws Exception {
    	PGApp app = testApp();
    	app = appRepo.save(app);
    	
        restMockMvc.perform(
                get("/api/public/apps"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    private PGApp testApp() {
    	PGApp app = new PGApp();
    	app.setName("test app");
    	app.setId(APP_ID);
    	return app;
    }
}
