package com.example.voiceassistent.service;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AI {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getAnswer(String question, final Consumer<String> callback) throws ParseException, IOException {
        HashMap<String, String> answers = new HashMap<String, String>();
        answers.put("прив", "Вітання");
        answers.put("как дела", "Погано");
        answers.put("чем занимаешься", "Відпочиваю");
        answers.put("кто ты", "Хто я? Президент України? " +
                "успішний адвокат? звичайна домогосподарка? " +
                "студент-філософ із Могилянки? агроном із Черкаської області? \n" +
                "Хто я? Той, хто десять років живе за кордоном та любить Україну в інтернеті? \n" +
                "Той, хто втратив усе у Криму, і почав усе з нуля у Харкові? " +
                "Айтішник, який мріє втекти з країни? \n" +
                "Чи полонений, який мріяв повернутися додому?");

        question = question.toLowerCase(Locale.ROOT);

        for (HashMap.Entry<String, String> entry : answers.entrySet()) {
            if (question.toLowerCase(Locale.ROOT).contains(entry.getKey()))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    callback.accept(String.join(", ", answers.get(entry.getKey())));
                    return;
                }
        }
        if (question.contains("какой сегодня день")) {
            String day = LocalDate.now().getDayOfMonth() + " " + LocalDate.now().getMonth().getDisplayName(
                    TextStyle.FULL,
                    new Locale("uk_UA"));
            callback.accept(day);
        } else if (question.contains("который час") | question.contains("сколько времени") | question.contains("время")) {
            String time = LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute();
            callback.accept(String.format(new Locale("uk_UA"), time));
        } else if (question.contains("какой день недели")) {
            String dayOfWeek = LocalDate.now().getDayOfWeek().getDisplayName(
                    TextStyle.FULL,
                    new Locale("uk_UA"));
            callback.accept(dayOfWeek);
        } else if (question.contains("погода")) {
            Pattern cityPattern = Pattern.compile("погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = cityPattern.matcher(question);
            if (matcher.find()) {
                String cityName = matcher.group(1);
                ForecastToString.getForecast(cityName, new Consumer<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void accept(String weatherString) {
                        final int number = Integer.parseInt(weatherString);
                        NumberToString.getNumber(weatherString, new Consumer<String>() {
                            @Override
                            public void accept(String numberString) {
                                String weatherString = (number < 0) ? "Сейчас где-то минус " + numberString : "Сейчас где-то " + numberString;
                                if (number / 10 % 10 == 1) {
                                    weatherString += " градусов";
                                } else if (number % 10 == 1) {
                                    weatherString += " градус";
                                } else if (number % 10 == 2 || number % 10 == 4 || number % 10 == 3) {
                                    weatherString += " градуса";
                                } else {
                                    weatherString += " градусов";
                                }
                                callback.accept(weatherString);
                            }
                        });
                    }
                });
            }
        } else if (question.contains("сколько дней до")) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate dateResult = LocalDate.now();
            if (question.contains("сколько дней до")) {
                String date = question.substring(question.lastIndexOf(" ") + 1);
                try {
                    dateResult = LocalDate.parse(date, format);
                } catch (Exception exception) {
                    callback.accept("Неверно введена дата");
                }
            }
            callback.accept(String.valueOf(Period.between(LocalDate.now(), dateResult).getDays()));
        } else if (question.contains("праздник")) {
            Date date = null;
            if (question.contains("сегодня")) {
                date = new Date();
            }
            else if (question.contains("завтра")) {
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                cal.add(Calendar.DAY_OF_YEAR, 1);
                date = cal.getTime();
            }
            else if (question.contains("вчера")) {
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                cal.add(Calendar.DAY_OF_YEAR, -1);
                date = cal.getTime();
            }

            String dateString;
            if (date != null) {
                dateString = getDate(date, "dd MMMM yyyy");
            }
            else {
                dateString = getDate(question, "dd MMMM yyyy");
            }
            /*new AsyncTask<String, Integer, Void>() {
                String answer;
                @Override
                protected Void doInBackground(String... strings) {
                    try {
                        answer = ParsingHtmlService.getHoliday(strings[0]);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        callback.accept(String.join(", ", answer));
                    }
                }
            }.execute(dateString);*/
            Observable.fromCallable(() -> ParsingHtmlService.getHoliday(dateString))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback::accept);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            callback.accept(String.join(", ", "Навіть не знаю, що й відповісти"));
        }
    }

    @NonNull
    private static String getDate(@NonNull String date, String format) throws ParseException {
        String[] dateNumbers = date.split("[^\\d]+", -1);
        Date nextDate;
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        DateFormat dateFormatForParse = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        nextDate = dateFormatForParse.parse(dateNumbers[1] + "." + dateNumbers[2] + "." + dateNumbers[3]);
        return dateFormat.format(nextDate);
    }

    @NonNull
    private static String getDate(Date date, String format) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(date);
    }
}

