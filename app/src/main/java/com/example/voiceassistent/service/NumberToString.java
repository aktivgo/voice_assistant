package com.example.voiceassistent.service;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.voiceassistent.bean.Forecast;
import com.example.voiceassistent.bean.ForecastApi;
import com.example.voiceassistent.bean.NumberApi;
import com.example.voiceassistent.bean.Number;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NumberToString {
    public static void getNumber(String num, final Consumer<String> callback) {
        NumberApi api = NumberService.getApi();
        Call<Number> call = api.getStringNumber(num);

        call.enqueue(new Callback<Number>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<Number> call, Response<Number> response) {
                Number result = response.body();
                if (result != null) {
                    String answer = result.current;
                    callback.accept(answer);
                }
                else callback.accept("Не получилось получить число");

            }

            @Override
            public void onFailure(Call<Number> call, Throwable t) {
                Log.w("NUMBER",t.getMessage());
            }
        });
    }
}
