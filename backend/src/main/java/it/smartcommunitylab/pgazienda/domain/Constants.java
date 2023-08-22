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

import java.util.Locale;

/**
 * @author raman
 *
 */
public class Constants {

	public static final String DEFAULT_TIME_ZONE = "Europe/Rome";
	public static final Locale DEFAULT_LOCALE = Locale.ITALY;

	public static final String AGG_DAY = "day";
	public static final String AGG_WEEK = "week";
	public static final String AGG_MONTH = "month";
	public static final String AGG_TOTAL = "total";	
	
	public static final String[] AGG_ARRAY = new String[] {AGG_DAY, AGG_MONTH, AGG_WEEK, AGG_TOTAL};
	public static final String METRIC_DISTANCE = "distance";
	public static final String METRIC_DURATION = "time";
	public static final String METRIC_CO2 = "co2";
	public static final String METRIC_TRACK = "tracks";
	
	public enum MEAN {bike, car, walk, bus, train, boat};

	public enum GROUP_BY_TIME {day, week, month, total};
	public enum GROUP_BY_DATA {company, location, employee, total};
	public enum STAT_FIELD  {score, limitedScore, trackCount, limitedTrackCount, co2, meanScore, limitedMeanScore, meanDistance, meanDuration, meanCo2, meanTracks }
}
