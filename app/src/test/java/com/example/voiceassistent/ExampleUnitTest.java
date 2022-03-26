package com.example.voiceassistent;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.voiceassistent.service.ParsingHtmlService;

import java.io.IOException;
import java.text.ParseException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws IOException, ParseException {
        ParsingHtmlService.getHoliday("1 января 2022");
    }
}