package com.example.voiceassistent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.voiceassistent.service.AI;
import com.example.voiceassistent.bean.Message;
import com.example.voiceassistent.service.DBHelper;
import com.example.voiceassistent.bean.MessageEntity;
import com.example.voiceassistent.service.MessageListAdapter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    protected Button sendButton;
    protected EditText questionText;
    protected TextToSpeech textToSpeech;
    protected RecyclerView chatMessageList;
    protected MessageListAdapter messageListAdapter;
    SharedPreferences sPref;

    public static final String APP_PREFERENCES = "mysettings";
    private boolean isLight = true;
    private String THEME = "THEME";

    DBHelper dBHelper;
    SQLiteDatabase database;
    Cursor cursor;

    @Override
    protected void onDestroy() {
        cursor.close();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(THEME, isLight);

        editor.apply();

        database.delete(dBHelper.TABLE_MESSAGES, null, null);

        for (int i = 0; i < messageListAdapter.messageList.size(); i++)
        {
            MessageEntity entity = new MessageEntity(messageListAdapter.messageList.get(i));

            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.FIELD_MESSAGE, entity.text);
            contentValues.put(DBHelper.FIELD_SEND, entity.isSend);
            contentValues.put(DBHelper.FIELD_DATE, entity.date);

            database.insert(dBHelper.TABLE_MESSAGES,null,contentValues);
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.day_settings:
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                isLight = true;
                break;
            case R.id.night_settings:
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                isLight = false;
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        isLight = sPref.getBoolean(THEME, true);

        if (isLight) getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        dBHelper = new DBHelper(this);
        database = dBHelper.getWritableDatabase();
        cursor = database.query(dBHelper.TABLE_MESSAGES, null, null, null, null, null, null);
        messageListAdapter = new MessageListAdapter();

        if (cursor.moveToFirst()){
            int messageIndex = cursor.getColumnIndex(dBHelper.FIELD_MESSAGE);
            int dateIndex = cursor.getColumnIndex(dBHelper.FIELD_DATE);
            int sendIndex = cursor.getColumnIndex(dBHelper.FIELD_SEND);

            do{
                MessageEntity entity = new MessageEntity(cursor.getString(messageIndex),
                        cursor.getString(dateIndex), cursor.getInt(sendIndex));
                Message message = null;
                try {
                    message = new Message(entity);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                messageListAdapter.messageList.add(message);
            }while (cursor.moveToNext());
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatMessageList = findViewById(R.id.chatMessageList);
        sendButton = findViewById(R.id.sendButton);
        questionText = findViewById(R.id.questionField);
        //chatWindow = findViewById(R.id.chatWindow);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(new Locale("ru"));
                    textToSpeech.setPitch((float)0.3);
                    textToSpeech.setSpeechRate((float)1);
                }
            }
        });

        chatMessageList.setLayoutManager(new LinearLayoutManager(this));
        chatMessageList.setAdapter(messageListAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onSend();
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onSend() throws ParseException, IOException {
        String text = questionText.getText().toString();

        if (text.equals("")) return;

        AI.getAnswer(text, new Consumer<String>() {
            @Override
            public void accept(String answer) {
                messageListAdapter.messageList.add(new Message(text, true));
                messageListAdapter.messageList.add(new Message(answer, false));
                textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null, null);

                messageListAdapter.notifyDataSetChanged();
                chatMessageList.scrollToPosition(messageListAdapter.messageList.size() - 1);
            }
        });

        questionText.setText("");
    }
/*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        *//*messageListAdapter.notifyDataSetChanged();
        chatMessageList.scrollToPosition(messageListAdapter.messageList.size()-1);*//*

        outState.putAll(outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        *//*messageListAdapter.notifyDataSetChanged();
        chatMessageList.scrollToPosition(messageListAdapter.messageList.size()-1);*//*
    }*/

}