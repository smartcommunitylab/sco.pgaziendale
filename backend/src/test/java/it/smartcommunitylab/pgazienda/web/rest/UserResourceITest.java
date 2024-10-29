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
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.RandomStringUtils;
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
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.repository.UserRepository;

/**
 * Integration tests for the {@link UserResource} REST controller.
 */
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = Constants.ROLE_ADMIN)
@SpringBootTest(classes = PGAziendaApp.class)
public class UserResourceITest {

    /**
	 * 
	 */
	private static final String COMPANY_ID = "companyId";
	private static final String DEFAULT_LOGIN = "johndoe@example.com";
    private static final String UPDATED_LOGIN = "someuser@example.com";

    private static final String DEFAULT_PASSWORD = "passjohndoe";
    private static final String UPDATED_PASSWORD = "passsomeuser";

    private static final String DEFAULT_FIRSTNAME = "john";
    private static final String UPDATED_FIRSTNAME = "someuserFirstName";

    private static final String DEFAULT_LASTNAME = "doe";
    private static final String UPDATED_LASTNAME = "someuserLastName";



    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc restUserMockMvc;

    private User user;

    /**
     * Create a User.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which has a required relationship to the User entity.
     */
    public static User createEntity() {
        User user = new User();
        user.setUsername(DEFAULT_LOGIN);
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setName(DEFAULT_FIRSTNAME);
        user.setSurname(DEFAULT_LASTNAME);
        user.setRoles(Collections.singletonList(UserRole.createMobilityManager(COMPANY_ID)));
        return user;
    }

    /**
     * Initialize the database with one user.
     * <p>
     * The user is created by calling {@link #createEntity()}.
     */
    @BeforeEach
    public void initTest() {
        userRepository.findAll().forEach(u -> {
        	if (!u.getUsername().equals("admin")) userRepository.delete(u);
        });;
        user = createEntity();
    }

    /**
     * Creates a User and validates that the created user is in the database.
     */
    @Test
    public void createUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        User managedUserVM = new User();
        managedUserVM.setUsername(DEFAULT_LOGIN);
        managedUserVM.setPassword(DEFAULT_PASSWORD);
        managedUserVM.setName(DEFAULT_FIRSTNAME);
        managedUserVM.setSurname(DEFAULT_LASTNAME);
        managedUserVM.setActivated(true);
        managedUserVM.setRoles(Collections.singletonList(UserRole.createMobilityManager(COMPANY_ID)));

        restUserMockMvc.perform(post("/api/companies/{companyId}/users", COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().is2xxSuccessful());

        assertPersistedUsers(users -> {
            assertThat(users).hasSize(databaseSizeBeforeCreate + 1);
            User testUser = users.get(users.size() - 1);
            assertThat(testUser.getUsername()).isEqualTo(DEFAULT_LOGIN);
            assertThat(testUser.getName()).isEqualTo(DEFAULT_FIRSTNAME);
            assertThat(testUser.getSurname()).isEqualTo(DEFAULT_LASTNAME);
        });
    }

    /**
     * Creates a User with an existing ID.
     *
     * This is an alternative to creating a User, so that the test can also succeed
     * when the JHipster server uses the {@link org.springframework.boot.autoconfigure.ConfigurationProperties}
     * Loader configuration, which the {@link UserResourceIntTest} does. To differentiate
     * this test from the {@link UserResourceIntTest}, a User is created with an existing ID.
     *
     * A User with an existing ID cannot be created, so this API call must fail.
     */
    @Test
    public void createUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        User managedUserVM = new User();
        managedUserVM.setId("1L");
        managedUserVM.setUsername(DEFAULT_LOGIN);
        managedUserVM.setPassword(DEFAULT_PASSWORD);
        managedUserVM.setName(DEFAULT_FIRSTNAME);
        managedUserVM.setSurname(DEFAULT_LASTNAME);
        managedUserVM.setActivated(true);
        managedUserVM.setRoles(Collections.singletonList(UserRole.createMobilityManager(COMPANY_ID)));

        restUserMockMvc.perform(post("/api/companies/{companyId}/users", COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isBadRequest());

        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
    }

