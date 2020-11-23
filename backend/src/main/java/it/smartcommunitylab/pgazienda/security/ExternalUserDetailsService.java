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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.service.UserService;

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

    @Value("${app.security.ext.endpoint-userinfo}")
    private String userInfoEndpoint;
    @Value("${app.security.ext.username-field:username}")
    private String playerField;
    @Value("${app.security.ext.player-field:sub}")
    private String userNameField;
    @Value("${app.security.ext.name-field:first_name}")
    private String nameField;
    @Value("${app.security.ext.surname-field:last_name}")
    private String surnameField;
    @Value("${app.security.ext.domain:}")
    private String userDomain;
    
    @Autowired
    private RestTemplate template;
    
    @Autowired
    private UserService userService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Authentication externalAuthentication(String token) {
    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Authorization", "Bearer " + extractToken(token));
    	HttpEntity entity = new HttpEntity(headers);
		Map<String, ?> userInfo = template.exchange(URI.create(userInfoEndpoint), HttpMethod.GET, entity , Map.class).getBody();
		
    	if (userInfo == null) throw new SecurityException("Missing user data");
    	String username = (String) userInfo.get(userNameField);
    	if (StringUtils.isEmpty(username)) throw new SecurityException("Missing username");
    	if (!StringUtils.isEmpty(userDomain)) {
    		username += userDomain;
    	}
    	String name = (String) userInfo.get(nameField);
    	String surname = (String) userInfo.get(surnameField);
    	User user = userService.getUserWithAuthoritiesByUsername(username.toLowerCase()).orElse(null);
    	if (user == null) {
    		User userDTO = new User();
    		userDTO.setName(name);
    		userDTO.setSurname(surname);
    		userDTO.setUsername(username);
    		userDTO.setPlayerId((String)userInfo.get(playerField));
    		userDTO.setRoles(Collections.singletonList(UserRole.createAppUserRole()));
			user = userService.createUser(userDTO, null);
    	} else if (user.findRole(Constants.ROLE_APP_USER).isEmpty()) {
    		user.getRoles().add(UserRole.createAppUserRole());
    		user = userService.updateUser(user, null).orElse(null);
    	}
        List<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getRole()))
                .collect(Collectors.toList());

    	
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), grantedAuthorities);
        authenticationToken.setDetails(new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                grantedAuthorities));
        return authenticationToken;
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
