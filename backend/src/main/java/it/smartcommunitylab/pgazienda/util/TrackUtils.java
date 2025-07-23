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

package it.smartcommunitylab.pgazienda.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import it.smartcommunitylab.pgazienda.domain.Shape;
import it.smartcommunitylab.pgazienda.dto.TrackDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO.TrackLegDTO;
import it.smartcommunitylab.pgazienda.dto.TrackDTO.TrackPointDTO;

/**
 * @author raman
 *
 */
public class TrackUtils {

	private final static int EARTH_RADIUS = 6371; // Earth radius in km.

	/**
	 * @param ti
	 * @param locations
	 * @return
	 */
	public static boolean matchLocations(TrackLegDTO ti, List<Shape> areas) {
		Object[] arr = ti.getPoints().toArray();
		return matchPoints(areas, arr);
	}

	public static int matchLocations(TrackDTO track, List<Shape> areas, Boolean useMulti, Shape requiredArea) {
		TrackLegDTO startLeg = track.getLegs().get(0);
		startLeg.getPoints().sort((a,b) -> {
			return a.getRecorded_at().compareTo(b.getRecorded_at()); 
		});
		TrackLegDTO endLeg = track.getLegs().get(track.getLegs().size() - 1);
		endLeg.getPoints().sort((a,b) -> {
			return a.getRecorded_at().compareTo(b.getRecorded_at()); 
		});

		List<TrackPointDTO> points = new LinkedList<>();
		if (startLeg.getPoints().size() > 5) points.addAll(startLeg.getPoints().subList(0, 5));
		else points.addAll(startLeg.getPoints());
		boolean matchStart = matchPoints(areas, points.toArray());

		List<TrackPointDTO> points2 = new LinkedList<>();
		if (endLeg.getPoints().size() > 5) points2.addAll(endLeg.getPoints().subList(endLeg.getPoints().size() - 5, endLeg.getPoints().size()));
		else points2.addAll(endLeg.getPoints());
		boolean matchEnd = matchPoints(areas, points2.toArray());

		// first or last should match required area if specified
		boolean matchRequired = requiredArea == null 
		? true 
		: matchPoints(Collections.singletonList(requiredArea), points.toArray()) || matchPoints(Collections.singletonList(requiredArea), points2.toArray());

		if (!matchRequired) return -1;

		// if useMulti should start and end in one of the locations
		if (!Boolean.TRUE.equals(useMulti)) {
			if (matchStart) return 0;
			if (matchEnd) return track.getLegs().size() - 1;
			return -1;
		} else {
			if (matchStart && matchEnd) return 0;
			return -1;
		}
		

	}
	
	private static boolean matchPoints(List<Shape> areas, Object[] arr) {
		Arrays.sort(arr, (a,b) -> {
			return ((TrackPointDTO)a).getRecorded_at().compareTo(((TrackPointDTO)b).getRecorded_at()); 
		});
		
		if (inAreas(areas, (TrackPointDTO)arr[0])) return true;
		if (inAreas(areas, (TrackPointDTO)arr[arr.length-1])) return true;
		if (arr.length > 3) {
			if (inAreas(areas, (TrackPointDTO)arr[1])) return true;
			if (inAreas(areas, (TrackPointDTO)arr[arr.length-2])) return true;
		}
		if (arr.length > 5) {
			if (inAreas(areas, (TrackPointDTO)arr[2])) return true;
			if (inAreas(areas, (TrackPointDTO)arr[arr.length-3])) return true;
		}

		return false;
	}
	
	public static boolean inAreas(List<Shape> shapes, TrackPointDTO point) {
		if (shapes != null) {
			for (Shape shape : shapes) {
				if (shape.inside(point.getLatitude(), point.getLongitude())) {
					return true;
				}
			}
		}
		return false;
	}

	public static double harvesineDistance(double lat1, double lon1, double lat2, double lon2) {
		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);

		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;

		double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return EARTH_RADIUS * c;
	}
	
	public static boolean checkWayBack(TrackLegDTO leg, Shape area) {
		if(leg.getPoints().size() < 10) return false;
		List<Shape> areas = Collections.singletonList(area);
		leg.getPoints().sort((a,b) -> {
			return a.getRecorded_at().compareTo(b.getRecorded_at()); 
		});
		List<TrackPointDTO> endPoints = new LinkedList<>();
		endPoints.addAll(leg.getPoints().subList(leg.getPoints().size() - 5, leg.getPoints().size()));
		return matchPoints(areas, endPoints.toArray());
	}
}
