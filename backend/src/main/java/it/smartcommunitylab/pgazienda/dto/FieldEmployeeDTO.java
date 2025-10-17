package it.smartcommunitylab.pgazienda.dto;

public class FieldEmployeeDTO {
    private Integer value;
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

    public void addValue() {
        if(this.value == null) {
            this.value = 1;
        } else {
            this.value++;
        }
    }

    public static FieldEmployeeDTO fromValue(Integer value) {
        FieldEmployeeDTO dto = new FieldEmployeeDTO();
        dto.setValue(value);
        return dto;
    }
}
