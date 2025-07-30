package it.smartcommunitylab.pgazienda.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.smartcommunitylab.pgazienda.domain.StatTrack;

public interface StatTrackRepository extends MongoRepository<StatTrack, String> {
	
	void deleteByPlayerIdAndCampaignAndCompanyAndDate(String playerId, String campaign, String company, String date);
	StatTrack findOneByPlayerIdAndCampaignAndCompanyAndTrackId(String playerId, String campaign, String company, String trackId);
}
