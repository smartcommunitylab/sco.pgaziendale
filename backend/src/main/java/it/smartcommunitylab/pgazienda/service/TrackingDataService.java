/*******************************************************************************
 * Copyright 2015 Fondazione Bruno Kessler
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/

package it.smartcommunitylab.pgazienda.service;

import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.opencsv.CSVWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Circle;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.Constants;
import it.smartcommunitylab.pgazienda.domain.Constants.MEAN;
import it.smartcommunitylab.pgazienda.domain.DayStat;
import it.smartcommunitylab.pgazienda.domain.DayStat.MeanScore;
import it.smartcommunitylab.pgazienda.domain.DayStat.Score;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.Shape;
import it.smartcommunitylab.pgazienda.domain.Subscription;
import it.smartcommunitylab.pgazienda.domain.TrackingData;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.domain.Campaign.VirtualScoreValue;
import it.smartcommunitylab.pgazienda.dto.TrackDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO.TrackLegDTO;
import it.smartcommunitylab.pgazienda.dto.TrackValidityDTO;
import it.smartcommunitylab.pgazienda.dto.TrackValidityDTO.TrackValidityLegDTO;
import it.smartcommunitylab.pgazienda.dto.TransportStatDTO;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.DayStatRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import it.smartcommunitylab.pgazienda.util.LimitsUtils;
import it.smartcommunitylab.pgazienda.util.TrackUtils;

/**
 * @author raman
 *
 */
@Service
public class TrackingDataService {

	/**
	 * 
	 */
	private static final Logger logger = LoggerFactory.getLogger(TrackingDataService.class);
	private static final DateTimeFormatter MONTH_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM");
	private static final DateTimeFormatter WEEK_PATTERN = DateTimeFormatter.ofPattern("YYYY-ww", Constants.DEFAULT_LOCALE);
	
	@Autowired
	private CampaignRepository campaignRepo;
	@Autowired
	private CompanyRepository companyRepo;
	@Autowired
	private EmployeeRepository employeeRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private DayStatRepository dayStatRepo;

	@Autowired
	private MongoTemplate template;

	/**
	 * Validate multimodal track. 
	 * 1. Check track starts/ends within the open campaign location areas.
	 * 2. Compute the limits for track number and the limits for virtual points 
	 * 3. Calculate the points each track leg brings. 
	 * @param campaignId
	 * @param playerId
	 * @param track
	 * @return
	 * @throws InconsistentDataException
	 */
	public TrackValidityDTO validate(String campaignId, String playerId, TrackDTO track) throws InconsistentDataException {
		logger.info("Validating track service for campaign {} player {}, track count {}", campaignId, playerId, track.getLegs().size());
		
		// campaign
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) {
			throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");
		} 
		// user, should be registered
		User user = userRepo.findByPlayerId(playerId).orElse(null);
		if (user == null) {
			throw new InconsistentDataException("Invalid user: " + playerId, "NO_USER");
		} 
		UserRole role = user.findRole(it.smartcommunitylab.pgazienda.Constants.ROLE_APP_USER).orElse(null);
		if (role == null) {
			throw new InconsistentDataException("Invalid user role: " + playerId, "NO_USER");
		}
		Subscription subscription = role.getSubscriptions().stream().filter(s -> s.getCampaign().equals(campaignId)).findAny().orElse(null);
		if (subscription == null) {
			throw new InconsistentDataException("Invalid user subscription: " + playerId, "NO_USER");
		}
		
		// company
		List<Company> companies = companyRepo.findByCode(subscription.getCompanyCode());
		if (companies.size() == 0) {
			throw new InconsistentDataException("Invalid company: " + subscription.getCompanyCode(), "NO_COMPANY");
		}
		Company company = companies.get(0);
		
		// employee matching subscription
		Employee employee = employeeRepo.findByCompanyIdAndCodeIgnoreCase(company.getId(), subscription.getKey()).stream().findAny().orElse(null);
		if (employee == null ) throw new InconsistentDataException("Invalid user key: " + playerId, "NO_USER");
		// locations
		List<Shape> locations = 
				company.getLocations().stream()
				.filter(l -> checkWorking(l, toLocalDate(track.getStartTime())))
				.map(l -> new Circle(new double[] {l.getLatitude(), l.getLongitude()}, l.getRadius()))
				.collect(Collectors.toList());
		
