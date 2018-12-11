package com.example.giangtm.baiby.presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.giangtm.baiby.interfaces.MessageInteract;
import com.example.giangtm.baiby.services.SocketService;
import com.example.giangtm.baiby.views.activities.MainActivity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MessagePresenter {
    private Context context;
    private MessageInteract messageInteract;
    private Socket socketOfClient;
    private BufferedWriter os;

    public MessagePresenter(Context context, MessageInteract messageInteract) {
        this.context = context;
        this.messageInteract = messageInteract;
    }

    public void initSocket() {
        new ConnectSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



    public void sendMessage(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    os.write(message);
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        messageInteract.sendMessageSuccess();
    }

    class ConnectSocket extends AsyncTask<Void, Void, Void> {
        boolean result = false;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                socketOfClient = new Socket("192.168.232.2", 9999);
                os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
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
                messageInteract.connectSocketSuccess();
            } else {
                messageInteract.connectSocketFail();
            }

        }
    }


}
