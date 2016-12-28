package com.cameocoder.capstoneproject.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Flag {

    @SerializedName("service_name")
    @Expose
    private String serviceName;
    @SerializedName("event_type")
    @Expose
    private String eventType;
    // greenbin,yardtrimmings,garbage,bluebox,blackbox
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("area_name")
    @Expose
    private String areaName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
