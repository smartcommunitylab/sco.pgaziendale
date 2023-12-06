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

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Campaign.VirtualScore;
import it.smartcommunitylab.pgazienda.domain.Campaign.VirtualScoreValue;
import it.smartcommunitylab.pgazienda.domain.Constants;
import it.smartcommunitylab.pgazienda.domain.Constants.MEAN;
import it.smartcommunitylab.pgazienda.domain.Territory;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;

/**
 * @author raman
 *
 */
@Service
public class PGAppService {

	private static final Logger logger = LoggerFactory.getLogger(PGAppService.class);

	private Map<String,Territory> territories = new HashMap<>();

	@Autowired
	private RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	private WebClient webClient;
	
	@Value("${app.engineEndpoint}")
	private String engineEndpoint;
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Campaign> retrieveExternalCampaigns() {
		final List<Campaign> list = new LinkedList<>(); 
		Map[] campaigns = restTemplate.getForObject(engineEndpoint + "/publicapi/campaign?type=company", Map[].class);
		for (Map cm: campaigns) {
			Campaign converted = convertCampaign(cm);
			list.add(converted);
		}
		return list;
	}

	/**
	 * @param cm
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Campaign convertCampaign(Map<String, Object> cm) {
		Campaign c = new Campaign();
		c.setActive(true);
		c.setId((String) cm.get("campaignId"));
		Map<String, Object> values = (Map<String, Object>) cm.get("description");
		if (values != null) {
			c.setDescription((String) values.getOrDefault("it", values.get("en")));
		}
		c.setFrom(Instant.ofEpochMilli((Long)cm.get("dateFrom")).atZone(ZoneId.of(Constants.DEFAULT_TIME_ZONE)).toLocalDate());
		c.setTerritoryId((String)cm.get("territoryId"));
		c.setLogo((String) ((Map)cm.get("logo")).get("url"));
		c.setMeans((List<String>) ((Map)cm.getOrDefault("validationData", Collections.emptyMap())).get("means"));
		if (cm.containsKey("privacy")) {
			c.setPrivacy((String) cm.get("privacy"));
		} else {
			c.setPrivacy(extractDetails(cm.get("details"), "privacy"));
		}
		if (cm.containsKey("rules")) {
			c.setPrivacy((String) cm.get("rules"));
		} else {
			c.setPrivacy(extractDetails(cm.get("details"), "rules"));
		}
		values = (Map<String, Object>) cm.get("name");
		c.setTitle((String) values.getOrDefault("it", values.get("en")));
		c.setTo(Instant.ofEpochMilli((Long)cm.get("dateTo")).atZone(ZoneId.of(Constants.DEFAULT_TIME_ZONE)).toLocalDate());

		c.setVirtualScore(extractVirtualScore((Map<String, Object>)((Map<String, Object>)cm.get("specificData")).get("virtualScore")));
		c.setTrackLimits(c.getVirtualScore().trackLimits());
		c.setScoreLimits(c.getVirtualScore().scoreLimits());
		return c;
	}

	@SuppressWarnings("unchecked")
	private VirtualScore extractVirtualScore(Map<String, Object> map) {
		VirtualScore score = new VirtualScore();
		score.setLabel((String)map.getOrDefault("label", "km"));
		score.setScoreDailyLimit(getNumValue(map, "scoreDailyLimit").doubleValue());
		score.setScoreWeeklyLimit(getNumValue(map, "scoreWeeklyLimit").doubleValue());
		score.setScoreMonthlyLimit(getNumValue(map, "scoreMonthlyLimit").doubleValue());

		score.setTrackDailyLimit(getNumValue(map, "trackDailyLimit").intValue());
		score.setTrackWeeklyLimit(getNumValue(map, "trackWeeklyLimit").intValue());
		score.setTrackMonthlyLimit(getNumValue(map, "trackMonthlyLimit").intValue());

		for (MEAN m : MEAN.values()) {
			if (map.get(m.name()) != null) {
				Map<String, Object> sv = (Map<String, Object>) map.get(m.name());
				VirtualScoreValue vsv = new VirtualScoreValue((String) sv.get("metric"), ((Number)sv.get("coefficient")).doubleValue());
				if (MEAN.bike.equals(m)) score.setBike(vsv);
				if (MEAN.bus.equals(m)) score.setBus(vsv);
				if (MEAN.train.equals(m)) score.setTrain(vsv);
				if (MEAN.car.equals(m)) score.setCar(vsv);
				if (MEAN.walk.equals(m)) score.setWalk(vsv);
				if (MEAN.boat.equals(m)) score.setBoat(vsv);
			}
		}
		return score;
	}

	private Number getNumValue(Map<String, Object> map, String key) {
		String v = map.getOrDefault(key, "0").toString();
		if (!StringUtils.hasText(v)) return 0;
		try {
			return Double.parseDouble(v);
		} catch (Exception e) {
			return 0;
		}	
	}

	/**
	 * @param object
	 * @param string
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String extractDetails(Object details, String type) {
		if (details == null) return null;
		details = ((Map<String, Object>)details).getOrDefault("it", ((Map<String, Object>)details).get("en"));
		List<Object> list = (List) details;
		if (details == null) return null;
		for (Object detail : list) {
			Map<String, Object> map = (Map<String, Object>) detail;
			if (type.equals(map.get("type"))) {
				return (String) map.get("content");
			}
		}
		return null;
	}

	public Territory getTerritory(String id) {
		if (!territories.containsKey(id)) {
			readTerritories();
		}
		return territories.get(id);
	}
	public void setTerritories(List<Territory> territories) {
		for (Territory t: territories) {
			this.territories.put(t.getTerritoryId(), t);
		}

	}

	public Collection<Territory> getTerritories() {
		if (territories.isEmpty()) {
			readTerritories();
		}
		return territories.values();
	}

	@Scheduled(fixedDelay=1000*60*60) 
	public void readTerritories() {

		ParameterizedTypeReference<List<Territory>>  resp = new ParameterizedTypeReference<List<Territory>>(){};
				
		try {
			List<Territory> res = restTemplate.exchange(engineEndpoint + "/publicapi/territory", HttpMethod.GET, null, resp).getBody();
			territories.clear();
			setTerritories(res);
			
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}	
	}

	public void resetTerritories() throws InconsistentDataException {
		this.territories.clear();;
	}
	
	public void unsubscribePlayer(String playerId, String campaignId) throws InconsistentDataException {
		String uri = new String(engineEndpoint);
		if(!engineEndpoint.endsWith("/")) uri += "/";
		uri += "api/ext/campaign/unsubscribe/player?campaignId=" + campaignId + "&playerId=" + playerId; 	    	
		try {
			ResponseEntity<Void> response = webClient.delete().uri(uri).retrieve().toEntity(Void.class).block();
			if(!response.getStatusCode().is2xxSuccessful())
				throw new InconsistentDataException(String.format("error calling %s:%s", uri, response.getStatusCodeValue()), "");			
		} catch (Exception e) {
			throw new InconsistentDataException(String.format("error calling %s:%s", uri, e.getMessage()), "");
		}
	}
	
}
