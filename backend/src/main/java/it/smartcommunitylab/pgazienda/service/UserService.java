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
import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.Subscription;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import it.smartcommunitylab.pgazienda.security.SecurityUtils;
import it.smartcommunitylab.pgazienda.security.UserInfo;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import it.smartcommunitylab.pgazienda.service.errors.InvalidPasswordException;
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
	private CompanyRepository companyRepo;
	@Autowired
	private CampaignRepository campaignRepo;

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

    public User createUser(User userDTO, String companyId) throws InconsistentDataException {
        User user = new User();
        user.setUsername(userDTO.getUsername().toLowerCase());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setPhone(userDTO.getPhone());
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        user.setPlayerId(userDTO.getPlayerId());

        if (companyId != null) {
            // check roles: only the company roles are applicable
            user.setRoles(userDTO.getRoles().stream().filter((r -> companyId.equals(r.getCompanyId()))).collect(Collectors.toList()));
            if (user.getRoles().isEmpty()) throw new InconsistentDataException("Empty company roles", "EMPTY_ROLES");
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
        user.setPhone(userDTO.getPhone());
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
    	user.setRoles(userDTO.getRoles());
    	user.setPlayerId(userDTO.getPlayerId());
        
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }
    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     * @throws InconsistentDataException 
     */
    public Optional<User> updateUser(User userDTO, String companyId) throws InconsistentDataException {
    	Optional<User> userOpt = Optional.of(userRepository
            .findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get);
        if (userOpt.isPresent()) {
        	User user = userOpt.get();
            user.setUsername(userDTO.getUsername().toLowerCase());
            user.setName(userDTO.getName());
            user.setSurname(userDTO.getSurname());
            user.setPhone(userDTO.getPhone());
            if (!StringUtils.isEmpty(userDTO.getPlayerId())) user.setPlayerId(userDTO.getPlayerId());

            if (companyId != null) {
                // check roles: only the company roles are applicable
                List<UserRole> roles = userDTO.getRoles().stream().filter((r -> companyId.equals(r.getCompanyId()))).collect(Collectors.toList());
                user.setRoles(mergeRoles(companyId, roles, user.getRoles()));
                if (user.getRoles().isEmpty()) throw new InconsistentDataException("Empty company roles", "EMPTY_ROLES");
            } else {
            	user.setRoles(userDTO.getRoles());
            }
            
            userRepository.save(user);
            if (log.isDebugEnabled()) {
                try {
					log.debug("Changed Information for User: {} / {}", new ObjectMapper().writeValueAsString(user), new ObjectMapper().writeValueAsString(userDTO));
				} catch (JsonProcessingException e) {
				}
            }
            return Optional.of(user);
        }
        return userOpt;
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
				Subscription sub = role.containsSubscription(s);
				if(sub != null) {
					sub.setAbandoned(false);
					sub.setUpgraded(s.getUpgraded());
				} else {
					role.getSubscriptions().add(s);	
				}
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
				Subscription s = role.containsSubscription(companyCode, campaignId, key);
				if(s != null) {
					s.setAbandoned(true);
				}
				userRepository.save(user);
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
    public Optional<User> updateUser(String id, String name, String surname, String phone) {
        return Optional.of(userRepository
            .findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                user.setName(name);
                user.setSurname(surname);
                user.setPhone(phone);
                userRepository.save(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            });
    }

    /**
	 * @param companyId 
     * @param newRoles
	 * @param oldRoles
	 * @return
	 */
	private List<UserRole> mergeRoles(String companyId, List<UserRole> newRoles, List<UserRole> oldRoles)
	{
		List<UserRole> extraRoles = oldRoles.stream().filter(r -> !companyId.equals(r.getCompanyId())).collect(Collectors.toList());
		if (newRoles == null || newRoles.isEmpty()) return extraRoles;
		if (oldRoles == null || oldRoles.isEmpty()) return newRoles;
		final Map<String, UserRole> map = new HashMap<>();
		newRoles.forEach(r -> map.put(r.key(), r));
		oldRoles.forEach(r -> {
			String key = r.key();

			// keep roles of other companies or global roles
			if (!companyId.equals(r.getCompanyId())) {
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
	
//	public void deleteUser(String playerId) {
//		Optional<User> opt = 
//		if(opt.isPresent()) {
//			User user = opt.get();
//			
//			campaignService.unsubscribeUser(user, campaignId);
//		}
//	}

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
    public UserInfo getUserDetail() {
    	return SecurityUtils.getCurrentUserInfo();
    }
    public List<User> getUserByEmployeeCode(String campaign, String companyCode, String userCode) {
        return userRepository.findByCampaignAndCompanyAndEmployeeCode(campaign, companyCode, "^" + userCode+"$");
    }
    public Optional<User> getUserByCampaignAndCompanyAndKey(String campaign, String companyCode, String userCode) {
    	return userRepository.findOneByCampaignAndCompanyAndKey(campaign, companyCode, userCode);
    }
    public User getUserByPlayerId(String playerId) {
    	return userRepository.findByPlayerId(playerId).orElse(null);
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
	 * @return true if the user is a platform admin or has the specified role for the specified company, its territory or campaign
	 */
	public boolean isInCompanyRole(String companyId, String ... roles) {
		UserInfo user = getUserDetail();
		Set<String> set = Sets.newHashSet(roles);
		if (user != null) {
			Company company = companyRepo.findById(companyId).orElse(null);
			if(company != null) {
				String territoryId = company.getTerritoryId();
				return user.getRoles().stream().anyMatch(r -> {
					if(Constants.ROLE_ADMIN.equals(r.getRole()))
						return true;
					if(set.contains(r.getRole())) {
						if(Constants.ROLE_TERRITORY_MANAGER.equals(r.getRole()) && territoryId.equals(r.getTerritoryId()))
							return true;
						if(Constants.ROLE_MOBILITY_MANAGER.equals(r.getRole()) && companyId.equals(r.getCompanyId()))
							return true;
						if(Constants.ROLE_CAMPAIGN_MANAGER.equals(r.getRole()) && company.getCampaigns().contains(r.getCampaignId()))
							return true;
					}
					return false;
				});				
			}
		}
		return false;
	}
	
	public boolean isInCampaignRole(String campaignId) {
		UserInfo user = getUserDetail();
		if (user != null) {
			Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
			if(campaign != null) {
				String territoryId = campaign.getTerritoryId();
				return user.getRoles().stream().anyMatch(r -> {
					if(Constants.ROLE_ADMIN.equals(r.getRole()))
						return true;
					if(Constants.ROLE_TERRITORY_MANAGER.equals(r.getRole()) && territoryId.equals(r.getTerritoryId()))
						return true;
					if(Constants.ROLE_CAMPAIGN_MANAGER.equals(r.getRole()) && campaignId.equals(r.getCampaignId()))
						return true;
					return false;
				});								
			}
		}
		return false;
	}
	
	public boolean isCampaignVisible(Campaign campaign) {
		UserInfo user = getUserDetail();
		if ((user != null) && (campaign != null)) {
			return user.getRoles().stream().anyMatch(r -> {
				if(Constants.ROLE_ADMIN.equals(r.getRole()))
					return true;
				if(Constants.ROLE_TERRITORY_MANAGER.equals(r.getRole()) && campaign.getTerritoryId().equals(r.getTerritoryId()))
					return true;
				if(Constants.ROLE_CAMPAIGN_MANAGER.equals(r.getRole()) && campaign.getId().equals(r.getCampaignId()))
					return true;
				if(Constants.ROLE_MOBILITY_MANAGER.equals(r.getRole())) {
					Company company = companyRepo.findById(r.getCompanyId()).orElse(null);
					if(company != null) {
						if(company.getCampaigns().contains(campaign.getId()))
							return true;
					}
				}
				return false;
			});
		}
		return false;
	}
	
	public boolean isCompanyVisible(Company company) {
		UserInfo user = getUserDetail();
		if ((user != null) && (company != null)) {
			return user.getRoles().stream().anyMatch(r -> {
				if(Constants.ROLE_ADMIN.equals(r.getRole()))
					return true;
				if(Constants.ROLE_TERRITORY_MANAGER.equals(r.getRole()) && company.getTerritoryId().equals(r.getTerritoryId()))
					return true;
				if(Constants.ROLE_MOBILITY_MANAGER.equals(r.getRole()) && company.getId().equals(r.getCompanyId()))
					return true;
				if(Constants.ROLE_CAMPAIGN_MANAGER.equals(r.getRole())) {
					if(company.getCampaigns().contains(r.getCampaignId()))
						return true;
				}
				return false;
			});
		}
		return false;
	}
	
	/**
	 * @param campaignId
	 */
	public void cleanSubscriptions(String campaignId) {
		List<User> users = userRepository.findByCampaignIn(Collections.singletonList(campaignId));
		users.forEach(u -> {
			u.getRoles().forEach(r -> {
				if (r.getSubscriptions() != null) {
					r.setSubscriptions(r.getSubscriptions().stream().filter(s -> !s.getCampaign().equals(campaignId)).collect(Collectors.toList()));
				}
			});
		});
		userRepository.saveAll(users);
	}

	public void markAsUpgraded(String legacyId, String campaignId) {
		User user = getUserByPlayerId(legacyId);
		if (user != null) {
			Optional<UserRole> role = user.findRole(Constants.ROLE_APP_USER);
			if (role != null) {
				List<Subscription> subs = role.get().getSubscriptions().stream()
				.filter(s -> s.getCampaign().equals(campaignId) && !Boolean.TRUE.equals(s.getUpgraded()))
				.collect(Collectors.toList());
				if (subs.size() > 0) {
					subs.forEach(s -> s.setUpgraded(true));
					userRepository.save(user);
				}
			}
		}
	}
	
	public long countCompanySubscription(String companyCode) {
		return userRepository.countSubscriptionByCompanyCode(companyCode);
	}
	
	public User saveUser(User user) {
		return userRepository.save(user);
	}
	
	public Optional<User> findOneByCompanyCodeAndEmployeeCodeAndActive(String companyCode, String key) {
		return userRepository.findOneByCompanyCodeAndEmployeeCodeAndActive(companyCode, key);
	}
}
