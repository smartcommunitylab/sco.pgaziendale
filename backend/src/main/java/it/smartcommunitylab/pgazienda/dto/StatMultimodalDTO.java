package it.smartcommunitylab.pgazienda.dto;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

public class StatMultimodalDTO {
	private String campaign;
	private String timeGroup;
	private String dataGroup;
	private String dataGroupName;

	private StatMultimodalValueDTO stats = new StatMultimodalValueDTO();
	private Map<String, StatMultimodalValueDTO> modeStatMap = new HashMap<>();
	
	public String getTimeGroup() {
		return timeGroup;
	}
	public void setTimeGroup(String timeGroup) {
		this.timeGroup = timeGroup;
	}
	public Map<String, StatMultimodalValueDTO> getModeStatMap() {
		return modeStatMap;
	}
	public void setModeStatMap(Map<String, StatMultimodalValueDTO> meanStatMap) {
		this.modeStatMap = meanStatMap;
	}
	public String getCampaign() {
		return campaign;
	}
	public void setCampaign(String campaign) {
		this.campaign = campaign;
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

	public String getDataGroupName() {
		return dataGroupName;
	}
	public void setDataGroupName(String dataGroupName) {
		this.dataGroupName = dataGroupName;
	}
	
	public static class Builder {
		private StatMultimodalDTO dto;
		
		public Builder populateKeyFields(Document doc) {
			if(dto == null)
				dto = new StatMultimodalDTO();
			
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
		
		public Builder mergeStatModeDoc(Document doc, String mode) {
			if(dto == null)
				dto = new StatMultimodalDTO();
			
			StatMultimodalValueDTO stats = dto.getStats();
			if(doc.containsKey("distance")) {
				if (stats.getDistance() == null) 
					stats.setDistance(FieldDTO.fromValue(doc.getDouble("distance")/1000.0));
				else
					stats.getDistance().sumValue(doc.getDouble("distance")/1000.0);
				if(dto.getModeStatMap().containsKey(mode)) {
					StatMultimodalValueDTO modeStats = dto.getModeStatMap().get(mode);
					if(modeStats.getDistance() == null) {
						modeStats.setDistance(FieldDTO.fromValue(doc.getDouble("distance")/1000.0));
					} else {
						modeStats.getDistance().sumValue(doc.getDouble("distance")/1000.0);
					}
				} else {
					StatMultimodalValueDTO modeStats = new StatMultimodalValueDTO();
					modeStats.setDistance(FieldDTO.fromValue(doc.getDouble("distance")/1000.0));
					dto.getModeStatMap().put(mode, modeStats);
				}
			}
			if(doc.containsKey("duration")) {
				if (stats.getDuration() == null) 
					stats.setDuration(FieldDTO.fromValue((double) doc.getLong("duration")/3600.0));
				else
					stats.getDuration().sumValue((double) doc.getLong("duration")/3600.0);
				if(dto.getModeStatMap().containsKey(mode)) {
					StatMultimodalValueDTO modeStats = dto.getModeStatMap().get(mode);
					if(modeStats.getDuration() == null) {
						modeStats.setDuration(FieldDTO.fromValue((double) doc.getLong("duration")/3600.0));
					} else {
						modeStats.getDuration().sumValue((double) doc.getLong("duration")/3600.0);
					}
				} else {
					StatMultimodalValueDTO modeStats = new StatMultimodalValueDTO();
					modeStats.setDuration(FieldDTO.fromValue((double) doc.getLong("duration")/3600.0));
					dto.getModeStatMap().put(mode, modeStats);
				}
			}
			if(doc.containsKey("count")) {
				if (stats.getCount() == null) 
					stats.setCount(FieldDTO.fromValue((double) doc.getInteger("count")));
				else
					stats.getCount().sumValue((double) doc.getInteger("count"));
				if(dto.getModeStatMap().containsKey(mode)) {
					StatMultimodalValueDTO modeStats = dto.getModeStatMap().get(mode);
					if(modeStats.getCount() == null) {
						modeStats.setCount(FieldDTO.fromValue((double) doc.getInteger("count")));
					} else {
						modeStats.getCount().sumValue((double) doc.getInteger("count"));
					}
				} else {
					StatMultimodalValueDTO modeStats = new StatMultimodalValueDTO();
					modeStats.setCount(FieldDTO.fromValue((double) doc.getInteger("count")));
					dto.getModeStatMap().put(mode, modeStats);
				}	
			}
			return this;
		}

		public Builder setModeCount(String mode, Integer count) {
			if(dto == null)
				dto = new StatMultimodalDTO();
			
			if(dto.getModeStatMap().containsKey(mode)) {
				StatMultimodalValueDTO stats = dto.getModeStatMap().get(mode);
				stats.setCount(FieldDTO.fromValue((double) count));
			}
			return this;
		}

		public Builder updateMainStats() {
			if(dto == null)
				dto = new StatMultimodalDTO();
			
			StatMultimodalValueDTO stats = new StatMultimodalValueDTO(); 
			dto.getModeStatMap().values().forEach(sv -> {
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
			// update main count
			int totalCount = 0;
			for(StatMultimodalValueDTO sv : dto.getModeStatMap().values()) {
				if(sv.getCount() != null) {
					totalCount += sv.getCount().getValue().intValue();
				}
			}
			stats.setCount(FieldDTO.fromValue((double) totalCount));
			// update main avg
			if(stats.getDistance() != null) {
				stats.getDistance().setAvgTrip(stats.getDistance().getValue() / stats.getCount().getValue());
			}
			if(stats.getDuration() != null) {
				stats.getDuration().setAvgTrip(stats.getDuration().getValue() / stats.getCount().getValue());
			}
			// update avg and prc for every mean
			dto.getModeStatMap().values().forEach(sv -> {
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
