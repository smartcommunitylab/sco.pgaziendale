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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Constants;
import it.smartcommunitylab.pgazienda.domain.DayStat;
import it.smartcommunitylab.pgazienda.domain.Campaign.Limit;
import it.smartcommunitylab.pgazienda.domain.Constants.MEAN;
import it.smartcommunitylab.pgazienda.domain.DayStat.Distances;

/**
 * @author raman
 *
 */
public class LimitsUtils {

	/**
	 * Clean data applying daily, monthly, and global Campaign thresholds
	 * @param res
	 * @param campaign
	 */
	public static void applyLimits(List<DayStat> res, String groupBy, Campaign campaign) {
		res.sort((a,b) -> a.getDate() != null ? a.getDate().compareTo(b.getDate()) : a.getMonth()!= null ? a.getMonth().compareTo(b.getMonth()) : 0);
		
		// init values
		Map<String, Map<String, Double>> limits = new HashMap<>();
		Map<String, Map<String, Double>> counters = new HashMap<>();

		for (String agg : Constants.AGG_ARRAY) {
			Map<String, Double> aggLimits = new HashMap<>();
			Map<String, Double> aggCounters = new HashMap<>();
			limits.put(agg, aggLimits);
			counters.put(agg, aggCounters);
			for (MEAN m: MEAN.values()) {
				aggCounters.put(m.toString(), 0d);
				aggLimits.put(m.toString(), Double.MAX_VALUE);
			}
		}
		for (Limit l: campaign.getLimits()) {
			limits.get(l.getSpan()).put(l.getMean(), l.getValue());
		}
		
		// only month changing
		Map<String, Double> currentMonthCounters = counters.get(Constants.AGG_MONTH);
		String currentMonth = null;
		
		for (DayStat ds: res) {
			if (ds.getDistances() == null) ds.setDistances(new Distances());
			if (ds.getMonth() != null && !ds.getMonth().equals(currentMonth)) {
				// reset counters
				for (MEAN m: MEAN.values()) currentMonthCounters.put(m.toString(), 0d);
				currentMonth = ds.getMonth();
			}
			if (ds.getDate() != null) {
				updateValues(ds.getDistances(), limits.get(Constants.AGG_DAY), currentMonthCounters, limits.get(Constants.AGG_MONTH), counters.get(Constants.AGG_TOTAL), limits.get(Constants.AGG_TOTAL));
			} else if (ds.getMonth() != null) {
				updateValues(ds.getDistances(), null, null, limits.get(Constants.AGG_MONTH), counters.get(Constants.AGG_TOTAL), limits.get(Constants.AGG_TOTAL));
			} else {
				updateValues(ds.getDistances(), null, null, null, counters.get(Constants.AGG_TOTAL), limits.get(Constants.AGG_TOTAL));
			}
		}
	}

	public static Distances applyLimits(Distances distances, String span, Campaign campaign) {
		if (campaign.getLimits() == null || campaign.getLimits().size() == 0) return distances;
		
		Distances res = Distances.copy(distances);
		
		for (Limit l : campaign.getLimits()) {
			if (l.getSpan().equals(span)) {
				Double value = distances.meanValue(MEAN.valueOf(l.getMean()));
				if (value != null && value > l.getValue()) {
					res.updateValue(MEAN.valueOf(l.getMean()), l.getValue());
				}
			}
		}
		
		return res;
	}
	
	/**
	 * 
	 * @param distances
	 * @param dayLimits
	 * @param monthCounters
	 * @param monthLimits
	 * @param totalCounters
	 * @param totalLimits
	 */
	private static void updateValues(Distances distances, Map<String, Double> dayLimits, 
			Map<String, Double> monthCounters, Map<String, Double> monthLimits, 
			Map<String, Double> totalCounters, Map<String, Double> totalLimits) 
	{
		for (MEAN m : MEAN.values()) {
			Double dist = distances.meanValue(m);
			String mean =  m.toString();
			dist = updateMean(dayLimits, monthCounters, monthLimits, totalCounters, totalLimits, dist, mean);
			distances.updateValue(m, dist);
		}
		
	}

	/**
	 * @param dayLimits
	 * @param monthCounters
	 * @param monthLimits
	 * @param totalCounters
	 * @param totalLimits
	 * @param dist
	 * @param mean
	 */
	private static Double updateMean(Map<String, Double> dayLimits, Map<String, Double> monthCounters,
			Map<String, Double> monthLimits, Map<String, Double> totalCounters, Map<String, Double> totalLimits,
			Double dist, String mean) 
	{
		if (dist == null) return null;
		
		Double totalLimit = totalLimits.getOrDefault(mean, Double.MAX_VALUE);
		Double totalCounter = totalCounters.getOrDefault(mean, 0d);
		Double monthLimit = null;
		Double monthCounter = null;
		
		if (totalCounter + dist > totalLimit) {
			dist = Math.min(totalLimit - totalCounter, dist);
		}
		
		if (monthLimits != null) {
			monthLimit = monthLimits.getOrDefault(mean, Double.MAX_VALUE);
			monthCounter = monthCounters != null ? monthCounters.getOrDefault(mean, 0d) : 0d;
			if (monthCounter + dist > monthLimit) {
				dist = Math.min(monthLimit - monthCounter, dist);
			}
		}
		if (dayLimits != null) {
			Double dayLimit = dayLimits.getOrDefault(mean, Double.MAX_VALUE);
			if (dist > dayLimit) {
				dist = dayLimit;
			} 
		}

		totalCounter += dist;
		totalCounters.put(mean, totalCounter);
		if (monthCounters != null) {
			monthCounters.put(mean, monthCounter + dist);
		}
		return dist;
	}

	/**
	 * @param dayDist
	 * @param currMonthAgg
	 * @param totalAgg
	 * @param campaign
	 * @return
	 */
	public static Distances applyLimits(Distances dayDist, List<DayStat> currMonthAgg, List<DayStat> totalAgg, Campaign campaign) {
		Distances dayLimited = applyLimits(dayDist, Constants.AGG_DAY, campaign);
		Distances month = null;
		if (currMonthAgg == null || currMonthAgg.isEmpty()) {
			month = Distances.copy(dayLimited);
		}
		else {
			month = currMonthAgg.get(0).getDistances();
			month.mergeDistances(dayLimited);
		}
		Distances total = Distances.copy(dayLimited);
		if (totalAgg != null && !totalAgg.isEmpty()) {
			totalAgg.forEach(ds -> total.mergeDistances(ds.getDistances()));
		}

		for (Limit l : campaign.getLimits()) {
			if (l.getSpan().equals(Constants.AGG_MONTH)) {
				MEAN mean = MEAN.valueOf(l.getMean());
				Double meanValue = month.meanValue(mean);
				Double dayValue = dayLimited.meanValue(mean);
				Double newValue = Math.min(dayValue, l.getValue() - meanValue + dayValue);
				dayLimited.updateValue(mean, newValue);
			}
		}
		for (Limit l : campaign.getLimits()) {
			if (l.getSpan().equals(Constants.AGG_TOTAL)) {
				MEAN mean = MEAN.valueOf(l.getMean());
				Double meanValue = total.meanValue(mean);
				Double dayValue = dayLimited.meanValue(mean);
				Double newValue = Math.min(dayValue, l.getValue() - meanValue + dayValue);
				dayLimited.updateValue(mean, newValue);
			}
		}
		
		return dayLimited;
	}
}
