package com.example.voice_assistant.helpers;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.voice_assistant.interfaces.NumberApi;
import com.example.voice_assistant.bean.Number;
import com.example.voice_assistant.service.NumberService;

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
            public void onResponse(@NonNull Call<Number> call, @NonNull Response<Number> response) {
                Number result = response.body();
                if (result != null) {
                    String answer = result.current;
                    callback.accept(answer);
                }
                else callback.accept("Не получилось получить число");
            }

            @Override
            public void onFailure(@NonNull Call<Number> call, @NonNull Throwable t) {
                Log.w("NUMBER",t.getMessage());
            }
        });
    }
}
