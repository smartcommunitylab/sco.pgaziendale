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

package it.smartcommunitylab.pgazienda.security;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.service.UserService;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;

/**
 * Do authentication with external OAuth2.0 token reading the user data from the
 * specified User Info endpont. The result may be parameterized to get 
 * the name, surname, and the username fields. To avoid duplicates,
 * the username field may be equipped with the 'domain' suffix. 
 * @author raman
 *
 */
@Service
public class ExternalUserDetailsService {

    private final Logger log = LoggerFactory.getLogger(ExternalUserDetailsService.class);

    @Value("${spring.security.oauth2.client.provider.custom.user-info-uri}")
    private String userInfoEndpoint;
    
    @Value("${app.security.ext.player-field:username}")
    private String playerField;
    @Value("${app.security.ext.username-field:sub}")
    private String userNameField;
    @Value("${app.security.ext.name-field:given_name}")
    private String nameField;
    @Value("${app.security.ext.surname-field:family_name}")
    private String surnameField;
    @Value("${app.security.ext.domain:}")
    private String userDomain;
    
    @Value("${app.engineEndpoint}")
    private String engineEndpoint;
    
    @Autowired
    private RestTemplate template;
    
    @Autowired
    private UserService userService;
    
    private ObjectMapper mapper = new ObjectMapper();

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Authentication externalAuthentication(String token) throws InconsistentDataException {
    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Authorization", "Bearer " + extractToken(token));
    	HttpEntity entity = new HttpEntity(headers);
    	log.debug("get user info:" + headers);
    	ResponseEntity<Map> responseInfo = template.exchange(URI.create(userInfoEndpoint), HttpMethod.GET, entity , Map.class);
    	if(!responseInfo.getStatusCode().is2xxSuccessful()) {
    		log.warn("get user info error:" + responseInfo.getStatusCodeValue());
    	}
		Map<String, ?> userInfo = responseInfo.getBody();
		
    	if (userInfo == null) throw new SecurityException("Missing user data");
    	String username = (String) userInfo.get(userNameField);
    	if (StringUtils.isEmpty(username)) throw new SecurityException("Missing username");
    	if (!StringUtils.isEmpty(userDomain)) {
    		username += userDomain;
    	}
    	
    	String name = (String) userInfo.get(nameField);
    	String surname = (String) userInfo.get(surnameField);
    	String playerId = (String)userInfo.get(playerField);
    	if (playerId == null) playerId = username;
    	User user = userService.getUserWithAuthoritiesByUsername(username.toLowerCase()).orElse(null);
    	if (user == null) {
			log.info("Registering new User: " + userInfo);
    		
    		User userDTO = new User();
    		userDTO.setName(name);
    		userDTO.setSurname(surname);
    		userDTO.setUsername(username);
    		userDTO.setPlayerId(playerId);
    		//userDTO.setRoles(Collections.singletonList(UserRole.createAppUserRole()));
			user = userService.createUser(userDTO, null);
    	} else {
    		log.info("Updating existing User: " + userInfo);
    		user.setName(name);
    		user.setSurname(surname);
			user.setUsername(username);
    		user.setPlayerId(playerId);
    	}
    	//get playandgo roles
    	ResponseEntity<String> responseEntity = template.exchange(URI.create(engineEndpoint + "api/console/role/my"), HttpMethod.GET, entity , String.class);
    	if(responseEntity.getStatusCode().is2xxSuccessful()) {
    		try {
				JsonNode rootNode = mapper.readTree(responseEntity.getBody());
				for (JsonNode jsonNode : rootNode) {
					mergeRoles(user, jsonNode);
				}
			} catch (Exception e) {
				log.warn("externalAuthentication: playandgo roles error:" + e.getMessage());
			}
    	}
    	user = userService.updateUser(user, null).orElse(null);
		log.debug("With fields: " + nameField +", " + surnameField +", " + userNameField +", " + playerField);
        List<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getRole()))
                .collect(Collectors.toList());

    	
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), grantedAuthorities);
        authenticationToken.setDetails(user);
        return authenticationToken;
    }
    
    private void mergeRoles(User user, JsonNode jsonNode) {
		String entityId = jsonNode.get("entityId").asText();
		String role = jsonNode.get("role").asText();
		user.setRoles(new LinkedList<>(user.getRoles().stream().filter(r -> r.getRole().equals(Constants.ROLE_ADMIN) || r.getRole().equals(Constants.ROLE_MOBILITY_MANAGER) || r.getRole().equals(Constants.ROLE_APP_USER)).collect(Collectors.toList())));
		if(StringUtils.isNotBlank(role)) {
			switch (role) {
			case "admin":
				if(!user.isAdmin()) 
					user.getRoles().add(UserRole.createAdminRole());
				break;
			case "territory":
				if(!user.hasTerritoryRole(entityId))
					user.getRoles().add(UserRole.createTerritoryManager(entityId));
				break;
			case "campaign":
				if(!user.hasCampaignRole(entityId))
					user.getRoles().add(UserRole.createCampaignManager(entityId));
				break;
			default:
				break;
			}
		}
    }
    
    public User checkOrRegister(String playerId) throws InconsistentDataException {
    	String username = playerId;
    	if (StringUtils.isEmpty(username)) throw new SecurityException("Missing username");
    	if (!StringUtils.isEmpty(userDomain)) {
    		username += userDomain;
    	}
	
    	User user = userService.getUserWithAuthoritiesByUsername(username.toLowerCase()).orElse(null);

    	if (user == null) {
    		log.info("Registering new User externally: " + username);
    		
    		User userDTO = new User();
    		userDTO.setUsername(username);
    		userDTO.setPlayerId(playerId);
    		userDTO.setRoles(Collections.singletonList(UserRole.createAppUserRole()));
			user = userService.createUser(userDTO, null);
    	}
    	return user;
    }

	/**
	 * @param token
	 * @return
	 */
	private String extractToken(String token) {
		if (!StringUtils.isEmpty(token)) {
			return token.toLowerCase().startsWith("bearer ") ? token.substring(token.indexOf(' ') + 1) : token;
		}
		return null;
	}
}
