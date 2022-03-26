package com.example.voice_assistant.interfaces;

import com.example.voice_assistant.bean.Number;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NumberApi {
    @GET("/json/convert/num2str?dec=0")
    Call<Number> getStringNumber(@Query("num") String num);
}
