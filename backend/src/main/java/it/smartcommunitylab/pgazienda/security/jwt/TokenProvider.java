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
package it.smartcommunitylab.pgazienda.security.jwt;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    private Key key;

    @Value("${app.security.authentication.jwt.base64-secret}")
    private String tokenSecret;
    @Value("${app.security.authentication.jwt.token-validity-in-seconds}")
    private long tokenValidityInSeconds;
    @Value("${app.security.authentication.jwt.token-validity-in-seconds-for-remember-me}")
    private long tokenValidityInSecondsForRememberMe;

    @Value("${app.security.ext.issuer-uri}")
    private String extJwtIssuerUri;
    @Value("${app.security.ext.client-id}")
    private String extJwtAudience;
    @Value("${app.security.ext.jwk-uri}")
    private String extJwkUri;
    
    @Value("${app.security.ext.domain:}")
    private String userDomain;

    private JwkProvider provider;

    @PostConstruct
    public void init() throws MalformedURLException {
        byte[] keyBytes;
        log.debug("Using a Base64-encoded JWT secret key");
        keyBytes = Decoders.BASE64.decode(tokenSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.provider = new UrlJwkProvider(new URL(extJwkUri));
        if (extJwtIssuerUri.endsWith("/")) extJwtIssuerUri = extJwtIssuerUri.substring(0, extJwtIssuerUri.length() - 1);
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInSecondsForRememberMe * 1000);
        } else {
            validity = new Date(now + this.tokenValidityInSeconds * 1000);
        }

        return Jwts.builder()
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact();
    }

	public Authentication getAuthentication(String token) {
    	DecodedJWT jwt = JWT.decode(token);
        
        Collection<? extends GrantedAuthority> authorities =
        		jwt.getClaims().containsKey(AUTHORITIES_KEY) 
        	? Arrays.stream(jwt.getClaims().get(AUTHORITIES_KEY).toString().replace("\"", "").split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList())
            : Collections.singletonList(new SimpleGrantedAuthority("ROLE_APP_USER"));

        String subj = jwt.getSubject();
        // external subj
        if (jwt.getIssuer() != null && jwt.getIssuer().equals(extJwtIssuerUri)) {
        	if (!StringUtils.isEmpty(userDomain)) {
        		subj = subj + userDomain;
        	}
        }
        
        User principal = new User(subj, "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

	public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | UnsupportedJwtException se) {
        	if (validateExtToken(authToken)) {
        		return true;
        	}
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", e);
        }
        return false;
    }
    
	private boolean validateExtToken(String token) {
    	DecodedJWT jwt = JWT.decode(token);
    	Algorithm algorithm = null;
		try {
			Jwk jwk = provider.get(jwt.getKeyId());
			algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
		} catch (Exception e) {
  		  log.error("Token exception", e);
		}
    	algorithm.verify(jwt);
    	if (jwt.getExpiresAt().before(Calendar.getInstance().getTime())) {
    		  log.info("Expired token");
    		  return false;
		}
    	if (!jwt.getAudience().contains(extJwtAudience)) {
    		log.info("Invalid audience");
    		return false;
    	}
    	if (!jwt.getIssuer().equals(extJwtIssuerUri)) {
    		log.info("Invalid issuer");
    		return false;
    	}
    	return true;
    }
}