		// index of matching leg (first or last of the trip)
		int matchingLegIndex = -1;
		// no locations, no legs with appropriate means, trip does not match any location
		if (locations.size() == 0) {
			return TrackValidityDTO.errLocations();
		} else if ((matchingLegIndex = TrackUtils.matchLocations(track, locations)) < 0) {
			return TrackValidityDTO.errMatches();
		} else {
			List<TrackLegDTO> validTrack = new LinkedList<>();
			// limit to valid legs towards the location
			if (matchingLegIndex == 0) {
				for (int i = 0; i < track.getLegs().size(); i++) {
					TrackLegDTO leg = track.getLegs().get(i);
					if (!Boolean.TRUE.equals(leg.getValid())) break;
					validTrack.add(leg);
				}
			} else {
				for (int i = track.getLegs().size() - 1; i >= 0; i--) {
					TrackLegDTO leg = track.getLegs().get(i);
					if (!Boolean.TRUE.equals(leg.getValid())) break;
					validTrack.add(leg);
				}
				Collections.reverse(validTrack);
			}
			if (validTrack.size() == 0) {
				return TrackValidityDTO.errMatches();
			}

			// legs with matching means
			List<TrackLegDTO> matchingLegs = validTrack.stream().filter(leg -> campaign.getMeans().indexOf(leg.getMean()) >= 0).collect(Collectors.toList());
			if (matchingLegs.size() == 0) {
				return TrackValidityDTO.errMatches();
			}

			LocalDate date = toLocalDate(track.getStartTime());
			// stat of current date
			DayStat stat = dayStatRepo.findOneByPlayerIdAndCampaignAndCompanyAndDate(playerId, campaign.getId(), company.getId(), date.toString());
			// new empty day stat
			if (stat == null) {
				stat = new DayStat();
				stat.setPlayerId(playerId);
				stat.setCampaign(campaign.getId());
				stat.setCompany(company.getId());
				stat.setDate(date.toString());
				stat.setTrackCount(0);
				stat.setMonth(date.format(MONTH_PATTERN));
				stat.setWeek(date.format(WEEK_PATTERN));
				stat.setScore(new Score());
			}

			Map<TrackLegDTO, TrackingData> trackMap = new HashMap<>();
			for (TrackLegDTO l : matchingLegs) {
				MEAN mean = MEAN.valueOf(l.getMean());
				// identify track data validated or re-validated
				TrackingData td = stat.getTracks().stream().filter(t -> t.getTrackId().equals(l.getId())).findAny().orElse(null);
				if (td == null) {
					td = new TrackingData();
					td.setTrackId(l.getId());
					td.setPlayerId(playerId);
					td.setStartedAt(Instant.ofEpochMilli(track.getStartTime()).toString());
					stat.getTracks().add(td);
				}
				td.setMode(mean.name());
				td.setDistance(l.getDistance());
				td.setDuration(l.getDuration());
				td.setCo2(l.getCo2());
				td.setMultimodalId(track.getMultimodalId());
				trackMap.put(l, td);
			}
			// recalculate the score from updated track list
			limitScore(campaign, playerId, stat);
			dayStatRepo.save(stat);
			
			// create result
			TrackValidityDTO validity = new TrackValidityDTO();
			validity.setValid(true);
			validity.setLegs(new LinkedList<>());
			// virtual in this case means recognized and should be count for stats
			validity.setVirtualTrack(stat.getTrackCount().equals(stat.getLimitedTrackCount()));
			for (TrackLegDTO l : matchingLegs) {
				TrackValidityLegDTO leg = new TrackValidityLegDTO();
				leg.setMean(l.getMean());
				leg.setDistance(l.getDistance());
				leg.setId(l.getId());
				TrackingData td = trackMap.get(l);
				// put valid value considering max imposed by the limit
				leg.setVirtualScore(td.getLimitedScore());
				validity.getLegs().add(leg);				
			}

			return validity;
		}
	}  
	
	/**
	 * @param campaignId
	 * @param playerId
	 * @param trackId
	 * @return
	 */
	public TrackValidityDTO invalidate(String campaignId, String playerId, String trackId) throws InconsistentDataException {
		// campaign
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) {
			throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");
		} 
		// user, should be registered
		User user = userRepo.findByPlayerId(playerId).orElse(null);
		if (user == null) {
			throw new InconsistentDataException("Invalid user: " + playerId, "NO_USER");
		} 
		UserRole role = user.findRole(it.smartcommunitylab.pgazienda.Constants.ROLE_APP_USER).orElse(null);
		if (role == null) {
			throw new InconsistentDataException("Invalid user: " + playerId, "NO_USER");
		}
		Subscription subscription = role.getSubscriptions().stream().filter(s -> s.getCampaign().equals(campaignId)).findAny().orElse(null);
		if (subscription == null) {
			throw new InconsistentDataException("Invalid user: " + playerId, "NO_USER");
		}
		
		// company
		List<Company> companies = companyRepo.findByCode(subscription.getCompanyCode());
		if (companies.size() == 0) {
			throw new InconsistentDataException("Invalid company: " + subscription.getCompanyCode(), "NO_COMPANY");
		}
		Company company = companies.get(0);
		
		// employee matching subscription
		Employee employee = employeeRepo.findByCompanyIdAndCodeIgnoreCase(company.getId(), subscription.getKey()).stream().findAny().orElse(null);
		if (employee == null ) throw new InconsistentDataException("Invalid user: " + playerId, "NO_USER");
		
		DayStat stat = dayStatRepo.findOneByPlayerIdAndCampaignAndCompanyAndTrack(playerId, campaign.getId(), company.getId(), trackId);
		if (stat != null) {
			TrackingData track = stat.getTracks().stream().filter(t -> t.getTrackId().equals(trackId)).findAny().get();
			if (track.getMultimodalId() != null) {
				stat.setTracks(stat.getTracks().stream().filter(t -> !track.getMultimodalId().equals(t.getMultimodalId())).collect(Collectors.toList()));
			} else {
				stat.setTracks(stat.getTracks().stream().filter(t -> !track.getTrackId().equals(t.getTrackId())).collect(Collectors.toList()));
			}
			limitScore(campaign, playerId, stat);
			dayStatRepo.save(stat);
		}

		return TrackValidityDTO.errInvalidated();
	}
	
	/**
	 * @param startTime
	 * @return
	 */
	private LocalDate toLocalDate(Long startTime) {
		return Instant.ofEpochMilli(startTime).atZone(ZoneId.of(Constants.DEFAULT_TIME_ZONE)).toLocalDate();
	}
	
	/**
	 * @param campaign
	 * @param playerId
	 * @param stat
	 */
	public void limitScore(Campaign campaign, String playerId, DayStat stat) {
		Criteria criteria = Criteria.where("playerId").is(playerId).and("date").lt(stat.getDate());
		List<DayStat> totalAgg = doAggregation(campaign, criteria, null,  false);

		criteria = Criteria.where("playerId").is(playerId).and("date").lt(stat.getDate()).gte(LocalDate.parse(stat.getDate()).withDayOfMonth(1).toString());
		List<DayStat> currMonthAgg = doAggregation(campaign, criteria, Constants.AGG_MONTH, false);

		criteria = Criteria.where("playerId").is(playerId).and("date").lt(stat.getDate()).gte(LocalDate.parse(stat.getDate()).with(WeekFields.ISO.dayOfWeek(), 1).toString());
		List<DayStat> currWeekAgg = doAggregation(campaign, criteria, Constants.AGG_WEEK, false);

		// number of multimodal tracks
		Set<Object> set = stat.getTracks().stream().map(td -> td.getMultimodalId() != null ? td.getMultimodalId() : td.hashCode()).collect(Collectors.toSet());
		stat.setTrackCount(set.size());
		// update number of tracks
		double limitedTracks = LimitsUtils.applyLimits(
			(double)stat.getTrackCount(), 
			currWeekAgg.stream().map(ds -> ds.getLimitedTrackCount().doubleValue()).collect(Collectors.toList()),
			currMonthAgg.stream().map(ds -> ds.getLimitedTrackCount().doubleValue()).collect(Collectors.toList()),
			totalAgg.stream().map(ds -> ds.getLimitedTrackCount().doubleValue()).collect(Collectors.toList()),
			campaign.getTrackLimits()
		);
		stat.setLimitedTrackCount((int)limitedTracks);

		// recalculate scores of the tracking data
		Set<String> mmIds = new HashSet<>();
		double score = 0d, limitedScore = 0d;
		for (TrackingData td : stat.getTracks()) {			
			VirtualScoreValue vsv = campaign.getVirtualScore().meanValue(MEAN.valueOf(td.getMode()));
			if (vsv != null) {
				double tds = getScore(td.getDistance(), td.getDuration(), td.getCo2(), vsv);
				td.setScore(tds);
				score += tds;
				if (mmIds.size() < stat.getLimitedTrackCount()) {
					limitedScore += tds;
				}
			}
			mmIds.add(td.getMultimodalId() != null ? td.getMultimodalId() : ("" + td.hashCode()));
		}
		stat.setScore(new Score(score));
		// apply limites to the score
		limitedScore = LimitsUtils.applyLimits(
			limitedScore, 
			currWeekAgg.stream().map(ds -> ds.getScore().getScore()).collect(Collectors.toList()),
			currMonthAgg.stream().map(ds ->  ds.getScore().getScore()).collect(Collectors.toList()),
			totalAgg.stream().map(ds ->  ds.getScore().getScore()).collect(Collectors.toList()),
			campaign.getScoreLimits()
		);
		stat.setLimitedScore(new Score(limitedScore));

		Score original = new Score(0d);

		for (TrackingData td : stat.getTracks()) {
				MEAN mean = MEAN.valueOf(td.getMode());
				// put valid value considering max imposed by the limit
				double max = stat.getLimitedScore().getScore();
				double curr = original.getScore();
				double ls = td.getScore();
				double limited = 0d;
				if ((curr + ls) <= max) {
					limited = ls;
				} else if (curr <= max ) {
					limited = max - curr;
				}
				td.setLimitedScore(limited);
				stat.getLimitedMeanScore().updateValue(mean, limited + stat.getLimitedMeanScore().meanValue(mean));
				stat.getMeanScore().updateValue(mean, ls + stat.getMeanScore().meanValue(mean));
				original.setScore(curr + ls);
		}

		// update CO2
		double co2 = stat.getTracks().stream().collect(Collectors.summingDouble(td -> td.getCo2() != null ? td.getCo2() : 0d));
		stat.setCo2saved(co2);
		
	}
	

	private double getScore(Double d, Long duration, Double co2, VirtualScoreValue vsv) {
		if (Constants.METRIC_DISTANCE.equals(vsv.getMetric())) {
			return d * vsv.getCoefficient();
		}
		if (Constants.METRIC_DURATION.equals(vsv.getMetric())) {
			return duration * vsv.getCoefficient();
		}
		if (Constants.METRIC_CO2.equals(vsv.getMetric())) {
			return co2 * vsv.getCoefficient();
		}
		if (Constants.METRIC_TRACK.equals(vsv.getMetric())) {
			return 1 * vsv.getCoefficient();
		}
		return 0d;
	}

	/**
	 * @param l
	 * @param today
	 * @return
	 */
	private boolean checkWorking(CompanyLocation l, LocalDate today) {
		boolean res = true;
		String todayStr = today.toString();
		if (l.getNonWorking() != null && !l.getNonWorking().isEmpty()) {
			res = res && !l.getNonWorking().contains(today.getDayOfWeek().getValue());
		}
		if (res && l.getNonWorkingDays() != null && !l.getNonWorkingDays().isEmpty()) {
			res = res && !l.getNonWorkingDays().contains(todayStr);
		}
		
		return res;
	}

	/**
	 * Retrieve aggregated statistics
	 * @param playerId
	 * @param campaignId
	 * @param from date from (including)
	 * @param to date to  (including)
	 * @param groupBy (day o month)
	 * @param withTracks (to inlcude tracks or not, applicable for day aggregation only)
	 * @return
	 * @throws InconsistentDataException 
	 */
	public List<DayStat> getUserCampaignData(String playerId, String campaignId, LocalDate from, LocalDate to, String groupBy, Boolean withTracks, Boolean noLimits) throws InconsistentDataException {
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");

		if (to == null) to = LocalDate.now();
		Criteria criteria = new Criteria("playerId").is(playerId).and("campaign").is(campaignId).and("date").lte(to.toString());
		if (from != null) criteria = criteria.gte(from.toString());
		List<DayStat> res = null;
		if (Constants.AGG_DAY.equals(groupBy)) {
			Query q = Query.query(criteria);
			if (!withTracks) q.fields().exclude("tracks");
			res = template.find(q, DayStat.class);
			if (!Boolean.TRUE.equals(noLimits)) {
				res.forEach(ds -> ds.setMeanScore(ds.getLimitedMeanScore()));
				// LimitsUtils.applyLimits(res, groupBy, campaign);
			} 
		} else if (Constants.AGG_MONTH.equals(groupBy) || Constants.AGG_TOTAL.equals(groupBy) || Constants.AGG_WEEK.equals(groupBy)){
			res = doAggregation(campaign, criteria, groupBy, noLimits);
		} else {
			throw new InconsistentDataException("Incorrect grouping: " + groupBy, "INVALID_GROUPING_DATA");
		} 
		res.forEach(stat -> {
			stat.setCampaign(campaignId);
		});
		return res;
	}
	
	/**
	 * Collect campaign statistics
	 * @param campaignId
	 * @param groupBy
	 * @param from
	 * @param to
	 * @param noLimits
	 * @return
	 * @throws InconsistentDataException 
	 */
	public List<DayStat> createCampaignStats(String campaignId, String groupBy, LocalDate from, LocalDate to, Boolean noLimits) throws InconsistentDataException {
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");
		Criteria criteria = new Criteria("campaign").is(campaignId).and("date").lte(to.toString());
		if (from != null) criteria = criteria.gte(from.toString());

		return extractStats(groupBy, false, noLimits, campaign, criteria);		
	}
	
	/**
	 * Collect campaign statistics
	 * @param campaignId
	 * @param groupBy
	 * @param from
	 * @param to
	 * @param noLimits
	 * @return
	 * @throws InconsistentDataException 
	 */
	public List<DayStat> createCampaignCompanyStats(String campaignId, String groupBy, LocalDate from, LocalDate to, Boolean noLimits) throws InconsistentDataException {
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");
		Criteria criteria = new Criteria("campaign").is(campaignId).and("date").lte(to.toString());
		if (from != null) criteria = criteria.gte(from.toString());

		return extractStats(groupBy, true, noLimits, campaign, criteria);		
	}

	/**
	 * Collect company statistics
	 * @param campaignId
	 * @param groupBy
	 * @param from
	 * @param to
	 * @param noLimits
	 * @return
	 * @throws InconsistentDataException 
	 */
	public List<DayStat> createCompanyStats(String campaignId, String companyId, String groupBy, LocalDate from, LocalDate to, Boolean noLimits) throws InconsistentDataException {
		Company company = companyRepo.findById(companyId).orElse(null);
		if (company == null) throw new InconsistentDataException("Invalid company: " + companyId, "NO_COMPANY");

		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");
		Criteria criteria = new Criteria("campaign").is(campaignId).and("date").lte(to.toString());
		if (from != null) criteria = criteria.gte(from.toString());
		
		List<String> users = userRepo.findByCampaignAndCompany(campaignId, company.getCode()).stream().map(u -> u.getPlayerId()).collect(Collectors.toList());
		criteria = criteria.and("playerId").in(users);
		return extractStats(groupBy, false, noLimits, campaign, criteria);		
	}

	/**
	 * Collect location statistics
	 * @param campaignId
	 * @param groupBy
	 * @param from
	 * @param to
	 * @param noLimits
	 * @return
	 * @throws InconsistentDataException 
	 */
	public List<DayStat> createCompanyLocationStats(String campaignId, String companyId, String location, String groupBy, LocalDate from, LocalDate to, Boolean noLimits) throws InconsistentDataException {
		Company company = companyRepo.findById(companyId).orElse(null);
		if (company == null) throw new InconsistentDataException("Invalid company: " + companyId, "NO_COMPANY");

		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");
		Criteria criteria = new Criteria("campaign").is(campaignId).and("date").lte(to.toString());
		if (from != null) criteria = criteria.gte(from.toString());
		
		Set<String> employeeKeys = employeeRepo.findByCompanyIdAndLocation(companyId, location).stream().map(e -> e.getCode()).collect(Collectors.toSet());
		
		List<String> users = userRepo.findByCampaignAndCompanyAndEmployeeCode(campaignId, company.getCode(), employeeKeys).stream().map(u -> u.getPlayerId()).collect(Collectors.toList());
		criteria = criteria.and("playerId").in(users);
		return extractStats(groupBy, false, noLimits, campaign, criteria);		
	}
	/**
	 * @param groupBy
	 * @param noLimits
	 * @param campaign
	 * @param group
	 * @param criteria
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<DayStat> extractStats(String groupBy, boolean company, Boolean noLimits, Campaign campaign, Criteria criteria) {
		
		String group = Constants.AGG_TOTAL.equals(groupBy) 
			? "campaign"
			: Constants.AGG_MONTH.equals(groupBy) 
			? "month" 
			: Constants.AGG_WEEK.equals(groupBy)
			? "week"
			: "date";

		MatchOperation filterOperation = Aggregation.match(criteria);
		String src = Boolean.TRUE.equals(noLimits) ? "meanScore" : "limitedMeanScore";

		String[] groupArray = company ? new String[]{"company", group} : new String[]{group};
		
		GroupOperation groupByOperation = Aggregation.group(groupArray)
				.sum("co2saved").as("co2saved")
				.sum("trackCount").as("trackCount")
				.sum("limitedTrackCount").as("limitedTrackCount");
		
		for (String mean: campaign.getMeans()) groupByOperation = groupByOperation.sum(src + "." + mean).as(mean);
		
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		AggregationResults<Map> aggResult = template.aggregate(aggregation, DayStat.class, Map.class);
		return aggResult.getMappedResults().stream().map(m -> {
			DayStat stat = new DayStat();
			stat.setCo2saved(((Number)m.getOrDefault("co2saved", 0d)).doubleValue());
			stat.setMeanScore(MeanScore.fromMap(m));
			stat.setTrackCount((Integer) m.getOrDefault("trackCount", 0));
			stat.setLimitedTrackCount((Integer) m.getOrDefault("limitedTrackCount", 0));
			
			if (company) {
				Map<String, String> idMap = (Map<String, String>) m.get("_id");
				stat.setCompany((String)idMap.get("company"));
				if (Constants.AGG_DAY.equals(groupBy)) {
					stat.setDate((String)idMap.get("date"));
					LocalDate ld = LocalDate.parse(stat.getDate());
					stat.setMonth(ld.format(MONTH_PATTERN));
					stat.setWeek(ld.format(WEEK_PATTERN));
				}
				if (Constants.AGG_MONTH.equals(groupBy)) {
					stat.setMonth((String)idMap.get("month"));
				}
				if (Constants.AGG_WEEK.equals(groupBy)) {
					stat.setWeek((String)idMap.get("week"));
					LocalDate ld = LocalDate.parse(stat.getWeek(), WEEK_PATTERN);
					stat.setMonth(ld.format(MONTH_PATTERN));
				}
			} else {
				if (Constants.AGG_DAY.equals(groupBy)) {
					stat.setDate((String)m.get("_id"));
					LocalDate ld = LocalDate.parse(stat.getDate());
					stat.setMonth(ld.format(MONTH_PATTERN));
					stat.setWeek(ld.format(WEEK_PATTERN));
				}
				if (Constants.AGG_MONTH.equals(groupBy)) {
					stat.setMonth((String)m.get("_id"));
				}
				if (Constants.AGG_WEEK.equals(groupBy)) {
					stat.setWeek((String)m.get("_id"));
					LocalDate ld = LocalDate.parse(stat.getWeek(), WEEK_PATTERN);
					stat.setMonth(ld.format(MONTH_PATTERN));
				}			
			}

			return stat;
		}).sorted((a,b) -> {
			if (a.getDate() != null) return a.getDate().compareTo(b.getDate());
			if (a.getMonth() != null) return a.getMonth().compareTo(b.getMonth());
			return 0;
		}).collect(Collectors.toList());
	}

	/**
	 * Create company stats for every single employee within given period
	 * @param writer
	 * @param campaignId
	 * @param companyId
	 * @param from
	 * @param to
	 * @throws InconsistentDataException 
	 */
	public void createEmployeeStatsCSV(Writer writer, String campaignId, String companyId, LocalDate from, LocalDate to) throws InconsistentDataException {
		Company company = companyRepo.findById(companyId).orElse(null);
		if (company == null) throw new InconsistentDataException("Invalid company: " + companyId, "NO_COMPANY");
		List<DayStat> stats = doPlayerAggregation(campaignId, from, to);
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");

		CSVWriter csvWriter = new CSVWriter(writer, ';', '"', '"', "\n");
		String header = "Nome;Cognome;CodiceSede;ViaggiValidi";
		for (int i = 0; i < campaign.getMeans().size(); i++) header += ";KmTotValidi_" + campaign.getMeans().get(i);
		
		csvWriter.writeNext(header.split(";"));
		stats.forEach(ds -> {
			List<Employee> employees = findCompanyEmployees(ds.getPlayerId(), campaignId, company.getCode(), companyId);
			if (employees == null) return;
			// filter company employee: should be only one for playerId
			Employee employee = employees.stream().filter(e -> e.getCompanyId().equals(companyId)).findFirst().orElse(null);
			if (employee == null) return;
			
			String[] rec = new String[4 + campaign.getMeans().size()];
			rec[0] = employee.getName();
			rec[1] = employee.getSurname();
			rec[2] = employee.getLocation();
			rec[3] = ds.getTrackCount() == null ? "0": ds.getTrackCount().toString();
			for (int i = 0; i < campaign.getMeans().size(); i++) {
				Double mv = ds.getMeanScore().meanValue(MEAN.valueOf(campaign.getMeans().get(i)));
				if (mv == null) mv = 0d;
				// mv = mv / 1000;
				rec[i + 4] = mv.toString();
			}
			csvWriter.writeNext(rec);
		});
		try {
			csvWriter.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Create company stats for each location
	 * @param writer
	 * @param campaignId
	 * @param companyId
	 * @param from
	 * @param to
	 * @throws InconsistentDataException 
	 */
	public void createLocationStatsCSV(Writer writer, String campaignId, String companyId, LocalDate from, LocalDate to) throws InconsistentDataException {
		Company company = companyRepo.findById(companyId).orElse(null);
		if (company == null) throw new InconsistentDataException("Invalid company: " + companyId, "NO_COMPANY");
		List<DayStat> stats = doPlayerAggregation(campaignId, from, to);
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");

		CSVWriter csvWriter = new CSVWriter(writer, ';', '"', '"', "\n");
		String header = "Indentificativo;Indirizzo;Numero;CAP;Comune;Provincia;ViaggiValidi";
		for (int i = 0; i < campaign.getMeans().size(); i++) header += ";KmTotValidi_" + campaign.getMeans().get(i);
		
		csvWriter.writeNext(header.split(";"));
		
		Map<String, List<DayStat>> locationStats = stats.stream().map(ds -> {
			List<Employee> employees = findCompanyEmployees(ds.getPlayerId(), campaignId, company.getCode(), companyId);
			if (employees == null) return ds;
			// filter company employee: should be only one for playerId
			Employee employee = employees.stream().filter(e -> e.getCompanyId().equals(companyId)).findFirst().orElse(null);
			if (employee == null) return ds;
			// trick to pass location for aggregation
			ds.setCompany(employee.getLocation());
			return ds;
		}).filter(ds -> ds.getCompany() != null).collect(Collectors.groupingBy(DayStat::getCompany));
		
		locationStats.keySet().forEach(locationId -> {
			CompanyLocation location = company.getLocations().stream().filter(l -> locationId.equals(l.getId())).findFirst().orElse(null);
			if (location == null) return;
			
			String[] rec = new String[7 + campaign.getMeans().size()];
			rec[0] = locationId;
			rec[1] = location.getAddress();
			rec[2] = location.getStreetNumber();
			rec[3] = location.getZip();
			rec[4] = location.getCity();
			rec[5] = location.getProvince();
			
			MeanScore d = new MeanScore();
			int totalTrips = 0;
			for (DayStat ds: locationStats.get(locationId)) {
				d.mergeScore(ds.getMeanScore());
				totalTrips += (ds.getTrackCount() == null ? 0 : ds.getTrackCount());
			}
			
			rec[6] = ""+totalTrips;
			for (int i = 0; i < campaign.getMeans().size(); i++) {
				Double mv = d.meanValue(MEAN.valueOf(campaign.getMeans().get(i)));
				if (mv == null) mv = 0d;
				// mv = mv / 1000;
				rec[i + 7] = mv.toString();
			}
			csvWriter.writeNext(rec);
		});


		try {
			csvWriter.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}	
	
	
	public void createCampaignStatsCSV(Writer writer, String campaignId, LocalDate from, LocalDate to) throws InconsistentDataException {
		List<DayStat> stats = doPlayerAggregation(campaignId, from, to);
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");

		CSVWriter csvWriter = new CSVWriter(writer, ';', '"', '"', "\n");
		String header = "Azienda;ViaggiValidi";
		for (int i = 0; i < campaign.getMeans().size(); i++) header += ";KmTotValidi_" + campaign.getMeans().get(i);
		
		csvWriter.writeNext(header.split(";"));
		
		final List<DayStat> newStats = new LinkedList<>();
		final Map<String, Company> companyCache = new HashMap<>();
		stats.forEach(ds -> {
			List<Employee> employees = findEmployees(ds.getPlayerId(), campaignId, companyCache);
			if (employees != null) {
				employees.forEach(e -> {
					DayStat newDs = new DayStat();
					newDs.setMeanScore(DayStat.MeanScore.copy(ds.getMeanScore()));
					newDs.setCompany(e.getCompanyId());
					newDs.setTrackCount(ds.getTrackCount());
					newStats.add(newDs);
				});
			}
			
		});
		
		
		Map<String, List<DayStat>> companyStats = newStats.stream().filter(ds -> ds.getCompany() != null).collect(Collectors.groupingBy(DayStat::getCompany));
				
		
		companyStats.keySet().forEach(companyId -> {
			Company company = companyRepo.findById(companyId).orElse(null);
			if (company == null) return;
			
			String[] rec = new String[2 + campaign.getMeans().size()];
			rec[0] = company.getName();
			MeanScore d = new MeanScore();
			int totalTrips = 0;
			for (DayStat ds: companyStats.get(companyId)) {
				d.mergeScore(ds.getMeanScore());
				totalTrips += (ds.getTrackCount() == null ? 0 : ds.getTrackCount());
			}
			
			rec[1] = ""+totalTrips;
			for (int i = 0; i < campaign.getMeans().size(); i++) {
				Double mv = d.meanValue(MEAN.valueOf(campaign.getMeans().get(i)));
				if (mv == null) mv = 0d;
				// mv = mv / 1000;
				rec[i + 2] = mv.toString();
			}
			csvWriter.writeNext(rec);
		});


		try {
			csvWriter.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}	
	/**
	 * Find employees prefiltered for specified company
	 * @param playerId
	 * @param campaignId
	 * @param companyCode
	 * @param companyId
	 * @return
	 */
	private List<Employee> findCompanyEmployees(String playerId, String campaignId, String companyCode, String companyId) {
		User user = userRepo.findByPlayerId(playerId).orElse(null);
		if (user != null && user.getRoles() != null) {
			final List<Employee> res = new LinkedList<>();
			user.getRoles().forEach(r -> {
				if (r.getSubscriptions() != null) {
					r.getSubscriptions().stream()
					.filter(s -> s.getCampaign().equals(campaignId) && s.getCompanyCode().equals(companyCode))
					.forEach(s -> {
						Employee e = employeeRepo.findByCompanyIdAndCodeIgnoreCase(companyId, s.getKey()).stream().findAny().orElse(null);
						if (e != null) res.add(e);
					});
				}
			});
			return res;
		}
		return Collections.emptyList();
	}
	
	private List<Employee> findEmployees(String playerId, String campaignId, Map<String, Company> companyCache) {
		User user = userRepo.findByPlayerId(playerId).orElse(null);
		if (user != null && user.getRoles() != null) {
			final List<Employee> res = new LinkedList<>();
			user.getRoles().forEach(r -> {
				if (r.getSubscriptions() != null) {
					r.getSubscriptions().stream()
					.filter(s -> s.getCampaign().equals(campaignId))
					.forEach(s -> {
						Company company = companyCache.getOrDefault(s.getCompanyCode(), companyRepo.findByCode(s.getCompanyCode()).stream().findFirst().orElse(null));
						companyCache.put(s.getCompanyCode(), company);
						if (company != null) {
							Employee e = employeeRepo.findByCompanyIdAndCodeIgnoreCase(company.getId(), s.getKey()).stream().findAny().orElse(null);
							if (e != null) res.add(e);
						}
					});
				}
			});
			return res;
		}
		return Collections.emptyList();
	}

	public List<DayStat> doPlayerAggregation(String campaignId, LocalDate from, LocalDate to) throws InconsistentDataException {
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");

		Criteria criteria = new Criteria("campaign").is(campaignId).and("date").lte(to.toString());
		if (from != null) {
			criteria = criteria.gte(from.toString());
		}		
		List<DayStat> aggregation = doAggregation(campaign, criteria, null, false);
		final Map<String, List<DayStat>> playerStatMap = aggregation.stream().collect(Collectors.groupingBy(DayStat::getPlayerId));
		return playerStatMap.keySet().stream().map(playerId -> {
			List<DayStat> playerStats = playerStatMap.get(playerId);
			return playerStats.get(0);
		}).collect(Collectors.toList());
	}
	

	/**
	 * @param playerId
	 * @param campaignId
	 * @param groupBy
	 * @param campaign
	 * @param criteria
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<DayStat> doAggregation(Campaign campaign, Criteria criteria, String agg, Boolean noLimits) {
		MatchOperation filterOperation = Aggregation.match(criteria);
		String src = Boolean.TRUE.equals(noLimits) ? "meanScore" : "limitedMeanScore";
		
		GroupOperation groupByOperation = agg != null ? Aggregation.group("playerId", agg) : Aggregation.group("playerId");
		
		groupByOperation =	groupByOperation
				.sum("co2saved").as("co2saved")
				.sum("trackCount").as("trackCount")
				.sum("limitedTrackCount").as("limitedTrackCount");
		
		for (String mean: campaign.getMeans()) groupByOperation = groupByOperation.sum(src + "." + mean).as(mean);
		
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		AggregationResults<Map> aggResult = template.aggregate(aggregation, DayStat.class, Map.class);
		return aggResult.getMappedResults().stream().map(m -> {
			DayStat stat = new DayStat();
			stat.setCo2saved(((Number)m.getOrDefault("co2saved", 0d)).doubleValue());
			if (Constants.AGG_MONTH.equals(agg)) {
				stat.setMonth((String)((Map) m.get("_id")).get(Constants.AGG_MONTH));
				stat.setPlayerId((String)((Map) m.get("_id")).get("playerId"));
			} else if (Constants.AGG_WEEK.equals(agg)) {
				stat.setWeek((String)((Map) m.get("_id")).get(Constants.AGG_WEEK));
				stat.setPlayerId((String)((Map) m.get("_id")).get("playerId"));
			} else {
				stat.setPlayerId((String)m.get("_id"));
			}
			stat.setMeanScore(MeanScore.fromMap(m));
			stat.getScore().setScore(stat.getMeanScore().total());
			stat.setTrackCount((Integer) m.getOrDefault("trackCount", 0));
			stat.setLimitedTrackCount((Integer) m.getOrDefault("limitedTrackCount", 0));
			return stat;
		}).collect(Collectors.toList());
	}
	
	public List<TransportStatDTO> getPlayerTransportStatsGroupByMean(String playerId, String campaignId, String groupMode, String meanStr, String dateFrom, String dateTo) throws InconsistentDataException {
		MEAN mean = StringUtils.hasText(meanStr) ? MEAN.valueOf(meanStr) : MEAN.bike;
		if ("day".equals(groupMode) || "month".equals(groupMode) || "week".equals(groupMode)) {
			List<DayStat> list = getUserCampaignData(playerId, campaignId, dateFrom == null ? null : LocalDate.parse(dateFrom), dateTo == null ? null : LocalDate.parse(dateTo), groupMode, false, false);
			return list.stream().map(ds -> {
				TransportStatDTO dto = new TransportStatDTO();
				dto.setPeriod( "month".equals(groupMode) ? ds.getMonth() : "week".equals(groupMode) ? ds.getWeek() : ds.getDate());
				dto.setValue(ds.getMeanScore().meanValue(mean));
				return dto;
			}).collect(Collectors.toList());
		} else {
			List<DayStat> list = getUserCampaignData(playerId, campaignId, dateFrom == null ? null :LocalDate.parse(dateFrom), dateTo == null ? null : LocalDate.parse(dateTo), Constants.AGG_TOTAL, false, false);
			return list.stream().map(ds -> {
				TransportStatDTO dto = new TransportStatDTO();
				dto.setPeriod(ds.getDate());
				dto.setValue(ds.getMeanScore().meanValue(mean));
				return dto;
			}).collect(Collectors.toList());
		}
	}


	
	public boolean hasCampaignData(String playerId, String campaignId) {
		Criteria criteria = new Criteria("playerId").is(playerId).and("campaign").is(campaignId);
		DayStat stat = template.findOne(Query.query(criteria), DayStat.class);
		return stat != null;
	}
	

	/**
	 * @param campaignId
	 */
	public void cleanCampaign(String campaignId) {
		dayStatRepo.deleteByCampaign(campaignId);
	}
	
}
