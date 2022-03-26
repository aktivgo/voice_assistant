package com.example.voiceassistent.service;

import com.example.voiceassistent.bean.ForecastApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastService {
    public static ForecastApi getApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.weatherstack.com") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create().create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();

        return retrofit.create(ForecastApi.class); //Создание объекта, при помощи которого будут выполняться запросы
    }
}
