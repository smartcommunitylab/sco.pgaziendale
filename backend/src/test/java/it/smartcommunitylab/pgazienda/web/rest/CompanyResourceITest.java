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

import java.util.Collections;
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
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.PGApp;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.PGAppRepository;

/**
 * @author raman
 *
 */
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = Constants.ROLE_ADMIN)
@SpringBootTest(classes = PGAziendaApp.class)
public class CompanyResourceITest {

    /**
	 * 
	 */
	private static final String APP_ID = "externalAppId";

	static final String ADMIN = "admin";

    @Autowired
    private CompanyRepository companyRepo;
    @Autowired
    private PGAppRepository appRepo;
    
    @Autowired
    private MockMvc restMockMvc;

    @BeforeEach
    public void setup() {
        appRepo.deleteAll();
    	PGApp app = testApp();
    	appRepo.save(app);

    	companyRepo.deleteAll();
    }
    
    @Test
    public void testCreateCompany() throws Exception {
    	Company c = testCompany();
    	c.setEnabledApps(Collections.singletonList(APP_ID));

        restMockMvc.perform(
                post("/api/companies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(c)))
                .andExpect(status().isOk());

            List<Company> updated = companyRepo.findByName(c.getName(), null).getContent();
            assertThat(updated.size()).isEqualTo(1);
            Company updatedCompany = updated.get(0);
            
            assertThat(updatedCompany.getName()).isEqualTo(c.getName());
            assertThat(updatedCompany.getAddress()).isEqualTo(c.getAddress());
            assertThat(updatedCompany.getContactEmail()).isEqualTo(c.getContactEmail());
            assertThat(updatedCompany.getContactPhone()).isEqualTo(c.getContactPhone());
            assertThat(updatedCompany.getEnabledApps()).isEqualTo(c.getEnabledApps());
            assertThat(updatedCompany.getLogo()).isEqualTo(c.getLogo());
            assertThat(updatedCompany.getWeb()).isEqualTo(c.getWeb());

    }
    
    @Test
    public void testUpdateCompany() throws Exception {
    	Company c = new Company();
    	c.setAddress("address1");
    	c.setContactEmail("email1");
    	c.setContactPhone("1231");
    	c.setLogo("logo1");
    	c.setName("company1");
    	c.setWeb("web1");
    	c.setEnabledApps(Collections.singletonList(APP_ID));
    	c = companyRepo.save(c);
    	

        restMockMvc.perform(
                put("/api/companies/{companyId}", c.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(c)))
                .andExpect(status().isOk());

            Company updatedCompany = companyRepo.findById(c.getId()).orElse(null);
            assertThat(updatedCompany).isNotNull();
            assertThat(updatedCompany.getName()).isEqualTo(c.getName());
            assertThat(updatedCompany.getAddress()).isEqualTo(c.getAddress());
            assertThat(updatedCompany.getContactEmail()).isEqualTo(c.getContactEmail());
            assertThat(updatedCompany.getContactPhone()).isEqualTo(c.getContactPhone());
            assertThat(updatedCompany.getEnabledApps()).isEqualTo(c.getEnabledApps());
            assertThat(updatedCompany.getLogo()).isEqualTo(c.getLogo());
            assertThat(updatedCompany.getWeb()).isEqualTo(c.getWeb());

    }
    
    @Test
    public void testDeleteCompany() throws Exception {
    	Company c = testCompany();
    	c.setEnabledApps(Collections.singletonList(APP_ID));
    	c = companyRepo.save(c);
    	

        restMockMvc.perform(
                delete("/api/companies/{companyId}", c.getId()))
                .andExpect(status().isOk());

            List<Company> updated = companyRepo.findByName(c.getName(), null).getContent();
            assertThat(updated.size()).isEqualTo(0);
    }
    
    @Test
    public void testReadCompany() throws Exception {
    	Company c = testCompany();
    	c.setEnabledApps(Collections.singletonList(APP_ID));
    	c = companyRepo.save(c);
    	
        restMockMvc.perform(
                get("/api/companies/{companyId}", c.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(c.getName()));
    }

    
    @Test
    public void testReadCompanies() throws Exception {
        restMockMvc.perform(
                get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value("0"));

        Company c = testCompany();
    	c.setEnabledApps(Collections.singletonList(APP_ID));
    	c = companyRepo.save(c);
    	
        restMockMvc.perform(
                get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.numberOfElements").value("1"));
    }

    private Company testCompany() {
    	Company c = new Company();
    	c.setAddress("address");
    	c.setContactEmail("email");
    	c.setContactPhone("123");
    	c.setLogo("logo");
    	c.setName("company");
    	c.setWeb("web");
    	return c;
    }

    private PGApp testApp() {
    	PGApp app = new PGApp();
    	app.setName("test app");
    	app.setId(APP_ID);
    	return app;
    }
}
