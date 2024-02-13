package it.smartcommunitylab.pgazienda.domain;

import java.util.HashMap;
import java.util.Map;

public class Territory {
    
    private String territoryId;
	private Map<String, String> name = new HashMap<>();
	private Map<String, String> description = new HashMap<>();
	private String timezone;
	
	private Map<String, Object> territoryData = new HashMap<>();

    public String getTerritoryId() {
        return territoryId;
    }

    public void setTerritoryId(String territoryId) {
        this.territoryId = territoryId;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Map<String, Object> getTerritoryData() {
        return territoryData;
    }

    public void setTerritoryData(Map<String, Object> territoryData) {
        this.territoryData = territoryData;
    }

    
}
