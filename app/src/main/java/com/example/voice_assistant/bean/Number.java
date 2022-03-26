package com.example.voice_assistant.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Number implements Serializable {
    @SerializedName("str")
    @Expose
    public String current;
}