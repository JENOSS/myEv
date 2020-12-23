package com.example.myevstation_android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Station {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("statNm")
    @Expose
    private String stationName;

    @SerializedName("chgerType")
    @Expose
    private String chgerType;

    @SerializedName("addr")
    @Expose
    private String addr;

    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("lng")
    @Expose
    private String lng;

    @SerializedName("useTime")
    @Expose
    private String useTime;

    @SerializedName("busiId")
    @Expose
    private String busiId;

    @SerializedName("busiNm")
    @Expose
    private String busiName;

    @SerializedName("busicall")
    @Expose
    private String busiCall;


    public String getId() {
        return id;
    }

    public String getStationName() {
        return stationName;
    }

    public String getChgerType() {
        return chgerType;
    }

    public String getAddr() {
        return addr;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getUseTime() {
        return useTime;
    }

    public String getBusiId() {
        return busiId;
    }

    public String getBusiName() {
        return busiName;
    }

    public String getBusiCall() {
        return busiCall;
    }
}
