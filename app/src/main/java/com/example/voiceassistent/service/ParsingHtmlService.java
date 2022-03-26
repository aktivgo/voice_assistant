package com.example.voiceassistent.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ParsingHtmlService {
    private static final String URL = "http://mirkosmosa.ru/holiday/2022";

    public static String getHoliday(String date) throws IOException, ParseException {
        Document document = Jsoup.connect(URL).get();
        Element body = document.body();

        Elements dates = body.getElementsByClass("next_phase month_row");

        for (Element elem : dates) {
            if (elem.getElementsByClass("month_cel_date")
                    .get(0)
                    .getAllElements()
                    .get(1)
                    .text()
                    .equals(date)) {
                Element listHoliday = elem.getElementsByClass("holiday_month_day_holiday").get(0);
                String nameOfHolidays = "";
                for (Element holiday: listHoliday.getElementsByTag("li")) {
                    nameOfHolidays += holiday.text() + "\n";
                };
                return nameOfHolidays;
            }
        }
        return "";
    }
}
