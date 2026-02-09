package it.smartcommunitylab.pgazienda.web.rest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_DATA;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;
import it.smartcommunitylab.pgazienda.dto.StatEmployeeDTO;
import it.smartcommunitylab.pgazienda.service.StatEmployeeService;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;

@RestController
@RequestMapping("/api")
public class StatEmployeeResource {
	@Autowired
	StatEmployeeService statEmployeeService;
	
	@GetMapping("/campaigns/{campaignId}/stats/employee")
	public ResponseEntity<List<StatEmployeeDTO>> statistics(
			@PathVariable String campaignId, 
			@RequestParam(required=false) String companyId,
			@RequestParam(required=false) String location,
			@RequestParam(required=false, defaultValue = "month") GROUP_BY_TIME timeGroupBy,
			@RequestParam(required=false) GROUP_BY_DATA dataGroupBy,
			@RequestParam(required=false) String from,
			@RequestParam(required=false) String to) throws InconsistentDataException {
	    	/*if(!userService.isInCampaignRole(campaignId)) {
			if(StringUtils.isNotBlank(companyId)) {
		    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_MOBILITY_MANAGER)) 
		    		throw new SecurityException("Insufficient rights");        		
	    	} else {
	    		throw new SecurityException("Insufficient rights");	
	    	}
		}*/
    	LocalDate toDate = to == null ? LocalDate.now() : LocalDate.parse(to);
    	LocalDate fromDate = from == null ? null : LocalDate.parse(from);
    	return ResponseEntity.ok(statEmployeeService.getEmployeeStats(campaignId, companyId, location, timeGroupBy, dataGroupBy, fromDate, toDate));
	}
	

	@GetMapping("/campaigns/{campaignId}/stats/employee/flat")
	public ResponseEntity<List<Map<String, Object>>> statisticsFlat(
			@PathVariable String campaignId, 
			@RequestParam(required=false) String companyId,
			@RequestParam(required=false) String location,
			@RequestParam(required=false, defaultValue = "month") GROUP_BY_TIME timeGroupBy,
			@RequestParam(required=false) GROUP_BY_DATA dataGroupBy,
			@RequestParam(required=false) String from,
			@RequestParam(required=false) String to) throws InconsistentDataException {
	    	/*if(!userService.isInCampaignRole(campaignId)) {
			if(StringUtils.isNotBlank(companyId)) {
		    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_MOBILITY_MANAGER)) 
		    		throw new SecurityException("Insufficient rights");        		
	    	} else {
	    		throw new SecurityException("Insufficient rights");	
	    	}
		}*/
    	LocalDate toDate = to == null ? LocalDate.now() : LocalDate.parse(to);
    	LocalDate fromDate = from == null ? null : LocalDate.parse(from);
    	return ResponseEntity.ok(statEmployeeService.getEmployeeStatsFlat(campaignId, companyId, location, timeGroupBy, dataGroupBy, fromDate, toDate));
	}
	@GetMapping("/campaigns/{campaignId}/stats/employee/count")
	public ResponseEntity<List<Entry<String, Long>>> employees(@PathVariable String campaignId,
			@RequestParam(required=false) String companyId) {
	    	/*if(!userService.isInCampaignRole(campaignId)) {
			if(StringUtils.isNotBlank(companyId)) {
		    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_MOBILITY_MANAGER)) 
		    		throw new SecurityException("Insufficient rights");        		
	    	} else {
	    		throw new SecurityException("Insufficient rights");	
	    	}
		}*/
		return ResponseEntity.ok(statEmployeeService.getEmployeeCount(campaignId, companyId));
	}
	
	@GetMapping("/campaigns/{campaignId}/stats/employee/csv")
	public void statisticsCsv(
			@PathVariable String campaignId, 
			@RequestParam(required=false) String companyId,
			@RequestParam(required=false) String location,
			@RequestParam(required=false, defaultValue = "month") GROUP_BY_TIME timeGroupBy,
			@RequestParam(required=false) GROUP_BY_DATA dataGroupBy,
			@RequestParam(required=false) String from,
			@RequestParam(required=false) String to,
			HttpServletResponse response) throws InconsistentDataException, IOException {
	    	/*if(!userService.isInCampaignRole(campaignId)) {
			if(StringUtils.isNotBlank(companyId)) {
		    	if (!userService.isInCompanyRole(companyId, Constants.ROLE_MOBILITY_MANAGER)) 
		    		throw new SecurityException("Insufficient rights");        		
	    	} else {
	    		throw new SecurityException("Insufficient rights");	
	    	}
		}*/
    	LocalDate toDate = to == null ? LocalDate.now() : LocalDate.parse(to);
    	LocalDate fromDate = from == null ? null : LocalDate.parse(from);		
		statEmployeeService.getEmployeeStatsCsv(response.getWriter(), campaignId, companyId, location, timeGroupBy, dataGroupBy, fromDate, toDate);
	}
			
}
