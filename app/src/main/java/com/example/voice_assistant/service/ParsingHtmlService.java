package com.example.voice_assistant.service;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ParsingHtmlService {
    private static final String URL = "http://mirkosmosa.ru/holiday/2022";

    @NonNull
    public static String getHoliday(String date) throws IOException {
        Document document = Jsoup.connect(URL).get();
        Element body = document.body();

        Elements dates = body.getElementsByClass("next_phase month_row");

        for (Element elem : dates) {
            if (elem.getElementsByClass("month_cel_date")
                    .get(0)
                    .getAllElements()
                    .get(1)
                    .text()
                    .equals(date)
            ) {
                Element listHoliday = elem.getElementsByClass("holiday_month_day_holiday")
                        .get(0);

                StringBuilder nameOfHolidays = new StringBuilder();

                for (Element holiday: listHoliday.getElementsByTag("li")) {
                    nameOfHolidays.append(holiday.text()).append("\n");
                }

                return nameOfHolidays.toString();
            }
        }

        return "Не удалось получить ответ";
    }
}
