package com.example.giangtm.baiby.views.activities;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.giangtm.baiby.R;
import com.example.giangtm.baiby.interfaces.MessageInteract;
import com.example.giangtm.baiby.models.Message;
import com.example.giangtm.baiby.presenters.MessagePresenter;
import com.example.giangtm.baiby.services.SocketService;
import com.example.giangtm.baiby.views.adapters.MessageAdapter;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MessageInteract, SocketService.Callbacks {
    ArrayList<Message> messages = new ArrayList<>();
    RecyclerView rvMessage;
    EditText inputTextMessage;
    TextView tvUserDescription;
    Button btnSend;
    MessagePresenter presenter;
    SocketService socketService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MessagePresenter(getBaseContext(), this);
        configureToolbar();
        configureInputTextMessage();
        configureRecyclerView();
        presenter.initSocket();

    }

    private void configureToolbar() {
        tvUserDescription = findViewById(R.id.tvUserDescription);
    }

    private void configureInputTextMessage() {
        inputTextMessage = findViewById(R.id.inputTextMessage);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.sendMessage(inputTextMessage.getText().toString());
            }
        });


    }

    private void configureRecyclerView() {
        rvMessage = findViewById(R.id.rvMessage);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        MessageAdapter adapter = new MessageAdapter(messages);
        rvMessage.setAdapter(adapter);
        rvMessage.setLayoutManager(layoutManager);
    }

    private void generateSampleData() {
        Message message;
        for (int i = 0; i < 100
                ; i++) {
            int type;
            String mess;
            if (i % 2 == 0) {
                type = 0;
                mess = "This is demo Sent Message " + i;
            } else {
                type = 1;
                mess = "This is demo Receive Message " + i;
            }
            message = new Message(type, mess, System.currentTimeMillis());
            messages.add(message);
        }
    }



    @Override
    public void sendMessageSuccess() {
        tvUserDescription.setVisibility(View.VISIBLE);
    }

    @Override
    public void connectSocketSuccess() {
        Toast.makeText(this, "Connect Successed", Toast.LENGTH_SHORT).show();
        ServiceConnection mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                Toast.makeText(MainActivity.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
                // We've binded to LocalService, cast the IBinder and get LocalService instance
                SocketService.LocalBinder binder = (SocketService.LocalBinder) service;
                socketService = binder.getServiceInstance(); //Get instance of your service!
                socketService.registerCallback(MainActivity.this); //Activity register in the service as client for callabcks!
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                Toast.makeText(MainActivity.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();
            }
        };
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        startService(intent);

    }

    @Override
    public void connectSocketFail() {
        Toast.makeText(this, "Connect Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateClient(Message data) {
        Toast.makeText(this, data.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
