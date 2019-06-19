package com.example.kemal.seniorproject.Settings;

import android.app.Activity;
import android.content.Context;

import com.example.kemal.seniorproject.MessageSendActivity;
import com.example.kemal.seniorproject.R;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class SocketIO {
    private static Socket mSocket;

    public static Socket getConnection(Context context, String myId, String status) {

        if (mSocket == null) {
            try {

                mSocket = IO.socket("https://kemaltuncc.herokuapp.com/");
                mSocket.connect();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", myId);
                jsonObject.put("status", status);
                mSocket.emit("connect user", jsonObject);

            } catch (URISyntaxException ex) {

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (mSocket != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", myId);
                jsonObject.put("status", status);
                mSocket.emit("check", jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return mSocket;
    }

    public static void disconnect() {
       /* mSocket.emit("disconnect user", myId);
        mSocket.disconnect();
        mSocket = null;*/

        mSocket.disconnect();
        mSocket = null;


    }


}
