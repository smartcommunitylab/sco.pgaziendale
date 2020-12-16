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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Company;
import it.smartcommunitylab.pgazienda.domain.DayStat;
import it.smartcommunitylab.pgazienda.domain.DayStat.Distances;
import it.smartcommunitylab.pgazienda.domain.Employee;
import it.smartcommunitylab.pgazienda.domain.TrackingData;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.CompanyRepository;
import it.smartcommunitylab.pgazienda.repository.DayStatRepository;
import it.smartcommunitylab.pgazienda.repository.EmployeeRepository;
import it.smartcommunitylab.pgazienda.repository.PGAppRepository;
import it.smartcommunitylab.pgazienda.repository.UserRepository;

/**
 * @author raman
 *
 */
@Service
public class TrackingDataService {

	private static final Logger logger = LoggerFactory.getLogger(TrackingDataService.class);
	private static final DateTimeFormatter MONTH_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM");
	private static final Object GROUPBY_DAY = "day";
	private static final Object GROUPBY_MONTH = "month";

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
	
	@Scheduled(initialDelay=5000, fixedDelay=1000*60*60*2)
	public void synchronizeApps() {
		appRepo.findAll().forEach(a -> {
			
			logger.info("Styncronizing app: " + a.getId());
			
			final List<Campaign> campaigns = campaignRepo.findByApplication(a.getId()).stream().filter(c -> !c.getFrom().isAfter(LocalDate.now()) && !c.getTo().isBefore(LocalDate.now())).collect(Collectors.toList());

			if (campaigns.isEmpty()) return;

			// Application campaigns
			campaigns.forEach(campaign -> {
				// campaign companies
				logger.info("Styncronizing app campaign: " + campaign.getId());
				List<Company> companies = companyRepo.findByCampaign(campaign.getId());
				companies.forEach(company -> {
					try {
						logger.info("Styncronizing app campaign company: " + company.getCode());
						// company employees subscribed to this campaign 
						List<Employee> employees = employeeRepo.findByCompanyIdAndCampaigns(company.getId(), campaign.getId());
						if (employees.isEmpty()) return;
						Set<String> codeSet = employees.stream().map(e -> e.getCode()).collect(Collectors.toSet());
						// users corresponding to this employees
						List<User>  users = userRepo.findByCampaignAndCompanyAndEmployeeCode(campaign.getId(), company.getCode(), codeSet);
						if (users.isEmpty()) return;
						List<String> playerIds = users.stream().map(u -> u.getPlayerId()).collect(Collectors.toList());
						logger.info("Styncronizing app campaign company users: " + playerIds);

						TrackingDataRequestDTO request = new TrackingDataRequestDTO();
						request.setFrom(LocalDate.now().toString());
						request.setTo(request.getFrom());
						List<String> means = campaigns.stream().flatMap(c -> c.getMeans().stream()).collect(Collectors.toList());
						request.setMeans(means);
						request.setMultimodal(true);
						request.setPlayerId(playerIds);
						request.setLocations(company.getLocations().stream()
						.filter(l -> l.getNonWorking() == null || l.getNonWorking().isEmpty() || l.getNonWorking().contains(LocalDate.now().getDayOfWeek().getValue()))
						.map(l -> {
							LocationDTO ldto = new LocationDTO();
							ldto.setLat(l.getLatitute());
							ldto.setLng(l.getLongitude());
							ldto.setRad(l.getRadius());
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
						
						logger.info("Styncronizing app campaign company data: " + new ObjectMapper().writeValueAsString(request));

						HttpEntity<TrackingDataRequestDTO> entity = new HttpEntity<>(request, headers);

						List<TrackingData> list = restTemplate.exchange(a.getEndpoint(), HttpMethod.POST, entity, resp).getBody();
						ImmutableListMultimap<String, TrackingData> multimap = Multimaps.index(list, track -> track.getPlayerId());

						multimap.keySet().forEach(key -> {
							List<TrackingData> playerList = multimap.get(key);
							logger.info("Received data for player: " + key +" - " + playerList.size()+ " entities");

							DayStat stat = new DayStat();
							stat.setPlayerId(key);
							stat.setCampaign(campaign.getId());
							stat.setCompany(company.getId());
							stat.setDate(LocalDate.now().toString());
							stat.setTrackCount(playerList.size());
							stat.setDistances(Distances.fromMap(playerList.stream().collect(Collectors.groupingBy(t -> t.getMode(), Collectors.summingDouble(t -> t.getDistance())))));
							stat.setTracks(playerList);
							stat.setCo2saved(0d);
							stat.setMonth(LocalDate.now().format(MONTH_PATTERN));
							DayStat old = dayStatRepo.findOneByPlayerIdAndCampaignAndCompanyAndDate(key, campaign.getId(), company.getId(), stat.getDate());
							if (old != null) {
								stat.setId(old.getId());
							}
							dayStatRepo.save(stat);

						});
					} catch (Exception e) {
						logger.error("Error processing campaign/company data: {}/{}", campaign.getId(), company.getId());
						logger.error(e.getMessage(), e);
					}	
				});
			});
		});
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
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<DayStat> getUserCampaignData(String playerId, String campaignId, LocalDate from, LocalDate to, String groupBy, Boolean withTracks) {
		Criteria criteria = new Criteria("playerId").is(playerId).and("campaign").is(campaignId).and("date").lte(to.toString()).gte(from.toString());
		if (GROUPBY_DAY.equals(groupBy)) {
			Query q = Query.query(criteria);
			if (!withTracks) q.fields().exclude("tracks");
			return template.find(q, DayStat.class);
		} else if (GROUPBY_MONTH.equals(groupBy)){
			Campaign campaign = campaignRepo.findById(campaignId).orElse(null);
			if (campaign == null) throw new IllegalArgumentException("Invalid campaign: " + campaignId);
			MatchOperation filterOperation = Aggregation.match(criteria);
	    	GroupOperation groupByOperation = Aggregation.group(groupBy)
	    			.sum("co2saved").as("co2saved")
	    			.sum("trackCount").as("trackCount")
	    			.sum("distances.bike").as("bike");
	    	
	    	for (String mean: campaign.getMeans()) groupByOperation = groupByOperation.sum("distances." + mean).as(mean);
	    	
	    	Aggregation aggregation = Aggregation.newAggregation(filterOperation, groupByOperation);
	    	AggregationResults<Map> aggResult = template.aggregate(aggregation, DayStat.class, Map.class);
	    	return aggResult.getMappedResults().stream().map(m -> {
	    		DayStat stat = new DayStat();
	    		stat.setCampaign(campaignId);
	    		stat.setPlayerId(playerId);
	    		stat.setCo2saved((double)m.getOrDefault("co2saved", 0d));
	    		stat.setMonth((String) m.get("_id"));
	    		stat.setDistances(Distances.fromMap(m));
	    		stat.setTrackCount((Integer) m.getOrDefault("trackCount", 0));
	    		return stat;
	    	}).collect(Collectors.toList());
		} else {
			throw new IllegalArgumentException("Incorrect grouping");
		}
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
