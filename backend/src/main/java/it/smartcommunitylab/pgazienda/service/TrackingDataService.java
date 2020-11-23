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
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.User;
import it.smartcommunitylab.pgazienda.repository.CampaignRepository;
import it.smartcommunitylab.pgazienda.repository.PGAppRepository;
import it.smartcommunitylab.pgazienda.repository.UserRepository;

/**
 * @author raman
 *
 */
@Service
public class TrackingDataService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private PGAppRepository appRepo;
	@Autowired
	private CampaignRepository campaignRepo;
	@Autowired
	private UserRepository userRepo;

	
	public void synchronizeApps() {
		appRepo.findAll().forEach(a -> {
			List<Campaign> campaigns = campaignRepo.findByApplication(a.getId());
			if (campaigns.isEmpty()) return;
			List<User>  users = userRepo.findByCampaignIn(campaigns.stream().map(c -> c.getId()).collect(Collectors.toList()));// eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNzMzMkBhYWMudG4uc21hcnRjb21tdW5pdHlsYWIuaXQiLCJhdXRoIjoiUk9MRV9BUFBfVVNFUiIsImV4cCI6MTYwNTg5MDQ3NH0.RvzFFgIPlWuiTIfDdL6_nIt-j8pnwUMFmjkO3Z5I9rygZMM_JbCzl0HOy2aFdfkqbowy6WgMsF88LCw9isuxmg
			if (users.isEmpty()) return;
			List<String> playerIds = users.stream().map(u -> u.getPlayerId()).collect(Collectors.toList());
			
			TrackingDataRequestDTO request = new TrackingDataRequestDTO();
			request.setFrom(LocalDate.now().toString());
			request.setTo(request.getFrom());
			List<String> means = campaigns.stream().flatMap(c -> c.getMeans().stream()).collect(Collectors.toList());
			request.setMeans(means);
			request.setMultimodal(true);
			request.setPlayerId(playerIds);
			
			ParameterizedTypeReference<List<TrackingDataDTO>>  resp = new ParameterizedTypeReference<List<TrackingDataDTO>>(){};
			HttpEntity<TrackingDataRequestDTO> entity = new HttpEntity<>(request);
			
			String s = a.getId() + ":" + a.getPassword();
			byte[] b = Base64Utils.encode(s.getBytes());
			String es = new String(b);

			entity.getHeaders().add("Authorization", "Basic " + es);
			entity.getHeaders().add("Accept", "application/json");
			entity.getHeaders().add("Content-Type", "application/json");
			restTemplate.exchange(a.getEndpoint(), HttpMethod.POST, entity, resp);
		});
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
	
	public static class TrackingDataDTO {
		private String trackId;
		private String playerId;
		private String startedAt;
		private String mode;
		private double distance;
		
		public String getTrackId() {
			return trackId;
		}
		public void setTrackId(String trackId) {
			this.trackId = trackId;
		}
		public String getStartedAt() {
			return startedAt;
		}
		public void setStartedAt(String startedAt) {
			this.startedAt = startedAt;
		}
		public String getMode() {
			return mode;
		}
		public void setMode(String mode) {
			this.mode = mode;
		}
		public double getDistance() {
			return distance;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
		public String getPlayerId() {
			return playerId;
		}
		public void setPlayerId(String playerId) {
			this.playerId = playerId;
		}
	}
}
