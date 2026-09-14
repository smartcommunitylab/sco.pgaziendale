package it.smartcommunitylab.pgazienda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import it.smartcommunitylab.pgazienda.domain.DayStat;
import it.smartcommunitylab.pgazienda.domain.StatTrack;
import it.smartcommunitylab.pgazienda.domain.TrackingData;
import it.smartcommunitylab.pgazienda.repository.DayStatRepository;
import it.smartcommunitylab.pgazienda.repository.StatTrackRepository;

public class StatTrackAggregationProcedureTest {

	@Test
	public void rebuildStatTrackCollectionReadsAllDayStatsWithoutDeletingExistingStatTracks() {
		DayStatRepository dayStatRepository = mock(DayStatRepository.class);
		StatTrackRepository statTrackRepository = mock(StatTrackRepository.class);
		StatTrackAggregationProcedure procedure = createProcedure(dayStatRepository, statTrackRepository);
		when(dayStatRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(createDayStat())));

		long count = procedure.rebuildStatTrackCollection(null);

		assertEquals(1L, count);
		ArgumentCaptor<Pageable> pageableCaptor = forClass(Pageable.class);
		verify(dayStatRepository).findAll(pageableCaptor.capture());
		assertDayStatSort(pageableCaptor.getValue().getSort());
		verify(dayStatRepository, never()).findByCampaign(any(String.class), any(Pageable.class));
		verify(statTrackRepository, never()).deleteAll();
		verify(statTrackRepository).saveAll(any(Iterable.class));
	}

	@Test
	public void rebuildStatTrackCollectionReadsOnlyCampaignDayStatsWhenCampaignIsProvided() {
		DayStatRepository dayStatRepository = mock(DayStatRepository.class);
		StatTrackRepository statTrackRepository = mock(StatTrackRepository.class);
		StatTrackAggregationProcedure procedure = createProcedure(dayStatRepository, statTrackRepository);
		when(dayStatRepository.findByCampaign(eq("campaign"), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(createDayStat())));

		long count = procedure.rebuildStatTrackCollection("campaign");

		assertEquals(1L, count);
		verify(dayStatRepository, never()).findAll(any(Pageable.class));
		ArgumentCaptor<Pageable> pageableCaptor = forClass(Pageable.class);
		verify(dayStatRepository).findByCampaign(eq("campaign"), pageableCaptor.capture());
		assertDayStatSort(pageableCaptor.getValue().getSort());
		verify(statTrackRepository, never()).deleteAll();
		verify(statTrackRepository).saveAll(any(Iterable.class));
	}

	@Test
	public void toStatTrackMapsAggregationProjection() {
		DayStat dayStat = new DayStat();
		dayStat.setCampaign("campaign");
		dayStat.setPlayerId("player");
		dayStat.setCompany("company");
		dayStat.setEmployeeCode("employee");
		dayStat.setDate("2026-09-14");
		dayStat.setYear("2026");
		dayStat.setMonth("2026-09");
		dayStat.setWeek("2026-38");
		dayStat.setDayOfWeek("1");

		TrackingData track = new TrackingData();
		track.setHour("08");
		track.setTrackId("track");
		track.setMultimodalId("multi");
		track.setStartedAt("2026-09-14T08:00:00Z");
		track.setMode("bike");
		track.setDistance(12.5d);
		track.setDuration(900L);
		track.setCo2(1.2d);
		track.setScore(10d);
		track.setLimitedScore(8d);
		track.setLocationId("location");
		track.setWayBack(true);

		StatTrack statTrack = new StatTrackAggregationProcedure().toStatTrack(dayStat, track);

		assertEquals("campaign", statTrack.getCampaign());
		assertEquals("player", statTrack.getPlayerId());
		assertEquals("company", statTrack.getCompany());
		assertEquals("employee", statTrack.getEmployeeCode());
		assertEquals("2026-09-14", statTrack.getDate());
		assertEquals("2026", statTrack.getYear());
		assertEquals("2026-09", statTrack.getMonth());
		assertEquals("2026-38", statTrack.getWeek());
		assertEquals("1", statTrack.getDayOfWeek());
		assertEquals("08", statTrack.getHour());
		assertEquals("track", statTrack.getTrackId());
		assertEquals("multi", statTrack.getMultimodalId());
		assertEquals("2026-09-14T08:00:00Z", statTrack.getStartedAt());
		assertEquals("bike", statTrack.getMode());
		assertEquals(12.5d, statTrack.getDistance());
		assertEquals(900L, statTrack.getDuration());
		assertEquals(1.2d, statTrack.getCo2());
		assertEquals(10d, statTrack.getScore());
		assertEquals(8d, statTrack.getLimitedScore());
		assertEquals("company__employee", statTrack.getEmployeeKey());
		assertEquals("location", statTrack.getLocation());
		assertEquals("company__location", statTrack.getLocationKey());
		assertTrue(statTrack.isWayBack());
	}

	private void assertDayStatSort(Sort sort) {
		assertEquals(Sort.Direction.ASC, sort.getOrderFor("campaign").getDirection());
		assertEquals(Sort.Direction.ASC, sort.getOrderFor("playerId").getDirection());
		assertEquals(Sort.Direction.ASC, sort.getOrderFor("date").getDirection());
	}

	private StatTrackAggregationProcedure createProcedure(DayStatRepository dayStatRepository, StatTrackRepository statTrackRepository) {
		StatTrackAggregationProcedure procedure = new StatTrackAggregationProcedure();
		ReflectionTestUtils.setField(procedure, "dayStatRepository", dayStatRepository);
		ReflectionTestUtils.setField(procedure, "statTrackRepository", statTrackRepository);
		return procedure;
	}

	private DayStat createDayStat() {
		DayStat dayStat = new DayStat();
		dayStat.setCampaign("campaign");
		dayStat.setCompany("company");
		dayStat.setEmployeeCode("employee");
		dayStat.setTracks(Collections.singletonList(createTrack()));
		return dayStat;
	}

	private TrackingData createTrack() {
		TrackingData track = new TrackingData();
		track.setTrackId("track");
		return track;
	}
}