package it.smartcommunitylab.pgazienda.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;

@Service
public class H3Service {
	private static final Logger logger = LoggerFactory.getLogger(H3Service.class);

    public enum H3API {
        AVG_DURATION("avg-duration"),
        TRIP("trip"),
        DEPARTURE("departure");

        private final String value;

        H3API(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

	@Autowired
	private RestTemplate restTemplate;

    @Value("${app.h3.minTracks:15}")
    private int defaultMintracks;

	@Value("${app.h3.endpoint:}")
	private String h3Endpoint;
    
    private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Retrieves GeoJSON data from an external API and converts it to a Map.
	 * 
	 * @param apiUrl the full API URL to call
	 * @return the GeoJSON data as a Map<String, Object>
	 * @throws InconsistentDataException if the API call fails or returns an error
	 */
	private Map<String, Object> getGeoJsonData(String apiUrl) throws InconsistentDataException {
		if (h3Endpoint == null || h3Endpoint.isEmpty()) {
			throw new InconsistentDataException("H3 endpoint not configured", "");
		}

		try {
			logger.debug("Calling H3 API: {}", apiUrl);
			
			String geoJson = restTemplate.getForObject(apiUrl, String.class);
			
			if (geoJson == null || geoJson.isEmpty()) {
				throw new InconsistentDataException("Empty response from H3 API", "");
			}
			
			Map<String, Object> responseMap = objectMapper.readValue(geoJson, new TypeReference<Map<String,Object>>(){});
			return responseMap;
			
		} catch (Exception e) {
			logger.error("Error retrieving GeoJSON data: {}", e.getMessage(), e);
			throw new InconsistentDataException(
				String.format("Error retrieving GeoJSON from H3 API: %s", e.getMessage()), 
				apiUrl);
		}
	}

	/**
	 * Builds the URL for the H3 API endpoint.
	 * 
	 * @param territoryId the territory identifier
	 * @param campaignId the campaign identifier
	 * @return the complete URL
	 */
	private String buildUrl(
            H3API api,
            String territoryId, 
            String campaignId,
            String h3Cell,
            int h3Res,
            String timeSlot,
            String groupId,
            boolean isDeparture,
            int minTracks,
            String mode) {
            String baseUrl = h3Endpoint.endsWith("/") ? h3Endpoint : h3Endpoint + "/";
        String url = baseUrl + api.getValue() + "?territory_id=" + territoryId + "&campaign_id=" + campaignId;
        url += "&target_resolution=" + h3Res;
        url += "&min_tracks=" + minTracks;
        if (groupId != null) {
            url += "&group_id=" + groupId;
        }
        if (timeSlot != null) {
            url += "&time_slot=" + timeSlot;
        } 
        switch (api) {
            case AVG_DURATION:
            case DEPARTURE:
                url += "&is_departure=" + isDeparture;
                url += "&h3_cell=" + h3Cell;
                break;
            case TRIP:
                if (mode != null) {
                    url += "&mode=" + mode;
                }
                break;
        }
		return url;
	}

    public Map<String, Object> getAvgDuration(String territoryId, String campaignId,
            String h3Cell, int h3Res, String timeSlot, String groupId, boolean isDeparture) throws InconsistentDataException {
        // TODO check user role
        String url = buildUrl(H3API.AVG_DURATION, territoryId, campaignId, h3Cell, h3Res, timeSlot, groupId, isDeparture, 
            defaultMintracks, null);
        return getGeoJsonData(url);
    }

    public Map<String, Object> getTrips(String territoryId, String campaignId, int h3Res, String timeSlot, String groupId, String mode) throws InconsistentDataException {
        // TODO check user role
        String url = buildUrl(H3API.TRIP, territoryId, campaignId, null, h3Res, timeSlot, groupId, false,
            defaultMintracks, mode);
        return getGeoJsonData(url);
    }

    public Map<String, Object> getUserDeparture(String territoryId, String campaignId,
            String h3Cell, int h3Res, String timeSlot, String groupId, boolean isDeparture) throws InconsistentDataException {
        // TODO check user role
        String url = buildUrl(H3API.DEPARTURE, territoryId, campaignId, h3Cell, h3Res, timeSlot, groupId, isDeparture, 
            defaultMintracks, null);
        return getGeoJsonData(url);
    }

}
