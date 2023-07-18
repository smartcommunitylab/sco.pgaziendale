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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Constants;
import it.smartcommunitylab.pgazienda.domain.PGApp;
import it.smartcommunitylab.pgazienda.domain.Campaign.Limit;
import it.smartcommunitylab.pgazienda.domain.Campaign.VirtualScore;
import it.smartcommunitylab.pgazienda.domain.Campaign.VirtualScoreValue;
import it.smartcommunitylab.pgazienda.domain.Constants.MEAN;
import it.smartcommunitylab.pgazienda.repository.PGAppRepository;

/**
 * @author raman
 *
 */
@Service
public class PGAppService {

	@Autowired
	private PGAppRepository repo;
	
	@Autowired
	private RestTemplate restTemplate = new RestTemplate();
	
	public List<PGApp> getApps() {
		return repo.findAll().stream().filter(app -> !Boolean.TRUE.equals(app.getSupportCampaignMgmt())).map(app -> app.cleanPassword()).collect(Collectors.toList());
	}

	public Optional<PGApp> getApp(String id) {
		return repo.findById(id).map(app -> app.cleanPassword());
	}

	public PGApp createApp(PGApp app) {
		return repo.save(app).cleanPassword();
	}

	public PGApp updateApp(PGApp app) {
		PGApp old = repo.findById(app.getId()).orElse(null);
		if (old != null) {
			old.setName(app.getName());
			old.setEndpoint(app.getEndpoint());
			if (app.getPassword() != null) old.setPassword(app.getPassword());
			return repo.save(old).cleanPassword();
		} else {
			return createApp(app);
		}
	}

	public void deleteApp(String id) {
		repo.deleteById(id);
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Campaign> retrieveExternalCampaigns() {
		final List<Campaign> list = new LinkedList<>(); 
		repo.findAll().stream().filter(app -> Boolean.TRUE.equals(app.getSupportCampaignMgmt())).forEach(app -> {
			Map[] campaigns = restTemplate.getForObject(app.getEndpoint(), Map[].class);
			for (Map cm: campaigns) {
				Campaign converted = convertCampaign(cm);
				converted.setApplication(app.getId());
				list.add(converted);
			}
		});
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
		score.setScoreDailyLimit((Double)map.get("scoreDailyLimit"));
		score.setScoreWeeklyLimit((Double)map.get("scoreWeeklyLimit"));
		score.setScoreMonthlyLimit((Double)map.get("scoreMonthlyLimit"));
		score.setTrackDailyLimit((Integer)map.get("trackDailyLimit"));
		score.setTrackWeeklyLimit((Integer)map.get("trackWeeklyLimit"));
		score.setTrackMonthlyLimit((Integer)map.get("trackMonthlyLimit"));
		for (MEAN m : MEAN.values()) {
			if (map.get(m.name()) != null) {
				Map<String, Object> sv = (Map<String, Object>) map.get(m.name());
				VirtualScoreValue vsv = new VirtualScoreValue((String) sv.get("metric"), (Double) sv.get("coefficient"));
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
}
