package com.example.giangtm.baiby.chat.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.giangtm.baiby.chat.interfaces.MessageInteract;
import com.example.giangtm.baiby.chat.models.Message;
import com.example.giangtm.baiby.chat.services.SocketService;

public class MessagePresenter {
    private Context context;
    private MessageInteract messageInteract;
    GetNewMessageBroadcast getNewMessageBroadcast;

    public MessagePresenter() {

    }

    public void attach(Context context, MessageInteract messageInteract) {
        this.context = context;
        this.messageInteract = messageInteract;
        getNewMessageBroadcast = new GetNewMessageBroadcast();
        IntentFilter filter = new IntentFilter("getMessage");
        this.context.registerReceiver(getNewMessageBroadcast,filter);
    }

    public class GetNewMessageBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        Message message = (Message) intent.getSerializableExtra("message");
        messageInteract.updateNewMessage(message);
        }
    }

    public void deattach(){
        this.context.unregisterReceiver(getNewMessageBroadcast);
    }

}
