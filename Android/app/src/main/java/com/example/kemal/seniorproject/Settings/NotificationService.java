package com.example.kemal.seniorproject.Settings;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.kemal.seniorproject.MainActivity;
import com.example.kemal.seniorproject.MessageSendActivity;
import com.example.kemal.seniorproject.PostShowActivity;
import com.example.kemal.seniorproject.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

public class NotificationService extends Service {

    private Socket mSocket;
    private String myId;

    public static String userId = "";


    @Override
    public void onCreate() {
        super.onCreate();


        SessionManager.init(getApplicationContext());
        myId = SessionManager.myId;


        mSocket = SocketIO.getConnection(getApplicationContext(), myId, "listen in the background...");

        mSocket.on("PRIVATE MESSAGE", onNewMessage);
        mSocket.on("POST NOTIFICATION", onPostNotification);

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            JSONObject data = (JSONObject) args[0];
            try {
                String name = data.getString("name");
                String senderId = data.getString("senderId");
                String message = data.getString("message");

                if (!userId.equals(senderId)) {

                    Intent intent = new Intent(getApplicationContext(), MessageSendActivity.class);
                    intent.putExtra("userId", senderId);

                    notifyfnc(name, message, intent);
                }

            } catch (Exception ex) {

            }
        }

    };
    private Emitter.Listener onPostNotification = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {


            JSONObject data = (JSONObject) args[0];
            try {
                String companyName = data.getString("companyName");
                String content = data.getString("content");
                String postId = data.getString("postId");

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                notifyfnc(companyName, content, intent);


            } catch (Exception ex) {

            }
        }

    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void notifyfnc(String title, String content, Intent intent) {


        Notification myNotify = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI).
                        setVibrate(new long[]{10, 500, 500, 500}).
                        setLights(Color.MAGENTA, 300, 500).
                        setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, intent, 0)).
                        setSmallIcon(R.drawable.icon)
                .setAutoCancel(true).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, myNotify);
    }
}
