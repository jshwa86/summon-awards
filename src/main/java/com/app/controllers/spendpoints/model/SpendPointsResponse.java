package com.app.controllers.spendpoints.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "For successful requests to spend points, this structure is used to detail which payer provided the spent points.")
public class SpendPointsResponse {

    @Schema(description = "Identifies the payer who provided the points.",example = "DANNON")
    private String payer;
    @Schema(description = "How many points were provided. Note this will typically be a negative number detailing how many points were removed from that payer's balance.",example = "-100")
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
