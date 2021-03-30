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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.smartcommunitylab.pgazienda.domain.Campaign;
import it.smartcommunitylab.pgazienda.domain.Campaign.Limit;
import it.smartcommunitylab.pgazienda.domain.Constants;
import it.smartcommunitylab.pgazienda.domain.DayStat;
import it.smartcommunitylab.pgazienda.util.LimitsUtils;

/**
 * @author raman
 *
 */
public class LimitUtilsTest {

	private Campaign campaign;
	
	private static DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
	
	@BeforeEach
	public void prepareData() {
		campaign = new Campaign();
		campaign.getLimits().add(new Limit(Constants.AGG_MONTH, Constants.MEAN.bike.toString(), 250d));
		campaign.getLimits().add(new Limit(Constants.AGG_DAY, Constants.MEAN.bike.toString(), 20d));
	}
	
	@Test
	public void testTotalAgg() {
		DayStat ds = new DayStat();
		ds.getDistances().setBike(300d);
		LimitsUtils.applyLimits(Collections.singletonList(ds), Constants.AGG_TOTAL, campaign);
		assertEquals(300d, ds.getDistances().getBike());
		
		campaign.getLimits().add(new Limit(Constants.AGG_TOTAL, Constants.MEAN.bike.toString(), 250d));
		LimitsUtils.applyLimits(Collections.singletonList(ds), Constants.AGG_TOTAL, campaign);
		assertEquals(250d, ds.getDistances().getBike());
	}
	
	@Test
	public void testMonthAgg() {
		
		List<DayStat> stats = new LinkedList<>();
		LocalDate date = LocalDate.parse("2020-01-01");
		for (int i = 0; i < 10; i++) {
			DayStat ds = new DayStat();
			ds.getDistances().setBike(200d + i*10);
			ds.setMonth(date.format(monthFormatter));
			date = date.plusMonths(1);
			stats.add(ds);
		}
		LimitsUtils.applyLimits(stats, Constants.AGG_MONTH, campaign);
		for (int i = 0; i < 10; i++) {
			assertEquals(i < 6 ? 200d + i* 10 : 250d, stats.get(i).getDistances().getBike());
		}
		
	}
	
	@Test
	public void testMonthAggWithTotal() {
		
		campaign.getLimits().add(new Limit(Constants.AGG_TOTAL, Constants.MEAN.bike.toString(), 520d));

		List<DayStat> stats = new LinkedList<>();
		LocalDate date = LocalDate.parse("2020-01-01");
		for (int i = 0; i < 10; i++) {
			DayStat ds = new DayStat();
			ds.getDistances().setBike(200d + i*10);
			ds.setMonth(date.format(monthFormatter));
			stats.add(ds);
			date = date.plusMonths(1);
		}
		LimitsUtils.applyLimits(stats, Constants.AGG_MONTH, campaign);
		
		assertEquals(200d, stats.get(0).getDistances().getBike());
		assertEquals(210d, stats.get(1).getDistances().getBike());
		assertEquals(110d, stats.get(2).getDistances().getBike());
		for (int i = 3; i < 10; i++) {
			assertEquals(0d, stats.get(i).getDistances().getBike());
		}
		
	}
	
	@Test
	public void testDayAgg() {
		
		List<DayStat> stats = new LinkedList<>();
		LocalDate date = LocalDate.parse("2020-01-01");
		for (int i = 0; i < 45; i++) {
			DayStat ds = new DayStat();
			ds.getDistances().setBike((double)i);
			ds.setMonth(date.format(monthFormatter));
			ds.setDate(date.toString());
			date = date.plusDays(1);
			stats.add(ds);
		}
		LimitsUtils.applyLimits(stats, Constants.AGG_DAY, campaign);
		
		int sum = 0;
		for (int i = 0; i < 45; i++) {
			sum += i;
			System.err.println(stats.get(i).getDate() +" - " + stats.get(i).getDistances().getBike() +"  / " + sum);
		}
		
	}
	
}
