package com.app.spendpoints.model;

public class SpendPointsResponse {

    private String payer;
    private Integer points;

    private SpendPointsResponse(){}

    public SpendPointsResponse(String payer, Integer points) {
        this.payer = payer;
        this.points = points;
    }

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
}
