package it.smartcommunitylab.pgazienda.web.rest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.pgazienda.service.H3Service;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;

/**
 * REST controller for H3 API endpoints.
 * Exposes endpoints to retrieve geospatial data from the H3 service.
 */
@RestController
@RequestMapping("/api/h3")
public class H3Resource {

    private static final Logger logger = LoggerFactory.getLogger(H3Resource.class);

    @Autowired
    private H3Service h3Service;

    /**
     * Get average duration data for a specific H3 cell.
     * 
     * @param campaignId the campaign identifier
     * @param h3Cell the H3 cell identifier
     * @param h3Res the H3 resolution level
     * @param timeSlot the time slot (optional)
     * @param groupId the group identifier (optional)
     * @param isDeparture whether to filter by departure
     * @return GeoJSON response with average duration data
     */
    @GetMapping("/avg-duration")
    public ResponseEntity<Map<String, Object>> getAvgDuration(
            @RequestParam String campaignId,
            @RequestParam String h3Cell,
            @RequestParam(required = false, defaultValue = "8") int h3Res,
            @RequestParam(required = false) String timeSlot,
            @RequestParam(required = false) String groupId,
            @RequestParam(required = false, defaultValue = "false") boolean isDeparture) {
        
        try {
            logger.info("Fetching average duration data for campaign: {}, cell: {}", 
                campaignId, h3Cell);
            
            Map<String, Object> responseMap = h3Service.getAvgDuration(campaignId, h3Cell, h3Res, timeSlot, groupId, isDeparture);
            return ResponseEntity.ok(responseMap);
            
        } catch (InconsistentDataException e) {
            logger.error("Error retrieving average duration data", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error retrieving average duration data", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get trip data for a specific territory and campaign.
     * 
     * @param campaignId the campaign identifier
     * @param h3Res the H3 resolution level
     * @param timeSlot the time slot (optional)
     * @param groupId the group identifier (optional)
     * @param mode the transport mode (optional)
     * @return GeoJSON response with trip data
     */
    @GetMapping("/trips")
    public ResponseEntity<Map<String, Object>> getTrips(
            @RequestParam String campaignId,
            @RequestParam(required = false, defaultValue = "8") int h3Res,
            @RequestParam(required = false) String timeSlot,
            @RequestParam(required = false) String groupId,
            @RequestParam(required = false) String mode) {
        
        try {
            logger.info("Fetching trips data forcampaign: {}", campaignId);
            
            Map<String, Object> responseMap = h3Service.getTrips(campaignId, h3Res, timeSlot, groupId, mode);
            return ResponseEntity.ok(responseMap);
            
        } catch (InconsistentDataException e) {
            logger.error("Error retrieving trips data", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error retrieving trips data", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get user departure data for a specific H3 cell.
     * 
     * @param campaignId the campaign identifier
     * @param h3Cell the H3 cell identifier
     * @param h3Res the H3 resolution level
     * @param timeSlot the time slot (optional)
     * @param groupId the group identifier (optional)
     * @param isDeparture whether to filter by departure
     * @return GeoJSON response with departure data
     */
    @GetMapping("/user-departure")
    public ResponseEntity<Map<String, Object>> getUserDeparture(
            @RequestParam String campaignId,
            @RequestParam String h3Cell,
            @RequestParam(required = false, defaultValue = "8") int h3Res,
            @RequestParam(required = false) String timeSlot,
            @RequestParam(required = false) String groupId,
            @RequestParam(required = false, defaultValue = "false") boolean isDeparture) {
        
        try {
            logger.info("Fetching user departure data for campaign: {}, cell: {}", 
                campaignId, h3Cell);
            
            Map<String, Object> responseMap = h3Service.getUserDeparture(campaignId, h3Cell, h3Res, timeSlot, groupId, isDeparture);
            return ResponseEntity.ok(responseMap);
            
        } catch (InconsistentDataException e) {
            logger.error("Error retrieving user departure data", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error retrieving user departure data", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
