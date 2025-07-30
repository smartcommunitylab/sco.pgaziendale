package it.smartcommunitylab.pgazienda.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import it.smartcommunitylab.pgazienda.domain.Constants.STAT_FIELD;
import it.smartcommunitylab.pgazienda.domain.Constants.STAT_TRACK_FIELD;
import it.smartcommunitylab.pgazienda.domain.StatTrack;
import it.smartcommunitylab.pgazienda.dto.StatTrackDTO;

@Service
public class StatTrackService {
	@Autowired
	private MongoTemplate template;

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
			LocalDate to) {
		Criteria criteria = Criteria
				.where("campaign").is(campaignId)
				.and("date").lte(to.toString()).gte(from.toString());
		if (StringUtils.isNotEmpty(companyId)) {
			criteria = criteria.and("company").is(companyId);
		}		
		if (StringUtils.isNotEmpty(locationId)) {
			criteria = criteria.and("locationId").is(locationId);
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
		if (GROUP_BY_DATA.employee.equals(dataGroupBy)) group.add("employeeCode");
		if (GROUP_BY_DATA.location.equals(dataGroupBy)) group.add("locationId");
		
		GroupOperation groupByOperation = Aggregation.group(group.toArray(new String[group.size()]));
		if (fields == null || fields.isEmpty()) {
			fields = Collections.singletonList(STAT_TRACK_FIELD.score);
		}
		if (fields.contains(STAT_TRACK_FIELD.score)) {
			groupByOperation = groupByOperation.sum("score").as("score");
		}
		if (fields.contains(STAT_TRACK_FIELD.trackCount)) {
			groupByOperation = groupByOperation.count().as("trackCount");
		}
		if (fields.contains(STAT_TRACK_FIELD.co2saved)) {
			groupByOperation = groupByOperation.sum("co2").as("co2saved");
		}
		if (fields.contains(STAT_TRACK_FIELD.distance)) {
			groupByOperation = groupByOperation.sum("distance").as("distance");
		}
		if (fields.contains(STAT_TRACK_FIELD.duration)) {
			groupByOperation = groupByOperation.sum("duration").as("duration");
		}
		
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		AggregationResults<Document> aggregationResults = template.aggregate(aggregation, StatTrack.class, Document.class);
		List<StatTrackDTO> result = new ArrayList<>();
		for(Document doc : aggregationResults.getMappedResults()) {
			result.add(populateKeyFields(doc, group));
		}
		return result;
	}
	
	private StatTrackDTO populateKeyFields(Document doc, List<String> group) {
		StatTrackDTO dto = new StatTrackDTO();
		Document idMap = (Document) doc.get("_id");
		if(idMap.containsKey("campaign")) dto.setCampaign(idMap.getString("campaign"));
		if(idMap.containsKey("mode")) dto.setMode(idMap.getString("mode"));
		
		if(idMap.containsKey("hour")) dto.setHour(idMap.getString("hour"));
		if(idMap.containsKey("dayOfWeek")) dto.setDayOfWeek(idMap.getString("dayOfWeek"));
		if(idMap.containsKey("date")) dto.setDate(idMap.getString("date"));
		if(idMap.containsKey("week")) dto.setWeek(idMap.getString("week"));
		if(idMap.containsKey("month")) dto.setMonth(idMap.getString("month"));
		if(idMap.containsKey("year")) dto.setYear(idMap.getString("year"));
		
		if(idMap.containsKey("company")) dto.setCompany(idMap.getString("company"));
		if(idMap.containsKey("playerId")) dto.setPlayerId(idMap.getString("playerId"));
		if(idMap.containsKey("employeeCode")) dto.setEmployeeCode(idMap.getString("employeeCode"));
		if(idMap.containsKey("locationId")) dto.setLocationId (idMap.getString("locationId"));
		
		if(doc.containsKey("score")) dto.setScore(doc.getDouble("score"));
		if(doc.containsKey("co2saved")) dto.setScore(doc.getDouble("co2saved"));
		if(doc.containsKey("distance")) dto.setDistance(doc.getDouble("distance"));
		if(doc.containsKey("duration")) dto.setDuration(doc.getLong("duration"));
		if(doc.containsKey("trackCount")) dto.setTrackCount(doc.getInteger("trackCount"));
		
		return dto;
	}	
}
