package com.cameocoder.capstoneproject.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Event {

    private static final String BLACK_BOX = "blackbox";
    private static final String BLUE_BOX = "bluebox";
    private static final String GREEN_BIN = "greenbin";
    private static final String YARD_WASTE = "yardtrimmings";
    private static final String GARBAGE = "garbage";


    /**
     * day is in yyyy-mm-dd format
     */
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


    public boolean isBlackBoxDay() {
        return hasWasteType(BLACK_BOX);
    }

    public boolean isBlueBoxDay() {
        return hasWasteType(BLUE_BOX);
    }

    public boolean isGarbageDay() {
        return hasWasteType(GARBAGE);
    }

    public boolean isGreenBinDay() {
        return hasWasteType(GREEN_BIN);
    }

    public boolean isYardWasteDay() {
        return hasWasteType(YARD_WASTE);
    }

    private boolean hasWasteType(String type) {
        if (flags == null) {
            return false;
        }
        for (int i = 0; i < flags.size(); i++) {
            if (type.equals(flags.get(i).getName())) {
                return true;
            }

        }
        return false;
    }
}
