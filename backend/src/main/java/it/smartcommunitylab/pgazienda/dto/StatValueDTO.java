package it.smartcommunitylab.pgazienda.dto;

public class StatValueDTO {
    private FieldDTO score, limitedScore, co2, distance, duration;
    private Integer track, tripCount, limitedTripCount;

    public FieldDTO getScore() {
        return score;
    }

    public void setScore(FieldDTO score) {
        this.score = score;
    }

    public FieldDTO getLimitedScore() {
        return limitedScore;
    }

    public void setLimitedScore(FieldDTO limitedScore) {
        this.limitedScore = limitedScore;
    }

    public FieldDTO getCo2() {
        return co2;
    }

    public void setCo2(FieldDTO co2) {
        this.co2 = co2;
    }

    public FieldDTO getDistance() {
        return distance;
    }

    public void setDistance(FieldDTO distance) {
        this.distance = distance;
    }

    public FieldDTO getDuration() {
        return duration;
    }

    public void setDuration(FieldDTO duration) {
        this.duration = duration;
    }

    public Integer getTrack() {
        return track;
    }

    public void setTrack(Integer track) {
        this.track = track;
    }

    public Integer getTripCount() {
        return tripCount;
    }

    public void setTripCount(Integer tripCount) {
        this.tripCount = tripCount;
    }

    public Integer getLimitedTripCount() {
        return limitedTripCount;
    }

    public void setLimitedTripCount(Integer limitedTripCount) {
        this.limitedTripCount = limitedTripCount;
    }
}
