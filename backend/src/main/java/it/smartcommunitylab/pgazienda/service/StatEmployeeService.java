package it.smartcommunitylab.pgazienda.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.Constants;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_DATA;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;
import it.smartcommunitylab.pgazienda.domain.Constants.STAT_TRACK_FIELD;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.StatTrack;
import it.smartcommunitylab.pgazienda.dto.StatEmployeeDTO;
import it.smartcommunitylab.pgazienda.dto.StatTrackDTO;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
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
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private StatTrackService statTrackService;

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
		
		Map<String, StatEmployeeDTO>mapStats = new HashMap<>();
		
		//set activeUsers
		List<StatTrackDTO> trackStats = statTrackService.getTrackStats(campaignId, companyId, locationId, null, null, "all", 
				timeGroupBy, GROUP_BY_DATA.employee, Collections.singletonList(STAT_TRACK_FIELD.track), false, from, to);
		for(StatTrackDTO dto : trackStats) {
			String timeGroup = dto.getTimeGroup();
			String employeeKey = dto.getDataGroup();
			String[] split = employeeKey.split(StatTrack.EMPLOYEE_KEY_DIV);
			String company = split[0];
			String employeeCode = split[1];
			Employee emp = employeeRepository.findByCompanyIdAndCodeIgnoreCase(company, employeeCode).stream().findAny().orElse(null);
			if(emp != null) {
				String dataGroup = getGroupByData(emp, dataGroupBy);
				String groupKey = getGroupKey(campaignId, timeGroup, dataGroup);
				StatEmployeeDTO stats = mapStats.get(groupKey);
				if(stats == null) {
					stats = new  StatEmployeeDTO();
					stats.setCampaign(campaignId);
					stats.setTimeGroup(timeGroup);
					if(StringUtils.isNotBlank(dataGroup)) stats.setDataGroup(dataGroup);
					mapStats.put(groupKey, stats);
				}
				stats.addActiveUsers();
			}
		}
		
		List<Employee> list = template.find(new Query(criteria), Employee.class);
		for(Employee employee : list) {
			LocalDate registrationDate = toLocalDate(employee.getTrackingRecord().get(campaignId).getRegistration());
			LocalDate dropoutDate = null;
			if(employee.getTrackingRecord().get(campaignId).getLeave() != null) {
				dropoutDate = toLocalDate(employee.getTrackingRecord().get(campaignId).getLeave());
			}
			if(registrationDate.isAfter(to)) continue;
			if((dropoutDate != null) && (dropoutDate.isBefore(from))) continue;
			
			//set registration
			String timeGroup = getGroupByTime(timeGroupBy, registrationDate);
			String dataGroup = getGroupByData(employee, dataGroupBy);
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
	
	private String getGroupByData(Employee employee, GROUP_BY_DATA dataGroupBy) {
		if (GROUP_BY_DATA.company.equals(dataGroupBy)) return employee.getCompanyId();
		if (GROUP_BY_DATA.location.equals(dataGroupBy)) return employee.getLocation();
		return null;
	}
	
	public List<Entry<String, Long>> getEmployeeCount(String campaignId, String companyId) {
		if(StringUtils.isNotBlank(companyId)) {
			Company company = companyRepository.findById(companyId).orElse(null);
			if(company != null) {
				Entry<String, Long> entry = new AbstractMap.SimpleEntry<>(companyId, employeeRepository.countByCompanyId(companyId));
				return Collections.singletonList(entry);
			}
		} else {
			List<Company> list = companyRepository.findByCampaign(campaignId);
			return list.stream().map(c -> {return (Entry<String, Long>) new AbstractMap.SimpleEntry<String, Long>(c.getId(), employeeRepository.countByCompanyId(c.getId()));})
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
 
}
