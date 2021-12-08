package com.app.recordtransaction.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Schema(description = "Represents the details tied to one specific point transaction.")
public class Transaction {

    @Schema(name="payer",description = "Identifies the payer associated with this transaction.",example = "DANNON")
    @NotBlank(message="payer may not be blank")
    private String payer;
    @Schema(name="points",description = "How many points are associated with this transaction. Negative values are allowed, but only if the associated payer already has a sufficient balance to remove that number of points.",example = "100")
    @NotNull(message="points may not be null")
    private Integer points;
    @NotNull(message="timestamp may not be null")
    @Schema(name="timestamp",description = "Timestamp detailing when the transaction happened.",example = "2020-11-02T14:00:00Z")
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
