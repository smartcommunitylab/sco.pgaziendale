package it.smartcommunitylab.pgazienda.web.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.pgazienda.Constants;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.Subscription;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.dto.ProfileDTO;
import it.smartcommunitylab.pgazienda.dto.UserDTO;
import it.smartcommunitylab.pgazienda.service.CompanyService;
import it.smartcommunitylab.pgazienda.service.UserService;

@RestController
@RequestMapping("/api")
public class ProfileResource {

    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;

	@GetMapping("/profile/campaign/{campaign}")
	public ResponseEntity<ProfileDTO> getProfile(@PathVariable String campaign) {
		Optional<User> userWithAuthorities = userService.getUserWithAuthorities();
		ProfileDTO profile = new ProfileDTO();
		if (userWithAuthorities.isPresent()) {
			User user = userWithAuthorities.get();
			UserRole role = user.getRoles().stream().filter(r -> r.getRole().equals(Constants.ROLE_APP_USER) && !r.getSubscriptions().isEmpty() && r.getSubscriptions().stream().anyMatch(s -> s.getCampaign().equals(campaign))).findAny().orElse(null);
			if (role != null) {
				Subscription sub = role.getSubscriptions().stream().filter(s -> s.getCampaign().equals(campaign)).findAny().orElse(null);
				Company company = companyService.findByCode(sub.getCompanyCode()).orElse(null);
		        final List<User> managers = userService.getAllManagedUsers(company.getId());
		        Employee employee = companyService.getEmployeeByCode(company.getId(), sub.getKey());
		        if (employee != null) {
		        	profile.setCompany(company);
		        	profile.setCompanyEmail(employee.getCompanyEmail());
		        	profile.setName(employee.getName());
		        	profile.setSurname(employee.getSurname());
		        	profile.setLocation(companyService.getLocation(company.getId(), employee.getLocation()));
		        	profile.setMobilityManagers(managers.stream().map(m -> {
		        		UserDTO dto = new UserDTO();
		        		dto.setName(m.getName());
		        		dto.setSurname(m.getSurname());
		        		dto.setPhone(m.getPhone());
		        		dto.setUsername(m.getUsername());
		        		return dto;
		        	}).collect(Collectors.toList()));
		        }
			} 
		}
		return ResponseEntity.ok(profile);
	}
}
