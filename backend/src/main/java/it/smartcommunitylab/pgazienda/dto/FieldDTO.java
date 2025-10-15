package it.smartcommunitylab.pgazienda.dto;

public class FieldDTO {
    private Double value, avgTrack, avgTrip, prcValue, prcTrack, prcTrip;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getAvgTrack() {
        return avgTrack;
    }

    public void setAvgTrack(Double avgTrack) {
        this.avgTrack = avgTrack;
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

    public Double getPrcTrack() {
        return prcTrack;
    }

    public void setPrcTrack(Double prcTrack) {
        this.prcTrack = prcTrack;
    }

    public Double getPrcTrip() {
        return prcTrip;
    }

    public void setPrcTrip(Double prcTrip) {
        this.prcTrip = prcTrip;
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
}
