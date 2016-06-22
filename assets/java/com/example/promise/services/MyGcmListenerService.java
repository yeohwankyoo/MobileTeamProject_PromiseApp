package com.example.promise.services;

/**
 * Created by Yeohwankyoo on 2016-06-14.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.promise.LateScreen;
import com.example.promise.ScreenOn;
import com.example.promise.ShowRoomState;
import com.google.android.gms.gcm.GcmListenerService;
import com.example.promise.MainActivity;
import com.example.promise.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 */
public class MyGcmListenerService extends GcmListenerService {

    //Context context;
    ArrayList<String> member = new ArrayList<String>();
    /**
     * This method run executed when message is received.
     * @param from Sender ID of the sender.
     * @param data message that has been received.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        Log.d("test", data.toString());
        String message = data.getString("message");
        Log.d("test2", message);
        try{
            JSONObject json = new JSONObject(message);

            String year = json.getString("year");
            String month = json.getString("month");
            String date = json.getString("date");
            String hour = json.getString("hour");
            String longti = json.getString("longti");
            String lati = json.getString("lati");
            String room = json.getString("room");
            String minute = json.getString("minute");
            int count = json.getInt("count");
            JSONArray json2 = json.getJSONArray("member");
            for(int i=0; i< count; i++)
            {
                String str = json2.get(i).toString();
                JSONObject json3 = new JSONObject(str);
                Log.d("Before ", json3.getString("memberID"));
                member.add(json3.getString("memberID"));
                Log.d("member ", member.get(i));
            }
            Intent intent = new Intent(this, ShowRoomState.class);//
            intent.putExtra("year", year);
            intent.putExtra("month", month);
            intent.putExtra("date", date);
            intent.putExtra("hour", hour);
            intent.putExtra("minute", minute);
            intent.putExtra("longti", longti);
            intent.putExtra("lati", lati);
            intent.putExtra("room", room);
            intent.putExtra("count", count);
            intent.putExtra("member", member);
            intent.putExtra("Check", "gcm");
            sendNotification(year, month, date, message, intent);
           Toast.makeText(getApplicationContext(), "longti: "+ longti + " lati: " + lati + " rom :" +room, Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            Log.d("Error: ", "JSON Parsing Error");
        }
        ;
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String year, String month, String date, String message, Intent intent) {

        intent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        PendingIntent pit = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);// 지각했다는 화면 띄워줌


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.promise_gcm)
                .setContentTitle("Promise")
                .setContentText("We will meet in " + year + month + date)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pit);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());



    }
}
