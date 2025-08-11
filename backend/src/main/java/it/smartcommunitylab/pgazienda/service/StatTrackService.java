package it.smartcommunitylab.pgazienda.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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

import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_DATA;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;
import it.smartcommunitylab.pgazienda.domain.Constants.STAT_TRACK_FIELD;
import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.StatTrack;
import it.smartcommunitylab.pgazienda.dto.StatTrackDTO;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;

@Service
public class StatTrackService {
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
		
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		AggregationResults<Document> aggregationResults = template.aggregate(aggregation, StatTrack.class, Document.class);
		if(!groupByMean) { 
			return populateStats(aggregationResults.getMappedResults(), group);
		} else {
			return populateStatsByMean(aggregationResults.getMappedResults(), group);
		}
	}
	
	private List<StatTrackDTO>populateStatsByMean(List<Document> documents, List<String> group) {
		Map<String, StatTrackDTO.Builder>groupMap = new HashMap<>();
		for(Document doc : documents) {
			String groupKey = getGroupKey(doc, group);
			if (!groupMap.containsKey(groupKey)) {
				groupMap.put(groupKey, new StatTrackDTO.Builder().populateKeyFields(doc, group));
			}
			groupMap.get(groupKey).populateStatMean(doc);			
		}
		return groupMap.values().stream().map(builder -> builder.updateMainStats().build()).collect(Collectors.toList());
	}
	
	private String getGroupKey(Document doc, List<String> group) {
		String key = "";
		Document idMap = (Document) doc.get("_id");
		for(String k : group) {
			if(!k.equalsIgnoreCase("mode"))
				key += idMap.getString(k) + "_";			
		}
		return key.substring(0, key.lastIndexOf('_'));
	}
	
	private List<StatTrackDTO> populateStats(List<Document> documents, List<String> group) {
		List<StatTrackDTO> result = new ArrayList<>();
		for(Document doc : documents) {
			result.add(new StatTrackDTO.Builder().populateKeyFields(doc, group).populateStatFields(doc).build());
		}
		return result;		
	}
	
}
