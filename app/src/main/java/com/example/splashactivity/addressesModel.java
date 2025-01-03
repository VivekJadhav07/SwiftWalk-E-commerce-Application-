package com.example.splashactivity;

public class addressesModel {

    private Boolean selected;
    private String city;
    private String locality;
    private String flatNo;
    private String pincode;
    private String landmark;
    private String name;
    private String mobileNo;
    private String alternatemobileNo;
    private String state;

    public addressesModel(Boolean selected, String city, String locality, String flatNo, String pincode, String landmark, String name, String mobileNo, String alternatemobileNo, String state) {
        this.selected = selected;
        this.city = city;
        this.locality = locality;
        this.flatNo = flatNo;
        this.pincode = pincode;
        this.landmark = landmark;
        this.name = name;
        this.mobileNo = mobileNo;
        this.alternatemobileNo = alternatemobileNo;
        this.state = state;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getFlatNo() {
        return flatNo;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAlternatemobileNo() {
        return alternatemobileNo;
    }

    public void setAlternatemobileNo(String alternatemobileNo) {
        this.alternatemobileNo = alternatemobileNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
