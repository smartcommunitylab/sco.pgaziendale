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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.Subscription;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import it.smartcommunitylab.pgazienda.security.SecurityUtils;
import it.smartcommunitylab.pgazienda.util.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;
    
    @PostConstruct
    public void loadAdmin() {
    	Optional<User> admin = userRepository.findByUsername(adminUsername);
    	if (!admin.isPresent()) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setName("admin");
            adminUser.setSurname("Administrator");
            adminUser.setActivated(true);
            adminUser.setCreatedDate(Instant.now());
            adminUser.setRoles(Collections.singletonList(UserRole.createAdminRole()));
            userRepository.save(adminUser);
    	}
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(60*60*24*3)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                userRepository.save(user);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByUsernameIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                userRepository.save(user);
                return user;
            });
    }

    public User createUser(User userDTO, String companyId) {
        User user = new User();
        user.setUsername(userDTO.getUsername().toLowerCase());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);

        if (companyId != null) {
            // check roles: only the company roles are applicable
            user.setRoles(userDTO.getRoles().stream().filter((r -> companyId.equals(r.getCompanyId()))).collect(Collectors.toList()));
            if (user.getRoles().isEmpty()) throw new IllegalArgumentException("Empty company roles");
        } else {
        	user.setRoles(userDTO.getRoles());
        }
        
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    
    public User createUser(User userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername().toLowerCase());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
    	user.setRoles(userDTO.getRoles());
        
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }
    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<User> updateUser(User userDTO, String companyId) {
        return Optional.of(userRepository
            .findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                user.setUsername(userDTO.getUsername().toLowerCase());
                user.setName(userDTO.getName());
                user.setSurname(userDTO.getSurname());
                if (!StringUtils.isEmpty(userDTO.getPlayerId())) user.setPlayerId(userDTO.getPlayerId());

                if (companyId != null) {
                    // check roles: only the company roles are applicable
                    List<UserRole> roles = userDTO.getRoles().stream().filter((r -> companyId.equals(r.getCompanyId()))).collect(Collectors.toList());
                    user.setRoles(mergeRoles(roles, user.getRoles()));
                    if (user.getRoles().isEmpty()) throw new IllegalArgumentException("Empty company roles");
                }
                
                userRepository.save(user);
                try {
					log.info("Changed Information for User: {} / {}", new ObjectMapper().writeValueAsString(user), new ObjectMapper().writeValueAsString(userDTO));
				} catch (JsonProcessingException e) {
				}
                return user;
            });
    }



	/**
	 * Add subscription to the app user role of the specified user
	 * @param id
	 * @param s
	 */
	public void addAppSubscription(String id, Subscription s) {
		User user = userRepository.findById(id).orElse(null);
		if (user != null) {
			UserRole role = user.findRole(Constants.ROLE_APP_USER).orElse(null);
			if (role == null) {
				role = UserRole.createAppUserRole(s);
				user.getRoles().add(role);
			} else {
				role.getSubscriptions().add(s);
			}
			userRepository.save(user);
		}
	}

	/**
	 * Remove subscription from the app user role of the specified user
	 * @param id
	 * @param key
	 * @param companyCode
	 * @param campaignId
	 */
	public void removeAppSubscription(String id, String key, String companyCode, String campaignId) {
		User user = userRepository.findById(id).orElse(null);
		if (user != null) {
			UserRole role = user.findRole(Constants.ROLE_APP_USER).orElse(null);
			if (role != null) {
				if (role.getSubscriptions().removeIf(s -> s.getCampaign().equals(campaignId) && s.getCompanyCode().equals(companyCode) && s.getKey().equals(key))) {
					userRepository.save(user);
				}
			}
		}
	}

    /**
     * Update all information for a specific user, and return the modified user.
     * @param string 
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<User> updateUser(String id, String name, String surname) {
        return Optional.of(userRepository
            .findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                user.setName(name);
                user.setSurname(surname);
                userRepository.save(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            });
    }

    /**
	 * @param roles
	 * @param roles2
	 * @return
	 */
	private List<UserRole> mergeRoles(List<UserRole> roles, List<UserRole> roles2)
	{
		if (roles == null || roles.isEmpty()) return roles2;
		if (roles2 == null || roles2.isEmpty()) return roles;
		final Map<String, UserRole> map = new HashMap<>();
		roles.forEach(r -> map.put(r.key(), r));
		roles2.forEach(r -> {
			String key = r.key();
			if (map.containsKey(key)) {
				UserRole toMerge = map.get(key);
				if (r.getLocations() != null) {
					Set<String> set =  new HashSet<>(r.getLocations());
					if (toMerge.getLocations() != null) set.addAll(toMerge.getLocations());
					toMerge.setLocations(new LinkedList<>(set));
				}
			} else {
				map.put(key, r);
			}
		});
		return new LinkedList<>(map.values());
	}

	public void deleteUser(String companyId, String login) {
        userRepository.findOneByUsernameIgnoreCase(login).ifPresent(user -> {
        	user.getRoles().removeIf(r -> companyId.equals(r.getCompanyId()));
        	if (user.getRoles().size() == 0) {
        		userRepository.delete(user);
                log.debug("Deleted User: {}", user);
        	} else {
        		userRepository.save(user);
                log.debug("Deleted User roles: {}", user);
        	}
        });
    }

    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByUsernameIgnoreCase)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                userRepository.save(user);
                log.debug("Changed password for User: {}", user);
            });
    }

    public Optional<User> getUserWithAuthoritiesByUsername(String login) {
        return userRepository.findOneByUsernameIgnoreCase(login);
    }

    public Optional<User> getUserWithAuthorities(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByUsernameIgnoreCase);
    }
    public List<User> getUserByEmployeeCode(String campaign, String companyCode, String userCode) {
        return userRepository.findByCampaignAndCompanyAndEmployeeCode(campaign, companyCode, Collections.singleton(userCode));
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                log.debug("Deleting not activated user {}", user.getUsername());
                userRepository.delete(user);
            });
    }

	/**
	 * @param pageable
	 * @return
	 */
	public Page<User> getAllManagedUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	/**
	 * @param pageable
	 * @return
	 */
	public List<User> getAllManagedUsers(String companyId) {
		List<User> res = userRepository.findByCompanyId(companyId);
		res.forEach(u -> u.setRoles(
				u.getRoles().stream().filter(r -> companyId.equals(r.getCompanyId()))
				.collect(Collectors.toList())));
		return res;
	}

	/**
	 * @param companyId
	 * @return true if the user is a platform admin or has the specified role for the specified company
	 */
	public boolean isInCompanyRole(String companyId, String ... roles) {
		User user = getUserWithAuthorities().orElse(null);
		Set<String> set = Sets.newHashSet(roles);
		if (user != null) {
			return user.getRoles().stream().anyMatch(r -> Constants.ROLE_ADMIN.equals(r.getRole()) || set.contains(r.getRole()) && companyId.equals(r.getCompanyId()));
		}
		return false;
	}

}
