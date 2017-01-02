package com.cameocoder.capstoneproject;

import com.cameocoder.capstoneproject.model.Places;
import com.cameocoder.capstoneproject.model.Schedule;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitRecollectInterface {
    // Determines a place_id from the current location (latitude,longitude)
    @GET("api/lookup/{lat},{lng}.json")
    Call<Places> getPlace(@Path("lat") double lat, @Path("lng") double lng);

    // Gets the schedule for the current month from the place_id
    @GET("api/places/{place_id}/services/waste/events")
    Call<Schedule> getScheduleFromPlace(@Path("place_id") String place_id);

    // Gets an extended schedule from the zone_id
    @GET("api/areas/Ottawa/services/waste/zones/{zone_id}/pickupdays")
    Call<Schedule> getScheduleFromZone(@Path("zone_id") String place_id);

}
