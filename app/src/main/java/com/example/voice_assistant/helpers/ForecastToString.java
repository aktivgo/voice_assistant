package com.example.voice_assistant.helpers;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.voice_assistant.bean.Forecast;
import com.example.voice_assistant.interfaces.ForecastApi;
import com.example.voice_assistant.service.ForecastService;

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
            public void onResponse(@NonNull Call<Forecast> call, @NonNull Response<Forecast> response) {
                String answer = "Не могу узнать погоду";

                Forecast result = response.body();
                if (result != null && result.current != null && result.current.temperature != null) {
                    answer = result.current.temperature.toString();
                }

                callback.accept(answer);
            }

            @Override
            public void onFailure(@NonNull Call<Forecast> call, @NonNull Throwable t) {
                Log.w("WEATHER", t.getMessage());
            }
        });
    }
}
