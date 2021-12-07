package com.app.model;

import javax.validation.constraints.NotBlank;
import java.util.Date;

public class Transaction {

    @NotBlank(message="payer may not be blank")
    private String payer;
    private int points;
    private Date date;

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
