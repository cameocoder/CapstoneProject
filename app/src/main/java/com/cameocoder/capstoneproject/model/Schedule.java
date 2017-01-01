package com.cameocoder.capstoneproject.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class Schedule {

    @SerializedName("events")
    @Expose
    private List<Event> events = null;
    @SerializedName("zones")
    @Expose
    private Map<String, Zones> zones = null;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Map<String, Zones> getZones() {
        return zones;
    }

    public void setZones(Map<String, Zones> zones) {
        this.zones = zones;
    }

}
