package com.cameocoder.capstoneproject;

import com.cameocoder.capstoneproject.model.Place;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitRecollectInterface {
    @GET("/lookup/{lat},{lng}.json")
    Call<Place> getPlace(@Path("lat") int lat, @Path("lng") int lng);

//    @GET("/3/movie/{id}/videos")
//    Call<Videos> getVideos(@Path("id") int id, @Query("api_key") String api_key);
//
//    @GET("/3/movie/{id}/reviews")
//    Call<Reviews> getReviews(@Path("id") int id, @Query("api_key") String api_key);
}
