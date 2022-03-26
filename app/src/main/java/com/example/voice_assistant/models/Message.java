package com.example.voice_assistant.models;

import androidx.annotation.NonNull;

import com.example.voice_assistant.entity.MessageEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    public String text;
    public Date date;
    public Boolean isSend;

    public Message(String text, Boolean isSend) {
        this.text = text;
        this.isSend = isSend;
        this.date = new Date();
    }

    public Message(@NonNull MessageEntity entity) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        this.text = entity.text;
        this.isSend = entity.isSend == 1;
        this.date = format.parse(entity.date);
    }
}