    /**
     * Creates a User with an existing login.
     *
     * A User with an existing login cannot be created, so this API call must fail.
     */
    @Test
    public void createUserWithExistingLogin() throws Exception {

        userRepository.save(user);
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        User managedUserVM = new User();
        managedUserVM.setUsername(DEFAULT_LOGIN);// this login should already be used
        managedUserVM.setPassword(DEFAULT_PASSWORD);
        managedUserVM.setName(DEFAULT_FIRSTNAME);
        managedUserVM.setSurname(DEFAULT_LASTNAME);
        managedUserVM.setActivated(true);
        managedUserVM.setRoles(Collections.singletonList(UserRole.createMobilityManager(COMPANY_ID)));

        restUserMockMvc.perform(post("/api/companies/{companyId}/users", COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isBadRequest());

        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
    }

    /**
     * Test case for verifying the retrieval of all users.
     *
     * This test saves a user in the repository and performs a GET request
     * to retrieve all users associated with a company. It expects the operation
     * to return an HTTP 200 OK status, and verifies that the response contains
     * the username, name, and surname of the saved user in JSON format.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void getAllUsers() throws Exception {

        userRepository.save(user);

        restUserMockMvc.perform(get("/api/companies/{companyId}/users", COMPANY_ID)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_LOGIN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_LASTNAME)));
    }

    /**
     * Test case for verifying the behavior of the get user endpoint
     * when attempting to retrieve a non-existing user.
     *
     * The test expects the endpoint to return an HTTP 404 Not Found status.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void getNonExistingUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/unknown"))
            .andExpect(status().isNotFound());
    }

    /**
     * Test case for verifying the behavior of the update user endpoint
     * when attempting to update an existing user.
     *
     * The test expects the endpoint to return an HTTP 200 OK status and
     * to update the user associated with the given id.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void updateUser() throws Exception {

        userRepository.save(user);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        User updatedUser = userRepository.findById(user.getId()).get();

        User managedUserVM = new User();
        managedUserVM.setId(updatedUser.getId());
        managedUserVM.setUsername(updatedUser.getUsername());
        managedUserVM.setPassword(UPDATED_PASSWORD);
        managedUserVM.setName(UPDATED_FIRSTNAME);
        managedUserVM.setSurname(UPDATED_LASTNAME);
        managedUserVM.setActivated(updatedUser.isActivated());
        managedUserVM.setCreatedBy(updatedUser.getCreatedBy());
        managedUserVM.setCreatedDate(updatedUser.getCreatedDate());
        managedUserVM.setLastModifiedBy(updatedUser.getLastModifiedBy());
        managedUserVM.setLastModifiedDate(updatedUser.getLastModifiedDate());
        managedUserVM.setRoles(Collections.singletonList(UserRole.createMobilityManager(COMPANY_ID)));

        restUserMockMvc.perform(put("/api/companies/{companyId}/users", COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isOk());

        assertPersistedUsers(users -> {
            assertThat(users).hasSize(databaseSizeBeforeUpdate);
            User testUser = users.get(users.size() - 1);
            assertThat(testUser.getName()).isEqualTo(UPDATED_FIRSTNAME);
            assertThat(testUser.getSurname()).isEqualTo(UPDATED_LASTNAME);
        });
    }

    /**
     * Test case for verifying the behavior of the update user endpoint
     * when attempting to update an existing user with a new login.
     *
     * The test expects the endpoint to return an HTTP 200 OK status and
     * to update the user associated with the given id.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void updateUserLogin() throws Exception {

        userRepository.save(user);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        User updatedUser = userRepository.findById(user.getId()).get();

        User managedUserVM = new User();
        managedUserVM.setId(updatedUser.getId());
        managedUserVM.setUsername(UPDATED_LOGIN);
        managedUserVM.setPassword(UPDATED_PASSWORD);
        managedUserVM.setName(UPDATED_FIRSTNAME);
        managedUserVM.setSurname(UPDATED_LASTNAME);
        managedUserVM.setActivated(updatedUser.isActivated());
        managedUserVM.setCreatedBy(updatedUser.getCreatedBy());
        managedUserVM.setCreatedDate(updatedUser.getCreatedDate());
        managedUserVM.setLastModifiedBy(updatedUser.getLastModifiedBy());
        managedUserVM.setLastModifiedDate(updatedUser.getLastModifiedDate());
        managedUserVM.setRoles(Collections.singletonList(UserRole.createMobilityManager(COMPANY_ID)));

        restUserMockMvc.perform(put("/api/companies/{companyId}/users", COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isOk());

        assertPersistedUsers(users -> {
            assertThat(users).hasSize(databaseSizeBeforeUpdate);
            User testUser = users.get(users.size() - 1);
            assertThat(testUser.getUsername()).isEqualTo(UPDATED_LOGIN);
            assertThat(testUser.getName()).isEqualTo(UPDATED_FIRSTNAME);
            assertThat(testUser.getSurname()).isEqualTo(UPDATED_LASTNAME);
        });
    }

    /**
     * Updates a user with an existing login.
     *
     * The test expects the endpoint to return an HTTP 400 Bad Request status.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void updateUserExistingLogin() throws Exception {

        userRepository.save(user);

        User anotherUser = new User();
        anotherUser.setUsername("someuser");
        anotherUser.setPassword(RandomStringUtils.random(60));
        anotherUser.setActivated(true);
        anotherUser.setName("java");
        anotherUser.setSurname("hipster");
        userRepository.save(anotherUser);

        User updatedUser = userRepository.findById(user.getId()).get();

        User managedUserVM = new User();
        managedUserVM.setId(updatedUser.getId());
        managedUserVM.setUsername("someuser");// this login should already be used by anotherUser
        managedUserVM.setPassword(updatedUser.getPassword());
        managedUserVM.setName(updatedUser.getName());
        managedUserVM.setSurname(updatedUser.getSurname());
        managedUserVM.setActivated(updatedUser.isActivated());
        managedUserVM.setCreatedBy(updatedUser.getCreatedBy());
        managedUserVM.setCreatedDate(updatedUser.getCreatedDate());
        managedUserVM.setLastModifiedBy(updatedUser.getLastModifiedBy());
        managedUserVM.setLastModifiedDate(updatedUser.getLastModifiedDate());
        managedUserVM.setRoles(Collections.singletonList(UserRole.createMobilityManager(COMPANY_ID)));

        restUserMockMvc.perform(put("/api/companies/{companyId}/users", COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isBadRequest());
    }

    /**
     * Deletes a user.
     *
     * The test expects the endpoint to return an HTTP 200 OK status and
     * to delete the user associated with the given id.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void deleteUser() throws Exception {

        userRepository.save(user);
        int databaseSizeBeforeDelete = userRepository.findAll().size();

        restUserMockMvc.perform(delete("/api/companies/{companyId}/users/{login}", COMPANY_ID, user.getUsername())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful());

        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeDelete - 1));
    }

    /**
     * Asserts the persisted users against the given assertion.
     *
     * @param userAssertion the assertion to perform on the persisted users
     */
    private void assertPersistedUsers(Consumer<List<User>> userAssertion) {
        userAssertion.accept(userRepository.findAll());
    }
}
