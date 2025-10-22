package it.smartcommunitylab.pgazienda.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.Constants;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_DATA;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;
import it.smartcommunitylab.pgazienda.domain.Constants.STAT_TRACK_FIELD;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.StatTrack;
import it.smartcommunitylab.pgazienda.dto.FieldEmployeeDTO;
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

		// get employee counts
		Map<String, Integer> employeeCountMap = getEmployeeCountByCompany(campaignId);
		
		Criteria criteria = new Criteria("trackingRecord." + campaignId).exists(true);
		if(StringUtils.isNotBlank(companyId)) {
			criteria = criteria.and("companyId").is(companyId);
			if(StringUtils.isNotBlank(locationId)) {
				criteria = criteria.and("location").is(locationId);
			}
		}
		
		Map<String, StatEmployeeDTO> mapStats = new HashMap<>();
		
		List<String> timeGroupList = getTimeGroupList(from, to, timeGroupBy);
		List<String> dataGroupList = new ArrayList<>();
		
		// set activeUsers
		List<StatTrackDTO> trackStats = statTrackService.getTrackStats(campaignId, companyId, locationId, null, "all", 
				timeGroupBy, GROUP_BY_DATA.employee, Collections.singletonList(STAT_TRACK_FIELD.track), false, false, from, to);
		for(StatTrackDTO dto : trackStats) {
			if(dto.getStats() == null) continue;
			if(dto.getStats().getTrack() == 0) continue;
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
					stats = addNewEmployeeStats(campaignId, timeGroup, dataGroup, company, employeeCountMap);
					mapStats.put(groupKey, stats);
				}
				stats.getActiveUsers().addValue();
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
				stats = addNewEmployeeStats(campaignId, timeGroup, dataGroup, employee.getCompanyId(), employeeCountMap);
				mapStats.put(groupKey, stats);
			}
			if(isRegistered(registrationDate, from, to)) stats.getRegistration().addValue();
			
			//set dropout
			if(dropoutDate != null) {
				timeGroup = getGroupByTime(timeGroupBy, dropoutDate);
				groupKey = getGroupKey(campaignId, timeGroup, dataGroup); 
				stats = mapStats.get(groupKey);
				if(stats == null) {
					stats = addNewEmployeeStats(campaignId, timeGroup, dataGroup, employee.getCompanyId(), employeeCountMap);
					mapStats.put(groupKey, stats);
				}
				if(isDropout(dropoutDate, from, to)) stats.getDropout().addValue();
			}
		}
		// set percentages
		for(StatEmployeeDTO stats : mapStats.values()) {
			stats.getRegistration().setPrcTot((stats.getRegistration().getValue() / (double) stats.getEmployee()) * 100.0);
			stats.getDropout().setPrcTot((stats.getDropout().getValue() / (double) stats.getEmployee()) * 100.0);
			stats.setRegistered(FieldEmployeeDTO.fromValue(stats.getRegistration().getValue() - stats.getDropout().getValue()));
			stats.getRegistered().setPrcTot((stats.getRegistered().getValue() / (double) stats.getEmployee()) * 100.0);
			stats.getActiveUsers().setPrcTot((stats.getActiveUsers().getValue() / (double) stats.getEmployee()) * 100.0);
			if(stats.getRegistration().getValue() > 0) {
				stats.getDropout().setPrcRegistered(((double) stats.getDropout().getValue() / (double) stats.getRegistration().getValue()) * 100.0);
				stats.getActiveUsers().setPrcRegistered(((double) stats.getActiveUsers().getValue() / (double) stats.getRegistration().getValue()) * 100.0);
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
		updateDateGroupNames(result, dataGroupBy, campaignId);
		Collections.sort(result, comparator);
		return result;
	}

	private StatEmployeeDTO addNewEmployeeStats(String campaignId, String timeGroup, String dataGroup, 
			String companyId, Map<String, Integer> employeeCountMap) {
		StatEmployeeDTO stats = new  StatEmployeeDTO();
		stats.setCampaign(campaignId);
		stats.setTimeGroup(timeGroup);
		if(StringUtils.isNotBlank(dataGroup)) stats.setDataGroup(dataGroup);
		if(StringUtils.isNotBlank(companyId)) stats.setEmployee(employeeCountMap.getOrDefault(companyId, 0));
		stats.setDropout(FieldEmployeeDTO.fromValue(0));
		stats.setActiveUsers(FieldEmployeeDTO.fromValue(0));
		stats.setRegistration(FieldEmployeeDTO.fromValue(0));
		stats.setRegistered(FieldEmployeeDTO.fromValue(0));
		return stats;
	}

	private Map<String, Integer> getEmployeeCountByCompany(String campaignId) {
		Criteria criteria = new Criteria("trackingRecord." + campaignId).exists(true);
		MatchOperation filterOperation = Aggregation.match(criteria);
		GroupOperation groupByOperation = Aggregation.group("companyId")
				.count().as("employeeCount");
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		List<Document> results = template.aggregate(aggregation, Employee.class, Document.class).getMappedResults();
		Map<String, Integer> map = new HashMap<>();
		for(Document doc : results) {
			String companyId = doc.getString("_id");
			Integer count = doc.getInteger("employeeCount", 0);
			map.put(companyId, count);
		}
		return map;
	}
	
/**
	 * Fill the dataGroupName field of the StatTrackDTO objects in the result list 
	 * by replacing the companyId or employeeKey or locationKey 
	 * with the corresponding name
	 * @param result the list of StatTrackDTO objects
	 * @param dataGroupBy the type of group
	 * @param campaignId the campaign id
	 */
	private void updateDateGroupNames(List<StatEmployeeDTO> result, GROUP_BY_DATA dataGroupBy, String campaignId) {
		Map<String, String> map = new HashMap<>();
		switch (dataGroupBy) {
			case company:
				Set<String> allCompanies = result.stream().filter(s -> s.getDataGroup() != null).map(s -> s.getDataGroup()).collect(Collectors.toSet());
				for (String companyId : allCompanies) {
					Company company = companyRepository.findById(companyId).orElse(null);
					if (company != null) {
						map.put(companyId, company.getName());
					}
				}
				break;
			case employee:
				List<String> companies = result.stream().filter(s -> s.getDataGroup() != null).map(s -> s.getDataGroup().split(StatTrack.KEY_DIV)[0]).collect(Collectors.toList());
				if (companies.size() > 0) {
					map = employeeRepository.findByCompanyIdIn(companies).stream().collect(Collectors.toMap(e -> e.getCompanyId() + StatTrack.KEY_DIV + e.getCode(), e -> e.getSurname() + " " + e.getName()));					
				}
			case location:
				Set<String> companyIds = result.stream().filter(s -> s.getDataGroup() != null).map(s -> s.getDataGroup().split(StatTrack.KEY_DIV)[0]).collect(Collectors.toSet());
				for (String companyId : companyIds) {
					Company company = companyRepository.findById(companyId).orElse(null);
					if (company != null) {
						List<CompanyLocation> locations = company.getLocations();
						for (CompanyLocation location : locations) {
							map.put(companyId + StatTrack.KEY_DIV + location.getId(), location.getName());
						}
					}
				}
			default:
				break;
		}
		logger.info("Map: {}", map);
		for (StatEmployeeDTO s : result) {
			s.setDataGroupName(map.getOrDefault(s.getDataGroup(), s.getDataGroup()));			
		}
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
		if (GROUP_BY_TIME.total.equals(timeGroupBy)) return "total";
		return null;
	}
	
	private List<String> getTimeGroupList(LocalDate start, LocalDate end, GROUP_BY_TIME timeGroupBy) {
		if (GROUP_BY_TIME.day.equals(timeGroupBy)) return DateUtils.getDateRangeStrings(start, end);
		if (GROUP_BY_TIME.week.equals(timeGroupBy)) return DateUtils.getDateRangeByWeek(start, end);
		if (GROUP_BY_TIME.month.equals(timeGroupBy)) return DateUtils.getDateRangeByMonth(start, end);
		if (GROUP_BY_TIME.year.equals(timeGroupBy)) return DateUtils.getDateRangeByYear(start, end);
		if (GROUP_BY_TIME.total.equals(timeGroupBy)) return DateUtils.getDateRangeByTotal(start, end);
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

	public void getEmployeeStatsCsv(PrintWriter writer, String campaignId, String companyId, String location,
			GROUP_BY_TIME timeGroupBy, GROUP_BY_DATA dataGroupBy, LocalDate fromDate, LocalDate toDate) throws InconsistentDataException, IOException {
		List<StatEmployeeDTO> employeeStats = getEmployeeStats(campaignId, companyId, location, timeGroupBy, dataGroupBy, fromDate, toDate);
		CSVWriter csvWriter = new CSVWriter(writer, ';', '"', '"', "\n");
		String[] headers = getHeaders(timeGroupBy, dataGroupBy);
		csvWriter.writeNext(headers);
		for(StatEmployeeDTO dto : employeeStats) {
			String[] row = getCSV(dto);
			csvWriter.writeNext(row);
		}
		csvWriter.close();
	}
 
	private String[] getCSV(StatEmployeeDTO dto) {
		List<String> row = new ArrayList<>();
		row.add(dto.getCampaign());
		row.add(dto.getTimeGroup());
		if(dto.getDataGroup() != null) {
			row.add(dto.getDataGroup());
			row.add(dto.getDataGroupName());
		}
		row.add(String.valueOf(dto.getEmployee()));
		row.add(getValue(dto.getRegistration()));
		row.add(getPrcTot(dto.getRegistration()));
		row.add(getValue(dto.getRegistered()));
		row.add(getPrcTot(dto.getRegistered()));
		row.add(getValue(dto.getActiveUsers()));
		row.add(getPrcTot(dto.getActiveUsers()));
		row.add(getPrcRegistered(dto.getActiveUsers()));
		row.add(getValue(dto.getDropout()));
		row.add(getPrcTot(dto.getDropout()));
		row.add(getPrcRegistered(dto.getDropout()));
		return row.toArray(new String[0]);
	}

	private String getValue(FieldEmployeeDTO field) {
		if(field != null && field.getValue() != null) {
			return String.valueOf(field.getValue());
		}
		return "";
	}

	private String getPrcTot(FieldEmployeeDTO field) {
		if(field != null && field.getPrcTot() != null) {
			return String.valueOf(field.getPrcTot());
		}
		return "";
	}

	private String getPrcRegistered(FieldEmployeeDTO field) {
		if(field != null && field.getPrcRegistered() != null) {
			return String.valueOf(field.getPrcRegistered());
		}
		return "";
	}

	private String[] getHeaders(GROUP_BY_TIME timeGroupBy, GROUP_BY_DATA dataGroupBy) {
		List<String>headers = new ArrayList<>();
		headers.add("campaign"); 
		headers.add("timeGroup"); 
		if(dataGroupBy != null) {
			headers.add("dataGroup");
			headers.add("dataGroupName");
		}
		headers.add("employee");
		headers.add("registration");
		headers.add("registration_prcTot");
		headers.add("registered");
		headers.add("registered_prcTot");
		headers.add("activeUsers");
		headers.add("activeUsers_prcTot");
		headers.add("activeUsers_prcRegistered");
		headers.add("dropout");
		headers.add("dropout_prcTot");
		headers.add("dropout_prcRegistered");
		return headers.toArray(new String[0]);
	}

}
