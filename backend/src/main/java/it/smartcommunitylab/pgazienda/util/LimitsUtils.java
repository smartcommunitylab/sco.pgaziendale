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

import java.util.List;
import it.smartcommunitylab.pgazienda.domain.Constants;
import it.smartcommunitylab.pgazienda.domain.Campaign.Limit;

/**
 * @author raman
 *
 */
public class LimitsUtils {

	public static double applyLimits(Double score, String span, List<Limit> limits) {
		if (limits == null || limits.size() == 0) return score;
				
		Double res = score;
		for (Limit l : limits) {
			if (l.getSpan().equals(span)) {
				Double value = score;
				if (value != null && value > l.getValue()) {
					res = l.getValue();
				}
			}
		}
		
		return res;
	}

	/**
	 * @param dayDist
	 * @param currMonthAgg
	 * @param totalAgg
	 * @param campaign
	 * @return
	 */
	public static double applyLimits(Double v, List<Double> currWeekAgg, List<Double> currMonthAgg, List<Double> totalAgg, List<Limit> limits) {
		double dayLimited = applyLimits(v, Constants.AGG_DAY, limits);

		double week = 0d;
		if (currWeekAgg == null || currWeekAgg.isEmpty()) {
			week = dayLimited;
		}
		else {
			week = currWeekAgg.get(0);
			week = mergeScore(week, dayLimited);
		}

		double month = 0d;
		if (currMonthAgg == null || currMonthAgg.isEmpty()) {
			month = dayLimited;
		}
		else {
			month = currMonthAgg.get(0);
			month = mergeScore(month, dayLimited);
		}

		double total = dayLimited;
		if (totalAgg != null && !totalAgg.isEmpty()) {
			for (Double ds : totalAgg) {
				total = mergeScore(total, ds);
			}
		}

		for (Limit l : limits) {
			if (l.getSpan().equals(Constants.AGG_WEEK)) {
				Double value = week;
				Double dayValue = dayLimited;
				Double newValue = Math.min(dayValue, l.getValue() - value + dayValue);
				dayLimited = newValue;
			}
		}
		for (Limit l : limits) {
			if (l.getSpan().equals(Constants.AGG_MONTH)) {
				Double value = month;
				Double dayValue = dayLimited;
				Double newValue = Math.min(dayValue, l.getValue() - value + dayValue);
				dayLimited = newValue;
			}
		}
		for (Limit l : limits) {
			if (l.getSpan().equals(Constants.AGG_TOTAL)) {
				Double value = total;
				Double dayValue = dayLimited;
				Double newValue = Math.min(dayValue, l.getValue() - value + dayValue);
				dayLimited = newValue;
			}
		}
		
		return dayLimited;
	}

	private static double mergeScore(Double s, Double r) {
		return (s == null ? 0d : s) + (r == null ? 0d : r);
	}
}
