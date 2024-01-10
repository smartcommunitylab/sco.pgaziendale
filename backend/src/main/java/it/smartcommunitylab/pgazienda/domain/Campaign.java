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

package it.smartcommunitylab.pgazienda.domain;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;

import it.smartcommunitylab.pgazienda.domain.Constants.MEAN;

/**
 * @author raman
 *
 */
public class Campaign {

	@Id
	@NotEmpty
	private String id;
	@NotEmpty
	private String title;
	private String description;
	private String logo;
	private String territoryId;


	@NotNull
	private LocalDate from;
	@NotNull
	private LocalDate to;
	private Boolean active;
	private List<String> means = new LinkedList<>();
	
	private String rules, privacy;
	
	// private List<Limit> limits; 
	private List<Limit> trackLimits = new LinkedList<>(); 
	private List<Limit> scoreLimits;

	private VirtualScore virtualScore;

	private Boolean useMultiLocation;
	
	private Boolean useEmployeeLocation;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the from
	 */
	public LocalDate getFrom() {
		return from;
	}
	/**
	 * @param from the from to set
	 */
	public void setFrom(LocalDate from) {
		this.from = from;
	}
	/**
	 * @return the to
	 */
	public LocalDate getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(LocalDate to) {
		this.to = to;
	}
	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active == null ? false : active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}
	/**
	 * @return the means
	 */
	public List<String> getMeans() {
		return means;
	}
	/**
	 * @param means the means to set
	 */
	public void setMeans(List<String> means) {
		this.means = means;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the logo
	 */
	public String getLogo() {
		return logo;
	}
	/**
	 * @param logo the logo to set
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}
	/**
	 * @return the rules
	 */
	public String getRules() {
		return rules;
	}
	/**
	 * @param rules the rules to set
	 */
	public void setRules(String rules) {
		this.rules = rules;
	}
	/**
	 * @return the privacy
	 */
	public String getPrivacy() {
		return privacy;
	}
	/**
	 * @param privacy the privacy to set
	 */
	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}

	public String getTerritoryId() {
		return territoryId;
	}
	public void setTerritoryId(String territoryId) {
		this.territoryId = territoryId;
	}
	
	// /**
	//  * @return the limits
	//  */
	// public List<Limit> getLimits() {
	// 	if (limits == null) {
	// 		limits = new LinkedList<>();
	// 		limits.add(new Limit(Constants.AGG_DAY, Constants.MEAN.bike.toString(), 20000d)); 
	// 		limits.add(new Limit(Constants.AGG_MONTH, Constants.MEAN.bike.toString(), 250000d));
	// 	}
	// 	return limits;
	// }
	// /**
	//  * @param limits the limits to set
	//  */
	// public void setLimits(List<Limit> limits) {
	// 	this.limits = limits;
	// }
	

	public List<Limit> getTrackLimits() {
		return trackLimits;
	}
	public void setTrackLimits(List<Limit> trackLimits) {
		this.trackLimits = trackLimits;
	}
	public List<Limit> getScoreLimits() {
		if (scoreLimits == null) {
			scoreLimits = new LinkedList<>();
			scoreLimits.add(new Limit(Constants.AGG_DAY, null, 20d)); 
			scoreLimits.add(new Limit(Constants.AGG_MONTH, null, 250d));
		}

		return scoreLimits;
	}
	public void setScoreLimits(List<Limit> scoreLimits) {
		this.scoreLimits = scoreLimits;
	}


	public VirtualScore getVirtualScore() {
		if (virtualScore == null) {
			virtualScore = new VirtualScore();
			virtualScore.setBike(new VirtualScoreValue());
			virtualScore.getBike().coefficient = 0.001d;
			virtualScore.getBike().metric = Constants.METRIC_DISTANCE;

		}
		return virtualScore;
	}
	public void setVirtualScore(VirtualScore virtualScore) {
		this.virtualScore = virtualScore;
	}

	public Boolean getUseEmployeeLocation() {
		return useEmployeeLocation;
	}
	public void setUseEmployeeLocation(Boolean useEmployeeLocation) {
		this.useEmployeeLocation = useEmployeeLocation;
	}
	public Boolean getUseMultiLocation() {
		return useMultiLocation;
	}
	public void setUseMultiLocation(Boolean useMultiLocation) {
		this.useMultiLocation = useMultiLocation;
	}

	

	public static class Limit {
		private String span;
		private String mean;
		private Double value;
		
		
		/**
		 * 
		 */
		public Limit() {
			super();
		}
		/**
		 * @param span
		 * @param mean
		 * @param value
		 */
		public Limit(String span, String mean, Double value) {
			super();
			this.span = span;
			this.mean = mean;
			this.value = value;
		}
		/**
		 * @return the span
		 */
		public String getSpan() {
			return span;
		}
		/**
		 * @param span the span to set
		 */
		public void setSpan(String span) {
			this.span = span;
		}
		/**
		 * @return the mean
		 */
		public String getMean() {
			return mean;
		}
		/**
		 * @param mean the mean to set
		 */
		public void setMean(String mean) {
			this.mean = mean;
		}
		/**
		 * @return the value
		 */
		public Double getValue() {
			return value;
		}
		/**
		 * @param value the value to set
		 */
		public void setValue(Double value) {
			this.value = value;
		}
		
	}

	public static class VirtualScore {
		private String label;
		private VirtualScoreValue bike, car, walk, bus, train, boat;
		private Double scoreDailyLimit, scoreWeeklyLimit, scoreMonthlyLimit;
		private Integer trackDailyLimit, trackWeeklyLimit, trackMonthlyLimit;
		
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public VirtualScoreValue getBike() {
			return bike;
		}
		public void setBike(VirtualScoreValue bike) {
			this.bike = bike;
		}
		public VirtualScoreValue getCar() {
			return car;
		}
		public void setCar(VirtualScoreValue car) {
			this.car = car;
		}
		public VirtualScoreValue getWalk() {
			return walk;
		}
		public void setWalk(VirtualScoreValue walk) {
			this.walk = walk;
		}
		public VirtualScoreValue getBus() {
			return bus;
		}
		public void setBus(VirtualScoreValue bus) {
			this.bus = bus;
		}
		public VirtualScoreValue getTrain() {
			return train;
		}
		public void setTrain(VirtualScoreValue train) {
			this.train = train;
		}
		public VirtualScoreValue getBoat() {
			return boat;
		}
		public void setBoat(VirtualScoreValue boat) {
			this.boat = boat;
		}

		public VirtualScoreValue meanValue(MEAN mean) {
			VirtualScoreValue res = null;
			switch(mean) {
			case bike: res = getBike(); break;
			case boat: res = getBoat(); break;
			case bus: res = getBus(); break;
			case car: res = getCar(); break;
			case train: res = getTrain(); break;
			case walk: res = getWalk(); break;
			}
			if (res == null) res = null;
			return res;
		}
		public Double getScoreDailyLimit() {
			return scoreDailyLimit;
		}
		public void setScoreDailyLimit(Double scoreDailyLimit) {
			this.scoreDailyLimit = scoreDailyLimit;
		}
		public Double getScoreWeeklyLimit() {
			return scoreWeeklyLimit;
		}
		public void setScoreWeeklyLimit(Double scoreWeeklyLimit) {
			this.scoreWeeklyLimit = scoreWeeklyLimit;
		}
		public Double getScoreMonthlyLimit() {
			return scoreMonthlyLimit;
		}
		public void setScoreMonthlyLimit(Double scoreMonthlyLimit) {
			this.scoreMonthlyLimit = scoreMonthlyLimit;
		}
		public Integer getTrackDailyLimit() {
			return trackDailyLimit;
		}
		public void setTrackDailyLimit(Integer trackDailyLimit) {
			this.trackDailyLimit = trackDailyLimit;
		}
		public Integer getTrackWeeklyLimit() {
			return trackWeeklyLimit;
		}
		public void setTrackWeeklyLimit(Integer trackWeeklyLimit) {
			this.trackWeeklyLimit = trackWeeklyLimit;
		}
		public Integer getTrackMonthlyLimit() {
			return trackMonthlyLimit;
		}
		public void setTrackMonthlyLimit(Integer trackMonthlyLimit) {
			this.trackMonthlyLimit = trackMonthlyLimit;
		}
        public List<Limit> trackLimits() {
			List<Limit> limits = new LinkedList<>();
			if (getTrackDailyLimit() != null && getTrackDailyLimit() > 0) {
				limits.add(new Limit(Constants.AGG_DAY, null, getTrackDailyLimit().doubleValue())); 
			}
			if (getTrackWeeklyLimit() != null && getTrackWeeklyLimit() > 0) {
				limits.add(new Limit(Constants.AGG_WEEK, null, getTrackWeeklyLimit().doubleValue())); 
			}
			if (getTrackMonthlyLimit() != null && getTrackMonthlyLimit() > 0) {
				limits.add(new Limit(Constants.AGG_MONTH, null, getTrackMonthlyLimit().doubleValue())); 
			}
            return limits;
        }
        public List<Limit> scoreLimits() {
			List<Limit> limits = new LinkedList<>();
			if (getScoreDailyLimit() != null && getScoreDailyLimit() > 0) {
				limits.add(new Limit(Constants.AGG_DAY, null, getScoreDailyLimit().doubleValue())); 
			}
			if (getScoreWeeklyLimit() != null && getScoreWeeklyLimit() > 0) {
				limits.add(new Limit(Constants.AGG_WEEK, null, getScoreWeeklyLimit().doubleValue())); 
			}
			if (getScoreMonthlyLimit() != null && getScoreMonthlyLimit() > 0) {
				limits.add(new Limit(Constants.AGG_MONTH, null, getScoreMonthlyLimit().doubleValue())); 
			}
            return limits;
        }
	}
	
	public static class VirtualScoreValue {
		private String metric;
		private Double coefficient;
		
		public VirtualScoreValue() {
		}
		public VirtualScoreValue(String metric, Double coefficient) {
			this.metric = metric;
			this.coefficient = coefficient;
		}
		public String getMetric() {
			return metric;
		}
		public void setMetric(String metric) {
			this.metric = metric;
		}
		public Double getCoefficient() {
			return coefficient;
		}
		public void setCoefficient(Double coefficient) {
			this.coefficient = coefficient;
		}

		
	}
}
