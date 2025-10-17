package it.smartcommunitylab.pgazienda.dto;

public class FieldDTO {
    private Double value, avgTrip, prcValue, prcAvgTrip;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getAvgTrip() {
        return avgTrip;
    }

    public void setAvgTrip(Double avgTrip) {
        this.avgTrip = avgTrip;
    }

    public Double getPrcValue() {
        return prcValue;
    }

    public void setPrcValue(Double prcValue) {
        this.prcValue = prcValue;
    }

    public Double getPrcAvgTrip() {
        return prcAvgTrip;
    }

    public void setPrcAvgTrip(Double prcTrip) {
        this.prcAvgTrip = prcTrip;
    }

    public static FieldDTO fromValue(Double value) {
        FieldDTO dto = new FieldDTO();
        dto.setValue(value);
        return dto;
    }

    public void sumValue(FieldDTO dto) {
        if (dto != null && dto.getValue() != null) {
            if (this.value == null) {
                this.value = dto.getValue();
            } else {
                this.value += dto.getValue();
            }
        }
    }

    public void sumValue(Double addValue) {
        if (addValue != null) {
            if (this.value == null) {
                this.value = addValue;
            } else {
                this.value += addValue;
            }
        }
    }
}
