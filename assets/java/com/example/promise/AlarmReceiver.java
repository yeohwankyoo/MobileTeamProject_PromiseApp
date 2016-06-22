package com.example.promise;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.Editable;
import android.util.Log;
import android.widget.Toast;
import android.media.AudioManager;
/**
 * Created by 광선 on 2016-06-08.
 */
public class AlarmReceiver extends BroadcastReceiver {

    /**
     * 받았을 때 호출되는 메소드
     *
     * @param context
     * @param intent
     */

    SharedPreferences sp,score;
    SharedPreferences.Editor e,e2;

    PopUp AActivity = (PopUp) this.AActivity;

    @Override
    public void onReceive(Context context, Intent intent) {

        int good,bad;
        score = context.getSharedPreferences("goodbad",Context.MODE_PRIVATE);
        e2 = score.edit();
        good = score.getInt("good",0);
        bad = score.getInt("bad",0);

        sp = context.getSharedPreferences("onSpot",context.MODE_PRIVATE);
        e = sp.edit();
        int num = sp.getInt("key",-1);// 1 = 도착해있음, -1 = 도착안함


        if(num == -1)// 약속장소에 도착 못했으면...
        {
            bad++;
            e2.putInt("bad",bad);
            e2.commit();

            Intent late = new Intent(context, LateScreen.class);// 지각 시 지각화면 intent를 위해
            PendingIntent pit = PendingIntent.getActivity(context, 0, late, 0);// 지각했다는 화면 띄워줌
            try {
                pit.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);// 강제로 소리모드로 바꿔줌

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
            //Toast.makeText(context, "알람!!!!!!! "+" num="+sp.getInt("key",-1), Toast.LENGTH_LONG).show();
            //------------------------------------ Notification -----------------------------------//
            //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // pendingIntent = notification 눌렀을 때 앱켜준다 //
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, LoadingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.icon2).setTicker("HETT").setWhen(System.currentTimeMillis())
                    .setNumber(1).setContentTitle("약속해줘").setContentText("약속 시간을 어겼다네~ ")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS).setContentIntent(pendingIntent).setAutoCancel(true);

            notificationmanager.notify(1, builder.build());
            //---------------------------------------------------------------------------------------//

            new ScreenOn().acquireCpuWakeLock(context);// 화면 꺼져있으면 켜짐
        }
        else// 약속 장소에 도착했으면 !
        {
            good++;
            e2.putInt("good",good);
            e2.commit();

            Intent ontime = new Intent(context, OnTime.class);// 지각 시 지각화면 intent를 위해
            PendingIntent pit = PendingIntent.getActivity(context, 0, ontime, 0);// 지각했다는 화면 띄워줌
            try {
                pit.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // 아래에 있는 pendingIntent => notification 눌렀을 때 앱켜준다 //
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, LoadingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.icon2).setTicker("HETT").setWhen(System.currentTimeMillis())
                    .setNumber(1).setContentTitle("약속해줘").setContentText("약속을 지켰다네! ")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS).setContentIntent(pendingIntent).setAutoCancel(true);
            notificationmanager.notify(1, builder.build());

            new ScreenOn().acquireCpuWakeLock(context);

            e.putInt("key", -1);
            e.commit();
        }

    }

}