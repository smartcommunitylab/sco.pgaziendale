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
	
	@NotEmpty
	private String application;

	@NotNull
	private LocalDate from;
	@NotNull
	private LocalDate to;
	private Boolean active;
	private List<String> means = new LinkedList<>();
	
	private String rules, privacy;
	
	private List<Limit> limits; 
	
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
	 * @return the application
	 */
	public String getApplication() {
		return application;
	}
	/**
	 * @param application the application to set
	 */
	public void setApplication(String application) {
		this.application = application;
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
	
	/**
	 * @return the limits
	 */
	public List<Limit> getLimits() {
		if (limits == null) {
			limits = new LinkedList<>();
			limits.add(new Limit(Constants.AGG_DAY, Constants.MEAN.bike.toString(), 20d)); 
			limits.add(new Limit(Constants.AGG_MONTH, Constants.MEAN.bike.toString(), 250d));
		}
		return limits;
	}
	/**
	 * @param limits the limits to set
	 */
	public void setLimits(List<Limit> limits) {
		this.limits = limits;
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
	
}
