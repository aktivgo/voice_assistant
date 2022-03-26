package com.example.voiceassistent.service;

import com.example.voiceassistent.bean.ForecastApi;
import com.example.voiceassistent.bean.NumberApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NumberService {
    public static NumberApi getApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://htmlweb.ru") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create().create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();

        return retrofit.create(NumberApi.class); //Создание объекта, при помощи которого будут выполняться запросы
    }
}
