package com.cameocoder.capstoneproject.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Event {
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("zone_id")
    @Expose
    private Integer zoneId;
    @SerializedName("flags")
    @Expose
    private List<Flag> flags = null;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public void setFlags(List<Flag> flags) {
        this.flags = flags;
    }

}
