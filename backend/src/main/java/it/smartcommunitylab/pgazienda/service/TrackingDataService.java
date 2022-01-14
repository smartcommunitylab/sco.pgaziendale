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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import com.opencsv.CSVWriter;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.CompanyLocation;
import it.smartcommunitylab.pgazienda.domain.Constants;
import it.smartcommunitylab.pgazienda.domain.Constants.MEAN;
import it.smartcommunitylab.pgazienda.domain.DayStat;
import it.smartcommunitylab.pgazienda.domain.DayStat.Distances;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.PGApp;
import it.smartcommunitylab.pgazienda.domain.TrackingData;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.DayStatRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.repository.PGAppRepository;
import it.smartcommunitylab.pgazienda.repository.UserRepository;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import it.smartcommunitylab.pgazienda.util.LimitsUtils;

/**
 * @author raman
 *
 */
@Service
public class TrackingDataService {

	private static final Logger logger = LoggerFactory.getLogger(TrackingDataService.class);
	private static final DateTimeFormatter MONTH_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM");
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private PGAppRepository appRepo;
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

//	@PostConstruct
	public void init() {
		dayStatRepo.findByEmptyLimitedDistances().forEach(ds -> {
			Campaign campaign = campaignRepo.findById(ds.getCampaign()).orElse(null);
			if (campaign != null) {
				limitDistances(campaign, ds.getPlayerId(), ds);
				dayStatRepo.save(ds);
			}
		});
	}	
	
	@Scheduled(initialDelay=5000, fixedDelay=1000*60*60*2)
	public void synchronizeApps() {
		appRepo.findAll().forEach(a -> {
			
			logger.info("Syncronizing app: " + a.getId());
			
			final List<Campaign> campaigns = campaignRepo.findByApplication(a.getId()).stream().filter(c -> !c.getFrom().isAfter(LocalDate.now()) && !c.getTo().isBefore(LocalDate.now())).collect(Collectors.toList());

			if (campaigns.isEmpty()) return;

			// Application campaigns
			campaigns.forEach(campaign -> {
				// campaign companies
				logger.info("Syncronizing app campaign: " + campaign.getId());
				List<Company> companies = companyRepo.findByCampaign(campaign.getId());
				companies.forEach(company -> {
					LocalDate today = LocalDate.now();
					syncCompanyData(a, campaign, company, today, today);	
				});
			});
		});
	}
	
	/**
	 * Sync company data for specified range of dates
	 * @param campaignId
	 * @param companyId
	 * @param dayFrom
	 * @param dayTo
	 */
	public void syncCompanyData(String campaignId, String companyId, LocalDate dayFrom, LocalDate dayTo) {
		campaignRepo.findById(campaignId).ifPresent(campaign -> {
			companyRepo.findById(companyId).ifPresent(company -> {
				appRepo.findById(campaign.getApplication()).ifPresent(a -> {
					syncCompanyData(a, campaign, company, dayFrom, dayTo);
				});
			});
		});
	}

