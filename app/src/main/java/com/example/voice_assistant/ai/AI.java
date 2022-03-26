package com.example.voice_assistant.ai;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.voice_assistant.helpers.ForecastToString;
import com.example.voice_assistant.helpers.NumberToString;
import com.example.voice_assistant.service.ParsingHtmlService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AI {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getAnswer(String question, final Consumer<String> callback) throws ParseException {
        HashMap<String, String> answers = new HashMap<>();
        answers.put("прив", "Привет");
        answers.put("как дела", "Не плохо");
        answers.put("чем занимаешься", "Отвечаю на вопросы");
        answers.put("что делаешь", "Отвечаю на вопросы");
        answers.put("анекдот", "Звонок в Киев.\n" +
                "- Алло. Цэ Украина?\n" +
                "- Пока да, но говорите быстрее!");
        answers.put("май", "Привет, Сакута");
        answers.put("одиночество", "Я ведь привыкла быть одной. " +
                "Не волнуйся. Ну забудешь ты меня — это мелочи.");
        answers.put("перемены", "Каждый день слышно фразочки вроде: «мне скучно»" +
                " или «тут хоть что-нибудь интересное происходит?»" +
                " Но на самом деле, никто не ищет перемен.");
        answers.put("признание", "Может быть наш мир настолько прост, " +
                "что одно признание может перевернуть его на 180 градусов.");

        question = question.toLowerCase(Locale.ROOT);

        for (HashMap.Entry<String, String> entry : answers.entrySet()) {
            if (question.toLowerCase(Locale.ROOT).contains(entry.getKey()))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    callback.accept(String.join(", ", answers.get(entry.getKey())));
                    return;
                }
        }
        if (question.contains("какой сегодня день недели")) {
            String dayOfWeek = LocalDate.now().getDayOfWeek().getDisplayName(
                    TextStyle.FULL,
                    new Locale("ru")
            );
            callback.accept("Сeгодня " + dayOfWeek);
        } else if (question.contains("какой сегодня день")) {
            String day = LocalDate.now().getDayOfMonth() + " " + LocalDate.now().getMonth()
                    .getDisplayName(
                        TextStyle.FULL,
                        new Locale("ru")
                    );
            callback.accept("Сeгодня " +day);
        } else if (question.contains("который час") | question.contains("сколько времени") | question.contains("время")) {
            String time = LocalDateTime.now().getHour() + ":" +
                    (LocalDateTime.now().getMinute() < 10
                            ? "0" + LocalDateTime.now().getMinute()
                            : LocalDateTime.now().getMinute()
                    );
            callback.accept(time);
        } else if (question.contains("погода")) {
            Pattern cityPattern = Pattern.compile(
                    "погода в городе (\\p{L}+)",
                    Pattern.CASE_INSENSITIVE
            );
            Matcher matcher = cityPattern.matcher(question);
            if (matcher.find()) {
                String cityName = matcher.group(1);
                ForecastToString.getForecast(cityName, weatherString -> {
                    if (weatherString.equals("Не могу узнать погоду")) {
                        callback.accept(weatherString);
                    } else {
                        final int number = Integer.parseInt(weatherString);
                        NumberToString.getNumber(weatherString, numberString -> {
                            String weatherResult = "В городе " +
                                    prepareCityName(Objects.requireNonNull(cityName)) + " ";
                            weatherResult += (number < 0) ? "минус " + numberString : numberString;

                            if (number / 10 % 10 == 1) {
                                weatherResult += " градусов";
                            } else if (number % 10 == 1) {
                                weatherResult += " градус";
                            } else if (number % 10 == 2 || number % 10 == 4 || number % 10 == 3) {
                                weatherResult += " градуса";
                            } else {
                                weatherResult += " градусов";
                            }

                            callback.accept(weatherResult);
                        });
                    }
                });
            }
        } else if (question.contains("сколько дней до")) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate dateResult = LocalDate.now();
            String date = question.substring(question.lastIndexOf(" ") + 1);
            try {
                dateResult = LocalDate.parse(date, format);
            } catch (Exception exception) {
                callback.accept("Пожалуйста, введи верную дату");
                return;
            }

            Period period = Period.between(LocalDate.now(), dateResult);
            String result = getNormalizedDate(period);

            callback.accept(result);
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
                dateString = getDate(date);
            } else {
                dateString = getDate(question);
            }

            Observable.fromCallable(() -> ParsingHtmlService.getHoliday(dateString))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback::accept);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            callback.accept(String.join(", ", "К сожалению, я тебя не поняла"));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    private static String getNormalizedDate(@NonNull Period period) {
        String result = "Машину времени ещё не изобрели";

        if (period.getDays() >= 0) {
            int y = period.getYears();
            int m = period.getMonths();
            int d = period.getDays();


            result = "";
            if (y > 0) {
                result += y;

                if (y == 1 || (y > 20 && y % 10 == 1)) {
                    result += " год";
                } else if (y < 5 || (y > 20 && y % 10 >= 2 && y % 10 < 5)) {
                    result += " года";
                } else {
                    result += " лет";
                }
            }

            if (m > 0) {
                result += " " + m;

                if (m == 1) {
                    result += " месяц";
                } else if (m < 5) {
                    result += " месяца";
                } else {
                    result += " месяцев";
                }
            }

            if (d > 0) {
                result += " " + d;

                if (d == 1 || (d > 20 && d % 10 == 1)) {
                    result += " день";
                } else if (d < 5 || (d > 20 && d % 10 >= 2 && d % 10 < 5)) {
                    result += " дня";
                } else {
                    result += " дней";
                }
            }
        }

        return result;
    }

    @NonNull
    private static String prepareCityName(@NonNull String cityName) {
        String firstLetter = String.valueOf(cityName.charAt(0)).toUpperCase();
        String tail = cityName.substring(1);
        return firstLetter + tail;
    }

    @NonNull
    private static String getDate(@NonNull String date) throws ParseException {
        String[] dateNumbers = date.split("[^\\d]+", -1);
        Date nextDate;
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        DateFormat dateFormatForParse = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        nextDate = dateFormatForParse.parse(dateNumbers[1] + "." + dateNumbers[2] + "." + dateNumbers[3]);
        return dateFormat.format(Objects.requireNonNull(nextDate));
    }

    @NonNull
    private static String getDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }
}

