package com.example.giangtm.baiby.chat.views.activities;


import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.giangtm.baiby.R;
import com.example.giangtm.baiby.chat.interfaces.MessageInteract;
import com.example.giangtm.baiby.chat.models.Message;
import com.example.giangtm.baiby.chat.presenters.MessagePresenter;
import com.example.giangtm.baiby.chat.services.SocketService;
import com.example.giangtm.baiby.chat.views.adapters.MessageAdapter;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import static com.example.giangtm.baiby.Constants.OUTCOMMINGMESSAGE;

public class MainActivity extends AppCompatActivity implements MessageInteract, SocketService.Callbacks {
    ArrayList<Message> messages = new ArrayList<>();
    RecyclerView rvMessage;
    MessageAdapter adapter;
    EditText inputTextMessage;
    TextView tvUserDescription;
    Button btnSend;
    MessagePresenter presenter = new MessagePresenter();
    SocketService socketService;
    ImageView btnConfig;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConfig = findViewById(R.id.btnConfig);
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
//            } else {
//
//            }
                builder = new AlertDialog.Builder(MainActivity.this);
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.config_dialog,null);
                final EditText etIP = view.findViewById(R.id.etIP);
                final EditText etPort = view.findViewById(R.id.etPort);
                String configIP = sp.getString("config_IP","127.0.0.1");
                int configPORT = sp.getInt("config_PORT",9999);
                etIP.setText(configIP);
                etPort.setText(String.valueOf(configPORT));
                builder.setTitle("Setting")
                        .setView(view)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editor = sp.edit();
                                editor.putString("config_IP",etIP.getText().toString());
                                editor.putInt("config_PORT",Integer.parseInt(etPort.getText().toString()));
                                editor.apply();
                                dialog.dismiss();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        }).show();
            }
        });
        configureToolbar();
        startSocketService();
        configureInputTextMessage();
        configureRecyclerView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.deattach();
        stopService(new Intent(MainActivity.this,SocketService.class));
    }

    private void startSocketService() {
        ServiceConnection mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've binded to LocalService, cast the IBinder and get LocalService instance
                SocketService.LocalBinder binder = (SocketService.LocalBinder) service;
                socketService = binder.getServiceInstance(); //Get instance of your service!
                socketService.initSocket();
                socketService.registerCallback(MainActivity.this); //Activity register in the service as client for callabcks!
                presenter.attach(getBaseContext(),MainActivity.this);

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

    private void configureToolbar() {
        tvUserDescription = findViewById(R.id.tvUserDescription);
    }

    private void configureInputTextMessage() {
        inputTextMessage = findViewById(R.id.inputTextMessage);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputTextMessage.getText().toString();
                if (socketService != null) {
                    socketService.sendMessage(new Message(OUTCOMMINGMESSAGE,
                            message,
                            System.currentTimeMillis()));
                    inputTextMessage.setText("");
                }
            }
        });


    }

    private void configureRecyclerView() {
        rvMessage = findViewById(R.id.rvMessage);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        adapter = new MessageAdapter(messages);
        rvMessage.setAdapter(adapter);
        rvMessage.setLayoutManager(layoutManager);
    }


    @Override
    public void connectSocketSuccess() {
        Toast.makeText(this, "Connect Successed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectSocketFail() {
        Toast.makeText(this, "Connect Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void sendMessageSuccess(Message message) {
        tvUserDescription.setVisibility(View.VISIBLE);
        messages.add(message);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void sendMessageFail() {
        Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateNewMessage(Message message) {
        messages.add(message);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
