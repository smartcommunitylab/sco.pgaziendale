package it.smartcommunitylab.pgazienda.dto;

public class FieldEmployeeDTO {
    private Integer value, progressive;
    private Double prcTot, prcRegistered;

    public Integer getValue() {
        return value;
    }
    public void setValue(Integer value) {
        this.value = value;
    }
    public Double getPrcTot() {
        return prcTot;
    }
    public void setPrcTot(Double prcTot) {
        this.prcTot = prcTot;
    }
    public Double getPrcRegistered() {
        return prcRegistered;
    }
    public void setPrcRegistered(Double prcRegistered) {
        this.prcRegistered = prcRegistered;
    }
    public Integer getProgressive() {
        return progressive;
    }
    public void setProgressive(Integer progressive) {
        this.progressive = progressive;
    }

    public void addValue() {
        if(this.value == null) {
            this.value = 1;
        } else {
            this.value++;
        }
    }

    public void addProgressive() {
        if(this.progressive == null) {
            this.progressive = 1;
        } else {
            this.progressive++;
        }
    }

    public static FieldEmployeeDTO fromValue(Integer value) {
        FieldEmployeeDTO dto = new FieldEmployeeDTO();
        dto.setValue(value);
        return dto;
    }
}
