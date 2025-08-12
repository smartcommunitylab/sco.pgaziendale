package it.smartcommunitylab.pgazienda.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import it.smartcommunitylab.pgazienda.util.DateUtils;

@Service
public class StatEmployeeService {
	private static final Logger logger = LoggerFactory.getLogger(TrackingDataService.class);

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
			if(StringUtils.isNotBlank(locationId)) {
				criteria = criteria.and("location").is(locationId);
			}
		}
		
		Map<String, StatEmployeeDTO>mapStats = new HashMap<>();
		
		List<String> timeGroupList = getTimeGroupList(from, to, timeGroupBy);
		List<String> dataGroupList = new ArrayList<>();
		
		//set activeUsers
		List<StatTrackDTO> trackStats = statTrackService.getTrackStats(campaignId, companyId, locationId, null, null, "all", 
				timeGroupBy, GROUP_BY_DATA.employee, Collections.singletonList(STAT_TRACK_FIELD.track), false, from, to);
		for(StatTrackDTO dto : trackStats) {
			String timeGroup = dto.getTimeGroup();
			String employeeKey = dto.getDataGroup();
			String[] split = employeeKey.split(StatTrack.KEY_DIV);
			String company = split[0];
			String employeeCode = split[1];
			Employee emp = employeeRepository.findByCompanyIdAndCodeIgnoreCase(company, employeeCode).stream().findAny().orElse(null);
			if(emp != null) {
				String dataGroup = getGroupByData(emp, dataGroupBy);
				addDataGroup(dataGroupList, dataGroup); 
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
			addDataGroup(dataGroupList, dataGroup);
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
		fillEmptyDate(campaignId, mapStats, timeGroupList, dataGroupList);
		Comparator<StatEmployeeDTO> comparator = new Comparator<StatEmployeeDTO>() {
			@Override
			public int compare(StatEmployeeDTO o1, StatEmployeeDTO o2) {
				return o1.getTimeGroup().compareTo(o2.getTimeGroup());
			}
		};
		List<StatEmployeeDTO>  result = new ArrayList<StatEmployeeDTO>(mapStats.values());
		Collections.sort(result, comparator);
		return result;
	}
	
	private void fillEmptyDate(String campaignId, Map<String, StatEmployeeDTO>mapStats, 
			List<String> timeGroupList, List<String> dataGroupList) {
		if(dataGroupList.size() == 0) {
			for(String timeGroup : timeGroupList) {
				String groupKey = getGroupKey(campaignId, timeGroup, null);
				if(!mapStats.containsKey(groupKey)) {
					StatEmployeeDTO stats = new  StatEmployeeDTO();
					stats.setCampaign(campaignId);
					stats.setTimeGroup(timeGroup);
					mapStats.put(groupKey, stats);
				}
			}
		} else {
			for(String dataGroup : dataGroupList) {
				for(String timeGroup : timeGroupList) {
					String groupKey = getGroupKey(campaignId, timeGroup, dataGroup);
					if(!mapStats.containsKey(groupKey)) {
						StatEmployeeDTO stats = new  StatEmployeeDTO();
						stats.setCampaign(campaignId);
						stats.setTimeGroup(timeGroup);
						stats.setDataGroup(dataGroup);
						mapStats.put(groupKey, stats);
					}					
				}
			}
		}
	}
	
	private void addDataGroup(List<String> dataGroupList, String dataGroup) {
		if(StringUtils.isNotBlank(dataGroup) && !dataGroupList.contains(dataGroup)) {
			dataGroupList.add(dataGroup);
		}
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
		if (GROUP_BY_TIME.week.equals(timeGroupBy)) return localDate.format(DateUtils.WEEK_PATTERN);
		if (GROUP_BY_TIME.month.equals(timeGroupBy)) return localDate.format(DateUtils.MONTH_PATTERN);
		if (GROUP_BY_TIME.year.equals(timeGroupBy)) return localDate.format(DateUtils.YEAR_PATTERN);
		return null;
	}
	
	private List<String> getTimeGroupList(LocalDate start, LocalDate end, GROUP_BY_TIME timeGroupBy) {
		if (GROUP_BY_TIME.day.equals(timeGroupBy)) return DateUtils.getDateRangeStrings(start, end);
		if (GROUP_BY_TIME.week.equals(timeGroupBy)) return DateUtils.getDateRangeByWeek(start, end);
		if (GROUP_BY_TIME.month.equals(timeGroupBy)) return DateUtils.getDateRangeByMonth(start, end);
		if (GROUP_BY_TIME.year.equals(timeGroupBy)) return DateUtils.getDateRangeByYear(start, end);
		return Collections.emptyList();		
	}
	
	private String getGroupByData(Employee employee, GROUP_BY_DATA dataGroupBy) {
		if (GROUP_BY_DATA.company.equals(dataGroupBy)) return employee.getCompanyId();
		if (GROUP_BY_DATA.location.equals(dataGroupBy)) return employee.getCompanyId() + StatTrack.KEY_DIV + employee.getLocation();
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
