package it.smartcommunitylab.pgazienda.dto;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

public class StatMultimodalDTO {
	private String campaign;
	private String timeGroup;
	private String modeGroup; 
	private String dataGroup;
	private StatValue stats = new StatValue();
	private Map<String, StatValue>meanStatMap = new HashMap<>();
	
	public String getTimeGroup() {
		return timeGroup;
	}
	public void setTimeGroup(String timeGroup) {
		this.timeGroup = timeGroup;
	}
	public Map<String, StatValue> getMeanStatMap() {
		return meanStatMap;
	}
	public void setMeanStatMap(Map<String, StatValue> meanStatMap) {
		this.meanStatMap = meanStatMap;
	}
	public String getCampaign() {
		return campaign;
	}
	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}
	public String getModeGroup() {
		return modeGroup;
	}
	public void setModeGroup(String modeGroup) {
		this.modeGroup = modeGroup;
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

	public static class StatValue {
		private Double score, limitedScore, co2, distance;
		private Long duration;
		private Integer track;
		
		public Double getScore() {
			return score != null ? score : Double.valueOf(0d);
		}
		public void setScore(Double score) {
			this.score = score;
		}
		public Double getLimitedScore() {
			return limitedScore != null ? limitedScore : Double.valueOf(0d);
		}
		public void setLimitedScore(Double limitedScore) {
			this.limitedScore = limitedScore;
		}
		public Double getCo2() {
			return co2 != null ? co2 : Double.valueOf(0d);
		}
		public void setCo2(Double co2) {
			this.co2 = co2;
		}
		public Double getDistance() {
			return distance != null ? distance : Double.valueOf(0);
		}
		public void setDistance(Double distance) {
			this.distance = distance;
		}
		public Long getDuration() {
			return duration != null ? duration : Long.valueOf(0);
		}
		public void setDuration(Long duration) {
			this.duration = duration;
		}
		public Integer getTrack() {
			return track != null ? track : Integer.valueOf(0);
		}
		public void setTrack(Integer track) {
			this.track = track;
		}
		
	}
	
	public static class Builder {
		private StatMultimodalDTO dto;
		
		public Builder populateKeyFields(Document doc, String modeGroup) {
			if(dto == null)
				dto = new StatMultimodalDTO();
			
			dto.setModeGroup(modeGroup);
			Document idMap = (Document) doc.get("_id");
			if(idMap.containsKey("campaign")) dto.setCampaign(idMap.getString("campaign"));
			if(idMap.containsKey("locationKey")) dto.setDataGroup(idMap.getString("locationKey"));
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
			
			return this;
		}
		
		public Builder populateStatFields(Document doc) {
			if(dto == null)
				dto = new StatMultimodalDTO();
			
			StatValue stats = new StatValue(); 
			if(doc.containsKey("score")) stats.setScore(doc.getDouble("score"));
			if(doc.containsKey("limitedScore")) stats.setLimitedScore(doc.getDouble("limitedScore"));
			if(doc.containsKey("co2")) stats.setCo2(doc.getDouble("co2"));
			if(doc.containsKey("distance")) stats.setDistance(doc.getDouble("distance"));
			if(doc.containsKey("duration")) stats.setDuration(doc.getLong("duration"));
			if(doc.containsKey("track")) stats.setTrack(doc.getInteger("track"));
			dto.setStats(stats);	
			return this;
		}
		
		public Builder mergeStatMean(Document doc) {
			if(dto == null)
				dto = new StatMultimodalDTO();
			
			Document idMap = (Document) doc.get("_id");
			if(idMap.containsKey("mode")) {
				String mode = idMap.getString("mode");			
				StatValue stats = dto.getMeanStatMap().get(mode);
				if(stats == null) {
					stats = new StatValue();
					 dto.getMeanStatMap().put(mode, stats);
				}
				
				if(doc.containsKey("score")) stats.setScore(stats.getScore() +  doc.getDouble("score"));
				if(doc.containsKey("limitedScore")) stats.setLimitedScore(stats.getScore() +  doc.getDouble("limitedScore"));
				if(doc.containsKey("co2")) stats.setCo2(stats.getCo2() + doc.getDouble("co2"));
				if(doc.containsKey("distance")) stats.setDistance(stats.getDistance() + doc.getDouble("distance"));
				if(doc.containsKey("duration")) stats.setDuration(stats.getDuration() + doc.getLong("duration"));
				if(doc.containsKey("track")) stats.setTrack(stats.getTrack() + doc.getInteger("track"));
			}
			return this;
		}

		public Builder updateMainStats() {
			if(dto == null)
				dto = new StatMultimodalDTO();
			
			StatValue stats = new StatValue(); 
			dto.getMeanStatMap().values().forEach(sv -> {
				if(sv.getScore() != null) {
					if (stats.getScore() == null) 
						stats.setScore(sv.getScore());
					else
						stats.setScore(stats.getScore() + sv.getScore());
				}
				if(sv.getLimitedScore() != null) {
					if (stats.getLimitedScore() == null) 
						stats.setLimitedScore(sv.getLimitedScore());
					else
						stats.setLimitedScore(stats.getLimitedScore() + sv.getLimitedScore());
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
		
		public StatMultimodalDTO build() {
			return dto;
		}
	}

}
