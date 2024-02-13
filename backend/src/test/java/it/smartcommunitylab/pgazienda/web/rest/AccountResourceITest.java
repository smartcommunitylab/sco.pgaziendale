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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    @BeforeEach
    public void setup() {
        userRepository.findAll().forEach(u -> {
        	if (!u.getUsername().equals("admin")) userRepository.delete(u);
        });;
    }

    @Test
    @WithUnauthenticatedMockUser
    public void testNonAuthenticatedUser() throws Exception {
        restAccountMockMvc.perform(get("/api/authenticate")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(""));
    }

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

    @Test
    public void testGetUnknownAccount() throws Exception {
        restAccountMockMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().isBadRequest());
    }

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

    @Test
    public void testActivateAccountWithWrongKey() throws Exception {
        restAccountMockMvc.perform(get("/api/activate?key=wrongActivationKey"))
            .andExpect(status().isBadRequest());
    }

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

    @Test
    public void testRequestPasswordResetWrongEmail() throws Exception {
        restAccountMockMvc.perform(
            post("/api/account/reset-password/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"password-reset-wrong-email@example.com\""))
            .andExpect(status().isOk());
    }

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
}
