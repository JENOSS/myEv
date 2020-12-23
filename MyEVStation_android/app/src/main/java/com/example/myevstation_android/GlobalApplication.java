package com.example.myevstation_android;

import android.app.Application;

import com.example.myevstation_android.remote.ApiService;
import com.example.myevstation_android.remote.RetrofitClient;
import com.google.android.gms.common.api.Api;

import retrofit2.Retrofit;

public class GlobalApplication extends Application {
    private static volatile GlobalApplication instance = null;
    private ApiService apiService;
    private Retrofit retrofit;

    private static String BASE_URL = "http://localhost:8080/";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

    public ApiService getApiService(){
        if (apiService == null){
            apiService = getRetrofit().create(ApiService.class);
        }
        return apiService;
    }

    public Retrofit getRetrofit(){
        if(retrofit == null){
            RetrofitClient retrofitClient = new RetrofitClient();
            retrofit= retrofitClient.getClient(BASE_URL);
        }
        return retrofit;
    }
}
