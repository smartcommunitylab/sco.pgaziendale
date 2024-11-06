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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.*;

import it.smartcommunitylab.pgazienda.web.rest.errors.AccountResourceException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import it.smartcommunitylab.pgazienda.service.UserService;
import it.smartcommunitylab.pgazienda.web.rest.vm.KeyAndPasswordVM;
import it.smartcommunitylab.pgazienda.web.rest.vm.PasswordChangeVM;


/**
 * Integration tests for the {@link AccountResource} REST controller.
 */
@AutoConfigureMockMvc
@WithMockUser(value = AccountResourceITest.TEST_USER_LOGIN)
@SpringBootTest(classes = PGAziendaApp.class)
public class AccountResourceITest {
    static final String TEST_USER_LOGIN = "test@example.com";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc restAccountMockMvc;
    @Autowired
    private AccountResource accountResource;

    /**
     * Initializes the test by removing all users except the default admin user.
     * This is done to ensure that the tests are isolated and start with a clean
     * slate.
     */
    @BeforeEach
    public void setup() {
        userRepository.findAll().forEach(u -> {
        	if (!u.getUsername().equals("admin")) userRepository.delete(u);
        });;
    }

    /**
     * Test case for verifying the behavior of the authenticate endpoint
     * when accessed by a non-authenticated (unauthenticated) user.
     *
     * The test expects the endpoint to return an HTTP 200 OK status with
     * an empty response body when no user is authenticated.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithUnauthenticatedMockUser
    public void testNonAuthenticatedUser() throws Exception {
        restAccountMockMvc.perform(get("/api/authenticate")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(""));
    }

    /**
     * Test case for verifying the behavior of the authenticate endpoint
     * when accessed by an authenticated user.
     *
     * The test expects the endpoint to return an HTTP 200 OK status with
     * a response body containing the username of the authenticated user.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testAuthenticatedUser() throws Exception {
        restAccountMockMvc.perform(get("/api/authenticate")
            .with(request -> {
                request.setRemoteUser(TEST_USER_LOGIN);
                return request;
            })
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("\""+TEST_USER_LOGIN + "\""));
    }

    /**
     * Test case for verifying the behavior of the getAccount endpoint
     * when accessed by an authenticated user who has an account in the system.
     *
     * The test expects the endpoint to return an HTTP 200 OK status with
     * a response body containing the account details of the authenticated user.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testGetExistingAccount() throws Exception {
        Set<String> authorities = new HashSet<>();
        authorities.add(Constants.ROLE_ADMIN);

        User user = new User();
        user.setUsername(TEST_USER_LOGIN);
        user.setName("john");
        user.setSurname("doe");
        user.setRoles(Collections.singletonList(UserRole.createAdminRole()));
        userService.createUser(user, null);

        restAccountMockMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.username").value(TEST_USER_LOGIN))
            .andExpect(jsonPath("$.name").value("john"))
            .andExpect(jsonPath("$.surname").value("doe"));
    }

    /**
     * Test case for verifying the behavior of the getAccount endpoint
     * when accessed by an authenticated user who does not have an account in the system.
     *
     * The test expects the endpoint to return an HTTP 400 Bad Request status with
     * a response body containing a Problem in JSON format.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testGetUnknownAccount() throws Exception {
        restAccountMockMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().isBadRequest());
    }

    /**
     * Test case for verifying the behavior of the activate endpoint
     * when a valid activation key is provided.
     *
     * The test expects the endpoint to return an HTTP 200 OK status and
     * to activate the user account associated with the activation key.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testActivateAccount() throws Exception {
        final String activationKey = "some activation key";
        User user = new User();
        user.setUsername("activate-account@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(false);
        user.setActivationKey(activationKey);

        userRepository.save(user);

        restAccountMockMvc.perform(get("/api/activate?key={activationKey}", activationKey))
            .andExpect(status().isOk());

        user = userRepository.findOneByUsernameIgnoreCase(user.getUsername()).orElse(null);
        assertThat(user.isActivated()).isTrue();
    }

    /**
     * Test case for verifying the behavior of the activate endpoint
     * when an invalid activation key is provided.
     *
     * The test expects the endpoint to return an HTTP 400 Bad Request status.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testActivateAccountWithWrongKey() throws Exception {
        restAccountMockMvc.perform(get("/api/activate?key=wrongActivationKey"))
            .andExpect(status().isBadRequest());
    }


    /**
     * Test case for verifying the behavior of the save endpoint
     * when a valid user data is provided.
     *
     * The test expects the endpoint to return an HTTP 200 OK status and
     * to update the user account associated with the username.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithMockUser("save-account@example.com")
    public void testSaveAccount() throws Exception {
        User user = new User();
        user.setUsername("save-account@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);

        userRepository.save(user);

        User userDTO = new User();
        userDTO.setName("firstname");
        userDTO.setSurname("lastname");
        userDTO.setUsername("save-account@example.com");
        userDTO.setActivated(false);
        userDTO.setRoles(Collections.singletonList(UserRole.createAdminRole()));

        restAccountMockMvc.perform(
            post("/api/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByUsernameIgnoreCase(user.getUsername()).orElse(null);
        assertThat(updatedUser.getName()).isEqualTo(userDTO.getName());
        assertThat(updatedUser.getSurname()).isEqualTo(userDTO.getSurname());
        assertThat(updatedUser.getUsername()).isEqualTo(userDTO.getUsername());
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(updatedUser.isActivated()).isEqualTo(true);
        assertThat(updatedUser.getRoles()).isEmpty();

    }

    /**
     * Test case for verifying the behavior of the save endpoint
     * when a user is not found in the system.
     *
     * The test expects the endpoint to return an HTTP 400 Bad Request status
     * with an exception indicating that the user could not be found.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithMockUser("save-account@example.com")
    public void testSaveAccountUserNotFound() throws Exception {

        userRepository.deleteAll();

        User userDTO = new User();
        userDTO.setUsername("save-account@example.com");

        restAccountMockMvc.perform(
                        post("/api/account")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccountResourceException))
                .andExpect(result -> assertEquals("User could not be found", result.getResolvedException().getMessage()));
    }

    /**
     * Test case for verifying the behavior of the save endpoint
     * when a user is passed with an invalid email.
     *
     * The test expects the endpoint to return an HTTP 400 Bad Request status
     * with an exception indicating that the user could not be found.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithMockUser("save-invalid-email@example.com")
    public void testSaveInvalidEmail() throws Exception {

        User userDTO = new User();
        userDTO.setName("firstname");
        userDTO.setSurname("lastname");
        userDTO.setUsername("invalid email");
        userDTO.setActivated(false);
        userDTO.setRoles(Collections.singletonList(UserRole.createAdminRole()));

        restAccountMockMvc.perform(
            post("/api/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isBadRequest());

        assertThat(userRepository.findOneByUsernameIgnoreCase("invalid email")).isNotPresent();
    }

    /**
     * Test case for verifying the behavior of the save endpoint
     * when a user is passed with an existing email and login.
     *
     * The test expects the endpoint to return an HTTP 200 OK status
     * with the updated user in the response body.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithMockUser("save-existing-email-and-login@example.com")
    public void testSaveExistingEmailAndLogin() throws Exception {
        User user = new User();
        user.setUsername("save-existing-email-and-login@example.com");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);

        userRepository.save(user);

        User userDTO = new User();
        userDTO.setName("firstname");
        userDTO.setSurname("lastname");
        userDTO.setUsername("save-existing-email-and-login@example.com");
        userDTO.setActivated(false);
        userDTO.setRoles(Collections.singletonList(UserRole.createAdminRole()));

        restAccountMockMvc.perform(
            post("/api/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByUsernameIgnoreCase("save-existing-email-and-login@example.com").orElse(null);
        assertThat(updatedUser.getUsername()).isEqualTo("save-existing-email-and-login@example.com");
    }

    /**
     * Test case for verifying the behavior of the change password endpoint
     * when the user is passing an invalid current password.
     *
     * The test expects the endpoint to return an HTTP 400 Bad Request status
     * and the user's password to remain unchanged.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithMockUser("change-password-wrong-existing-password@example.com")
    public void testChangePasswordWrongExistingPassword() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setUsername("change-password-wrong-existing-password@example.com");
        userRepository.save(user);

        restAccountMockMvc.perform(post("/api/account/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeVM("1"+currentPassword, "new password")))
)
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByUsernameIgnoreCase("change-password-wrong-existing-password@example.com").orElse(null);
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(currentPassword, updatedUser.getPassword())).isTrue();
    }

    /**
     * Test case for verifying the behavior of the change password endpoint
     * when the user is passing the correct current password.
     *
     * The test expects the endpoint to return an HTTP 200 OK status
     * and the user's password to be changed.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithMockUser("change-password@example.com")
    public void testChangePassword() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setUsername("change-password@example.com");
        userRepository.save(user);

        restAccountMockMvc.perform(post("/api/account/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeVM(currentPassword, "new password")))
)
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByUsernameIgnoreCase("change-password@example.com").orElse(null);
        assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isTrue();
    }

    /**
     * Test case for verifying the behavior of the change password endpoint
     * when the user is passing a new password that is too small.
     *
     * The test expects the endpoint to return an HTTP 400 Bad Request status
     * and the user's password to remain unchanged.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithMockUser("change-password-too-small@example.com")
    public void testChangePasswordTooSmall() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setUsername("change-password-too-small@example.com");
        userRepository.save(user);

        String newPassword = RandomStringUtils.random(AccountResource.PASSWORD_MIN_LENGTH - 1);

        restAccountMockMvc.perform(post("/api/account/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeVM(currentPassword, newPassword)))
)
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByUsernameIgnoreCase("change-password-too-small@example.com").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    /**
     * Test case for verifying the behavior of the change password endpoint
     * when the user is passing a new password that is too long.
     *
     * The test expects the endpoint to return an HTTP 400 Bad Request status
     * and the user's password to remain unchanged.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithMockUser("change-password-too-long@example.com")
    public void testChangePasswordTooLong() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setUsername("change-password-too-long@example.com");
        userRepository.save(user);

        String newPassword = RandomStringUtils.random(AccountResource.PASSWORD_MAX_LENGTH + 1);

        restAccountMockMvc.perform(post("/api/account/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeVM(currentPassword, newPassword)))
)
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByUsernameIgnoreCase("change-password-too-long@example.com").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    /**
     * Test case for verifying the behavior of the change password endpoint
     * when the user is passing an empty new password.
     *
     * The test expects the endpoint to return an HTTP 400 Bad Request status
     * and the user's password to remain unchanged.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithMockUser("change-password-empty@example.com")
    public void testChangePasswordEmpty() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(60);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setUsername("change-password-empty@example.com");
        userRepository.save(user);

        restAccountMockMvc.perform(post("/api/account/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeVM(currentPassword, "")))
)
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByUsernameIgnoreCase("change-password-empty@example.com").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }

    /**
     * Test case for verifying the behavior of the request password reset endpoint
     * when a valid user email is passed.
     *
     * The test expects the endpoint to return an HTTP 200 OK status
     * and the user's password reset key to be updated.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testRequestPasswordReset() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setUsername("password-reset@example.com");
        userRepository.save(user);

        restAccountMockMvc.perform(post("/api/account/reset-password/init")
            .contentType(MediaType.APPLICATION_JSON)
            .content("\"password-reset@example.com\"")
)
            .andExpect(status().isOk());
    }

    /**
     * Test case for verifying the behavior of the request password reset endpoint
     * when a valid user email is provided in uppercase.
     *
     * The test expects the endpoint to return an HTTP 200 OK status,
     * indicating that the request is processed successfully, regardless
     * of the case of the email.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testRequestPasswordResetUpperCaseEmail() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setUsername("password-reset@example.com");
        userRepository.save(user);

        restAccountMockMvc.perform(post("/api/account/reset-password/init")
            .contentType(MediaType.APPLICATION_JSON)
            .content("\"password-reset@EXAMPLE.COM\"")
)
            .andExpect(status().isOk());
    }

    /**
     * Test case for verifying the behavior of the request password reset endpoint
     * when an invalid user email is passed.
     *
     * The test expects the endpoint to return an HTTP 200 OK status,
     * simulating a successful request even if the email does not exist,
     * to prevent checking which emails actually exist in the system.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testRequestPasswordResetWrongEmail() throws Exception {
        restAccountMockMvc.perform(
            post("/api/account/reset-password/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"password-reset-wrong-email@example.com\""))
            .andExpect(status().isOk());
    }

    /**
     * Test case for verifying the behavior of the finish password reset endpoint
     * when a valid reset key and new password are provided.
     *
     * The test expects the endpoint to return an HTTP 200 OK status
     * and the user's password to be successfully updated.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testFinishPasswordReset() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setUsername("finish-password-reset@example.com");
        user.setResetDate(Instant.now().plusSeconds(60));
        user.setResetKey("reset key");
        userRepository.save(user);

        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey(user.getResetKey());
        keyAndPassword.setNewPassword("new password");

        restAccountMockMvc.perform(
            post("/api/account/reset-password/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByUsernameIgnoreCase(user.getUsername()).orElse(null);
        assertThat(passwordEncoder.matches(keyAndPassword.getNewPassword(), updatedUser.getPassword())).isTrue();
    }

    /**
     * Test case for verifying the behavior of the finish password reset endpoint
     * when a valid reset key and a new password which is too small are provided.
     *
     * The test expects the endpoint to return an HTTP 400 Bad Request status
     * and the user's password to remain unchanged.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testFinishPasswordResetTooSmall() throws Exception {
        User user = new User();
        user.setPassword(RandomStringUtils.random(60));
        user.setUsername("finish-password-reset-too-small@example.com");
        user.setResetDate(Instant.now().plusSeconds(60));
        user.setResetKey("reset key too small");
        userRepository.save(user);

        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey(user.getResetKey());
        keyAndPassword.setNewPassword("foo");

        restAccountMockMvc.perform(
            post("/api/account/reset-password/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByUsernameIgnoreCase(user.getUsername()).orElse(null);
        assertThat(passwordEncoder.matches(keyAndPassword.getNewPassword(), updatedUser.getPassword())).isFalse();
    }

    /**
     * Test case for verifying the behavior of the finish password reset endpoint
     * when a wrong reset key is provided.
     *
     * The test expects the endpoint to return an HTTP 400 Bad Request status
     * and the user's password to remain unchanged.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    public void testFinishPasswordResetWrongKey() throws Exception {
        KeyAndPasswordVM keyAndPassword = new KeyAndPasswordVM();
        keyAndPassword.setKey("wrong reset key");
        keyAndPassword.setNewPassword("new password");

        restAccountMockMvc.perform(
            post("/api/account/reset-password/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
            .andExpect(status().isBadRequest());
    }


    /**
     * Test case for verifying the behavior of the getAccounts endpoint
     * when accessed by an administrator.
     *
     * The test expects the endpoint to return an HTTP 200 OK status
     * with a response body containing the list of users in the system.
     *
     * @throws Exception if an error occurs during the mock request
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAccounts() throws Exception {

        userRepository.deleteAll();

        Set<String> authorities = new HashSet<>();
        authorities.add(Constants.ROLE_ADMIN);

        User user = new User();
        user.setUsername("admin1@example.com");
        userService.createUser(user, null);

        restAccountMockMvc.perform(get("/api/accounts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.content[0].username").value("admin1@example.com"));

    }
}
