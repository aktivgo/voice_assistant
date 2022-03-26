package com.example.voice_assistant.interfaces;

import com.example.voice_assistant.bean.Forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastApi {
    @GET("/current?access_key=d7db883411892ffc00b338a4b9af4496")
    Call<Forecast> getCurrentWeather(@Query("query") String city);
}
