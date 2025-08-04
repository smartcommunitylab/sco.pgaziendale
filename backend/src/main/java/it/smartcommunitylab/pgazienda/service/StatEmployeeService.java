package it.smartcommunitylab.pgazienda.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Constants;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_DATA;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.dto.StatEmployeeDTO;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;

@Service
public class StatEmployeeService {
	private static final Logger logger = LoggerFactory.getLogger(TrackingDataService.class);
	private static final DateTimeFormatter MONTH_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM");
	private static final DateTimeFormatter YEAR_PATTERN = DateTimeFormatter.ofPattern("yyyy");
	private static final DateTimeFormatter WEEK_PATTERN = DateTimeFormatter.ofPattern("yyyy-ww", Constants.DEFAULT_LOCALE);

	@Autowired
	private MongoTemplate template;
	@Autowired
	private CampaignRepository campaignRepo;

	public List<StatEmployeeDTO> getEmployeeStats(
			String campaignId,
			String companyId,
			String locationId,
			GROUP_BY_TIME timeGroupBy,
			GROUP_BY_DATA dataGroupBy,
			LocalDate from, 
			LocalDate to) throws InconsistentDataException {
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");
		if (from == null) {
			from = campaign.getFrom();
			to = campaign.getTo();
		}
		
		Criteria criteria = new Criteria("trackingRecord." + campaignId).exists(true);
		if(StringUtils.isNotBlank(companyId)) {
			criteria = criteria.and("companyId").is(companyId);
		}
		if(StringUtils.isNotBlank(locationId)) {
			criteria = criteria.and("location").is(locationId);
		}
		List<Employee> list = template.find(new Query(criteria), Employee.class);
		Map<String, StatEmployeeDTO>mapStats = new HashMap<>();
		for(Employee employee : list) {
			LocalDate registrationDate = toLocalDate(employee.getTrackingRecord().get(campaignId).getRegistration());
			LocalDate dropoutDate = null;
			LocalDate trackingDate = null;
			if(employee.getTrackingRecord().get(campaignId).getLeave() != null) {
				dropoutDate = toLocalDate(employee.getTrackingRecord().get(campaignId).getLeave());
			}
			if(employee.getTrackingRecord().get(campaignId).getTracking() != null) {
				trackingDate = toLocalDate(employee.getTrackingRecord().get(campaignId).getTracking());
			}
			if(registrationDate.isAfter(to)) continue;
			if((dropoutDate != null) && (dropoutDate.isBefore(from))) continue;
			
			//set registration
			String timeGroup = getGroupByTime(timeGroupBy, registrationDate);
			String dataGroup = getGroupByDate(employee, dataGroupBy);
			String groupKey = getGroupKey(campaignId, timeGroup, dataGroup); 
			StatEmployeeDTO stats = mapStats.get(groupKey);
			if(stats == null) {
				stats = new  StatEmployeeDTO();
				stats.setCampaign(campaignId);
				stats.setTimeGroup(timeGroup);
				if(StringUtils.isNotBlank(dataGroup)) stats.setDataGroup(dataGroup);
				mapStats.put(groupKey, stats);
			}
			if(isRegistered(registrationDate, from, to)) stats.addRegistration();
			
			//set activeUsers
			if(trackingDate != null) {
				timeGroup = getGroupByTime(timeGroupBy, trackingDate);
				groupKey = getGroupKey(campaignId, timeGroup, dataGroup); 
				stats = mapStats.get(groupKey);
				if(stats == null) {
					stats = new  StatEmployeeDTO();
					stats.setCampaign(campaignId);
					stats.setTimeGroup(timeGroup);
					if(StringUtils.isNotBlank(dataGroup)) stats.setDataGroup(dataGroup);
					mapStats.put(groupKey, stats);
				}
				if(isActive(trackingDate, from, to)) stats.addActiveUsers();
			}
			
			//set dropout
			if(dropoutDate != null) {
				timeGroup = getGroupByTime(timeGroupBy, dropoutDate);
				groupKey = getGroupKey(campaignId, timeGroup, dataGroup); 
				stats = mapStats.get(groupKey);
				if(stats == null) {
					stats = new  StatEmployeeDTO();
					stats.setCampaign(campaignId);
					stats.setTimeGroup(timeGroup);
					if(StringUtils.isNotBlank(dataGroup)) stats.setDataGroup(dataGroup);
					mapStats.put(groupKey, stats);
				}
				if(isDropout(dropoutDate, from, to)) stats.addDropout();
			}
		}
		return new ArrayList<StatEmployeeDTO>(mapStats.values());
	}
	
	private boolean isActive(LocalDate trackingDate, LocalDate from, LocalDate to) {
		if((trackingDate != null) && from.isBefore(trackingDate) && to.isAfter(trackingDate)) return true;
		return false;		
	}
	
	private boolean isDropout(LocalDate dropoutDate, LocalDate from, LocalDate to) {
		if((dropoutDate != null) && from.isBefore(dropoutDate) && to.isAfter(dropoutDate)) return true;
		return false;
	}
	
	private boolean isRegistered(LocalDate registrationDate, LocalDate from, LocalDate to) {
		if(from.isBefore(registrationDate) && to.isAfter(registrationDate)) return true;
		return false;
	}
	
	private String getGroupKey(String campaignId, String timeGroup, String dataGroup) {
		String key = campaignId + "_" + timeGroup;
		if(StringUtils.isNotBlank(dataGroup)) key += "_" + dataGroup;
		return key;
	}
	
	private LocalDate toLocalDate(Long timestamp) {
		return Instant.ofEpochMilli(timestamp).atZone(ZoneId.of(Constants.DEFAULT_TIME_ZONE)).toLocalDate();
	}
	
	private String getGroupByTime(GROUP_BY_TIME timeGroupBy, LocalDate localDate) {		
		if (GROUP_BY_TIME.day.equals(timeGroupBy)) return localDate.toString();
		if (GROUP_BY_TIME.week.equals(timeGroupBy)) return localDate.format(WEEK_PATTERN);
		if (GROUP_BY_TIME.month.equals(timeGroupBy)) return localDate.format(MONTH_PATTERN);
		if (GROUP_BY_TIME.year.equals(timeGroupBy)) return localDate.format(YEAR_PATTERN);
		return null;
	}
	
	private String getGroupByDate(Employee employee, GROUP_BY_DATA dataGroupBy) {
		if (GROUP_BY_DATA.company.equals(dataGroupBy)) return employee.getCompanyId();
		if (GROUP_BY_DATA.location.equals(dataGroupBy)) return employee.getLocation();
		return null;
	}
}
