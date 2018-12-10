package com.example.giangtm.baiby.presenters;

import android.content.Context;
import android.util.Log;

import com.example.giangtm.baiby.interfaces.MessageInteract;

import java.io.IOException;
import java.net.Socket;

public class MessagePresenter {
    private Context context;
    private MessageInteract messageInteract;
    private Socket socketOfClient;

    public MessagePresenter(Context context, MessageInteract messageInteract) {
        this.context = context;
        this.messageInteract = messageInteract;
    }

    public void initSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketOfClient = new Socket("localhost", 9999);
//                    messageInteract.connectSocketSuccess();
                    Log.d("giangtm1", "run: success");
                } catch (IOException e) {
                    e.printStackTrace();
//                    messageInteract.connectSocketFail();
                    Log.d("giangtm1", "run: fail");
                }
            }
        }).start();

    }

    public void sendMessage(String message) {


        messageInteract.sendMessageSuccess();
    }
}
