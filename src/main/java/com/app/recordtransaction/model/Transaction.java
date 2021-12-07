package com.app.recordtransaction.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class Transaction {

    @NotBlank(message="payer may not be blank")
    private String payer;
    @NotNull(message="points may not be null")
    private Integer points;
    @NotNull(message="timestamp may not be null")
    private Date timestamp;

    @JsonIgnore
    private Integer spentPoints = 0;

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getSpentPoints() {
        return spentPoints;
    }

    public void setSpentPoints(Integer spentPoints) {
        this.spentPoints = spentPoints;
    }

    @JsonIgnore
    public Integer getUnspentPoints(){
        return getPoints() - getSpentPoints();
    }
}
