package com.example.giangtm.baiby.chat.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.giangtm.baiby.chat.models.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static com.example.giangtm.baiby.Constants.INCOMMINGMESSAGE;

public class SocketService extends Service {
    Callbacks callbacks;
    private Socket socketOfClient;
    private BufferedWriter os;
    private BufferedReader is;
    SharedPreferences sp;


    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public SocketService getServiceInstance() {
            return SocketService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void registerCallback(Activity activity) {
        this.callbacks = (Callbacks) activity;
    }

    public void initSocket() {
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        new ConnectSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getMessage() {
        GetMessageToSever getMessageToSever = new GetMessageToSever();
        getMessageToSever.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void sendMessage(final Message message) {
        new SendMessageToSever(message).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                    Intent intent = new Intent();
//                    intent.putExtra("message",message);
//                    intent.setAction("getMessage");
//                    sendBroadcast(intent);

    }

    class ConnectSocket extends AsyncTask<Void, Void, Void> {
        boolean result = false;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String configIP = sp.getString("config_IP","127.0.0.1");
                int configPORT = sp.getInt("config_PORT",9999);
                socketOfClient = new Socket(configIP, configPORT);
                os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
                is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
                getMessage();
                result = true;
                Log.d("giangtm1", "run: success");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("giangtm1", "run: fail");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result) {
                callbacks.connectSocketSuccess();
            } else {
                callbacks.connectSocketFail();
            }

        }
    }

    class SendMessageToSever extends AsyncTask<Void, Void, Void> {
        boolean result = false;
        Message message;

        public SendMessageToSever(Message message) {
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(os != null) {
                    os.write(message.getMessage());
                    os.flush();
                    result = true;
                    Log.d("giangtm1", "send: success");
                } else {
                    result = false;
                }

            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                Log.d("giangtm1", "send: fail");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result) {
                callbacks.sendMessageSuccess(message);
            } else {
                callbacks.sendMessageFail();
            }

        }
    }

    class GetMessageToSever extends AsyncTask<Void, Void, Void> {
        boolean result = false;
        boolean isRunning = true;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                while (isRunning) {
                    String line = is.readLine();
                    if (line != null) {
                        Message message = new Message(INCOMMINGMESSAGE, line, System.currentTimeMillis());
                        Intent intent = new Intent();
                        intent.putExtra("message", message);
                        intent.setAction("getMessage");
                        sendBroadcast(intent);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                Log.d("giangtm1", "send: fail");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public interface Callbacks {
        void connectSocketSuccess();

        void connectSocketFail();

        void sendMessageSuccess(Message message);

        void sendMessageFail();
    }

}
