package com.example.voiceassistent.bean;

import com.example.voiceassistent.bean.Message;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MessageEntity {

    public String text;
    public String date;
    public int isSend;

    public MessageEntity(String text, String date, int isSend) {
        this.text = text;
        this.date = date;
        this.isSend = isSend;
    }

    public MessageEntity(Message message) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

        this.text = message.text;
        this.date = format.format(message.date);
        this.isSend = (message.isSend) ? 1 : 0;
    }

}
