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
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import it.smartcommunitylab.pgazienda.web.rest.vm.LoginVM;


/**
 * Integration tests for the {@link UserJWTController} REST controller.
 */
@AutoConfigureMockMvc
@SpringBootTest(classes = PGAziendaApp.class)
public class UserJWTControllerITest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;
    
    private MockRestServiceServer mockServer;
    
    @Value("${spring.security.oauth2.client.provider.custom.user-info-uri}")
    private String userInfoEndpoint;
    @Value("${app.engineEndpoint}")
    private String engineEndpoint;

    
    /**
     * Setup a mock server for the {@link RestTemplate} used by the {@link UserJWTController}.
     * This allows us to mock the response from the userinfo endpoint.
     */
    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
    
    /**
     * Set up the test environment by removing all users from the database except the admin user.
     * This allows us to test the {@link UserJWTController} without any existing users getting in the way.
     */
    @BeforeEach
    public void setUp() {
        userRepository.findAll().forEach(u -> {
        	if (!u.getUsername().equals("admin")) userRepository.delete(u);
        });;
    }
    
    /**
     * Tests that the {@link UserJWTController} properly authenticates users
     * and returns a valid JWT token.
     *
     * This test verifies that the controller properly calls the user service
     * to authenticate the user and returns the JWT token in the response.
     *
     * The test is parameterized with the following parameters:
     * - username: the username of the user
     * - password: the password of the user
     *
     * The test performs a POST request to the /api/authenticate endpoint
     * with the username and password of the user as JSON in the request body.
     * It then verifies that the response status is 200 OK and that the
     * JWT token is returned in the response header.
     */
    @Test
    public void testAuthorize() throws Exception {
        User user = new User();
        user.setUsername("user-jwt-controller@example.com");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("test"));

        userRepository.save(user);

        LoginVM login = new LoginVM();
        login.setUsername("user-jwt-controller@example.com");
        login.setPassword("test");
        mockMvc.perform(post("/api/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isString())
            .andExpect(jsonPath("$.id_token").isNotEmpty())
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(is(emptyString()))));
    }

    /**
     * Tests that the {@link UserJWTController} properly authenticates users
     * with the "remember me" option enabled and returns a valid JWT token.
     *
     * This test verifies that the controller correctly calls the user service
     * to authenticate the user with the "remember me" option and returns the
     * JWT token in the response.
     *
     * The test performs a POST request to the /api/authenticate endpoint
     * with the username, password, and "remember me" flag of the user as JSON
     * in the request body. It then verifies that the response status is 200 OK
     * and that the JWT token is returned in the response header.
     */
    @Test
    public void testAuthorizeWithRememberMe() throws Exception {
        User user = new User();
        user.setUsername("user-jwt-controller-remember-me@example.com");
        user.setActivated(true);
        user.setPassword(passwordEncoder.encode("test"));

        userRepository.save(user);

        LoginVM login = new LoginVM();
        login.setUsername("user-jwt-controller-remember-me@example.com");
        login.setPassword("test");
        login.setRememberMe(true);
        mockMvc.perform(post("/api/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isString())
            .andExpect(jsonPath("$.id_token").isNotEmpty())
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(is(emptyString()))));
    }

    /**
     * Tests that the {@link UserJWTController} correctly handles failed login
     * attempts by returning a 401 Unauthorized status.
     *
     * This test verifies that the controller returns a 401 Unauthorized status
     * when an invalid username and password are provided. It also verifies that
     * the JWT token is not returned in the response and that the response header
     * does not contain the Authorization header.
     */
    @Test
    public void testAuthorizeFails() throws Exception {
        LoginVM login = new LoginVM();
        login.setUsername("wrong-user");
        login.setPassword("wrong password");
        mockMvc.perform(post("/api/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.id_token").doesNotExist())
            .andExpect(header().doesNotExist("Authorization"));
    }
    
    /**
     * Tests that the {@link UserJWTController} correctly handles external authentication
     * by sending a request to the user info endpoint and receiving a valid response.
     *
     * This test verifies that the controller can authenticate a user using an external
     * JWT token, fetch user details from the configured user info endpoint, and retrieve
     * roles from the engine endpoint.
     *
     * The test performs a GET request to the /api/authenticate/extjwt endpoint with a
     * Bearer token in the Authorization header. It then validates that the response is
     * successful, contains a non-empty JWT token, and includes the Authorization header.
     * Additionally, it checks that a user with the expected username is present in the
     * repository.
     */
    @Test
    public void testExternalAuth() throws Exception {
    	Map<String, String> map = new HashMap<>();
    	map.put("userId", "1");
    	map.put("name", "Name");
    	map.put("surname", "Surname");
    	
    	mockServer.expect(requestTo(new URI(userInfoEndpoint)))
    	          .andExpect(method(HttpMethod.GET))
    	          .andRespond(withStatus(HttpStatus.OK)
    	          .contentType(MediaType.APPLICATION_JSON)
    	          .body(new ObjectMapper().writeValueAsString(map))
    	        );

    	mockServer.expect(requestTo(new URI(engineEndpoint + "api/console/role/my")))
    	          .andExpect(method(HttpMethod.GET))
    	          .andRespond(withStatus(HttpStatus.OK)
    	          .contentType(MediaType.APPLICATION_JSON)
    	          .body("{}")
    	        );
                
        mockMvc.perform(get("/api/authenticate/extjwt")
        		.header("Authorization", "Bearer 123456"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_token").isString())
                .andExpect(jsonPath("$.id_token").isNotEmpty())
                .andExpect(header().string("Authorization", not(nullValue())))
                .andExpect(header().string("Authorization", not(is(emptyString()))));
        
        Optional<User> user = userRepository.findOneByUsernameIgnoreCase("1@test.com");
        assertThat(user).isPresent();

    }
    
}
