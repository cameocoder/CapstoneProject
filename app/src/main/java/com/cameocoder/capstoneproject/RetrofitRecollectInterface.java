package com.cameocoder.capstoneproject;

import com.cameocoder.capstoneproject.model.Places;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitRecollectInterface {
    @GET("api/lookup/{lat},{lng}.json")
    Call<Places> getPlace(@Path("lat") double lat, @Path("lng") double lng);

    @GET("api/places/{place_id}/services/waste/events")
    Call<Places> getSchedule(@Path("place_id") String place_id);

}
