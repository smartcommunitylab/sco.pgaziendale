package it.smartcommunitylab.pgazienda.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

public class StatTrackDTO {
	private String campaign;
	private String timeGroup; 
	private String dataGroup;
	private String dataGroupName;
	private StatValueDTO stats;
	private Map<String, StatValueDTO>meanStatMap = new HashMap<>();
	
	public String getCampaign() {
		return campaign;
	}
	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}
	public String getTimeGroup() {
		return timeGroup;
	}
	public void setTimeGroup(String timeGroup) {
		this.timeGroup = timeGroup;
	}
	public String getDataGroup() {
		return dataGroup;
	}
	public void setDataGroup(String dataGroup) {
		this.dataGroup = dataGroup;
	}
	public StatValueDTO getStats() {
		return stats;
	}
	public void setStats(StatValueDTO stats) {
		this.stats = stats;
	}
	public Map<String, StatValueDTO> getMeanStatMap() {
		return meanStatMap;
	}
	public void setMeanStatMap(Map<String, StatValueDTO> meanStatMap) {
		this.meanStatMap = meanStatMap;
	}

	public String getDataGroupName() {
		return dataGroupName;
	}

	public void setDataGroupName(String dataGroupName) {
		this.dataGroupName = dataGroupName;
	}
	
	public static class Builder {
		private StatTrackDTO dto;
		
		public Builder populateKeyFields(Document doc, List<String> groupKey) {
			if(dto == null)
				dto = new StatTrackDTO();
			
			if(doc.get("_id") instanceof Document) {
				Document idMap = (Document) doc.get("_id");
				if(idMap.containsKey("campaign")) dto.setCampaign(idMap.getString("campaign"));
				if(idMap.containsKey("locationKey")) dto.setDataGroup(idMap.getString("locationKey"));
				if(idMap.containsKey("employeeKey")) dto.setDataGroup(idMap.getString("employeeKey"));
				if(idMap.containsKey("company")) dto.setDataGroup(idMap.getString("company"));
				
				if(idMap.containsKey("hour")) 
					dto.setTimeGroup(idMap.getString("hour"));
				else if(idMap.containsKey("dayOfWeek")) 
					dto.setTimeGroup(idMap.getString("dayOfWeek"));
				else if(idMap.containsKey("date")) 
					dto.setTimeGroup(idMap.getString("date"));
				else if(idMap.containsKey("week")) 
					dto.setTimeGroup(idMap.getString("week"));
				else if(idMap.containsKey("month")) 
					dto.setTimeGroup(idMap.getString("month"));
				else if(idMap.containsKey("year")) 
					dto.setTimeGroup(idMap.getString("year"));
				else dto.setTimeGroup("total");
			} else {
				dto.setCampaign(doc.getString("_id"));
				dto.setTimeGroup("total");
			}
			return this;
		}
		
		public Builder populateStatFields(Document doc) {
			if(dto == null)
				dto = new StatTrackDTO();
			
			StatValueDTO stats = new StatValueDTO(); 
			if(doc.containsKey("track")) stats.setTrack(doc.getInteger("track"));
			if(doc.containsKey("tripCount")) stats.setTripCount(doc.getInteger("tripCount"));
			if(doc.containsKey("limitedTripCount")) stats.setLimitedTripCount(doc.getInteger("limitedTripCount"));
			if(doc.containsKey("score")) stats.setScore(FieldDTO.fromValue(doc.getDouble("score")));
			if(doc.containsKey("limitedScore")) stats.setLimitedScore(FieldDTO.fromValue(doc.getDouble("limitedScore")));
			if(doc.containsKey("co2")) {
				stats.setCo2(FieldDTO.fromValue(doc.getDouble("co2")));
				if (stats.getTripCount() != null && stats.getTripCount() > 0) 
					stats.getCo2().setAvgTrip(doc.getDouble("co2") / (double) stats.getTripCount());
				if (stats.getTrack() != null && stats.getTrack() > 0) 
					stats.getCo2().setAvgTrack(doc.getDouble("co2") / (double) stats.getTrack());	
			}
			if(doc.containsKey("distance")) {
				Double dist = doc.get("distance") != null ? doc.getDouble("distance")/1000.0 : 0.0; // to km
				stats.setDistance(FieldDTO.fromValue(dist));
				if (stats.getTripCount() != null && stats.getTripCount() > 0) 
					stats.getDistance().setAvgTrip(dist / (double) stats.getTripCount());
				if (stats.getTrack() != null && stats.getTrack() > 0) 
					stats.getDistance().setAvgTrack(dist / (double) stats.getTrack());
			}
			if(doc.containsKey("duration")) {
				Double duration = doc.get("duration") != null ? (double) doc.getLong("duration")/3600.0 : 0.0; // to hours
				stats.setDuration(FieldDTO.fromValue(duration));
				if (stats.getTripCount() != null && stats.getTripCount() > 0) 
					stats.getDuration().setAvgTrip(duration / (double) stats.getTripCount());
				if (stats.getTrack() != null && stats.getTrack() > 0) 
					stats.getDuration().setAvgTrack(duration / (double) stats.getTrack());
			}
			dto.setStats(stats);	
			return this;
		}
		
		public Builder populateStatMean(Document doc) {
			if(dto == null)
				dto = new StatTrackDTO();
			
			Document idMap = (Document) doc.get("_id");
			if(idMap.containsKey("mode")) {
				String mode = idMap.getString("mode");
				StatValueDTO stats = new StatValueDTO(); 

				Double dist = doc.get("distance") != null ? doc.getDouble("distance")/1000.0 : 0.0; // to km
				Double duration = doc.get("duration") != null ? (double) doc.getLong("duration")/3600.0 : 0.0; // to hours

				if(doc.containsKey("score")) stats.setScore(FieldDTO.fromValue(doc.getDouble("score")));
				if(doc.containsKey("limitedScore")) stats.setLimitedScore(FieldDTO.fromValue(doc.getDouble("limitedScore")));
				if(doc.containsKey("co2")) stats.setCo2(FieldDTO.fromValue(doc.getDouble("co2")));
				if(doc.containsKey("distance")) stats.setDistance(FieldDTO.fromValue(dist));
				if(doc.containsKey("duration")) stats.setDuration(FieldDTO.fromValue(duration));
				if(doc.containsKey("track")) stats.setTrack(doc.getInteger("track"));
				dto.getMeanStatMap().put(mode, stats);
			}
			return this;
		}
		
		public Builder updateMainStats(Integer tripCount, Integer limitedTripCount) {
			if(dto == null)
				dto = new StatTrackDTO();
			
			StatValueDTO stats = new StatValueDTO();
			if(tripCount != null)
				stats.setTripCount(tripCount);
			if(limitedTripCount != null)
				stats.setLimitedTripCount(limitedTripCount); 
			dto.getMeanStatMap().values().forEach(sv -> {
				if(sv.getScore() != null) {
					if (stats.getScore() == null) 
						stats.setScore(FieldDTO.fromValue(sv.getScore().getValue()));
					else
						stats.getScore().sumValue(sv.getScore());
				}
				if(sv.getLimitedScore() != null) {
					if (stats.getLimitedScore() == null) 
						stats.setLimitedScore(FieldDTO.fromValue(sv.getLimitedScore().getValue()));
					else
						stats.getLimitedScore().sumValue(sv.getLimitedScore());
				}
				if(sv.getCo2() != null) {
					if (stats.getCo2() == null) 
						stats.setCo2(FieldDTO.fromValue(sv.getCo2().getValue()));
					else
						stats.getCo2().sumValue(sv.getCo2());
				}
				if(sv.getDistance() != null) {
					if (stats.getDistance() == null) 
						stats.setDistance(FieldDTO.fromValue(sv.getDistance().getValue()));
					else
						stats.getDistance().sumValue(sv.getDistance());
				}
				if(sv.getTrack() != null) {
					if (stats.getTrack() == null) 
						stats.setTrack(sv.getTrack());
					else
						stats.setTrack(stats.getTrack() + sv.getTrack());
				}
				if(sv.getTripCount() != null) {
					if (stats.getTripCount() == null) 
						stats.setTripCount(sv.getTripCount());
					else
						stats.setTripCount(stats.getTripCount() + sv.getTripCount());
				}
				if(sv.getLimitedTripCount() != null) {
					if (stats.getLimitedTripCount() == null) 
						stats.setLimitedTripCount(sv.getLimitedTripCount());
					else
						stats.setLimitedTripCount(stats.getLimitedTripCount() + sv.getLimitedTripCount());
				}
				if(sv.getDuration() != null) {
					if (stats.getDuration() == null) 
						stats.setDuration(FieldDTO.fromValue(sv.getDuration().getValue()));
					else
						stats.getDuration().sumValue(sv.getDuration());
				}
			});
			// update main avg
			if(stats.getCo2() != null) {
				if (stats.getTripCount() != null && stats.getTripCount() > 0)
					stats.getCo2().setAvgTrip(stats.getCo2().getValue() / (double) stats.getTripCount());
				if (stats.getTrack() != null && stats.getTrack() > 0)
					stats.getCo2().setAvgTrack(stats.getCo2().getValue() / (double) stats.getTrack());
			}
			if(stats.getDistance() != null) {
				if (stats.getTripCount() != null && stats.getTripCount() > 0)
					stats.getDistance().setAvgTrip(stats.getDistance().getValue() / (double) stats.getTripCount());
				if (stats.getTrack() != null && stats.getTrack() > 0)
					stats.getDistance().setAvgTrack(stats.getDistance().getValue() / (double) stats.getTrack());
			}
			if(stats.getDuration() != null) {
				if (stats.getTripCount() != null && stats.getTripCount() > 0)
					stats.getDuration().setAvgTrip(stats.getDuration().getValue() / (double) stats.getTripCount());
				if (stats.getTrack() != null && stats.getTrack() > 0)
					stats.getDuration().setAvgTrack(stats.getDuration().getValue() / (double) stats.getTrack());
			}
			// update avg and prc for every mean
			dto.getMeanStatMap().values().forEach(sv -> {
				if(sv.getCo2() != null) {
					if (stats.getTripCount() != null && stats.getTripCount() > 0)
						sv.getCo2().setAvgTrip(sv.getCo2().getValue() / (double) stats.getTripCount());
					if (stats.getTrack() != null && stats.getTrack() > 0)
						sv.getCo2().setAvgTrack(sv.getCo2().getValue() / (double) stats.getTrack());
					if (stats.getCo2().getValue() != null && stats.getCo2().getValue() > 0)
						sv.getCo2().setPrcValue((sv.getCo2().getValue() / stats.getCo2().getValue()) * 100.0);
					if (stats.getCo2().getAvgTrip() != null && stats.getCo2().getAvgTrip() > 0)
						sv.getCo2().setPrcAvgTrip((sv.getCo2().getAvgTrip() / stats.getCo2().getAvgTrip()) * 100.0);
					if (stats.getCo2().getAvgTrack() != null && stats.getCo2().getAvgTrack() > 0)
						sv.getCo2().setPrcAvgTrack((sv.getCo2().getAvgTrack() / stats.getCo2().getAvgTrack()) * 100.0);
				}
				if(sv.getDistance() != null) {
					if (stats.getTripCount() != null && stats.getTripCount() > 0)
						sv.getDistance().setAvgTrip(sv.getDistance().getValue() / (double) stats.getTripCount());
					if (stats.getTrack() != null && stats.getTrack() > 0)
						sv.getDistance().setAvgTrack(sv.getDistance().getValue() / (double) stats.getTrack());
					if (stats.getDistance().getValue() != null && stats.getDistance().getValue() > 0)
						sv.getDistance().setPrcValue((sv.getDistance().getValue() / stats.getDistance().getValue()) * 100.0);
					if (stats.getDistance().getAvgTrip() != null && stats.getDistance().getAvgTrip() > 0)
						sv.getDistance().setPrcAvgTrip((sv.getDistance().getAvgTrip() / stats.getDistance().getAvgTrip()) * 100.0);
					if (stats.getDistance().getAvgTrack() != null && stats.getDistance().getAvgTrack() > 0)
						sv.getDistance().setPrcAvgTrack((sv.getDistance().getAvgTrack() / stats.getDistance().getAvgTrack()) * 100.0);
				}
				if(sv.getDuration() != null) {
					if (stats.getTrack() != null && stats.getTrack() > 0) 
						sv.getDuration().setAvgTrack(sv.getDuration().getValue() / (double) stats.getTrack());
					if (stats.getTripCount() != null && stats.getTripCount() > 0)
						sv.getDuration().setAvgTrip(sv.getDuration().getValue() / (double) stats.getTripCount());
					if (stats.getDuration().getValue() != null && stats.getDuration().getValue() > 0)
						sv.getDuration().setPrcValue((sv.getDuration().getValue() / stats.getDuration().getValue()) * 100.0);
					if (stats.getDuration().getAvgTrip() != null && stats.getDuration().getAvgTrip() > 0)
						sv.getDuration().setPrcAvgTrip((sv.getDuration().getAvgTrip() / stats.getDuration().getAvgTrip()) * 100.0);
					if (stats.getDuration().getAvgTrack() != null && stats.getDuration().getAvgTrack() > 0)
						sv.getDuration().setPrcAvgTrack((sv.getDuration().getAvgTrack() / stats.getDuration().getAvgTrack()) * 100.0);
				}
			});
			dto.setStats(stats);
			return this;
		}
		
		public StatTrackDTO build() {
			return dto;
		}
	}

}
