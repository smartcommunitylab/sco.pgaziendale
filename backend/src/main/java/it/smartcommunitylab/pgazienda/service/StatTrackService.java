package it.smartcommunitylab.pgazienda.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Log;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_DATA;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;
import it.smartcommunitylab.pgazienda.domain.Constants.STAT_TRACK_FIELD;
import it.smartcommunitylab.pgazienda.domain.StatTrack;
import it.smartcommunitylab.pgazienda.dto.StatTrackDTO;
import it.smartcommunitylab.pgazienda.dto.StatTrackDTO.StatValue;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import it.smartcommunitylab.pgazienda.util.DateUtils;

@Service
public class StatTrackService {

	private static final Logger logger = LoggerFactory.getLogger(StatTrackService.class);

	@Autowired
    private CompanyRepository companyRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private MongoTemplate template;
	@Autowired
	private CampaignRepository campaignRepo;


	public List<StatTrackDTO> getTrackStats(
			String campaignId,
			String companyId,
			String locationId,
			Set<String> means,
			Set<String> employeeId,
			String way,
			GROUP_BY_TIME timeGroupBy,
			GROUP_BY_DATA dataGroupBy,
			List<STAT_TRACK_FIELD> fields,
			boolean groupByMean,
			LocalDate from, 
			LocalDate to) throws InconsistentDataException {
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");
		if (from == null) {
			from = campaign.getFrom();
			to = campaign.getTo();
		}
		
		Criteria criteria = Criteria
				.where("campaign").is(campaignId)
				.and("date").lte(to.toString()).gte(from.toString());
		if (StringUtils.isNotEmpty(companyId)) {
			criteria = criteria.and("company").is(companyId);
			if (StringUtils.isNotEmpty(locationId)) {
				criteria = criteria.and("location").is(locationId);
			}			
		}		
		if ((means != null) && !means.isEmpty()) {
			criteria = criteria.and("mode").in(means);
		}
		if (!way.equalsIgnoreCase("all")) {
			if(way.equalsIgnoreCase("wayBack")) {
				criteria = criteria.and("wayBack").is(true);
			}
			if(way.equalsIgnoreCase("wayThere")) {
				criteria = criteria.and("wayBack").is(false);
			}			
		}
		MatchOperation filterOperation = Aggregation.match(criteria);
		
		List<String> group = new LinkedList<>();
		group.add("campaign");
		if(groupByMean)
			group.add("mode");
		if (GROUP_BY_TIME.hour.equals(timeGroupBy)) group.add("hour");
		if (GROUP_BY_TIME.dayOfWeek.equals(timeGroupBy)) group.add("dayOfWeek");
		if (GROUP_BY_TIME.day.equals(timeGroupBy)) group.add("date");
		if (GROUP_BY_TIME.week.equals(timeGroupBy)) group.add("week");
		if (GROUP_BY_TIME.month.equals(timeGroupBy)) group.add("month");
		if (GROUP_BY_TIME.year.equals(timeGroupBy)) group.add("year");

		if (GROUP_BY_DATA.company.equals(dataGroupBy)) group.add("company");
		if (GROUP_BY_DATA.employee.equals(dataGroupBy)) group.add("employeeKey");
		if (GROUP_BY_DATA.location.equals(dataGroupBy)) group.add("locationKey");
		
		GroupOperation groupByOperation = Aggregation.group(group.toArray(new String[group.size()]));
		if (fields == null || fields.isEmpty()) {
			fields = Collections.singletonList(STAT_TRACK_FIELD.score);
		}
		if (fields.contains(STAT_TRACK_FIELD.score)) {
			groupByOperation = groupByOperation.sum("score").as("score");
		}
		if (fields.contains(STAT_TRACK_FIELD.track)) {
			groupByOperation = groupByOperation.count().as("track");
		}
		if (fields.contains(STAT_TRACK_FIELD.co2)) {
			groupByOperation = groupByOperation.sum("co2").as("co2");
		}
		if (fields.contains(STAT_TRACK_FIELD.distance)) {
			groupByOperation = groupByOperation.sum("distance").as("distance");
		}
		if (fields.contains(STAT_TRACK_FIELD.duration)) {
			groupByOperation = groupByOperation.sum("duration").as("duration");
		}
		
		Map<String, StatTrackDTO>mapStats = new HashMap<>();
		List<String> timeGroupList = getTimeGroupList(from, to, timeGroupBy);
		List<String> dataGroupList = new ArrayList<>();

		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		AggregationResults<Document> aggregationResults = template.aggregate(aggregation, StatTrack.class, Document.class);
		if(!groupByMean) { 
			 populateStats(aggregationResults.getMappedResults(), group, mapStats, dataGroupList, dataGroupBy, timeGroupBy, campaignId);
		} else {
			populateStatsByMean(aggregationResults.getMappedResults(), group, mapStats, dataGroupList,  dataGroupBy, timeGroupBy, campaignId);
		}
		fillEmptyDate(campaignId, mapStats, timeGroupList, dataGroupList);
		Comparator<StatTrackDTO>comparator = new Comparator<StatTrackDTO>() {
			@Override
			public int compare(StatTrackDTO o1, StatTrackDTO o2) {
				return o1.getTimeGroup().compareTo(o2.getTimeGroup());
			}
		};
		List<StatTrackDTO> result = new ArrayList<>(mapStats.values());
		updateDateGroupNames(result, dataGroupBy, campaignId);
		Collections.sort(result, comparator);
		return result;		
	}
	
