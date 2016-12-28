package com.cameocoder.capstoneproject;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRecollectService {
    private static final String API_URL = "http://recollect.net/";

    public static RetrofitRecollectInterface createRecollectService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RetrofitRecollectInterface.class);
    }

}
