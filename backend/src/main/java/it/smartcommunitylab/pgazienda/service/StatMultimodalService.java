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
import it.smartcommunitylab.pgazienda.dto.StatMultimodalDTO;
import it.smartcommunitylab.pgazienda.dto.StatMultimodalDTO.Builder;
import it.smartcommunitylab.pgazienda.dto.StatMultimodalValueDTO;
import it.smartcommunitylab.pgazienda.dto.StatTrackDTO;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import it.smartcommunitylab.pgazienda.util.DateUtils;

@Service
public class StatMultimodalService {
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
		if((allDataGroupBy && GROUP_BY_DATA.company.equals(dataGroupBy)) || 
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
		Collections.sort(result, comparator);
		return result;		
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
		}).collect(Collectors.toList());
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
		if(dto.getModeStatMap().isEmpty()) {
			List<String> row = new ArrayList<>();
			row.add(dto.getCampaign());
			//row.add(dto.getModeGroup());
			row.add(dto.getTimeGroup());
			if(dto.getDataGroup() != null) row.add(dto.getDataGroup());
			row.add("none");
			row.addAll(getStatValue(dto.getStats(), fields));
			result.add(row.toArray(new String[0]));			
		} else {
			for(String mean : dto.getModeStatMap().keySet()) {
				List<String> row = new ArrayList<>();
				row.add(dto.getCampaign());
				//row.add(dto.getModeGroup());
				row.add(dto.getTimeGroup());
				if(dto.getDataGroup() != null) row.add(dto.getDataGroup());			
				row.add(mean);
				row.addAll(getStatValue(dto.getModeStatMap().get(mean), fields));
				result.add(row.toArray(new String[0]));
			}					
		}
		return result;
	}

	private String[] getHeaders(GROUP_BY_TIME timeGroupBy, GROUP_BY_DATA dataGroupBy, List<STAT_TRACK_FIELD> fields) {
		List<String>headers = new ArrayList<>();
		headers.add("campaign");
		headers.add("modeGroup");
		headers.add("timeGroup"); 
		if(dataGroupBy != null) headers.add("dataGroup");
		headers.add("mean");
		fields.forEach(f -> headers.add(f.toString()));
		return headers.toArray(new String[0]);
	}

	private String[] getStatCsv(StatMultimodalDTO dto, String mean, List<STAT_TRACK_FIELD> fields, boolean groupByMean, boolean mainStats) {
		List<String> row = new ArrayList<>();
		row.add(dto.getCampaign());
		row.add(dto.getTimeGroup());
		if(dto.getDataGroup() != null) {
			row.add(dto.getDataGroup());			
		}
		if(mean != null) row.add(mean);
		if(mainStats)
			row.addAll(getStatValue(dto.getStats(), fields));
		else
			row.addAll(getStatValue(dto.getModeStatMap().get(mean), fields));
		return row.toArray(new String[0]);
	}

	private List<String> getStatValue(StatMultimodalValueDTO stat, List<STAT_TRACK_FIELD> fields) {
		List<String> row = new ArrayList<>();
		fields.forEach(f -> {
			switch (f) {
			case track:
				row.add(String.valueOf(stat.getCount()));
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
	
}
