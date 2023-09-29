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

import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
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
import org.springframework.stereotype.Service;
import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Circle;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.Constants;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_DATA;
import it.smartcommunitylab.pgazienda.domain.Constants.GROUP_BY_TIME;
import it.smartcommunitylab.pgazienda.domain.Constants.MEAN;
import it.smartcommunitylab.pgazienda.domain.Constants.STAT_FIELD;
import it.smartcommunitylab.pgazienda.domain.DayStat;
import it.smartcommunitylab.pgazienda.domain.DayStat.MeanScore;
import it.smartcommunitylab.pgazienda.domain.DayStat.Score;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.Employee.TrackingRecord;
import it.smartcommunitylab.pgazienda.domain.Shape;
import it.smartcommunitylab.pgazienda.domain.Subscription;
import it.smartcommunitylab.pgazienda.domain.TrackingData;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.domain.UserRole;
import it.smartcommunitylab.pgazienda.domain.Campaign.VirtualScoreValue;
import it.smartcommunitylab.pgazienda.dto.TrackDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO.TrackLegDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO.TrackPointDTO;
import it.smartcommunitylab.pgazienda.dto.TrackValidityDTO;
import it.smartcommunitylab.pgazienda.dto.TrackValidityDTO.TrackValidityLegDTO;
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

	// @PostConstruct
	// public void update() {
	// 	List<DayStat> list = dayStatRepo.findAll();
	// 	for (DayStat ds : list) {
	// 		ds.recalculate();
	// 	}
	// 	dayStatRepo.saveAll(list);
	// } //{"mode":"bike","co2":0.16107355494667425,"trackId":"6512c91a96c8612943ad29d5","multimodalId":"multimodal_1695722858437","score":1.3328386838781485,"startedAt":"2023-09-26T10:07:38.437Z","duration":427,"distance":666.4193419390742,"limitedScore":0,"playerId":"u_894d81d0825046a4bbc574de4131832c"}

	@PostConstruct
	public void reval() throws InconsistentDataException {

		// dayStatRepo.findByCampaign("").forEach(stat -> {
			
		// 	if (stat.getLimitedScore().getScore() < 0) stat.getLimitedScore().setScore(0d);

		// 	stat.recalculate();

		// 	Score original = new Score(0d);

		// 	stat.getMeanScore().reset();
		// 	stat.getLimitedMeanScore().reset();
		// 	for (TrackingData td : stat.getTracks()) {
		// 			MEAN mean = MEAN.valueOf(td.getMode());
		// 			// put valid value considering max imposed by the limit
		// 			double max = stat.getLimitedScore().getScore();
		// 			double curr = original.getScore();
		// 			double ls = td.getScore();
		// 			double limited = 0d;
		// 			if ((curr + ls) <= max) {
		// 				limited = ls;
		// 			} else if (curr <= max ) {
		// 				limited = max - curr;
		// 			}
		// 			td.setLimitedScore(limited);
		// 			stat.getLimitedMeanScore().updateValue(mean, limited + stat.getLimitedMeanScore().meanValue(mean));
		// 			stat.getMeanScore().updateValue(mean, ls + stat.getMeanScore().meanValue(mean));
		// 			original.setScore(curr + ls);
		// 	}

		// 	dayStatRepo.save(stat);

		// });

		// DayStat st = dayStatRepo.findOneByPlayerIdAndCampaignAndCompanyAndDate("", "", "", "2023-09-26");
		// TrackDTO track = new TrackDTO();
		// CompanyLocation l = companyRepo.findById("").get().getLocations().get(0);
		// track.setStartTime(LocalDateTime.parse("2023-09-26T10:07:38.437").atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		// TrackPointDTO p = new TrackPointDTO();
		// p.setLatitude(l.getLatitude());
		// p.setLongitude(l.getLongitude());
		// p.setRecorded_at(track.getStartTime());
		// track.setMultimodalId("");;
		// track.setLegs(new LinkedList<>());
		// st.getTracks().forEach(t -> {
		// 	TrackLegDTO tl = new TrackLegDTO();
		// 	tl.setId(t.getTrackId());
		// 	tl.setMean(t.getMode());
		// 	tl.setDistance(t.getDistance());
		// 	tl.setDuration(t.getDuration());
		// 	tl.setCo2(t.getCo2());
		// 	tl.setValid(true);
		// 	tl.setPoints(new LinkedList<>());
		// 	tl.getPoints().add(p);
		// 	track.getLegs().add(tl); 
		// });
		// this.validate("", "", track);
	}

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
		logger.info("Validating track service for campaign {} player {}, tracks {}", campaignId, playerId, track.getLegs().stream().map(t -> t.getId()).collect(Collectors.toList()));
		
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

			Map<String, TrackingData> trackMap = new HashMap<>();
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
				trackMap.put(l.getId(), td);
			}
			// recalculate the score from updated track list
			limitScore(campaign, playerId, stat);
			stat.recalculate();
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
				TrackingData td = trackMap.get(l.getId());
				// put valid value considering max imposed by the limit
				leg.setVirtualScore(td.getLimitedScore());
				validity.getLegs().add(leg);				
			}

			TrackingRecord rec = employee.getTrackingRecord().get(campaignId);
			if (rec == null) {
				rec = new TrackingRecord();
				rec.setRegistration(System.currentTimeMillis());
				employee.getTrackingRecord().put(campaignId, rec);
			}
			rec.setTracking(System.currentTimeMillis());
			employeeRepo.save(employee);

			logger.info("Validation result {}", validity.toString());
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
		Criteria criteria = Criteria.where("playerId").is(playerId).and("campaign").is(campaign.getId()).and("date").lt(stat.getDate());
		List<DayStat> totalAgg = doAggregation(campaign, criteria, null);

		criteria = Criteria.where("playerId").is(playerId).and("campaign").is(campaign.getId()).and("date").lt(stat.getDate()).gte(LocalDate.parse(stat.getDate()).withDayOfMonth(1).toString());
		List<DayStat> currMonthAgg = doAggregation(campaign, criteria, Constants.AGG_MONTH);

		criteria = Criteria.where("playerId").is(playerId).and("campaign").is(campaign.getId()).and("date").lt(stat.getDate()).gte(LocalDate.parse(stat.getDate()).with(WeekFields.ISO.dayOfWeek(), 1).toString());
		List<DayStat> currWeekAgg = doAggregation(campaign, criteria, Constants.AGG_WEEK);

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
			String mmId = td.getMultimodalId() != null ? td.getMultimodalId() : ("" + td.hashCode());
			VirtualScoreValue vsv = campaign.getVirtualScore().meanValue(MEAN.valueOf(td.getMode()));
			if (vsv != null) {
				double tds = getScore(td.getDistance(), td.getDuration(), td.getCo2(), vsv);
				td.setScore(tds);
				score += tds;
				if (mmIds.contains(mmId) || mmIds.size() < stat.getLimitedTrackCount()) {
					limitedScore += tds;
				}
			}
			mmIds.add(mmId);
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

		stat.getMeanScore().reset();
		stat.getLimitedMeanScore().reset();
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
	 * Generate statistics
	 * @param campaignId
	 * @param companyId (optional) filter by company
	 * @param location (optional) filter by location. Requires companyId 
	 * @param employeeId (optional) filter by employee
	 * @param timeGroupBy group by for time: day, month, week, total. Default month 
	 * @param dataGroupBy grouping of data: employee, location, company, total. Default total
	 * @param fields data fields to include: score (total points), limitedScore (with limits), trackCount, limitedTrackCount, co2saved, meanScore, limitedMeanScore, meanDistance, meanDuration, meanCo2 
	 * @param from starting date
	 * @param to ending date
	 * @param all whether to take all the items, even without data
	 * @return List of stat objects
	 * @throws InconsistentDataException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<DayStat> statistics(
			String campaignId,
			String companyId,
			String location,
			Set<String> employeeId,			
			GROUP_BY_TIME timeGroupBy,
			GROUP_BY_DATA dataGroupBy,
			List<STAT_FIELD> fields,
			LocalDate from, 
			LocalDate to,
			boolean all
	) throws InconsistentDataException {
		Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
		if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");

		if (from == null) from = campaign.getFrom();
		
		Criteria criteria = Criteria
				.where("campaign").is(campaignId)
				.and("date").lte(to.toString()).gte(from.toString());

		if (companyId != null) {
			criteria = criteria.and("company").is(companyId);
		}
		if (employeeId != null && companyId != null) {
			List<Employee> employees = employeeRepo.findByIdIn(employeeId);
			if (employees.isEmpty()) {
				throw new InconsistentDataException("Invalid employees: " + employeeId, "NO_USER");
			}
			Company c = companyRepo.findById(companyId).orElse(null);
			if (c == null) {
				throw new InconsistentDataException("Invalid company: " + companyId, "NO_COMPANY");
			}
			List<User> users = new LinkedList<>();
			for (Employee e: employees) {
				users.addAll(userRepo.findByCampaignAndCompanyAndEmployeeCode(campaignId, c.getCode(), e.getCode()));
			}
			criteria = criteria.and("playerId").in(users.stream().map(u -> u.getPlayerId()).collect(Collectors.toList()));
		} else if (location != null && companyId != null) {
			Company company = companyRepo.findById(companyId).orElse(null);
			if (company == null) throw new InconsistentDataException("Invalid company: " + companyId, "NO_COMPANY");
			Set<String> employeeKeys = employeeRepo.findByCompanyIdAndLocation(companyId, location).stream().map(e -> e.getCode()).collect(Collectors.toSet());
			List<String> users = userRepo.findByCampaignAndCompanyAndEmployeeCode(campaignId, company.getCode(), employeeKeys).stream().map(u -> u.getPlayerId()).collect(Collectors.toList());
			criteria = criteria.and("playerId").in(users);
		}

		MatchOperation filterOperation = Aggregation.match(criteria);
		List<String> group = new LinkedList<>();
		group.add("campaign");
		if (GROUP_BY_TIME.day.equals(timeGroupBy)) group.add("date");
		if (GROUP_BY_TIME.week.equals(timeGroupBy)) group.add("week");
		if (GROUP_BY_TIME.month.equals(timeGroupBy)) group.add("month");

		if (GROUP_BY_DATA.company.equals(dataGroupBy)) group.add("company");
		if (GROUP_BY_DATA.employee.equals(dataGroupBy)) group.add("playerId");
		if (GROUP_BY_DATA.location.equals(dataGroupBy)) group.add("playerId");
		
		GroupOperation groupByOperation = Aggregation.group(group.toArray(new String[group.size()]));
		Set<STAT_FIELD> meanFields = new HashSet<>(fields);
		if (fields == null || fields.isEmpty()) {
			fields = Collections.singletonList(STAT_FIELD.score);
		}
		if (fields.contains(STAT_FIELD.score)) {
			groupByOperation = groupByOperation.sum("score.score").as("score");
			meanFields.remove(STAT_FIELD.score);
		}
		if (fields.contains(STAT_FIELD.limitedScore)) {
			groupByOperation = groupByOperation.sum("limitedScore.score").as("limitedScore");
			meanFields.remove(STAT_FIELD.limitedScore);
		}
		if (fields.contains(STAT_FIELD.trackCount)) {
			groupByOperation = groupByOperation.sum("trackCount").as("trackCount");
			meanFields.remove(STAT_FIELD.trackCount);
		}
		if (fields.contains(STAT_FIELD.limitedTrackCount)) {
			groupByOperation = groupByOperation.sum("limitedTrackCount").as("limitedTrackCount");
			meanFields.remove(STAT_FIELD.limitedTrackCount);
		}
		if (fields.contains(STAT_FIELD.co2saved)) {
			groupByOperation = groupByOperation.sum("co2saved").as("co2saved");
			meanFields.remove(STAT_FIELD.co2saved);
		}
		List<MEAN> means = campaign.getMeans().stream().map(m -> MEAN.valueOf(m)).collect(Collectors.toList());
		for (STAT_FIELD f: meanFields) {
			groupByOperation = groupByMean(f.name(), means, groupByOperation);
		}
	
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		AggregationResults<Map> aggResult = template.aggregate(aggregation, DayStat.class, Map.class);
		List<DayStat> res = new LinkedList<>();
		for (Map m : aggResult.getMappedResults()) {
			DayStat stat = new DayStat();
			populateKeyFields(stat, m, group);
			stat.setCo2saved(((Number)m.getOrDefault("co2saved", 0d)).doubleValue());
			stat.setTrackCount(((Number)m.getOrDefault("trackCount", 0)).intValue());
			stat.setLimitedTrackCount(((Number)m.getOrDefault("limitedTrackCount", 0)).intValue());
			stat.getScore().setScore(((Number)m.getOrDefault("score", 0d)).doubleValue());
			stat.getLimitedScore().setScore(((Number)m.getOrDefault("limitedScore", 0d)).doubleValue());
			for (STAT_FIELD f: meanFields) {
				populateMeanFields(stat, m, f, means);
			}
			res.add(stat);
		}

		// grouping by company
		if (GROUP_BY_DATA.company.equals(dataGroupBy)) {
			List<Company> list = 
			  all && companyId == null 
			? companyRepo.findByCampaign(campaignId) 
			: companyRepo.findByIdIn(res.stream().map(ds -> ds.getCompany()).collect(Collectors.toSet()));
			Map<String, Company> companies = list.stream().collect(Collectors.toMap(c -> c.getId(), c -> c));
			Set<String> companyIds = new HashSet<>(companies.keySet());
			res.forEach(ds -> {
				companyIds.remove(ds.getCompany());
				if (companies.containsKey(ds.getCompany())) ds.setPlayerId(companies.get(ds.getCompany()).getName());
			});
			if (all && !companyIds.isEmpty()) {
				res.addAll(companyIds.stream().map(id -> {
					DayStat ds = new DayStat();
					ds.setCompany(id);
					ds.setPlayerId(companies.get(id).getName());
					return ds;
				}).collect(Collectors.toList()));
			}
		}
		if (GROUP_BY_DATA.campaign.equals(dataGroupBy)) {
			res.forEach(ds -> {
				ds.setPlayerId(campaign.getTitle());
			});
			if (res.isEmpty()) {
				DayStat ds = new DayStat();
				ds.setCampaign(campaignId);
				ds.setPlayerId(campaign.getTitle());
				res.add(ds);
			}
		}
		if (GROUP_BY_DATA.employee.equals(dataGroupBy)) {
			Company company = companyRepo.findById(companyId).orElse(null);
			if (company == null) throw new InconsistentDataException("Invalid company: " + companyId, "NO_COMPANY");

			Set<String> players = res.stream().map(ds -> ds.getPlayerId()).collect(Collectors.toSet());
			List<User> users = userRepo.findByPlayerIdIn(players);
			// get the company employes subscribing the campaign
			List<Employee> employees = employeeRepo.findByCompanyIdAndCampaigns(companyId, campaignId);
			Map<String, Employee> employeeMap = new HashMap<>();
			Map<String, Employee> playerEmployeeMap = new HashMap<>();
			employees.forEach(e -> employeeMap.put(e.getCode(), e));
			users.forEach(u -> {
				u.getRoles().forEach(r -> {
					if (r.getSubscriptions() != null) {
						r.getSubscriptions().forEach(s -> {
							if (s.getCampaign().equals(campaignId) && s.getCompanyCode().equals(company.getCode()) && employeeMap.containsKey(s.getKey())) {
								playerEmployeeMap.put(u.getPlayerId(), employeeMap.get(s.getKey()));
							}
						});
					}
				});
			});
			Set<String> employeesWithData = new HashSet<>();
			res.forEach(ds -> {
				if (playerEmployeeMap.containsKey(ds.getPlayerId())) {
					Employee e = playerEmployeeMap.get(ds.getPlayerId());
					if (e != null) {
						ds.setPlayerId(e.getSurname()+ " " + e.getName());
						employeesWithData.add(e.getId());
					}
				}
			});
			if (all && (employeeId == null || employeeId.isEmpty())) {
				employees.forEach(e -> {
					if (!employeesWithData.contains(e.getId())) {
						DayStat ds = new DayStat();
						ds.setCampaign(campaignId);
						ds.setCompany(company.getId());
						ds.setPlayerId(e.getSurname()+ " " + e.getName());
						res.add(ds);
					}
				});
			} else if (employeeId != null && !employeeId.isEmpty()) {
				employees.forEach(e -> {
					if (employeeId.contains(e.getId()) && !employeesWithData.contains(e.getId())) {
						DayStat ds = new DayStat();
						ds.setCampaign(campaignId);
						ds.setCompany(company.getId());
						ds.setPlayerId(e.getSurname()+ " " + e.getName());
						res.add(ds);
					}
				});
			}
		}

		// Special case: group by location. Map employees to locations and sum
		if (GROUP_BY_DATA.location.equals(dataGroupBy)) {
			Company company = companyRepo.findById(companyId).orElse(null);
			if (company == null) throw new InconsistentDataException("Invalid company: " + companyId, "NO_COMPANY");
			// Map<String, CompanyLocation> locations = company.getLocations().stream().collect(Collectors.toMap(l -> l.getId(), l -> l));

			// get the players involved
			Set<String> players = res.stream().map(ds -> ds.getPlayerId()).collect(Collectors.toSet());
			List<User> users = userRepo.findByPlayerIdIn(players);
			// get the company employes subscribing the campaign
			List<Employee> employees = employeeRepo.findByCompanyIdAndCampaigns(companyId, campaignId);
			// map the players to location using the codes of the employees in subscriptions
			Map<String, String> employeeLocationMap = new HashMap<>();
			Map<String, String> playerLocationMap = new HashMap<>();
			employees.forEach(e -> employeeLocationMap.put(e.getCode(), e.getLocation()));
			users.forEach(u -> {
				u.getRoles().forEach(r -> {
					if (r.getSubscriptions() != null) {
						r.getSubscriptions().forEach(s -> {
							if (s.getCampaign().equals(campaignId) && s.getCompanyCode().equals(company.getCode()) && employeeLocationMap.containsKey(s.getKey())) {
								playerLocationMap.put(u.getPlayerId(), employeeLocationMap.get(s.getKey()));
							}
						});
					}
				});
			});
			List<DayStat> locationRes = new LinkedList<>();
			Map<String, Map<String,DayStat>> locationStats = new HashMap<>();
			Set<String> locationsWithData = new HashSet<>();
			for (DayStat ds: res) {
				String pid = ds.getPlayerId();
				String lid = playerLocationMap.get(pid);
				if (lid == null) continue;
				locationsWithData.add(lid);
				String key = getAggKey(ds, timeGroupBy);
				Map<String,DayStat> lsMap = locationStats.get(lid);
				if (lsMap == null) {
					lsMap = new HashMap<>();
					locationStats.put(lid, lsMap);
				}
				DayStat ls = lsMap.get(key);
				if (ls == null) {
					ls = new DayStat();
					ls.setCampaign(campaignId);
					ls.setCompany(companyId);
					ls.setPlayerId(lid);
					ls.setDate(ds.getDate());
					ls.setWeek(ds.getWeek());
					ls.setMonth(ds.getMonth());

					ls.setCo2saved(ds.getCo2saved());
					ls.setTrackCount(ds.getTrackCount());
					ls.setLimitedTrackCount(ds.getLimitedTrackCount());
					ls.setScore(Score.copy(ds.getScore()));
					ls.setLimitedScore(Score.copy(ds.getLimitedScore()));
					ls.setMeanScore(MeanScore.copy(ds.getMeanScore()));
					ls.setLimitedMeanScore(MeanScore.copy(ds.getLimitedMeanScore()));
					ls.setMeanCo2(MeanScore.copy(ds.getMeanCo2()));
					ls.setMeanDistance(MeanScore.copy(ds.getMeanDistance()));
					ls.setMeanDuration(MeanScore.copy(ds.getMeanDuration()));
					ls.setMeanTracks(MeanScore.copy(ds.getMeanTracks()));
					lsMap.put(key, ls);
					locationRes.add(ls);
				} else {
					ls.setCo2saved(ls.getCo2saved() + ds.getCo2saved());
					ls.setTrackCount(ls.getTrackCount() + ds.getTrackCount());
					ls.setLimitedTrackCount(ls.getLimitedTrackCount() + ds.getLimitedTrackCount());
					ls.getScore().mergeScore(ds.getScore());
					ls.getLimitedScore().mergeScore(ds.getLimitedScore());
					ls.getMeanScore().mergeScore(ds.getMeanScore());
					ls.getLimitedMeanScore().mergeScore(ds.getLimitedMeanScore());
					ls.getMeanCo2().mergeScore(ds.getMeanCo2());
					ls.getMeanDistance().mergeScore(ds.getMeanDistance());
					ls.getMeanDuration().mergeScore(ds.getMeanDuration());
					ls.getMeanTracks().mergeScore(ds.getMeanTracks());
				}
			}
			if (all && location == null) {
				company.getLocations().forEach(l -> {
					if (!locationsWithData.contains(l.getId())) {
						DayStat ls = new DayStat();
						ls.setCampaign(campaignId);
						ls.setCompany(companyId);
						ls.setPlayerId(l.getId());
						locationRes.add(ls);
					}
				});
			}
			return locationRes;
		}

		return res;
	}

	private String getAggKey(DayStat ds, GROUP_BY_TIME timeGroupBy) {
		if (GROUP_BY_TIME.day.equals(timeGroupBy)) return ds.getDate();
		if (GROUP_BY_TIME.week.equals(timeGroupBy)) return ds.getWeek();
		if (GROUP_BY_TIME.month.equals(timeGroupBy)) return ds.getMonth();
		return "";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void populateKeyFields(DayStat stat, Map m, List<String> group) {
		if (group.size() == 1) {
			stat.setCampaign((String)m.get("_id"));
		} else {
			Map<String, String> idMap = (Map<String, String>) m.get("_id");
			stat.setCampaign((String)idMap.get("campaign"));
			stat.setCompany(idMap.getOrDefault("company", null));
			stat.setPlayerId(idMap.getOrDefault("playerId", null));
			if (idMap.containsKey("date")) {
				stat.setDate((String)idMap.get("date"));
				LocalDate ld = LocalDate.parse(stat.getDate());
				stat.setMonth(ld.format(MONTH_PATTERN));
				stat.setWeek(ld.format(WEEK_PATTERN));
			}
			if (idMap.containsKey("week")) {
				stat.setWeek((String)idMap.get("week"));
				LocalDate ld = LocalDate.parse(stat.getWeek(), WEEK_PATTERN);
				stat.setMonth(ld.format(MONTH_PATTERN));
			}
			if (idMap.containsKey("month")) {
				stat.setMonth((String)idMap.get("month"));
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void populateMeanFields(DayStat stat, Map map, STAT_FIELD field, List<MEAN> means) {
		Map<Object, Double> values = new HashMap<>();
		for(MEAN m : means) {
			values.put(m.name(), ((Number)map.getOrDefault(field.name() + "_" + m.name(), 0d)).doubleValue());
		}
		if (STAT_FIELD.meanScore.equals(field)) stat.setMeanScore(MeanScore.fromMap(values));
		if (STAT_FIELD.limitedMeanScore.equals(field)) stat.setLimitedMeanScore(MeanScore.fromMap(values));
		if (STAT_FIELD.meanCo2.equals(field)) stat.setMeanCo2(MeanScore.fromMap(values));
		if (STAT_FIELD.meanDistance.equals(field)) stat.setMeanDistance(MeanScore.fromMap(values));
		if (STAT_FIELD.meanDuration.equals(field)) stat.setMeanDuration(MeanScore.fromMap(values));
		if (STAT_FIELD.meanTracks.equals(field)) stat.setMeanTracks(MeanScore.fromMap(values));
	}

	private GroupOperation groupByMean(String field, List<MEAN> means, GroupOperation groupByOperation) {
		for(MEAN m : means) {
			groupByOperation = groupByOperation.sum(field+ "." + m.name()).as(field + "_" + m.name());
		}
		return groupByOperation;
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
	private List<DayStat> doAggregation(Campaign campaign, Criteria criteria, String agg) {
		MatchOperation filterOperation = Aggregation.match(criteria);
		String src = "limitedMeanScore";
		
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

	/**
	 * @param campaignId
	 */
	public void cleanCampaign(String campaignId) {
		dayStatRepo.deleteByCampaign(campaignId);
	}

	public void csvStatistics(
			PrintWriter writer, 			
			String campaignId,
			String companyId,
			String location,
			Set<String> employeeId,			
			GROUP_BY_TIME timeGroupBy,
			GROUP_BY_DATA dataGroupBy,
			List<STAT_FIELD> fields,
			LocalDate from, 
			LocalDate to,
			boolean all
	) throws InconsistentDataException {
		try {
			Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
			if (from == null) from = campaign.getFrom();
			if (to == null) to = LocalDate.now();

			if (campaign == null) throw new InconsistentDataException("Invalid campaign: " + campaignId, "NO_CAMPAIGN");

			CSVWriter csvWriter = new CSVWriter(writer, ';', '"', '"', "\n");
			List<DayStat> res = statistics(campaignId, companyId, location, employeeId, timeGroupBy, dataGroupBy, fields, from, to, all);
			
			// headers
			List<String> headers = createHeaders(timeGroupBy, from, to);
			List<String> fieldHeaders = createSubheaders(headers, fields, campaign);
			String nameHeader = getNameHeader(dataGroupBy);
			// write headers
			if (!headers.isEmpty()) {
				String s = ";";
				for (String h: headers) {
					s += h;
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
			Multimap<String, DayStat> mm = Multimaps.index(res, ds -> ds.getPlayerId());
			List<String[]> table = new LinkedList<>();
			mm.keySet().forEach(id -> {
				List<String> values = extractValues(id, mm.get(id), timeGroupBy, fields, headers, campaign);
				table.add(values.toArray(new String[values.size()]));
			});
			table.sort((a,b) -> a[0].compareToIgnoreCase(b[0]));
			table.forEach(l -> {
				csvWriter.writeNext(l);
			});
		} finally  {
			if (writer != null) {
				try {
					writer.close();					
				} catch (Exception e) {
				}
			}
		}

	}

	private List<String> extractValues(String id, Collection<DayStat> list, GROUP_BY_TIME timeGroupBy, List<STAT_FIELD> fields, List<String> headers, Campaign campaign) {
		List<String> res = new LinkedList<>();
		res.add(id);
		// total aggregation, assume one record exists
		if (headers.isEmpty()) {
			res.addAll(extractValuesForHeader("", list.iterator().next(), fields, campaign));
		} else {
			Map<String, DayStat> objectMap = new HashMap<>();
			list.forEach(ds -> {
				if (GROUP_BY_TIME.day.equals(timeGroupBy)) {
					objectMap.put(ds.getDate(), ds);
				}
				else if (GROUP_BY_TIME.week.equals(timeGroupBy)) {
					objectMap.put(ds.getWeek(), ds);
				}
				else if (GROUP_BY_TIME.month.equals(timeGroupBy)) {
					objectMap.put(ds.getMonth(), ds);
				}
			});
			headers.forEach(h -> {
				res.addAll(extractValuesForHeader(h, objectMap.get(h), fields, campaign));
			});
		}
		return res;
	}

	private List<String> extractValuesForHeader(String h, DayStat inds, List<STAT_FIELD> fields, Campaign campaign) {
		List<String> res = new LinkedList<>();
		final DayStat ds = inds == null ? new DayStat() : inds;
		fields.forEach(f -> {
			switch (f) {
				case score:
					res.add(normalizedValue(ds.getScore().getScore(), 1)); break;
				case limitedScore:
					res.add(normalizedValue(ds.getLimitedScore().getScore(), 1)); break;
				case trackCount:
					res.add(normalizedValue(ds.getTrackCount(), 1)); break;
				case limitedTrackCount:
					res.add(normalizedValue(ds.getLimitedTrackCount(), 1)); break;
				case co2saved:
					res.add(normalizedValue(ds.getCo2saved(), 1)); break;
				case meanScore: 
					campaign.getMeans().forEach(ms -> {
						MEAN m = MEAN.valueOf(ms);
						res.add(normalizedValue(ds.getMeanScore().meanValue(m), 1)); 					
					});
					break;
				case limitedMeanScore:
					campaign.getMeans().forEach(ms -> {
						MEAN m = MEAN.valueOf(ms);
						res.add(normalizedValue(ds.getLimitedMeanScore().meanValue(m), 1)); 					
					});
					break;
				case meanDistance: 
					campaign.getMeans().forEach(ms -> {
						MEAN m = MEAN.valueOf(ms);
						res.add(normalizedValue(ds.getMeanDistance().meanValue(m), 0.001)); 					
					});
					break;
				case meanDuration: //, , 
					campaign.getMeans().forEach(ms -> {
						MEAN m = MEAN.valueOf(ms);
						res.add(normalizedValue(ds.getMeanDuration().meanValue(m), 1/3600)); 					
					});
					break;
				case meanCo2: 
					campaign.getMeans().forEach(ms -> {
						MEAN m = MEAN.valueOf(ms);
						res.add(normalizedValue(ds.getMeanCo2().meanValue(m), 1)); 					
					});
					break;
				case meanTracks: 
					campaign.getMeans().forEach(ms -> {
						MEAN m = MEAN.valueOf(ms);
						res.add(normalizedValue(ds.getMeanCo2().meanValue(m), 1)); 					
					});
					break;
			}
		});
		return res;
	}

	private String normalizedValue(Number score, double multiplier) {
		return score != null ? ""+(score.doubleValue() * multiplier) : "0";
	}

	private List<String> createHeaders(GROUP_BY_TIME timeGroupBy, LocalDate from, LocalDate to) {
		List<String> list = new LinkedList<>();
		if (GROUP_BY_TIME.day.equals(timeGroupBy)) {
			LocalDate curr = from;
			while (!curr.isAfter(to)) {
				list.add(curr.toString());
				curr = curr.plusDays(1);
			}
		}
		else if (GROUP_BY_TIME.week.equals(timeGroupBy)) {
			LocalDate curr = from;
			while (!curr.isAfter(to)) {
				list.add(curr.format(WEEK_PATTERN));
				curr = curr.plusWeeks(1);
			}
		}
		else if (GROUP_BY_TIME.month.equals(timeGroupBy)) {
			LocalDate curr = from;
			while (!curr.isAfter(to)) {
				list.add(curr.format(MONTH_PATTERN));
				curr = curr.plusMonths(1);
			}
		}
		return list;
	}
	private List<String> createSubheaders(List<String> headers, List<STAT_FIELD> fields, Campaign campaign) {
		List<String> fList = new LinkedList<>();
		fields.forEach(f -> {
			switch (f) {
				case score:
				case limitedScore:
				case trackCount:
				case limitedTrackCount:
				case co2saved:
					fList.add(f.name());
					break;
				default:
					campaign.getMeans().forEach(m -> {
						fList.add(f.name() + "_" + m);
					});
					break;
			}
		});
		return fList;
	}
	private String getNameHeader(GROUP_BY_DATA dataGroupBy) {
		switch (dataGroupBy) {
			case employee: return "Dipendente";
			case location: return "Sede";
			case company: return "Azienda";
			case campaign: return "Campagna";
		}
		return dataGroupBy.name();
	}
}