	/**
	 * @param a
	 * @param campaigns
	 * @param campaign
	 * @param company
	 * @param day
	 */
	private void syncCompanyData(PGApp a, Campaign campaign, Company company, LocalDate dayFrom, LocalDate dayTo) {
		try {

			logger.info("Syncronizing app campaign company: " + company.getCode());
			// company employees subscribed to this campaign 
			List<Employee> employees = employeeRepo.findByCompanyIdAndCampaigns(company.getId(), campaign.getId());
			if (employees.isEmpty()) return;
			Set<String> codeSet = employees.stream().map(e -> e.getCode()).collect(Collectors.toSet());
			// users corresponding to this employees
			List<User>  users = userRepo.findByCampaignAndCompanyAndEmployeeCode(campaign.getId(), company.getCode(), codeSet);
			if (users.isEmpty()) return;
			List<String> playerIds = users.stream().map(u -> u.getPlayerId()).collect(Collectors.toList());
			logger.info("Styncronizing app campaign company users: " + playerIds);

			LocalDate d = dayFrom;
			while (!d.isAfter(dayTo)) {
				final LocalDate day = d;
				TrackingDataRequestDTO request = new TrackingDataRequestDTO();
				request.setFrom(day.toString());
				request.setTo(request.getFrom());
				List<String> means = campaign.getMeans();
				request.setMeans(means);
				request.setMultimodal(true);
				request.setPlayerId(playerIds);
				request.setLocations(company.getLocations().stream()
				.filter(l -> checkWorking(l, day))
				.map(l -> {
					LocationDTO ldto = new LocationDTO();
					ldto.setLat(l.getLatitude());
					ldto.setLng(l.getLongitude());
					ldto.setRad(l.getRadius() / 1000);
					return ldto;
				}).collect(Collectors.toList()));
				
				ParameterizedTypeReference<List<TrackingData>>  resp = new ParameterizedTypeReference<List<TrackingData>>(){};
				MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
				String s = a.getId() + ":" + a.getPassword();
				byte[] b = Base64Utils.encode(s.getBytes());
				String es = new String(b);

				headers.add("Authorization", "Basic " + es);
				headers.add("Accept", "application/json");
				headers.add("Content-Type", "application/json");
				
				logger.info("Syncronizing app campaign company data: " + new ObjectMapper().writeValueAsString(request));

				HttpEntity<TrackingDataRequestDTO> entity = new HttpEntity<>(request, headers);

				try {
					List<TrackingData> list = restTemplate.exchange(a.getEndpoint(), HttpMethod.POST, entity, resp).getBody();
					ImmutableListMultimap<String, TrackingData> playerMap = Multimaps.index(list, track -> track.getPlayerId());

					playerMap.keySet().forEach(key -> {
						List<TrackingData> playerList = playerMap.get(key);
						logger.info("Received data for player: " + key +" - " + playerList.size()+ " entities");

						DayStat stat = new DayStat();
						stat.setPlayerId(key);
						stat.setCampaign(campaign.getId());
						stat.setCompany(company.getId());
						stat.setDate(day.toString());
						stat.setTrackCount(playerList.size());
						stat.setDistances(Distances.fromMap(playerList.stream().collect(Collectors.groupingBy(t -> t.getMode(), Collectors.summingDouble(t -> t.getDistance())))));
						stat.setTracks(playerList);
						stat.setCo2saved(stat.computeCO2());
						stat.setMonth(day.format(MONTH_PATTERN));

						limitDistances(campaign, key, stat);

						DayStat old = dayStatRepo.findOneByPlayerIdAndCampaignAndCompanyAndDate(key, campaign.getId(), company.getId(), stat.getDate());
						if (old != null) {
							stat.setId(old.getId());
						}
						dayStatRepo.save(stat);

					});
				} catch (Exception dayEx) {
					logger.error("Error processing campaign/company data for date: {}/{} {}", campaign.getId(), company.getId(), d.toString());
				}				
				d = d.plusDays(1);
			}
			

		} catch (Exception e) {
			logger.error("Error processing campaign/company data: {}/{}", campaign.getId(), company.getId());
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * @param campaign
	 * @param playerId
	 * @param stat
	 */
	public void limitDistances(Campaign campaign, String playerId, DayStat stat) {
		Criteria criteria = Criteria.where("playerId").is(playerId).and("date").lt(stat.getDate());
		List<DayStat> totalAgg = doAggregation(campaign, criteria, false, false);
		criteria = Criteria.where("playerId").is(playerId).and("date").lt(stat.getDate()).gte(LocalDate.parse(stat.getDate()).withDayOfMonth(1).toString());
		List<DayStat> currMonthAgg = doAggregation(campaign, criteria, true, false);
		stat.setLimitedDistances(LimitsUtils.applyLimits(stat.getDistances(), currMonthAgg, totalAgg, campaign));
		System.err.println(stat.getDistances().getBike() +" -> "+ stat.getLimitedDistances().getBike());
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

		Criteria criteria = new Criteria("playerId").is(playerId).and("campaign").is(campaignId).and("date").lte(to.toString());
		if (from != null) criteria = criteria.gte(from.toString());
		List<DayStat> res = null;
		if (Constants.AGG_DAY.equals(groupBy)) {
			Query q = Query.query(criteria);
			if (!withTracks) q.fields().exclude("tracks");
			res = template.find(q, DayStat.class);
			if (!Boolean.TRUE.equals(noLimits)) {
				res.forEach(ds -> ds.setDistances(ds.getLimitedDistances()));
				LimitsUtils.applyLimits(res, groupBy, campaign);
			} 
		} else if (Constants.AGG_MONTH.equals(groupBy)){
			res = doAggregation(campaign, criteria, true, noLimits);
		} else if (Constants.AGG_TOTAL.equals(groupBy)){
			res = doAggregation(campaign, criteria, false, noLimits);
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
		
		String group = Constants.AGG_TOTAL.equals(groupBy) ? "campaign": Constants.AGG_MONTH.equals(groupBy) ? "month" : "date";

		MatchOperation filterOperation = Aggregation.match(criteria);
		String src = Boolean.TRUE.equals(noLimits) ? "distances" : "limitedDistances";

		String[] groupArray = company ? new String[]{"company", group} : new String[]{group};
		
		GroupOperation groupByOperation = Aggregation.group(groupArray)
				.sum("co2saved").as("co2saved")
				.sum("trackCount").as("trackCount");
		
		for (String mean: campaign.getMeans()) groupByOperation = groupByOperation.sum(src + "." + mean).as(mean);
		
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		AggregationResults<Map> aggResult = template.aggregate(aggregation, DayStat.class, Map.class);
		return aggResult.getMappedResults().stream().map(m -> {
			DayStat stat = new DayStat();
			stat.setCo2saved(((Number)m.getOrDefault("co2saved", 0d)).doubleValue());
			stat.setDistances(Distances.fromMap(m));
			stat.setTrackCount((Integer) m.getOrDefault("trackCount", 0));
			
			if (company) {
				Map<String, String> idMap = (Map<String, String>) m.get("_id");
				stat.setCompany((String)idMap.get("company"));
				if (Constants.AGG_DAY.equals(groupBy)) {
					stat.setDate((String)idMap.get("date"));
					stat.setMonth(LocalDate.parse(stat.getDate()).format(MONTH_PATTERN));
				}
				if (Constants.AGG_MONTH.equals(groupBy)) {
					stat.setMonth((String)idMap.get("month"));
				}
			} else {
				if (Constants.AGG_DAY.equals(groupBy)) {
					stat.setDate((String)m.get("_id"));
					stat.setMonth(LocalDate.parse(stat.getDate()).format(MONTH_PATTERN));
				}
				if (Constants.AGG_MONTH.equals(groupBy)) {
					stat.setMonth((String)m.get("_id"));
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
				Double mv = ds.getDistances().meanValue(MEAN.valueOf(campaign.getMeans().get(i)));
				if (mv == null) mv = 0d;
				mv = mv / 1000;
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
			
			Distances d = new Distances();
			int totalTrips = 0;
			for (DayStat ds: locationStats.get(locationId)) {
				d.mergeDistances(ds.getDistances());
				totalTrips += (ds.getTrackCount() == null ? 0 : ds.getTrackCount());
			}
			
			rec[6] = ""+totalTrips;
			for (int i = 0; i < campaign.getMeans().size(); i++) {
				Double mv = d.meanValue(MEAN.valueOf(campaign.getMeans().get(i)));
				if (mv == null) mv = 0d;
				mv = mv / 1000;
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
					newDs.setDistances(DayStat.Distances.copy(ds.getDistances()));
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
			Distances d = new Distances();
			int totalTrips = 0;
			for (DayStat ds: companyStats.get(companyId)) {
				d.mergeDistances(ds.getDistances());
				totalTrips += (ds.getTrackCount() == null ? 0 : ds.getTrackCount());
			}
			
			rec[1] = ""+totalTrips;
			for (int i = 0; i < campaign.getMeans().size(); i++) {
				Double mv = d.meanValue(MEAN.valueOf(campaign.getMeans().get(i)));
				if (mv == null) mv = 0d;
				mv = mv / 1000;
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
						Employee e = employeeRepo.findOneByCompanyIdAndCode(companyId, s.getKey()).orElse(null);
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
						Company company = companyCache.getOrDefault(s.getCompanyCode(), companyRepo.findOneByCode(s.getCompanyCode()).orElse(null));
						companyCache.put(s.getCompanyCode(), company);
						if (company != null) {
							Employee e = employeeRepo.findOneByCompanyIdAndCode(company.getId(), s.getKey()).orElse(null);
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
		List<DayStat> aggregation = doAggregation(campaign, criteria, false, false);
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
	private List<DayStat> doAggregation(Campaign campaign, Criteria criteria, boolean byMonth, Boolean noLimits) {
		MatchOperation filterOperation = Aggregation.match(criteria);
		String src = Boolean.TRUE.equals(noLimits) ? "distances" : "limitedDistances";
		
		GroupOperation groupByOperation = byMonth ? Aggregation.group("playerId", Constants.AGG_MONTH) : Aggregation.group("playerId");
		
		groupByOperation =	groupByOperation
				.sum("co2saved").as("co2saved")
				.sum("trackCount").as("trackCount");
		
		for (String mean: campaign.getMeans()) groupByOperation = groupByOperation.sum(src + "." + mean).as(mean);
		
		Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
		AggregationResults<Map> aggResult = template.aggregate(aggregation, DayStat.class, Map.class);
		return aggResult.getMappedResults().stream().map(m -> {
			DayStat stat = new DayStat();
			stat.setCo2saved(((Number)m.getOrDefault("co2saved", 0d)).doubleValue());
			if (byMonth) {
				stat.setMonth((String)((Map) m.get("_id")).get(Constants.AGG_MONTH));
				stat.setPlayerId((String)((Map) m.get("_id")).get("playerId"));
			} else {
				stat.setPlayerId((String)m.get("_id"));
			}
			stat.setDistances(Distances.fromMap(m));
			stat.setTrackCount((Integer) m.getOrDefault("trackCount", 0));
			return stat;
		}).collect(Collectors.toList());
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
	
	public static class TrackingDataRequestDTO {
		private List<String> playerId;
		private String from, to;
		private List<String> means;
		private Boolean multimodal;
		
		private List<LocationDTO> locations;

		public List<String> getPlayerId() {
			return playerId;
		}

		public void setPlayerId(List<String> playerId) {
			this.playerId = playerId;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public List<String> getMeans() {
			return means;
		}

		public void setMeans(List<String> means) {
			this.means = means;
		}

		public List<LocationDTO> getLocations() {
			return locations;
		}

		public void setLocations(List<LocationDTO> locations) {
			this.locations = locations;
		}

		public Boolean getMultimodal() {
			return multimodal;
		}

		public void setMultimodal(Boolean multimodal) {
			this.multimodal = multimodal;
		}
	}
	
	public static class LocationDTO {
		private double lng, lat, rad;

		public double getLng() {
			return lng;
		}

		public void setLng(double lng) {
			this.lng = lng;
		}

		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public double getRad() {
			return rad;
		}

		public void setRad(double rad) {
			this.rad = rad;
		}
	}

}
