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
    private Map<String, Zone> zones = null;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Map<String, Zone> getZones() {
        return zones;
    }

    public void setZones(Map<String, Zone> zones) {
        this.zones = zones;
    }

}
