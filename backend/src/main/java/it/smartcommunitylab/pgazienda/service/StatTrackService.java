package it.smartcommunitylab.pgazienda.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import com.opencsv.CSVWriter;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_DATA;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;
import it.smartcommunitylab.pgazienda.domain.Constants.STAT_TRACK_FIELD;
import it.smartcommunitylab.pgazienda.domain.StatTrack;
import it.smartcommunitylab.pgazienda.dto.FieldDTO;
import it.smartcommunitylab.pgazienda.dto.StatTrackDTO;
import it.smartcommunitylab.pgazienda.dto.StatValueDTO;
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

	private static final DateTimeFormatter MONTH_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM");
	private static final DateTimeFormatter YEAR_PATTERN = DateTimeFormatter.ofPattern("yyyy");
	private static final DateTimeFormatter WEEK_PATTERN = DateTimeFormatter.ofPattern("yyyy-ww");
	

	public List<StatTrackDTO> getTrackStats(
			String campaignId,
			String companyId,
			String locationId,
			Set<String> means,
			String way,
			GROUP_BY_TIME timeGroupBy,
			GROUP_BY_DATA dataGroupBy,
			List<STAT_TRACK_FIELD> fields,
			boolean groupByMean,
			boolean allDataGroupBy,
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
		// check fields for avg
		if(fields.contains(STAT_TRACK_FIELD.distance) || fields.contains(STAT_TRACK_FIELD.duration) || 
			fields.contains(STAT_TRACK_FIELD.co2)) {
			if(!fields.contains(STAT_TRACK_FIELD.track)) {
				fields.add(STAT_TRACK_FIELD.track);
			}
			if(!fields.contains(STAT_TRACK_FIELD.tripCount)) {
				fields.add(STAT_TRACK_FIELD.tripCount);
			}
		}
		if (fields.contains(STAT_TRACK_FIELD.score)) {
			groupByOperation = groupByOperation.sum("score").as("score");
		}
		if (fields.contains(STAT_TRACK_FIELD.limitedScore)) {
			groupByOperation = groupByOperation.sum("limitedScore").as("limitedScore");
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
		Map<String, Integer> mapTrips = new HashMap<>();
		if (fields.contains(STAT_TRACK_FIELD.tripCount)) {
			getTripCount(criteria, group, dataGroupBy, timeGroupBy, campaignId, mapTrips);
		}
		Map<String, Integer> mapLimitedTrips = new HashMap<>();
		if (fields.contains(STAT_TRACK_FIELD.limitedTripCount)) {
			getLimitedTripCount(criteria, group, dataGroupBy, timeGroupBy, campaignId, mapLimitedTrips);
		}

		Map<String, StatTrackDTO>mapStats = new HashMap<>();
		List<String> timeGroupList = getTimeGroupList(from, to, timeGroupBy);
		List<String> dataGroupList = new ArrayList<>();

		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		AggregationResults<Document> aggregationResults = template.aggregate(aggregation, StatTrack.class, Document.class);
		if(!groupByMean) { 
			 populateStats(aggregationResults.getMappedResults(), group, mapStats, mapTrips, mapLimitedTrips, dataGroupList, dataGroupBy, 
			 	timeGroupBy, fields, campaignId);
		} else {
			populateStatsByMean(aggregationResults.getMappedResults(), group, mapStats, mapTrips, mapLimitedTrips, dataGroupList,  dataGroupBy, 
				timeGroupBy, fields, campaignId);
		}
		//add all poassibly dataGroupBy value if requested
		if((allDataGroupBy && GROUP_BY_DATA.company.equals(dataGroupBy) && StringUtils.isEmpty(companyId)) || 
			(allDataGroupBy && GROUP_BY_DATA.location.equals(dataGroupBy) && StringUtils.isNotEmpty(companyId)) || 
			(allDataGroupBy && GROUP_BY_DATA.employee.equals(dataGroupBy) && StringUtils.isNotEmpty(companyId))) {
			fillAllDataGroup(campaignId, companyId, dataGroupBy, dataGroupList);
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

	private void fillAllDataGroup(String campaignId, String companyId, GROUP_BY_DATA dataGroupBy, List<String> dataGroupList) {
		if (GROUP_BY_DATA.company.equals(dataGroupBy)) {
			//add all companies subscribed to the campaign
			companyRepository.findByCampaign(campaignId).forEach(c -> {
				if(!dataGroupList.contains(c.getId())) {
					dataGroupList.add(c.getId());
				}
			});
		} else if (GROUP_BY_DATA.location.equals(dataGroupBy)) {
			Company company = companyRepository.findById(companyId).orElse(null);
			if (company != null) {
				List<CompanyLocation> locations = company.getLocations();
				for (CompanyLocation location : locations) {
					String locationKey = companyId + StatTrack.KEY_DIV + location.getId();
					if(!dataGroupList.contains(locationKey)) {
						dataGroupList.add(locationKey);
					}
				}
			}
		} else if (GROUP_BY_DATA.employee.equals(dataGroupBy)) {
			List<Employee> employees = employeeRepository.findByCompanyId(companyId);
			for (Employee employee : employees) {
				String employeeKey = employee.getCompanyId() + StatTrack.KEY_DIV + employee.getCode();
				if(!dataGroupList.contains(employeeKey)) {
					dataGroupList.add(employeeKey);
				}
			}
		}		
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
		if(dataGroupBy != null) {
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
						try {
							map = employeeRepository.findByCompanyIdIn(companies).stream().collect(Collectors.toMap(e -> e.getCompanyId() + StatTrack.KEY_DIV + e.getCode(), e -> e.getSurname() + " " + e.getName()));
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}					
					}
					break;
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

	private void populateStatsByMean(List<Document> documents, List<String> group, Map<String, StatTrackDTO> mapStats, 
			Map<String, Integer> mapTrips, Map<String, Integer> mapLimitedTrips, List<String> dataGroupList,
			GROUP_BY_DATA dataGroupBy, GROUP_BY_TIME timeGroupBy, List<STAT_TRACK_FIELD> fields, String campaignId) {
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
			Integer tripCount = mapTrips.get(key);
			Integer limitedTripCount = mapLimitedTrips.get(key);
			Integer multimodalCount = mapTrips.get(key + "_multi");
			mapStats.put(key, groupMap.get(key).updateMainStats(tripCount, limitedTripCount, multimodalCount).build());
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
	
	private void populateStats(List<Document> documents, List<String> group, Map<String, StatTrackDTO> mapStats, 
			Map<String, Integer> mapTrips, Map<String, Integer> mapLimitedTrips, List<String> dataGroupList,
			GROUP_BY_DATA dataGroupBy, GROUP_BY_TIME timeGroupBy, List<STAT_TRACK_FIELD> fields, String campaignId) {
		for(Document doc : documents) {
			String timeGroup = getGroupByTime(doc, timeGroupBy);
			String dataGroup = getGroupByData(doc, dataGroupBy);
			addDataGroup(dataGroupList, dataGroup);
			String groupKey = getGroupKey(campaignId, timeGroup, dataGroup);
			if(fields.contains(STAT_TRACK_FIELD.tripCount)) {
				Integer tripCount = mapTrips.get(groupKey);
				if(tripCount != null) {
					doc.put("tripCount", tripCount);
				}
				Integer multimodalCount = mapTrips.get(groupKey + "_multi");
				if(multimodalCount != null) {
					doc.put("multimodalCount", multimodalCount);
				}
			}
			if(fields.contains(STAT_TRACK_FIELD.limitedTripCount)) {
				Integer limitedTripCount = mapLimitedTrips.get(groupKey);
				if(limitedTripCount != null) {
					doc.put("limitedTripCount", limitedTripCount);
				}
			}
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

	private void getLimitedTripCount(Criteria criteria, List<String> group, GROUP_BY_DATA dataGroupBy, GROUP_BY_TIME timeGroupBy, 
			String campaignId, Map<String, Integer> mapLimitedTrips) {
		criteria = criteria.and("limitedScore").gt(Double.valueOf(0));
		MatchOperation filterOperation = Aggregation.match(criteria);
		List<String> copyGroup = new ArrayList<>(group);
		copyGroup.remove("mode");
		copyGroup.add("multimodalId");
		GroupOperation groupByOperation = Aggregation.group(copyGroup.toArray(new String[copyGroup.size()]));
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		AggregationResults<Document> aggregationResults = template.aggregate(aggregation, StatTrack.class, Document.class);
		for(Document doc : aggregationResults.getMappedResults()) {
			String timeGroup = getGroupByTime(doc, timeGroupBy);
			String dataGroup = getGroupByData(doc, dataGroupBy);
			String groupKey = getGroupKey(campaignId, timeGroup, dataGroup);
			Integer count = mapLimitedTrips.get(groupKey);
			if(count == null) count = 0;
			mapLimitedTrips.put(groupKey, count + 1);
		}
	}

	private void getTripCount(Criteria criteria, List<String> group, GROUP_BY_DATA dataGroupBy, GROUP_BY_TIME timeGroupBy, String campaignId, Map<String, Integer> mapTrips) {
		MatchOperation filterOperation = Aggregation.match(criteria);

		List<String> copyGroup = new ArrayList<>(group);
		copyGroup.remove("mode");
		copyGroup.add("multimodalId");
		GroupOperation groupByOperation = Aggregation.group(copyGroup.toArray(new String[copyGroup.size()])).count().as("trackCount");
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		AggregationResults<Document> aggregationResults = template.aggregate(aggregation, StatTrack.class, Document.class);
		for(Document doc : aggregationResults.getMappedResults()) {
			String timeGroup = getGroupByTime(doc, timeGroupBy);
			String dataGroup = getGroupByData(doc, dataGroupBy);
			String groupKey = getGroupKey(campaignId, timeGroup, dataGroup);
			mapTrips.put(groupKey, mapTrips.getOrDefault(groupKey, 0) + 1);
			mapTrips.put(groupKey + "_multi", mapTrips.getOrDefault(groupKey + "_multi", 0)+1);
		}
	}

	public void getTrackStatsCSV(PrintWriter writer, 
			String campaignId, 
			String companyId, 
			String location,
			Set<String> means, 
			String way, 
			GROUP_BY_TIME timeGroupBy, 
			GROUP_BY_DATA dataGroupBy,
			List<STAT_TRACK_FIELD> fields, 
			boolean groupByMean, 
			boolean allDataGroupBy,
			LocalDate fromDate, 
			LocalDate toDate) throws InconsistentDataException, IOException {
		List<StatTrackDTO> trackStats = getTrackStats(campaignId, companyId, location, means, way, timeGroupBy, dataGroupBy, fields, groupByMean, allDataGroupBy, fromDate, toDate);
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
			result.add(getStatCsv(dto, null, fields, groupByMean, true));
		} else {
			result.add(getStatCsv(dto, "all", fields, groupByMean, true));
			for(String mean : dto.getMeanStatMap().keySet()) {
				result.add(getStatCsv(dto, mean, fields, groupByMean, false));
			}
		}
		return result;
	}

	private String[] getStatCsv(StatTrackDTO dto, String mean, List<STAT_TRACK_FIELD> fields, boolean groupByMean, boolean mainStats) {
		List<String> row = new ArrayList<>();
		row.add(dto.getCampaign());
		row.add(dto.getTimeGroup());
		if(dto.getDataGroup() != null) {
			row.add(dto.getDataGroup());			
			row.add(dto.getDataGroupName());			
		}
		if(mean != null) row.add(mean);
		if(mainStats)
			row.addAll(getStatValue(dto.getStats(), fields, groupByMean, mainStats));
		else
			row.addAll(getStatValue(dto.getMeanStatMap().get(mean), fields, groupByMean, mainStats));
		return row.toArray(new String[0]);
	}
	
	private List<String> getStatValue(StatValueDTO stat, List<STAT_TRACK_FIELD> fields, boolean groupByMean, boolean mainStats) {
		List<String> row = new ArrayList<>();
		fields.forEach(f -> {
			switch (f) {
			case score:
				if(stat.getScore() != null)
					row.add(String.valueOf(stat.getScore().getValue()));
				else 
					row.add("");
				break;
			case limitedScore:
				if(stat.getLimitedScore() != null)
					row.add(String.valueOf(stat.getLimitedScore().getValue()));
				else	
					row.add("");
				break;
			case track:
				if(stat.getTrack() != null)
					row.add(String.valueOf(stat.getTrack()));
				else 
					row.add("");
				break;
			case co2:
				if(stat.getCo2() != null) {
					row.add(String.valueOf(stat.getCo2().getValue()));
					row.add(String.valueOf(stat.getCo2().getAvgTrip()));
					if(!mainStats) {
						row.add(String.valueOf(stat.getCo2().getPrcValue()));
						row.add(String.valueOf(stat.getCo2().getPrcAvgTrip()));
					}
					if(mainStats && groupByMean) {
						row.add("");
						row.add("");
					}
				} else {
					row.add("");
					row.add("");
					if(!mainStats) {
						row.add("");
						row.add("");
					}
					if(mainStats && groupByMean) {
						row.add("");
						row.add("");
					}
				}
				break;
			case distance:
				if(stat.getDistance() != null) {
					row.add(String.valueOf(stat.getDistance().getValue()));
					row.add(String.valueOf(stat.getDistance().getAvgTrip()));
					if(!mainStats) {
						row.add(String.valueOf(stat.getDistance().getPrcValue()));
						row.add(String.valueOf(stat.getDistance().getPrcAvgTrip()));
					}
					if(mainStats && groupByMean) {
						row.add("");
						row.add("");
					}
				} else {
					row.add("");
					row.add("");
					if(!mainStats) {
						row.add("");
						row.add("");
					}
					if(mainStats && groupByMean) {
						row.add("");
						row.add("");
					}
				}
				break;
			case duration:
				if(stat.getDuration() != null) {
					row.add(String.valueOf(stat.getDuration().getValue()));
					row.add(String.valueOf(stat.getDuration().getAvgTrip()));
					if(!mainStats) {
						row.add(String.valueOf(stat.getDuration().getPrcValue()));
						row.add(String.valueOf(stat.getDuration().getPrcAvgTrip()));
					} 
					if(mainStats && groupByMean) {
						row.add("");
						row.add("");
					}
				} else {
					row.add("");
					row.add("");
					if(!mainStats) {
						row.add("");
						row.add("");
					}
					if(mainStats && groupByMean) {
						row.add("");
						row.add("");
					}
				}
				break;
			case tripCount:
				if(stat.getTripCount() != null)
					row.add(String.valueOf(stat.getTripCount()));
				else 
					row.add("");
				break;
			case limitedTripCount:
				if(stat.getLimitedTripCount() != null)
					row.add(String.valueOf(stat.getLimitedTripCount()));
				else
					row.add("");
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
		if(dataGroupBy != null) {
			headers.add("dataGroup");
			headers.add("dataGroupName");
		}
		if(groupByMean) headers.add("mean");
		fields.forEach(f -> {
			headers.add(f.toString());
			if((f == STAT_TRACK_FIELD.distance) || (f == STAT_TRACK_FIELD.duration) || (f == STAT_TRACK_FIELD.co2)) {
				headers.add(f.toString() + "_avgTrip");
				if(groupByMean) {
					headers.add(f.toString() + "_prcValue");
					headers.add(f.toString() + "_prcAvgTrip");
				}
			}
		});
		return headers.toArray(new String[0]);
	}


	public List<Map<String, Object>> getTrackStatsFlat(
		String campaignId,
		String companyId,
		String locationId,
		Set<String> means,
		String way,
		GROUP_BY_TIME timeGroupBy,
		GROUP_BY_DATA dataGroupBy,
		List<STAT_TRACK_FIELD> fields,
		boolean groupByMean,
		boolean allDataGroupBy,
		LocalDate from, 
		LocalDate to) throws InconsistentDataException 
	{
		List<StatTrackDTO> stats = getTrackStats(campaignId, companyId, locationId, means, way, timeGroupBy, dataGroupBy, fields, groupByMean, allDataGroupBy, from, to);
		return flattenTrackStats(stats, timeGroupBy, fields);
	}

	/**
	 * Flatten the stats in a flat format with all fields exploded
	 * @param stats the stats to be flattened
	 * @param timeGroupBy the time group by used for the stats
	 * @param fields the fields to be included in the result
	 * @return List of records representing the stats in a flat format
	 * @throws IOException
	 * @throws InconsistentDataException
	*/
    private List<Map<String, Object>> flattenTrackStats(List<StatTrackDTO> stats, GROUP_BY_TIME timeGroupBy, List<STAT_TRACK_FIELD> fields) {
		List<Map<String, Object>> res = new java.util.ArrayList<>();
		for(StatTrackDTO ds : stats) {
			Map<String, Object> r = flattenStat(ds, timeGroupBy, fields);
			r.put(timeGroupBy.toString(), mapTimeLabel(timeGroupBy, ds.getTimeGroup()));	
			r.put("campaign", ds.getCampaign());
			r.put("id", ds.getDataGroup());
			r.put("name", ds.getDataGroupName() != null ? ds.getDataGroupName() : ds.getDataGroup());
			res.add(r);
		}
		return res;
	}

	/**
	 * Flatten the stat in a flat format with all fields exploded
	 * @param ds
	 * @param timeGroupBy
	 * @param fields
	 * @return
	 */
	private Map<String, Object> flattenStat(StatTrackDTO ds, GROUP_BY_TIME timeGroupBy, List<STAT_TRACK_FIELD> fields) {
		Map<String, Object> res = new java.util.HashMap<>();
		adjustStat(ds);
		res.put("track", ds.getStats().getTrack() != null ? ds.getStats().getTrack() : 0);
		res.put("tripCount", ds.getStats().getTripCount() != null ? ds.getStats().getTripCount() : 0);
		res.put("limitedTripCount", ds.getStats().getLimitedTripCount() != null ? ds.getStats().getLimitedTripCount() : 0);
		res.put("mutlimodalCount", ds.getStats().getMultimodalCount() != null ? ds.getStats().getMultimodalCount() : 0);	
		res.put("singleCount", ds.getStats().getSingleCount() != null ? ds.getStats().getSingleCount() : 0);

		if (ds.getMeanStatMap() != null && !ds.getMeanStatMap().isEmpty()) {
			for(Map.Entry<String, StatValueDTO> e : ds.getMeanStatMap().entrySet()) {
				String mean = e.getKey();
				StatValueDTO ms = e.getValue();
				res.put(mean + "_mean_track", ms.getTrack() != null ? ms.getTrack() : 0);
				res.put(mean + "_mean_tripCount", ms.getTripCount() != null ? ms.getTripCount() : 0);
				res.put(mean + "_mean_limitedTripCount", ms.getLimitedTripCount() != null ? ms.getLimitedTripCount() : 0);
			}
		}	

		Set<STAT_TRACK_FIELD> fieldsToAdd = fields.stream().filter(f -> f != STAT_TRACK_FIELD.track && f != STAT_TRACK_FIELD.tripCount && f != STAT_TRACK_FIELD.limitedTripCount).collect(Collectors.toSet());

		for(STAT_TRACK_FIELD f : fieldsToAdd) {
			FieldDTO stat = getFieldStat(ds.getStats(), f);
			if (stat != null) {
				res.put(f.toString(), stat.getValue() != null ? stat.getValue() : 0);
				res.put(f.toString() + "__avgTrack", stat.getAvgTrack() != null ? stat.getAvgTrack() : 0);
				res.put(f.toString() + "__avgTrip", stat.getAvgTrip() != null ? stat.getAvgTrip() : 0);
			} else {
				res.put(f.toString(), 0);
				res.put(f.toString() + "__avgTrack", 0);
				res.put(f.toString() + "__avgTrip", 0);
			}
			if (ds.getMeanStatMap() != null && !ds.getMeanStatMap().isEmpty()) {
				for(Map.Entry<String, StatValueDTO> e : ds.getMeanStatMap().entrySet()) {
					String mean = e.getKey();
					StatValueDTO ms = e.getValue();
					FieldDTO meanStat = getFieldStat(ms, f);
					if (meanStat != null) {
						res.put(mean + "_mean_" + f.toString(), meanStat.getValue() != null ? meanStat.getValue() : 0);
						res.put(mean + "_mean_" + f.toString() + "__avgTrack", meanStat.getAvgTrack() != null ? meanStat.getAvgTrack() : 0);
						res.put(mean + "_mean_" + f.toString() + "__avgTrip", meanStat.getAvgTrip() != null ? meanStat.getAvgTrip() : 0);
						res.put(mean + "_mean_" + f.toString() + "__prcValue", meanStat.getPrcValue() != null ? meanStat.getPrcValue() : 0);
					} else {
						res.put(mean + "_mean_" + f.toString(), 0);
						res.put(mean + "_mean_" + f.toString() + "__avgTrack", 0);
						res.put(mean + "_mean_" + f.toString() + "__avgTrip", 0);
						res.put(mean + "_mean_" + f.toString() + "__prcValue", 0);
					}
				}
			}
		}
		return res;
	}

	private FieldDTO getFieldStat(StatValueDTO sv, STAT_TRACK_FIELD f) {
		switch (f) {
			case score:
				return sv.getScore();
			case limitedScore:
				return sv.getLimitedScore();
			case co2:
				return sv.getCo2();
			case distance:
				return sv.getDistance();
			case duration:
				return sv.getDuration();	
			default:
				break;
		}
		return null;
	}

	private void adjustStat(StatTrackDTO ds) {
		if (ds.getStats() == null) {
			ds.setStats(new StatValueDTO());
			ds.getStats().setTrack(0);
			ds.getStats().setTripCount(0);
			ds.getStats().setLimitedTripCount(0);
		}
	}

	private String mapTimeLabel(GROUP_BY_TIME timeGroupBy, String timeGroup) {
		if (timeGroupBy == GROUP_BY_TIME.dayOfWeek) {
			return DayOfWeek.valueOf(timeGroup).getDisplayName(TextStyle.FULL, Locale.ITALIAN);
		}	
		return timeGroup;
	}

	public void csvStatistics(
		PrintWriter writer, 
		String campaignId,
		String companyId,
		String locationId,
		Set<String> means,
		String way,
		GROUP_BY_TIME timeGroupBy,
		GROUP_BY_DATA dataGroupBy,
		List<STAT_TRACK_FIELD> fields,
		boolean groupByMean,
		boolean allDataGroupBy,
		LocalDate from, 
		LocalDate to) throws InconsistentDataException
	{
		List<Map<String, Object>> stats = getTrackStatsFlat(campaignId, companyId, locationId, means, way, timeGroupBy, dataGroupBy, fields, groupByMean, allDataGroupBy, from, to);
		CSVWriter csvWriter = new CSVWriter(writer, ';', '"', '"', "\n");
		try{
			// headers
			List<String> headers = createHeadersFlat(timeGroupBy, from, to);
			List<String> fieldHeaders = createSubheaders(headers, fields, means);
			String nameHeader = "name";
			// write headers
			if (!headers.isEmpty()) {
				String s = ";";
				for (String h: headers) {
					s += translateSubHeader(h);
					for (String f: fieldHeaders) {
						s += ";";
					}
				}
				csvWriter.writeNext(s.split(";"));
			}
			String ss = nameHeader;
			for (int i = 0; i < Math.max(headers.size(), 1); i++){
				for (String f: fieldHeaders) {
					ss += ";" +f;
				}
			}
			csvWriter.writeNext(ss.split(";"));
			List<String[]> table = new LinkedList<>();

			for (Map<String, Object> r : stats) {
				String[] row = new String[headers.size() * fieldHeaders.size() + 1];
				row[0] = r.getOrDefault("name", "").toString();
			}

			Map<String, List<Map<String, Object>>> groupedById = stats.stream().collect(Collectors.groupingBy(r -> r.getOrDefault("id", "").toString()));

			groupedById.forEach((id, rs) -> {
				String[] row = new String[headers.size() * fieldHeaders.size() + 1];
				row[0] = rs.get(0).getOrDefault("name", "").toString();
				Map<String, Map<String, Object>> timeInstanceMap = rs.stream().collect(Collectors.toMap(r -> r.getOrDefault(timeGroupBy.toString(), "").toString(), r -> r));

				for (int i = 0; i < headers.size(); i++) {
					 Map<String, Object> r = timeInstanceMap.getOrDefault(headers.get(i), Collections.emptyMap());					 
					for (int j = 0; j < fieldHeaders.size(); j++) {
						String key = fieldHeaders.get(j);
						row[i * fieldHeaders.size() + j + 1] = r.getOrDefault(key, 0).toString();
					}
				}
				table.add(row);
			});
			table.sort((a,b) -> a[0].compareToIgnoreCase(b[0]));
			csvWriter.writeAll(table);
		} finally  {
			if (writer != null) {
				try {
					writer.close();					
				} catch (Exception e) {
				}
			}
		}

	}

	private String translateSubHeader(String h) {
		return h;
	}

	private List<String> createHeadersFlat(GROUP_BY_TIME timeGroupBy, LocalDate from, LocalDate to) {
		List<String> list = new LinkedList<>();
		switch (timeGroupBy) {
			case day: {
				LocalDate curr = from;
				while (!curr.isAfter(to)) {
					list.add(curr.toString());
					curr = curr.plusDays(1);
				}
				break;
			}
			case week: {
				LocalDate curr = from;
				while (!curr.isAfter(to)) {
					list.add(curr.format(WEEK_PATTERN));
					curr = curr.plusWeeks(1);
				}
				break;
			}
			case month: {
				LocalDate curr = from;
				while (!curr.isAfter(to)) {
					list.add(curr.format(MONTH_PATTERN));
					curr = curr.plusMonths(1);
				}
				break;
			}
			case year: {
				LocalDate curr = from;
				while (!curr.isAfter(to)) {
					list.add(curr.format(YEAR_PATTERN));
					curr = curr.plusYears(1);
				}
				break;
			}
			case dayOfWeek: {
				list = DateUtils.getDateRangeByDayOfWeek(from, to).stream().map(d -> DayOfWeek.valueOf(d).getDisplayName(TextStyle.FULL, Locale.ITALIAN)).collect(Collectors.toList());
				break;
			}
			case hour: {
				list = DateUtils.getDateRangeByHour(from, to);
				break;
			}
			case total: {
				list = DateUtils.getDateRangeByTotal(from, to);
				break;
			}
			default:
		}
		return list;
	}

	private List<String> createSubheaders(List<String> headers, List<STAT_TRACK_FIELD> fields, Set<String> means) {
		List<String> fList = new LinkedList<>();
		for (STAT_TRACK_FIELD f : fields) {
			switch (f) {
				case track:
				case tripCount:
				case limitedTripCount:
					fList.add(f.name());
					for (String mean : means) {
						fList.add(mean + "_mean_" + f.name());
					}
					break;
				case score:
				case limitedScore:
				case distance:
				case duration:	
				case co2:
					fList.add(f.name());
					fList.add(f.name() + "__avgTrack");
					fList.add(f.name() + "__avgTrip");
					for (String mean : means) {
						fList.add(mean + "_mean_" + f.name());
						fList.add(mean + "_mean_" + f.name() + "__avgTrack");
						fList.add(mean + "_mean_" + f.name() + "__avgTrip");
						fList.add(mean + "_mean_" + f.name() + "__prcValue");
					}
			}
		}
		return fList;
	}
}