	/**
	 * Fill the dataGroupName field of the StatTrackDTO objects in the result list 
	 * by replacing the companyId or employeeKey or locationKey 
	 * with the corresponding name
	 * @param result the list of StatTrackDTO objects
	 * @param dataGroupBy the type of group
	 * @param campaignId the campaign id
	 */
	private void updateDateGroupNames(List<StatTrackDTO> result, GROUP_BY_DATA dataGroupBy, String campaignId) {
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
		for (StatTrackDTO s : result) {
			s.setDataGroupName(map.getOrDefault(s.getDataGroup(), s.getDataGroup()));			
		}
	}

	private void fillEmptyDate(String campaignId, Map<String, StatTrackDTO> mapStats, List<String> timeGroupList,
			List<String> dataGroupList) {
		if(dataGroupList.size() == 0) {
			for(String timeGroup : timeGroupList) {
				String groupKey = getGroupKey(campaignId, timeGroup, null);
				if(!mapStats.containsKey(groupKey)) {
					StatTrackDTO stats = new  StatTrackDTO();
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
						StatTrackDTO stats = new  StatTrackDTO();
						stats.setCampaign(campaignId);
						stats.setTimeGroup(timeGroup);
						stats.setDataGroup(dataGroup);
						mapStats.put(groupKey, stats);
					}					
				}
			}
		}
	}

	private String getGroupByData(Document doc, GROUP_BY_DATA dataGroupBy) {
		if(doc.get("_id") instanceof Document) {
			Document idMap = (Document) doc.get("_id");
			if (GROUP_BY_DATA.company.equals(dataGroupBy)) return idMap.getString("company");
			if (GROUP_BY_DATA.employee.equals(dataGroupBy)) return idMap.getString("employeeKey");
			if (GROUP_BY_DATA.location.equals(dataGroupBy)) return idMap.getString("locationKey");					
		}
		return null;
	}
	
	private String getGroupByTime(Document doc, GROUP_BY_TIME timeGroupBy) {		
		if(doc.get("_id") instanceof Document) {
			Document idMap = (Document) doc.get("_id");
			if (GROUP_BY_TIME.day.equals(timeGroupBy)) return idMap.getString("date");
			if (GROUP_BY_TIME.week.equals(timeGroupBy)) return idMap.getString("week");
			if (GROUP_BY_TIME.month.equals(timeGroupBy)) return idMap.getString("month");
			if (GROUP_BY_TIME.year.equals(timeGroupBy)) return idMap.getString("year");
			if (GROUP_BY_TIME.hour.equals(timeGroupBy)) return idMap.getString("hour");
			if (GROUP_BY_TIME.dayOfWeek.equals(timeGroupBy)) return idMap.getString("dayOfWeek");			
		}
		if (GROUP_BY_TIME.total.equals(timeGroupBy)) return "total";
		return null;
	}

	private String getGroupKey(String campaignId, String timeGroup, String dataGroup) {
		String key = campaignId + "_" + timeGroup;
		if(StringUtils.isNotBlank(dataGroup)) key += "_" + dataGroup;
		return key;
	}

	private void populateStatsByMean(List<Document> documents, List<String> group, Map<String, StatTrackDTO> mapStats, List<String> dataGroupList,
			GROUP_BY_DATA dataGroupBy, GROUP_BY_TIME timeGroupBy, String campaignId) {
		Map<String, StatTrackDTO.Builder>groupMap = new HashMap<>();
		for(Document doc : documents) {
			String timeGroup = getGroupByTime(doc, timeGroupBy);
			String dataGroup = getGroupByData(doc, dataGroupBy);
			addDataGroup(dataGroupList, dataGroup);
			String groupKey = getGroupKey(campaignId, timeGroup, dataGroup);
			if (!groupMap.containsKey(groupKey)) {
				groupMap.put(groupKey, new StatTrackDTO.Builder().populateKeyFields(doc, group));
			}
			groupMap.get(groupKey).populateStatMean(doc);			
		}
		for(String key :  groupMap.keySet()) {
			mapStats.put(key, groupMap.get(key).updateMainStats().build());
		}
	}
	
	@SuppressWarnings("unused")
	private String getGroupKey(Document doc, List<String> group) {
		String key = "";
		Document idMap = (Document) doc.get("_id");
		for(String k : group) {
			if(!k.equalsIgnoreCase("mode"))
				key += idMap.getString(k) + "_";			
		}
		return key.substring(0, key.lastIndexOf('_'));
	}
	
	private void populateStats(List<Document> documents, List<String> group, Map<String, StatTrackDTO> mapStats, List<String> dataGroupList,
			GROUP_BY_DATA dataGroupBy, GROUP_BY_TIME timeGroupBy, String campaignId) {
		for(Document doc : documents) {
			String timeGroup = getGroupByTime(doc, timeGroupBy);
			String dataGroup = getGroupByData(doc, dataGroupBy);
			addDataGroup(dataGroupList, dataGroup);
			String groupKey = getGroupKey(campaignId, timeGroup, dataGroup);
			mapStats.put(groupKey, new StatTrackDTO.Builder().populateKeyFields(doc, group).populateStatFields(doc).build());
		}
	}
	
	private void addDataGroup(List<String> dataGroupList, String dataGroup) {
		if(StringUtils.isNotBlank(dataGroup) && !dataGroupList.contains(dataGroup)) {
			dataGroupList.add(dataGroup);
		}
	}

	private List<String> getTimeGroupList(LocalDate start, LocalDate end, GROUP_BY_TIME timeGroupBy) {
		if (GROUP_BY_TIME.day.equals(timeGroupBy)) return DateUtils.getDateRangeStrings(start, end);
		if (GROUP_BY_TIME.week.equals(timeGroupBy)) return DateUtils.getDateRangeByWeek(start, end);
		if (GROUP_BY_TIME.month.equals(timeGroupBy)) return DateUtils.getDateRangeByMonth(start, end);
		if (GROUP_BY_TIME.year.equals(timeGroupBy)) return DateUtils.getDateRangeByYear(start, end);
		if (GROUP_BY_TIME.hour.equals(timeGroupBy)) return DateUtils.getDateRangeByHour(start, end);
		if (GROUP_BY_TIME.dayOfWeek.equals(timeGroupBy)) return DateUtils.getDateRangeByDayOfWeek(start, end); 
		if (GROUP_BY_TIME.total.equals(timeGroupBy)) return DateUtils.getDateRangeByTotal(start, end);
		return Collections.emptyList();		
	}

	public void getTrackStatsCSV(PrintWriter writer, 
			String campaignId, 
			String companyId, 
			String location,
			Set<String> means, 
			Set<String> employeeId, 
			String way, 
			GROUP_BY_TIME timeGroupBy, 
			GROUP_BY_DATA dataGroupBy,
			List<STAT_TRACK_FIELD> fields, 
			boolean groupByMean, 
			LocalDate fromDate, 
			LocalDate toDate) throws InconsistentDataException, IOException {
		List<StatTrackDTO> trackStats = getTrackStats(campaignId, companyId, location, means, employeeId, way, timeGroupBy, dataGroupBy, fields, groupByMean, fromDate, toDate);
		CSVWriter csvWriter = new CSVWriter(writer, ';', '"', '"', "\n");
		String[] headers = getHeaders(timeGroupBy, dataGroupBy, groupByMean, fields);
		csvWriter.writeNext(headers);
		for(StatTrackDTO dto : trackStats) {
			List<String[]> rows = getCSV(dto, groupByMean, fields);
			csvWriter.writeAll(rows);
		}
		csvWriter.close();
	}

	private List<String[]> getCSV(StatTrackDTO dto, boolean groupByMean, List<STAT_TRACK_FIELD> fields) {
		List<String[]> result = new ArrayList<>();
		if(!groupByMean) {
			List<String> row = new ArrayList<>();
			row.add(dto.getCampaign());
			row.add(dto.getTimeGroup());
			if(dto.getDataGroupName() != null) row.add(dto.getDataGroupName());			
			row.addAll(getStatValue(dto.getStats(), fields));
			result.add(row.toArray(new String[0]));
		} else {
			for(String mean : dto.getMeanStatMap().keySet()) {
				List<String> row = new ArrayList<>();
				row.add(dto.getCampaign());
				row.add(dto.getTimeGroup());
				if(dto.getDataGroupName() != null) row.add(dto.getDataGroupName());			
				row.add(mean);
				row.addAll(getStatValue(dto.getMeanStatMap().get(mean), fields));
				result.add(row.toArray(new String[0]));
			}
		}
		return result;
	}
	
	private List<String> getStatValue(StatValue stat, List<STAT_TRACK_FIELD> fields) {
		List<String> row = new ArrayList<>();
		fields.forEach(f -> {
			switch (f) {
			case score:
				row.add(String.valueOf(stat.getScore()));
				break;
			case track:
				row.add(String.valueOf(stat.getTrack()));
				break;
			case co2:
				row.add(String.valueOf(stat.getCo2()));
				break;
			case distance:
				row.add(String.valueOf(stat.getDistance()));
				break;
			case duration:
				row.add(String.valueOf(stat.getDuration()));
				break;				
			default:
				break;
			}
		});
		return row;
	}

	private String[] getHeaders(GROUP_BY_TIME timeGroupBy, GROUP_BY_DATA dataGroupBy, boolean groupByMean,
			List<STAT_TRACK_FIELD> fields) {
		List<String>headers = new ArrayList<>();
		headers.add("campaign"); 
		headers.add("timeGroup"); 
		if(dataGroupBy != null) headers.add("dataGroup");
		if(groupByMean) headers.add("mean");
		fields.forEach(f -> headers.add(f.toString()));
		return headers.toArray(new String[0]);
	}
	
	
}
