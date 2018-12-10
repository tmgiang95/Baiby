package com.example.giangtm.baiby.services;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.giangtm.baiby.models.Message;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketService extends Service {
    Callbacks callbacks;

    int count = 0;
    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public SocketService getServiceInstance(){
            return SocketService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void registerCallback(Activity activity){
        this.callbacks = (Callbacks)activity;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        count ++;
        Log.d("giangtm1", "onStartCommand: " + count);
        return START_NOT_STICKY;
    }

    public interface Callbacks{
        void updateClient(Message data);
    }

}
