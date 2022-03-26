package com.example.voice_assistant.service;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voice_assistant.R;
import com.example.voice_assistant.models.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MessageViewHolder extends RecyclerView.ViewHolder{
    protected TextView messageText;
    protected TextView messageDate;
    protected TextView messageName;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);

        messageText = itemView.findViewById(R.id.messageTextView);
        messageDate = itemView.findViewById(R.id.messageDateView);
        messageName = itemView.findViewById(R.id.messageNameView);
    }

    public void bind(@NonNull Message message) {
        messageText.setText(message.text);
        DateFormat fmt = new SimpleDateFormat("HH:mm:ss");
        messageDate.setText(fmt.format(message.date));
        messageName.setText(message.isSend ? "Вы" : "Май Сакурадзима");
    }
}
