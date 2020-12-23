package com.example.myevstation_android.remote;

import com.example.myevstation_android.model.Station;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("station/myLocation/")
    Call<List<Station>> getStation(@Query("lat") Double lat, @Query("lng") Double lng);

}
