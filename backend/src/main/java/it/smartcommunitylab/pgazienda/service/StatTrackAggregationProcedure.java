package it.smartcommunitylab.pgazienda.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.pgazienda.domain.DayStat;
import it.smartcommunitylab.pgazienda.domain.StatTrack;
import it.smartcommunitylab.pgazienda.domain.TrackingData;
import it.smartcommunitylab.pgazienda.repository.DayStatRepository;
import it.smartcommunitylab.pgazienda.repository.StatTrackRepository;

@Service
public class StatTrackAggregationProcedure {

	private static final int PAGE_SIZE = 500;
	private static final Sort DAY_STAT_SORT = Sort.by("campaign", "playerId", "date");

	@Autowired
	private DayStatRepository dayStatRepository;
	@Autowired
	private StatTrackRepository statTrackRepository;

	public long rebuildStatTrackCollection(String campaign) {
		long count = 0;
		int pageNumber = 0;
		Page<DayStat> page;

		do {
			page = findDayStatPage(campaign, pageNumber);
			List<StatTrack> statTracks = new ArrayList<>();

			for (DayStat dayStat : page.getContent()) {
				if (dayStat.getTracks() == null) {
					continue;
				}

				for (TrackingData track : dayStat.getTracks()) {
					statTracks.add(toStatTrack(dayStat, track));
				}
			}

			statTrackRepository.saveAll(statTracks);
			count += statTracks.size();
			pageNumber++;
		} while (page.hasNext());

		return count;
	}

	public long rebuildStatTrackCollection() {
		return rebuildStatTrackCollection(null);
	}

	private Page<DayStat> findDayStatPage(String campaign, int pageNumber) {
		PageRequest pageRequest = PageRequest.of(pageNumber, PAGE_SIZE, DAY_STAT_SORT);
		if (campaign == null || campaign.trim().isEmpty()) {
			return dayStatRepository.findAll(pageRequest);
		}

		return dayStatRepository.findByCampaign(campaign, pageRequest);
	}

	StatTrack toStatTrack(DayStat dayStat, TrackingData track) {
		StatTrack statTrack = new StatTrack();
		statTrack.setCampaign(dayStat.getCampaign());
		statTrack.setPlayerId(dayStat.getPlayerId());
		statTrack.setCompany(dayStat.getCompany());
		statTrack.setEmployeeCode(dayStat.getEmployeeCode());
		statTrack.setDate(dayStat.getDate());
		statTrack.setYear(dayStat.getYear());
		statTrack.setMonth(dayStat.getMonth());
		statTrack.setWeek(dayStat.getWeek());
		statTrack.setDayOfWeek(dayStat.getDayOfWeek());
		statTrack.setHour(track.getHour());
		statTrack.setTrackId(track.getTrackId());
		statTrack.setMultimodalId(track.getMultimodalId());
		statTrack.setStartedAt(track.getStartedAt());
		statTrack.setMode(track.getMode());
		statTrack.setDistance(track.getDistance());
		statTrack.setDuration(track.getDuration());
		statTrack.setCo2(track.getCo2() != null ? track.getCo2() : 0d);
		statTrack.setScore(track.getScore() != null ? track.getScore() : 0d);
		statTrack.setLimitedScore(track.getLimitedScore() != null ? track.getLimitedScore() : 0d);
		statTrack.setEmployeeKey(dayStat.getCompany() + StatTrack.KEY_DIV + dayStat.getEmployeeCode());
        if (track.getLocationId() != null) {
            statTrack.setLocation(track.getLocationId());
            statTrack.setLocationKey(dayStat.getCompany() + StatTrack.KEY_DIV + track.getLocationId());
        }
        statTrack.setWayBack(track.isWayBack());
		return statTrack;
	}
}