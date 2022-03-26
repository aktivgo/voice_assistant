package com.example.voice_assistant.service;

import androidx.annotation.NonNull;

import com.example.voice_assistant.interfaces.NumberApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NumberService {
    @NonNull
    public static NumberApi getApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://htmlweb.ru")
                .addConverterFactory(GsonConverterFactory.create().create())
                .build();
        return retrofit.create(NumberApi.class);
    }
}
