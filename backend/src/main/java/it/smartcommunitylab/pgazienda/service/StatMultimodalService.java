package it.smartcommunitylab.pgazienda.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
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
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_DATA;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;
import it.smartcommunitylab.pgazienda.domain.Constants.STAT_TRACK_FIELD;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.StatTrack;
import it.smartcommunitylab.pgazienda.dto.FieldDTO;
import it.smartcommunitylab.pgazienda.dto.StatMultimodalDTO;
import it.smartcommunitylab.pgazienda.dto.StatMultimodalValueDTO;
import it.smartcommunitylab.pgazienda.dto.StatTrackDTO;
import it.smartcommunitylab.pgazienda.dto.StatValueDTO;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import it.smartcommunitylab.pgazienda.util.DateUtils;

@Service
public class StatMultimodalService {

	private static final Logger logger = LoggerFactory.getLogger(StatMultimodalService.class);

	@Autowired
	private MongoTemplate template;
	@Autowired
	private CampaignRepository campaignRepo;
	@Autowired
    private CompanyRepository companyRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	
	public List<StatMultimodalDTO> getMultimodalStats(
			String campaignId,
			String companyId,
			String locationId,
			GROUP_BY_TIME timeGroupBy,
			GROUP_BY_DATA dataGroupBy,
			List<STAT_TRACK_FIELD> fields,
			boolean allDataGroupBy,
			LocalDate from, 
			LocalDate to) throws InconsistentDataException {
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");
		if (from == null) {
			from = campaign.getFrom();
			to = campaign.getTo();
		}

		Criteria criteria = new Criteria("campaign").is(campaignId)
				.and("date").lte(to.toString()).gte(from.toString());
		
		if(StringUtils.isNotBlank(companyId)) {
			criteria = criteria.and("companyId").is(companyId);
			if(StringUtils.isNotBlank(locationId)) {
				criteria = criteria.and("location").is(locationId);
			}
		}
		
		MatchOperation filterOperation = Aggregation.match(criteria);
		
		List<String> group = new LinkedList<>();
		group.add("campaign");
		group.add("multimodalId");
		group.add("mode");
		if (GROUP_BY_TIME.hour.equals(timeGroupBy)) group.add("hour");
		if (GROUP_BY_TIME.dayOfWeek.equals(timeGroupBy)) group.add("dayOfWeek");
		if (GROUP_BY_TIME.day.equals(timeGroupBy)) group.add("date");
		if (GROUP_BY_TIME.week.equals(timeGroupBy)) group.add("week");
		if (GROUP_BY_TIME.month.equals(timeGroupBy)) group.add("month");
		if (GROUP_BY_TIME.year.equals(timeGroupBy)) group.add("year");
		
		if (GROUP_BY_DATA.company.equals(dataGroupBy)) group.add("company");
		if (GROUP_BY_DATA.location.equals(dataGroupBy)) group.add("locationKey");
		
		GroupOperation groupByOperation = Aggregation.group(group.toArray(new String[group.size()]));
		groupByOperation = groupByOperation.count().as("count");
		//if(!fields.contains(STAT_TRACK_FIELD.track)) {
		//	fields.add(STAT_TRACK_FIELD.track);
		//}
		if (fields.contains(STAT_TRACK_FIELD.distance)) {
			groupByOperation = groupByOperation.sum("distance").as("distance");
		}
		if (fields.contains(STAT_TRACK_FIELD.duration)) {
			groupByOperation = groupByOperation.sum("duration").as("duration");
		}
		
		Map<String, StatMultimodalDTO> mapStats = new HashMap<>(); 
		List<String> timeGroupList = getTimeGroupList(from, to, timeGroupBy);
		List<String> dataGroupList = new ArrayList<>();
		
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		AggregationResults<Document> aggregationResults = template.aggregate(aggregation, StatTrack.class, Document.class);
		
		Map<String, List<Document>> mapMultimodal = new HashMap<>();
		for(Document doc : aggregationResults.getMappedResults()) {
			Document idMap = (Document) doc.get("_id");
			List<Document> list = mapMultimodal.get(idMap.getString("multimodalId"));
			if(list == null) {
				list = new ArrayList<>();
				mapMultimodal.put(idMap.getString("multimodalId"), list);
			}
			list.add(doc);
		}

		// map multimodalId to mode list
		Map<String, String> mapModes = new HashMap<>();
		for(String multimodalId : mapMultimodal.keySet()) {
			String modes = getModeList(mapMultimodal.get(multimodalId));
			mapModes.put(multimodalId, modes);
		}

		/*Map<String, Integer> mapModeMultimodalCount = new HashMap<>();
		for(String modes : mapModes.keySet()) {
			mapModeMultimodalCount.put(modes, countMultimodal(mapModes.get(modes)));
		}*/
		
		Map<String, StatMultimodalDTO.Builder> mapBuilders = new HashMap<>();
		Map<String, List<Document>> mapModeKeyDocs = new HashMap<>();
		for(String multimodalId : mapMultimodal.keySet()) {
			String mode = mapModes.get(multimodalId);
			for(Document doc : mapMultimodal.get(multimodalId)) {
				String timeGroup = getGroupByTime(doc, timeGroupBy);
				String dataGroup = getGroupByData(doc, dataGroupBy);
				addDataGroup(dataGroupList, dataGroup);
				String groupKey = getGroupKey(campaignId, timeGroup, dataGroup);
				String modeKey = getModeKey(campaignId, timeGroup, dataGroup, mode);
				StatMultimodalDTO.Builder builder = mapBuilders.get(groupKey);
				if(builder == null) {
					builder = new StatMultimodalDTO.Builder();
					builder.populateKeyFields(doc);
					mapBuilders.put(groupKey, builder);
				}
				builder.mergeStatModeDoc(doc, mode);
				List<Document> docList = mapModeKeyDocs.get(groupKey);
				if(docList == null) {
					docList = new ArrayList<>();
					mapModeKeyDocs.put(modeKey, docList);
				}
				docList.add(doc);
			}
		}
		// set mode trip counter
		for(String modeKey : mapModeKeyDocs.keySet()) {
			List<Document> docs = mapModeKeyDocs.get(modeKey);
			int count = countMultimodal(docs);
			StatMultimodalDTO.Builder builder = mapBuilders.get(modeKey.substring(0, modeKey.lastIndexOf("___")));
			if(builder != null) {
				builder.setModeCount(modeKey.substring(modeKey.lastIndexOf("___") + 3), count);
			}
		}
		for(String groupKey : mapBuilders.keySet()) {
			mapStats.put(groupKey, mapBuilders.get(groupKey).updateMainStats().build());
		}
		//add all poassibly dataGroupBy value if requested
		if((allDataGroupBy && GROUP_BY_DATA.company.equals(dataGroupBy) && StringUtils.isEmpty(companyId)) || 
			(allDataGroupBy && GROUP_BY_DATA.location.equals(dataGroupBy) && StringUtils.isNotEmpty(companyId)) || 
			(allDataGroupBy && GROUP_BY_DATA.employee.equals(dataGroupBy) && StringUtils.isNotEmpty(companyId))) {
			fillAllDataGroup(campaignId, companyId, dataGroupBy, dataGroupList);
		}
		fillEmptyDate(campaignId, mapStats, timeGroupList, dataGroupList);
		Comparator<StatMultimodalDTO>comparator = new Comparator<StatMultimodalDTO>() {
			@Override
			public int compare(StatMultimodalDTO o1, StatMultimodalDTO o2) {
				return o1.getTimeGroup().compareTo(o2.getTimeGroup());
			}
		};
		List<StatMultimodalDTO> result = new ArrayList<>(mapStats.values());
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
	private void updateDateGroupNames(List<StatMultimodalDTO> result, GROUP_BY_DATA dataGroupBy, String campaignId) {
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
					Set<String> companies = result.stream().filter(s -> s.getDataGroup() != null).map(s -> s.getDataGroup().split(StatTrack.KEY_DIV)[0]).collect(Collectors.toSet());
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
		for (StatMultimodalDTO s : result) {
			s.setDataGroupName(map.getOrDefault(s.getDataGroup(), s.getDataGroup()));			
		}
	}

	public List<Map<String, Object>> getMultimodalStatsFlat(
		String campaignId,
		String companyId,
		String locationId,
		GROUP_BY_TIME timeGroupBy,
		GROUP_BY_DATA dataGroupBy,
		List<STAT_TRACK_FIELD> fields,
		boolean allDataGroupBy,
		LocalDate from, 
		LocalDate to) throws InconsistentDataException {
			List<StatMultimodalDTO> stats = getMultimodalStats(campaignId, companyId, locationId, timeGroupBy, dataGroupBy, fields, allDataGroupBy, from, to);
			return flattenTrackStats(stats, timeGroupBy);
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
    private List<Map<String, Object>> flattenTrackStats(List<StatMultimodalDTO> stats, GROUP_BY_TIME timeGroupBy) {
		List<Map<String, Object>> res = new java.util.ArrayList<>();
		for(StatMultimodalDTO ds : stats) {
			Map<String, Object> r = flattenStat(ds, timeGroupBy);
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
	private Map<String, Object> flattenStat(StatMultimodalDTO ds, GROUP_BY_TIME timeGroupBy) {
		Map<String, Object> res = new java.util.HashMap<>();
		adjustStat(ds);
		res.put("count", ds.getStats().getCount() != null ? ds.getStats().getCount().getValue() : 0);
		res.put("distance", ds.getStats().getDistance() != null ? ds.getStats().getDistance().getValue() : 0);
		res.put("duration", ds.getStats().getDuration() != null ? ds.getStats().getDuration().getValue() : 0);
		res.put("distance_avg", ds.getStats().getDistance() != null && ds.getStats().getCount() != null && ds.getStats().getCount().getValue() != 0 ? ds.getStats().getDistance().getValue() / ds.getStats().getCount().getValue() : 0);
		res.put("duration_avg", ds.getStats().getDuration() != null && ds.getStats().getCount() != null && ds.getStats().getCount().getValue() != 0 ? ds.getStats().getDuration().getValue() / ds.getStats().getCount().getValue() : 0);

		if (ds.getModeStatMap() != null && !ds.getModeStatMap().isEmpty()) {
			for(Map.Entry<String, StatMultimodalValueDTO> e : ds.getModeStatMap().entrySet()) {
				String mode = e.getKey();
				StatMultimodalValueDTO ms = e.getValue();
				res.put(mode + "_count", ms.getCount() != null && ms.getCount().getValue() != null ? ms.getCount().getValue() : 0);
				res.put(mode + "_distance", ms.getDistance() != null && ms.getDistance().getValue() != null ? ms.getDistance().getValue() : 0);
				res.put(mode + "_duration", ms.getDuration() != null && ms.getDuration().getValue() != null ? ms.getDuration().getValue() : 0);
				res.put(mode + "_distance_avg", ms.getDistance() != null && ms.getDistance().getValue() != 0 && ms.getCount() != null && ms.getCount().getValue() != null && ms.getCount().getValue() != 0 ? ms.getDistance().getValue() / ms.getCount().getValue() : 0);
				res.put(mode + "_duration_avg", ms.getDuration() != null && ms.getDuration().getValue() != 0 && ms.getCount() != null && ms.getCount().getValue() != null && ms.getCount().getValue() != 0 ? ms.getDuration().getValue() / ms.getCount().getValue() : 0);
				res.put(mode + "_prcCount", ms.getCount() != null && ms.getCount().getValue() != null && ds.getStats().getCount() != null && ds.getStats().getCount().getValue() != 0 ? (ms.getCount().getValue() / ds.getStats().getCount().getValue()) * 100 : 0);
				res.put(mode + "_prcDistance", ms.getDistance() != null && ms.getDistance().getValue() != null && ds.getStats().getDistance() != null && ds.getStats().getDistance().getValue() != 0 ? (ms.getDistance().getValue() / ds.getStats().getDistance().getValue()) * 100 : 0);
				res.put(mode + "_prcDuration", ms.getDuration() != null && ms.getDuration().getValue() != null && ds.getStats().getDuration() != null && ds.getStats().getDuration().getValue() != 0 ? (ms.getDuration().getValue() / ds.getStats().getDuration().getValue()) * 100 : 0);

			}
		}

		return res;
	}

	private void adjustStat(StatMultimodalDTO ds) {
		if (ds.getStats() == null) {
			ds.setStats(new StatMultimodalValueDTO());
		}
		if (ds.getStats().getCount() == null) {
			ds.getStats().setCount(FieldDTO.fromValue(0.0));
		}
		if (ds.getStats().getDistance() == null) {
			ds.getStats().setDistance(FieldDTO.fromValue(0.0));
		}	
		if (ds.getStats().getDuration() == null) {
			ds.getStats().setDuration(FieldDTO.fromValue(0.0));
		}

	}


	private String mapTimeLabel(GROUP_BY_TIME timeGroupBy, String timeGroup) {
		if (timeGroupBy == GROUP_BY_TIME.dayOfWeek) {
			return DayOfWeek.valueOf(timeGroup).getDisplayName(TextStyle.FULL, Locale.ITALIAN);
		}	
		return timeGroup;
	}

	private Integer countMultimodal(List<Document> list) {
		List<String> multimodalIds = new ArrayList<>();
		for(Document doc : list) {
			Document idMap = (Document) doc.get("_id");
			String multimodalId = idMap.getString("multimodalId");
			if(!multimodalIds.contains(multimodalId)) {
				multimodalIds.add(multimodalId);
			}
		}
		return multimodalIds.size();
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
	
	private void fillEmptyDate(String campaignId, Map<String, StatMultimodalDTO> mapStats, 
			List<String> timeGroupList, List<String> dataGroupList) {
		if(dataGroupList.size() == 0) {
			for(String timeGroup : timeGroupList) {
				String groupKey = getGroupKey(campaignId, timeGroup, null);
				if(!mapStats.containsKey(groupKey)) {
					StatMultimodalDTO stats = new  StatMultimodalDTO();
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
						StatMultimodalDTO stats = new  StatMultimodalDTO();
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

	private String getGroupKey(String campaignId, String timeGroup, String dataGroup) {
		String key = campaignId + "_" + timeGroup;
		if(StringUtils.isNotBlank(dataGroup)) key += "_" + dataGroup;
		return key;
	}

	private String getModeKey(String campaignId, String timeGroup, String dataGroup, String mode) {
		String key = campaignId + "_" + timeGroup;
		if(StringUtils.isNotBlank(dataGroup)) key += "_" + dataGroup;
		key += "___" + mode;
		return key;
	}

	private String getGroupByData(Document doc, GROUP_BY_DATA dataGroupBy) {
		Document idMap = (Document) doc.get("_id");
		if (GROUP_BY_DATA.company.equals(dataGroupBy)) return idMap.getString("company");
		if (GROUP_BY_DATA.location.equals(dataGroupBy)) return idMap.getString("locationKey");
		return null;
	}

	private String getGroupByTime(Document doc, GROUP_BY_TIME timeGroupBy) {
		Document idMap = (Document) doc.get("_id");
		if(idMap.containsKey("hour")) 
			return idMap.getString("hour");
		else if(idMap.containsKey("dayOfWeek")) 
			return idMap.getString("dayOfWeek");
		else if(idMap.containsKey("date")) 
			return idMap.getString("date");
		else if(idMap.containsKey("week")) 
			return idMap.getString("week");
		else if(idMap.containsKey("month")) 
			return idMap.getString("month");
		else if(idMap.containsKey("year")) 
			return idMap.getString("year");
		else return "total";
	}

	private String getModeList(List<Document> docs) {
		List<String> modes = docs.stream().map(doc -> {
			Document idMap = (Document) doc.get("_id");
			return idMap.getString("mode");
		}).distinct().collect(Collectors.toList());
		Collections.sort(modes);
		return String.join("_", modes);
	}

	public void getMultimodalStatsCsv(
			PrintWriter writer, 
			String campaignId, 
			String companyId, 
			String location,
			GROUP_BY_TIME timeGroupBy, 
			GROUP_BY_DATA dataGroupBy, 
			List<STAT_TRACK_FIELD> fields, 
			boolean allDataGroupBy,
			LocalDate fromDate,
			LocalDate toDate) throws InconsistentDataException, IOException {
		List<StatMultimodalDTO> multimodalStats = getMultimodalStats(campaignId, companyId, location, timeGroupBy, dataGroupBy, fields, allDataGroupBy, fromDate, toDate);
		CSVWriter csvWriter = new CSVWriter(writer, ';', '"', '"', "\n");
		String[] headers = getHeaders(timeGroupBy, dataGroupBy, fields);
		csvWriter.writeNext(headers);
		for(StatMultimodalDTO dto : multimodalStats) {
			List<String[]> rows = getCSV(dto, fields);
			csvWriter.writeAll(rows);
		}
		csvWriter.close();
	}

	private List<String[]> getCSV(StatMultimodalDTO dto, List<STAT_TRACK_FIELD> fields) {
		List<String[]> result = new ArrayList<>();
		result.add(getStatCsv(dto, "all", fields, true));
		for(String mean : dto.getModeStatMap().keySet()) {
			result.add(getStatCsv(dto, mean, fields, false));
		}
		return result;
	}

	private String[] getStatCsv(StatMultimodalDTO dto, String mean, List<STAT_TRACK_FIELD> fields, boolean mainStats) {
		List<String> row = new ArrayList<>();
		row.add(dto.getCampaign());
		row.add(dto.getTimeGroup());
		if(dto.getDataGroup() != null) {
			row.add(dto.getDataGroup());			
		}
		if(mean != null) row.add(mean);
		if(mainStats) {
			row.addAll(getStatValue(dto.getStats(), fields, mainStats));
		} else {
			row.addAll(getStatValue(dto.getModeStatMap().get(mean), fields, mainStats));
		}
		return row.toArray(new String[0]);
	}

	private List<String> getStatValue(StatMultimodalValueDTO stat, List<STAT_TRACK_FIELD> fields, boolean mainStats) {
		List<String> row = new ArrayList<>();
		fields.forEach(f -> {
			switch (f) {
			case track:
				if(stat.getCount() != null) {
					row.add(String.valueOf(stat.getCount().getValue()));
					if(!mainStats) {
						row.add(String.valueOf(stat.getCount().getPrcValue()));
					} else {
						row.add("");
					}
				} else {
					row.add("");
					row.add("");
				}
				break;
			case distance:
				if(stat.getDistance() != null) {
					row.add(String.valueOf(stat.getDistance().getValue()));
					row.add(String.valueOf(stat.getDistance().getAvgTrip()));
					if(!mainStats) {
						row.add(String.valueOf(stat.getDistance().getPrcValue()));
						row.add(String.valueOf(stat.getDistance().getPrcAvgTrip()));
					} else {
						row.add("");
						row.add("");
					}
				} else {
					row.add("");
					row.add("");
					row.add("");
					row.add("");
				}
				break;
			case duration:
				if(stat.getDuration() != null) {
					row.add(String.valueOf(stat.getDuration().getValue()));
					row.add(String.valueOf(stat.getDuration().getAvgTrip()));
					if(!mainStats) {
						row.add(String.valueOf(stat.getDuration().getPrcValue()));
						row.add(String.valueOf(stat.getDuration().getPrcAvgTrip()));
					} else {
						row.add("");
						row.add("");
					}
				} else {
					row.add("");
					row.add("");
					row.add("");
					row.add("");
				}
				break;				
			default:
				break;
			}
		});
		return row;
	}

	private String[] getHeaders(GROUP_BY_TIME timeGroupBy, GROUP_BY_DATA dataGroupBy, List<STAT_TRACK_FIELD> fields) {
		List<String>headers = new ArrayList<>();
		headers.add("campaign");
		headers.add("timeGroup"); 
		if(dataGroupBy != null) headers.add("dataGroup");
		headers.add("mean");
		fields.forEach(f -> {
			if(STAT_TRACK_FIELD.track == f) {
				headers.add("count"); 
				headers.add("count_prcValue");
			}
			else {
				headers.add(f.toString());
				headers.add(f.toString() + "_avgTrip");
				headers.add(f.toString() + "_prcValue");
				headers.add(f.toString() + "_prcAvgTrip");
			}
		});
		return headers.toArray(new String[0]);
	}



	
}
