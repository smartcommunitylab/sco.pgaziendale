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
import it.smartcommunitylab.pgazienda.domain.PGApp;
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
		c.setDescription((String) cm.get("description"));
		c.setFrom(LocalDate.parse((String) cm.get("dateFrom")));
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
		c.setTitle((String) cm.get("name"));
		c.setTo(LocalDate.parse((String) cm.get("dateTo")));
		return c;
	}

	/**
	 * @param object
	 * @param string
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String extractDetails(Object details, String type) {
		if (details == null) return null;
		List<Object> list = (List) details;
		for (Object detail : list) {
			Map<String, Object> map = (Map<String, Object>) detail;
			if (type.equals(map.get("type"))) {
				return (String) map.get("content");
			}
		}
		return null;
	}
}
