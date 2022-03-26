package com.example.voiceassistent.service;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.voiceassistent.bean.Forecast;
import com.example.voiceassistent.bean.ForecastApi;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastToString {
    public static void getForecast(String city, final Consumer<String> callback) {
        ForecastApi api = ForecastService.getApi();
        Call<Forecast> call = api.getCurrentWeather(city);

        call.enqueue(new Callback<Forecast>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                Forecast result = response.body();
                if (result != null) {
                    String answer = result.current.temperature.toString();
                    callback.accept(answer);
                }
                else {
                    callback.accept("Не могу узнать погоду");
                }
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.w("WEATHER",t.getMessage());
            }
        });
    }
}
