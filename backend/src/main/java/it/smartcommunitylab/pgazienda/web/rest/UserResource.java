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

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import it.smartcommunitylab.pgazienda.service.MailService;
import it.smartcommunitylab.pgazienda.service.UserService;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import it.smartcommunitylab.pgazienda.service.errors.UserAnotherOrgException;
import it.smartcommunitylab.pgazienda.service.errors.UsernameAlreadyUsedException;
import it.smartcommunitylab.pgazienda.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link User} entity.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    /**
     * {@code POST  /{companyId}/users}  : Creates a new user for a company.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param companyId company
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws InconsistentDataException 
     * @throws LoginAlreadyUsedException 
     */
    @PostMapping("/companies/{companyId}/users")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN+"\")")
    public ResponseEntity<User> createUser(@PathVariable String companyId, @Valid @RequestBody User userDTO) throws URISyntaxException, BadRequestAlertException, UsernameAlreadyUsedException, InconsistentDataException {
        log.debug("REST request to save User : {}", userDTO);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");

    	Optional<User> old = null;
        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID");
            // Lowercase the user login before comparing with database
        } else if ((old = userRepository.findOneByUsernameIgnoreCase(userDTO.getUsername().toLowerCase())).isPresent()) {
        	Set<String> companyRoles = old.get().companyRoles().stream().map(r -> r.getCompanyId()).collect(Collectors.toSet());
        	if (companyRoles.size() > 1 || companyRoles.size() == 1 && !companyRoles.contains(companyId)) {
        		throw new UserAnotherOrgException();
        	}
        	userDTO.setId(old.get().getId());
        	User newUser = userService.updateUser(userDTO, companyId).orElse(null);
            return ResponseEntity.ok(newUser);
        } else {
            User newUser = userService.createUser(userDTO, companyId);
            mailService.sendCreationEmail(newUser);
            return ResponseEntity.ok(newUser);
        }
    }

    /**
     * {@code PUT /users} : Updates an existing User.
     *
     * @param userDTO the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
     * @throws InconsistentDataException 
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already in use.
     */
    @PutMapping("/companies/{companyId}/users")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN+"\")")
    public ResponseEntity<User> updateUser(@PathVariable String companyId, @Valid @RequestBody User userDTO) throws UsernameAlreadyUsedException, InconsistentDataException {
        log.debug("REST request to update User : {}", userDTO);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
        Optional<User> existingUser = userRepository.findOneByUsernameIgnoreCase(userDTO.getUsername().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new UsernameAlreadyUsedException();
        }
        Optional<User> updatedUser = userService.updateUser(userDTO, companyId);

        return ResponseEntity.ok(updatedUser.orElse(null));
    }

    /**
     * {@code GET /users} : get all users.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/companies/{companyId}/users")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN+"\")")
    public ResponseEntity<List<User>> getAllUsers(@PathVariable String companyId) {
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN, Constants.ROLE_MOBILITY_MANAGER)) throw new SecurityException("Insufficient rights");
        final List<User> page = userService.getAllManagedUsers(companyId);
        return ResponseEntity.ok(page);
    }

    /**
     * {@code DELETE /users/:login} : delete the "login" User.
     *
     * @param login the login of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/companies/{companyId}/users/{login:.*}")
    @PreAuthorize("hasAnyAuthority(\"" + Constants.ROLE_ADMIN + "\", \""+Constants.ROLE_COMPANY_ADMIN+"\")")
    public ResponseEntity<Void> deleteUser(@PathVariable String companyId, @PathVariable String login) {
        log.debug("REST request to delete User: {}", login);
    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_COMPANY_ADMIN)) throw new SecurityException("Insufficient rights");
        userService.deleteUser(companyId, login);
        return ResponseEntity.ok(null);
    }
}
