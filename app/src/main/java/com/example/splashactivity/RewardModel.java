package com.example.splashactivity;

import java.sql.Time;
import com.google.firebase.Timestamp;
import java.util.Date;


public class RewardModel {
    private String type;
    private String lowerLimit;
    private String upperLimit;
    private String discountOrAmount;
    private String coupenBody;
    private Timestamp timeStamp;
    private boolean alreadyUsed;
    private String coupenId;

    public RewardModel(String coupenId,String type, String lowerLimit, String upperLimit, String discountOrAmount, String coupenBody, Timestamp timeStamp,Boolean alreadyUsed) {
        this.coupenId=coupenId;
        this.type = type;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.discountOrAmount = discountOrAmount;
        this.coupenBody = coupenBody;
        this.timeStamp = timeStamp;
        this.alreadyUsed=alreadyUsed;

    }

    public String getCoupenId() {
        return coupenId;
    }

    public void setCoupenId(String coupenId) {
        this.coupenId = coupenId;
    }

    public boolean isAlreadyUsed() {
        return alreadyUsed;
    }

    public void setAlreadyUsed(boolean alreadyUsed) {
        this.alreadyUsed = alreadyUsed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLowerLimit() {
        return lowerLimit;
    }
    public boolean getAlreadyUsed() {
        return alreadyUsed;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getDiscountOrAmount() {
        return discountOrAmount;
    }

    public void setDiscountOrAmount(String discountOrAmount) {
        this.discountOrAmount = discountOrAmount;
    }

    public String getCoupenBody() {
        return coupenBody;
    }

    public void setCoupenBody(String coupenBody) {
        this.coupenBody = coupenBody;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }
}

