package it.smartcommunitylab.pgazienda.dto;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

public class StatMultimodalDTO {
	private String campaign;
	private String timeGroup;
	private String modeGroup; 
	private String dataGroup;
	private StatMultimodalValueDTO stats = new StatMultimodalValueDTO();
	private Map<String, StatMultimodalValueDTO>meanStatMap = new HashMap<>();
	
	public String getTimeGroup() {
		return timeGroup;
	}
	public void setTimeGroup(String timeGroup) {
		this.timeGroup = timeGroup;
	}
	public Map<String, StatMultimodalValueDTO> getMeanStatMap() {
		return meanStatMap;
	}
	public void setMeanStatMap(Map<String, StatMultimodalValueDTO> meanStatMap) {
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
	public StatMultimodalValueDTO getStats() {
		return stats;
	}
	public void setStats(StatMultimodalValueDTO stats) {
		this.stats = stats;
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
			
			StatMultimodalValueDTO stats = new StatMultimodalValueDTO(); 
			if(doc.containsKey("track")) stats.setCount(FieldDTO.fromValue((double) doc.getInteger("track")));
			if(doc.containsKey("distance")) {
				stats.setDistance(FieldDTO.fromValue(doc.getDouble("distance")));
				stats.getDistance().setAvgTrip(doc.getDouble("distance") / (double) doc.getInteger("track"));
			}
			if(doc.containsKey("duration")) {
				stats.setDuration(FieldDTO.fromValue((double) doc.getLong("duration")));
				stats.getDuration().setAvgTrip((double) doc.getLong("duration") / (double) doc.getInteger("track"));
			}

			dto.setStats(stats);	
			return this;
		}
		
		public Builder mergeStatMean(Document doc) {
			if(dto == null)
				dto = new StatMultimodalDTO();
			
			Document idMap = (Document) doc.get("_id");
			if(idMap.containsKey("mode")) {
				String mode = idMap.getString("mode");			
				StatMultimodalValueDTO stats = dto.getMeanStatMap().get(mode);
				if(stats == null) {
					stats = new StatMultimodalValueDTO();
					 dto.getMeanStatMap().put(mode, stats);
				}
				if(doc.containsKey("distance")) stats.setDistance(FieldDTO.fromValue(doc.getDouble("distance")));
				if(doc.containsKey("duration")) stats.setDuration(FieldDTO.fromValue((double)doc.getLong("duration")));
				if(doc.containsKey("track")) stats.setCount(FieldDTO.fromValue((double) doc.getInteger("track")));
			}
			return this;
		}

		public Builder updateMainStats() {
			if(dto == null)
				dto = new StatMultimodalDTO();
			
			StatMultimodalValueDTO stats = new StatMultimodalValueDTO(); 
			dto.getMeanStatMap().values().forEach(sv -> {
				if(sv.getDuration() != null) {
					if (stats.getDuration() == null) 
						stats.setDuration(FieldDTO.fromValue(sv.getDuration().getValue()));
					else
						stats.getDuration().sumValue(sv.getDuration());
				}
				if(sv.getDistance() != null) {
					if (stats.getDistance() == null) 
						stats.setDistance(FieldDTO.fromValue(sv.getDistance().getValue()));
					else
						stats.getDistance().sumValue(sv.getDistance());
				}
				if(sv.getCount() != null) {
					if (stats.getCount() == null) 
						stats.setCount(FieldDTO.fromValue(sv.getCount().getValue()));
					else
						stats.getCount().sumValue(sv.getCount());
				}

			});
			// update main avg
			if(stats.getDistance() != null) {
				stats.getDistance().setAvgTrip(stats.getDistance().getValue() / stats.getCount().getValue());
			}
			if(stats.getDuration() != null) {
				stats.getDuration().setAvgTrip(stats.getDuration().getValue() / stats.getCount().getValue());
			}
			// update avg and prc for every mean
			dto.getMeanStatMap().values().forEach(sv -> {
				if(sv.getCount() != null) {
					sv.getCount().setPrcValue((sv.getCount().getValue() / stats.getCount().getValue()) * 100.0);
				}
				if(sv.getDistance() != null) {
					sv.getDistance().setAvgTrip(sv.getDistance().getValue() / stats.getCount().getValue());
					sv.getDistance().setPrcValue((sv.getDistance().getValue() / stats.getDistance().getValue()) * 100.0);
					sv.getDistance().setPrcAvgTrip((sv.getDistance().getAvgTrip() / stats.getDistance().getAvgTrip()) * 100.0);
				}
				if(sv.getDuration() != null) {
					sv.getDuration().setAvgTrip(sv.getDuration().getValue() / stats.getCount().getValue());
					sv.getDuration().setPrcValue((sv.getDuration().getValue() / stats.getDuration().getValue()) * 100.0);
					sv.getDuration().setPrcAvgTrip((sv.getDuration().getAvgTrip() / stats.getDuration().getAvgTrip()) * 100.0);
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
