package com.app.controllers.spendpoints.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SpendPointsRequest {

    @NotNull(message = "points must be defined for this operation")
    @Min(value=1,message="points must be a positive number")
    private Integer points;

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
