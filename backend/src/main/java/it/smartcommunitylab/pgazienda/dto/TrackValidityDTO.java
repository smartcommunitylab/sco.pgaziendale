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

package it.smartcommunitylab.pgazienda.dto;

import java.util.List;

/**
 * @author raman
 *
 */
public class TrackValidityDTO {

	public static String ERR_DATA = "INVALID_DATA";
	public static String ERR_NO_LOCATIONS = "NO_LOCATIONS";
	public static String ERR_NO_MATCHES = "NO_MATCHES";
	
	private boolean valid;
	private String errorCode;
	private List<TrackValidityLegDTO> legs;
	
	public static TrackValidityDTO errData() {
		return new TrackValidityDTO(ERR_DATA);
	}
	public static TrackValidityDTO errLocations() {
		return new TrackValidityDTO(ERR_NO_LOCATIONS);
	}
	public static TrackValidityDTO errMatches() {
		return new TrackValidityDTO(ERR_NO_MATCHES);
	}
	
	public TrackValidityDTO() {
		super();
	}

	public TrackValidityDTO(String errorCode) {
		super();
		this.errorCode = errorCode;
		this.valid = false;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public List<TrackValidityLegDTO> getLegs() {
		return legs;
	}

	public void setLegs(List<TrackValidityLegDTO> legs) {
		this.legs = legs;
	}

	public static class TrackValidityLegDTO {
		private String id;
		private Double distance;
		private Double validDistance;
		private String mean;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Double getDistance() {
			return distance;
		}
		public void setDistance(Double distance) {
			this.distance = distance;
		}
		public Double getValidDistance() {
			return validDistance;
		}
		public void setValidDistance(Double validDistance) {
			this.validDistance = validDistance;
		}
		public String getMean() {
			return mean;
		}
		public void setMean(String mean) {
			this.mean = mean;
		} 
	}
}
