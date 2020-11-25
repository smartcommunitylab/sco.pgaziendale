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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import it.smartcommunitylab.pgazienda.dto.DataModelDTO;
import it.smartcommunitylab.pgazienda.repository.PGAppRepository;

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
    private MockMvc restMockMvc;

    @BeforeEach
    public void setup() {
        appRepo.deleteAll();
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
}
