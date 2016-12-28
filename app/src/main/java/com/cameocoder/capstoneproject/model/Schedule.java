package com.cameocoder.capstoneproject.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Schedule {

    @SerializedName("events")
    @Expose
    private List<Event> events = null;
//    @SerializedName("zones")
//    @Expose
//    private Zones zones;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

//    public Zones getZones() {
//        return zones;
//    }
//
//    public void setZones(Zones zones) {
//        this.zones = zones;
//    }

}
