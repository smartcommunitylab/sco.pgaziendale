package it.smartcommunitylab.pgazienda.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

public class StatTrackDTO {
	private String campaign;
	private String timeGroup; 
	private String dataGroup;
	private StatValue stats;
	private Map<String, StatValue>meanStatMap = new HashMap<>();
	
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
	public StatValue getStats() {
		return stats;
	}
	public void setStats(StatValue stats) {
		this.stats = stats;
	}
	public Map<String, StatValue> getMeanStatMap() {
		return meanStatMap;
	}
	public void setMeanStatMap(Map<String, StatValue> meanStatMap) {
		this.meanStatMap = meanStatMap;
	}

	public static class StatValue {
		private Double score, co2, distance;
		private Long duration;
		private Integer track;
		
		public Double getScore() {
			return score;
		}
		public void setScore(Double score) {
			this.score = score;
		}
		public Double getCo2() {
			return co2;
		}
		public void setCo2(Double co2) {
			this.co2 = co2;
		}
		public Double getDistance() {
			return distance;
		}
		public void setDistance(Double distance) {
			this.distance = distance;
		}
		public Long getDuration() {
			return duration;
		}
		public void setDuration(Long duration) {
			this.duration = duration;
		}
		public Integer getTrack() {
			return track;
		}
		public void setTrack(Integer track) {
			this.track = track;
		}
		
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
				dto.setTimeGroup("total");
			}
			return this;
		}
		
		public Builder populateStatFields(Document doc) {
			if(dto == null)
				dto = new StatTrackDTO();
			
			StatValue stats = new StatValue(); 
			if(doc.containsKey("score")) stats.setScore(doc.getDouble("score"));
			if(doc.containsKey("co2")) stats.setCo2(doc.getDouble("co2"));
			if(doc.containsKey("distance")) stats.setDistance(doc.getDouble("distance"));
			if(doc.containsKey("duration")) stats.setDuration(doc.getLong("duration"));
			if(doc.containsKey("track")) stats.setTrack(doc.getInteger("track"));
			dto.setStats(stats);	
			return this;
		}
		
		public Builder populateStatMean(Document doc) {
			if(dto == null)
				dto = new StatTrackDTO();
			
			Document idMap = (Document) doc.get("_id");
			if(idMap.containsKey("mode")) {
				String mode = idMap.getString("mode");
				StatValue stats = new StatValue(); 
				if(doc.containsKey("score")) stats.setScore(doc.getDouble("score"));
				if(doc.containsKey("co2")) stats.setCo2(doc.getDouble("co2"));
				if(doc.containsKey("distance")) stats.setDistance(doc.getDouble("distance"));
				if(doc.containsKey("duration")) stats.setDuration(doc.getLong("duration"));
				if(doc.containsKey("track")) stats.setTrack(doc.getInteger("track"));
				dto.getMeanStatMap().put(mode, stats);
			}
			return this;
		}
		
		public Builder updateMainStats() {
			if(dto == null)
				dto = new StatTrackDTO();
			
			StatValue stats = new StatValue(); 
			dto.getMeanStatMap().values().forEach(sv -> {
				if(sv.getScore() != null) {
					if (stats.getScore() == null) 
						stats.setScore(sv.getScore());
					else
						stats.setScore(stats.getScore() + sv.getScore());
				}
				if(sv.getCo2() != null) {
					if (stats.getCo2() == null) 
						stats.setCo2(sv.getCo2());
					else
						stats.setCo2(stats.getCo2() + sv.getCo2());
				}
				if(sv.getDistance() != null) {
					if (stats.getDistance() == null) 
						stats.setDistance(sv.getDistance());
					else
						stats.setDistance(stats.getDistance() + sv.getDistance());
				}
				if(sv.getTrack() != null) {
					if (stats.getTrack() == null) 
						stats.setTrack(sv.getTrack());
					else
						stats.setTrack(stats.getTrack() + sv.getTrack());
				}
				if(sv.getDuration() != null) {
					if (stats.getDuration() == null) 
						stats.setDuration(sv.getDuration());
					else
						stats.setDuration(stats.getDuration() + sv.getDuration());
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
