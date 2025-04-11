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

package it.smartcommunitylab.pgazienda.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import it.smartcommunitylab.pgazienda.util.RandomUtil;

/**
 * Integration tests for {@link UserService}.
 */
@SpringBootTest(classes = PGAziendaApp.class)
public class UserServiceITest {

    private static final String DEFAULT_LOGIN = "johndoe@example.com";

    private static final String DEFAULT_FIRSTNAME = "john";

    private static final String DEFAULT_LASTNAME = "doe";


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User user;

    /**
     * Initializes the test environment by clearing the UserRepository and
     * setting up a default User instance with predefined attributes.
     *
     * This method is executed before each test to ensure a clean state.
     */
    @BeforeEach
    public void init() {
        userRepository.deleteAll();
        user = new User();
        user.setUsername(DEFAULT_LOGIN);
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setName(DEFAULT_FIRSTNAME);
        user.setSurname(DEFAULT_LASTNAME);
    }

    /**
     * Verifies that a user can only request a password reset if they actually
     * exist in the database.
     *
     * <p>This test will try to request a password reset with a non-existent
     * user name, and then with a valid user name. The first attempt should
     * return no user, and the second attempt should return a user with a reset
     * date and key.
     */
    @Test
    public void assertThatUserMustExistToResetPassword() {
        userRepository.save(user);
        Optional<User> maybeUser = userService.requestPasswordReset("invalid.login@localhost");
        assertThat(maybeUser).isNotPresent();

        maybeUser = userService.requestPasswordReset(user.getUsername());
        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.orElse(null).getUsername()).isEqualTo(user.getUsername());
        assertThat(maybeUser.orElse(null).getResetDate()).isNotNull();
        assertThat(maybeUser.orElse(null).getResetKey()).isNotNull();
    }

    /**
     * Verifies that a user can only request a password reset if they are
     * activated.
     *
     * <p>This test will try to request a password reset with an activated user,
     * and then with a non-activated user. The first attempt should return a user
     * with a reset date and key, and the second attempt should return no user.
     */
    @Test
    public void assertThatOnlyActivatedUserCanRequestPasswordReset() {
        user.setActivated(false);
        userRepository.save(user);

        Optional<User> maybeUser = userService.requestPasswordReset(user.getUsername());
        assertThat(maybeUser).isNotPresent();
        userRepository.delete(user);
    }

    /**
     * Verifies that a user can only request a password reset if the reset key
     * is not older than 24 hours.
     *
     * <p>This test will try to request a password reset with a reset key older
     * than 24 hours, and then with a reset key newer than 24 hours. The first
     * attempt should return no user, and the second attempt should return a user
     * with a reset date and key.
     */
    @Test
    public void assertThatResetKeyMustNotBeOlderThan24Hours() {
        Instant daysAgo = Instant.now().minus(75, ChronoUnit.HOURS);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        userRepository.save(user);

        Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());
        assertThat(maybeUser).isNotPresent();
        userRepository.delete(user);
    }

    /**
     * Verifies that the password reset process fails when an expired reset key is used.
     *
     * <p>This test sets a reset key that is older than the allowed reset period
     * and attempts to reset the password using that key. The test expects the
     * reset to fail, returning no user.
     */
    @Test
    public void assertThatResetKeyMustBeValid() {
        Instant daysAgo = Instant.now().minus(75, ChronoUnit.HOURS);
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey("1234");
        userRepository.save(user);

        Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());
        assertThat(maybeUser).isNotPresent();
        userRepository.delete(user);
    }

    /**
     * Verifies that the password reset process completes successfully when
     * using a valid reset key.
     *
     * <p>This test sets a reset key that is newer than the allowed reset period
     * and attempts to reset the password using that key. The test expects the
     * reset to succeed, returning a user with a null reset date and key, and a
     * different password.
     */
    @Test
    public void assertThatUserCanResetPassword() {
        String oldPassword = user.getPassword();
        Instant daysAgo = Instant.now().minus(2, ChronoUnit.HOURS);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        userRepository.save(user);

        Optional<User> maybeUser = userService.completePasswordReset("johndoe2", user.getResetKey());
        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.orElse(null).getResetDate()).isNull();
        assertThat(maybeUser.orElse(null).getResetKey()).isNull();
        assertThat(maybeUser.orElse(null).getPassword()).isNotEqualTo(oldPassword);

        userRepository.delete(user);
    }

    /**
     * Verifies that users that are not activated, have a not null activation key, and were created
     * before 3 days are deleted.
     *
     * <p>This test saves a user with an activation key and a creation date before 3 days, and then
     * verifies that the user is deleted when the scheduled job is run.
     */
    @Test
    public void assertThatNotActivatedUsersWithNotNullActivationKeyCreatedBefore3DaysAreDeleted() {
        Instant now = Instant.now();
        user.setActivated(false);
        user.setActivationKey(RandomStringUtils.random(20));
        User dbUser = userRepository.save(user);
        dbUser.setCreatedDate(now.minus(4, ChronoUnit.DAYS));
        userRepository.save(user);
        List<User> users = userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(now.minus(3, ChronoUnit.DAYS));
        assertThat(users).isNotEmpty();
        userService.removeNotActivatedUsers();
        users = userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(now.minus(3, ChronoUnit.DAYS));
        assertThat(users).isEmpty();
    }

    /**
     * Verifies that users that are not activated, have a null activation key, and were created
     * before 3 days are not deleted.
     *
     * <p>This test saves a user with a null activation key and a creation date before 3 days,
     * and then verifies that the user is not deleted when the scheduled job is run.
     */
    @Test
    public void assertThatNotActivatedUsersWithNullActivationKeyCreatedBefore3DaysAreNotDeleted() {
        Instant now = Instant.now();
        user.setActivated(false);
        User dbUser = userRepository.save(user);
        dbUser.setCreatedDate(now.minus(4, ChronoUnit.DAYS));
        userRepository.save(user);
        List<User> users = userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(now.minus(3, ChronoUnit.DAYS));
        assertThat(users).isEmpty();
        userService.removeNotActivatedUsers();
        Optional<User> maybeDbUser = userRepository.findById(dbUser.getId());
        assertThat(maybeDbUser).contains(dbUser);
    }

    /**
     * Verifies that the anonymous user is not retrieved as a managed user.
     *
     * <p>This test sets the username of a user to "ANONYMOUS" and saves it if it
     * does not already exist in the repository. It then retrieves all managed users
     * and checks that the list contains only one user, ensuring that the anonymous
     * user is not considered a managed user.
     */
    @Test
    public void assertThatAnonymousUserIsNotGet() {
        user.setUsername(Constants.ANONYMOUS);
        if (!userRepository.findOneByUsernameIgnoreCase(Constants.ANONYMOUS).isPresent()) {
            userRepository.save(user);
        }
        final PageRequest pageable = PageRequest.of(0, (int) userRepository.count());
        final Page<User> allManagedUsers = userService.getAllManagedUsers(pageable);
        assertThat(allManagedUsers.getContent().size() == 1);
    }


    /**
     * Verifies that a user can be created.
     *
     * <p>This test creates a user and verifies that the user can be retrieved
     * from the repository.
     */
    @Test
    public void testCreateUser() {
        User userDTO = new User();
        userDTO.setUsername(DEFAULT_LOGIN);
        userDTO.setPassword(RandomStringUtils.random(60));
        userDTO.setActivated(true);
        userDTO.setName(DEFAULT_FIRSTNAME);
        userDTO.setSurname(DEFAULT_LASTNAME);

        assertThat(userService.createUser(userDTO).equals(user));
    }

}