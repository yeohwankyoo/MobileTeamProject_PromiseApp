package com.example.promise;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.view.View.OnClickListener;

import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import android.content.BroadcastReceiver;

import android.content.IntentFilter;

import android.provider.Settings;


public class PopUp extends Activity implements AdapterView.OnItemSelectedListener {


    Intent make_room;
    Calendar calendar;


    //----------------------- gps -------------------------------//

    private static final String TAG = "MainActivity";

    private LocationManager mLocationManager;
    private LocationReceiver mIntentReceiver;

    ArrayList mPendingIntentList;

    String intentKey = "coffeeProximity";

    //----------------------- gps -------------------------------//
    //ArrayList<SharedPreferences> pfAL = new ArrayList<SharedPreferences>();
    //ArrayList<SharedPreferences.Editor> edt = new ArrayList<SharedPreferences.Editor>();
    SharedPreferences pfAL, pfID, alarmID, LocationAlarmCode;
    SharedPreferences.Editor edt, edt2, alarmEdt, ELocationAlarmCode;

    String[] year = new String[10];
    String[] month = new String[12];
    String[] date = new String[31];
    String[] hour = new String[24];
    String[] minute = new String[60];
    String yyyy;
    Spinner s1, s2, s3, s4, s5;
    String[] t = new String[5];
    Button b1, b2;
    EditText et;  //방이름

    int alarm_code;
    String when = "";
    String result = "x", lati = "x", longti = "x";
    Intent intent;
    PendingIntent pi;
    AlarmManager am;
    PendingIntent sender;
    EditText editText;
    int check = 0;
    int mCount;
    ArrayList<String> memberID;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);// 액티비티 타이틀바 없애줌
        //// 뒷배경 흐리게 처리 ////
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;// 뒷배경의 어두운 정도
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.popup);

        //--------- gps ---------//
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mPendingIntentList = new ArrayList();
        //-----------------------//

        pfID = getSharedPreferences("PFID", MODE_PRIVATE);
        edt2 = pfID.edit();

        final Intent Listinfo = getIntent();

        s1 = (Spinner) findViewById(R.id.year);
        s1.setOnItemSelectedListener(this);
        s2 = (Spinner) findViewById(R.id.month);
        s2.setOnItemSelectedListener(this);
        s3 = (Spinner) findViewById(R.id.date);
        s3.setOnItemSelectedListener(this);
        s4 = (Spinner) findViewById(R.id.hour);
        s4.setOnItemSelectedListener(this);
        s5 = (Spinner) findViewById(R.id.minute);
        s5.setOnItemSelectedListener(this);

        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);
        et = (EditText) findViewById(R.id.room_name);
        insert();// 배열에 값들을 넣어줌

        ArrayAdapter<String> a1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, year);
        ArrayAdapter<String> a2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, month);
        ArrayAdapter<String> a3 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, date);
        ArrayAdapter<String> a4 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, hour);
        ArrayAdapter<String> a5 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, minute);

        a1.setDropDownViewResource(R.layout.spinner);
        a2.setDropDownViewResource(R.layout.spinner);
        a3.setDropDownViewResource(R.layout.spinner);
        a4.setDropDownViewResource(R.layout.spinner);
        a5.setDropDownViewResource(R.layout.spinner);

        s1.setAdapter(a1);
        s2.setAdapter(a2);
        s3.setAdapter(a3);
        s4.setAdapter(a4);
        s5.setAdapter(a5);

        /* 선택된 친구목록 , 수 일단 받아놓음
        ArrayList<String> people = new ArrayList<String>();
        int count = Listinfo.getIntExtra("count",0);
        for(int i=0;i<count;i++){
            people.add(Listinfo.getStringExtra("item"+i));
        }*/
        b1.setOnClickListener(new OnClickListener() {// 구글 지도 띄우기 눌렀을 때
            @Override
            public void onClick(View v) {
                LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                boolean enabled = service
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
                if (!enabled) {
                    Toast.makeText(PopUp.this, "위치 서비스를 활성화 해주세요!", Toast.LENGTH_LONG).show();
                    Intent map = new Intent(PopUp.this, Map.class);
                    startActivityForResult(map, 1111);

                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                } else {
                    Intent map = new Intent(PopUp.this, Map.class);
                    startActivityForResult(map, 1111);
                }
            }
        });

        b2.setOnClickListener(new OnClickListener() {// 방만들기 눌렀을때
            @Override
            public void onClick(View v) {
                if (et.getText().toString().equals("") || et.getText().toString().equals(" ")) {// 방이름 입력 안했을 때
                    Toast.makeText(PopUp.this, "방 이름을 입력하세요", Toast.LENGTH_LONG).show();
                    int id = pfID.getInt("id", -1) + 1;/////
                }/////
                else if (result.equals("x")) {
                    Toast.makeText(PopUp.this, "장소를 설정해주세요", Toast.LENGTH_LONG).show();
                } else {// 방이름을 입력했다면 방만들고 알람예약 바로 설정

                    check++;
                    if (check == 1) {

                        int id = pfID.getInt("id", -1) + 1;// 체크해서 방이 없으면(처음 만드는거면 id를 0부터 시작하게)
                        pfAL = getSharedPreferences("Rooms" + id, MODE_PRIVATE);// preference 사용 위해(이름을 'Rooms변수'로 해서 각 방마다 자기만의 preference를 가지게된다)
                        edt = pfAL.edit();// 에티터와 연결

                        edt2.putInt("id", id);// 방하나 만들떄마다 그 번호(id)를 저장해줌
                        edt2.commit();

                        make_room = new Intent(PopUp.this, Room.class);
                        //ArrayList<String> people = new ArrayList<String>();
                        int count = Listinfo.getIntExtra("count", 0);
                        mCount = count;
                        //  Listinfo.getStringExtra("item" + i);
                        ///// 선택된 리스트 수, 친구 이름들, 방이름, 날짜 장소 시간 --> 다 실어줌 /////
                        memberID = new ArrayList<String>();
                        for (int i = 0; i < count; i++) {
                            //people.add(Listinfo.getStringExtra("item"+i));
                            make_room.putExtra("items" + i, Listinfo.getStringExtra("item" + i));// 친구이름 put
                            edt.putString("name" + i, Listinfo.getStringExtra("item" + i));// preference 에도 이름 저장
                            memberID.add(Listinfo.getStringExtra("itemID" + i));
                            Log.d("select ", memberID.get(i));
                        }
                        make_room.putExtra("c", count);// 수 put
                        make_room.putExtra("room_name", et.getText().toString());// 방 이름 put

                        edt.putString("Rname" + id, et.getText().toString());// preference에 방이름도 저장
                        edt.putInt("cnt", count);// preference에 사람 수 저장

                        for (int i = 0; i <= 4; i++) {
                            make_room.putExtra("info" + i, t[i]);// 날짜, 장소, 시간 put
                            edt.putString("else info" + i, t[i]);// preference 에도 날짜..등 저장
                        }
                        // 지도 좌표 정보는 밑에서 받아오므로 아래 알람 설정에서 알람 request code랑 매칭된 이름의 preference를 써서 저장해줌
                        edt.commit();// 약속정보 최종 저장

                        //------------------------------------ GCM 전달 -----------------------------------//
                        Log.d("Before Connection:  ", "---------------------------------------------------");
                         new MakeGroupAsyncTask().execute("http://xxxxxxxxxxxxxxxxxxxxxxxx");
                        Log.d("After Connection: ", "-----------------------------------------------------");
                        //---------------------------------------------------------------------------------//

                        //------------------------------------ 알람 설정 ----------------------------------//
                        alarmID = getSharedPreferences("alarmID", MODE_PRIVATE);
                        alarm_code = alarmID.getInt("num", 0) + 1;// 알람을 여러개 만들기 위해 request code 에 들어갈 숫자(id)를 다 다르게 설정
                        alarmEdt = alarmID.edit();
                        alarmEdt.putInt("num", alarm_code);
                        alarmEdt.commit();// +1 해준거 저장
                        Log.d("qqq", "알람 코드 = " + alarm_code);

                        LocationAlarmCode = getSharedPreferences("LocationID", MODE_PRIVATE);
                        ELocationAlarmCode = LocationAlarmCode.edit();
                        ELocationAlarmCode.putInt("Alarm1", alarm_code);
                        ELocationAlarmCode.commit();

                        //---------------------------- gps --------------------------//

                        int countTargets = 1;
                        register(alarm_code, Double.parseDouble(lati), Double.parseDouble(longti), 200, -1);// id, lati, longti, 범위, expiration

                        // 수신자 객체 생성하여 등록
                        mIntentReceiver = new LocationReceiver(intentKey);
                        registerReceiver(mIntentReceiver, mIntentReceiver.getFilter());

                        //---------------------------- gps --------------------------//

                        //edt.putString("address", result);// 주소도 넣고
                        //edt.putString("latitude"+alarm_code,lati);// + alarm 하는 이유 알람코드와 일치하는 약속 시간에 해당하는 장소를 구별하기위해
                        //edt.putString("longtitude"+alarm_code,longti);
                        //edt.commit();// 약속정보 최종 저장

                        Intent intents = getIntent();
                        String data = intents.getStringExtra("STR");

                        am = (AlarmManager) getSystemService(ALARM_SERVICE);// 알람변수

                        // AlarmReceiver.class로 값을 보내기위한 intent 변수
                        intent = new Intent(PopUp.this, AlarmReceiver.class);
                        intent.putExtra("Alarm", alarm_code);// gps의 좌표 비교위해 사용
                        intent.putExtra("latitude", lati);
                        intent.putExtra("longtitude", longti);
                        //intent2 = new Intent(MainActivity.this, NotificationReceiver.class);
                        sender = PendingIntent.getBroadcast(PopUp.this, alarm_code, intent, 0);

                        //처음 시작할때 AlarmReceiver.class에서 data의 값이 넘어오지 않기 때문에
                        //else부분부터 먼저 시작한다.
                        //펜딩센드로 받아오면 그때부턴 data부분이 null이 아니기 때문에 am.cancel을
                        //실행한다.

                        // 이제 when 에다가 설정한 날짜 시간 정보를 넣어주자 //
                        for (int i = 1; i <= 4; i++) {// 뒤에 붙은 '월' '일' 빼주는 작업
                            if (t[i].substring(1, 2).equals(" "))
                                t[i] = "0" + t[i];// 00 식으로 표현해야되서 1자리면 앞에 0 붙여준다

                            t[i] = t[i].substring(0, 2);// 그럼 이제 앞에 숫자 부분만 뽑아서 다시 저장
                        }
                        for (int i = 1; i <= 4; i++) {// 다듬은 날짜 시간을 when에 저장
                            if (i == 1 && t[1].substring(0, 1).equals("0")) {//
                                t[1] = "0" + (Integer.parseInt(t[1]) - 1);// 월은 1 빼준다
                            }
                            if (i == 1 && t[1].substring(0, 1).equals("1")) {
                                if (t[1].substring(1, 2).equals("0"))
                                    t[1] = "0" + (Integer.parseInt(t[1]) - 1);
                                else
                                    t[1] = "" + (Integer.parseInt(t[1]) - 1);
                            }
                            when += t[i];
                        }
                        int temp = when.length();
                        if (temp == 7)
                            when = "0" + when;
                        //Log.d("ggg","when = ? "+when);
                        //     Toast.makeText(PopUp.this,"when =? "+when,Toast.LENGTH_SHORT).show();////  when 확인
                        //when = editText.getText().toString();
                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.MONTH, Integer.parseInt(when.substring(0, 2)));
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(when.substring(2, 4)));
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(when.substring(4, 6)));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(when.substring(6, 8)));
                        calendar.set(Calendar.SECOND, 00);
                        //calendar.add(Calendar.SECOND, 5); // 5초 후
                        //Log.d("ㄱㄱㄱ", "캘린더? \n" + calendar.toString() + "\n" + calendar.getTimeInMillis());

                        //am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);// 실제로 알람을 지정된 시간에 울리도록 세팅해줌
                        //am.set(AlarmManager.RTC,System.currentTimeMillis(), sender);// 그냥 버튼 누르면 바로 울리기

                        Toast.makeText(PopUp.this, "정말 이대로 정하시겠습니까?  한번 더 누르면 생성", Toast.LENGTH_LONG).show();
                        // ----------------------------- 알람 설정 -------------------------------//

                    } else if (check == 2) {

                        //---------------------------- gps --------------------------//

                        int countTargets = 1;
                        register(alarm_code, Double.parseDouble(lati), Double.parseDouble(longti), 200, -1);// id, lati, longti, 범위, expiration

                        // 수신자 객체 생성하여 등록
                        mIntentReceiver = new LocationReceiver(intentKey);
                        registerReceiver(mIntentReceiver, mIntentReceiver.getFilter());

                        //---------------------------- gps --------------------------//

                        check = 0;
                        am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);// 실제로 알람을 지정된 시간에 울리도록 세팅해줌
                        startActivity(make_room);// 최종적으로 방만들기
                        PopUp.this.finish();
                    }
                }
                //----------------------- gps ----------------------//
                if (false) {
                    unregister();
                }
                //------------------- gps ---------------------//
            }
        });
        checkDangerousPermissions();
    }

    //-------------------------------- gps ----------------------------------//
    public  void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
              //  Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                   // Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void register(int r_code, double latitude, double longitude, float radius, long expiration) {
        Intent proximityIntent = new Intent(intentKey);
        proximityIntent.putExtra("Alarm1", "" + r_code);
        proximityIntent.putExtra("latitude", latitude);
        proximityIntent.putExtra("longitude", longitude);
        //proximityIntent.putExtra("signal","-1");
        PendingIntent intentt = PendingIntent.getBroadcast(this, alarm_code, proximityIntent, PendingIntent.FLAG_ONE_SHOT);

        Log.d("lll", "code= " + alarm_code + " lati= " + lati + " longti= " + longti);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.addProximityAlert(latitude, longitude, radius, expiration, intentt);

        mPendingIntentList.add(intentt);
    }

    /*  public void onStop() {
          super.onStop();

          unregister();
      }*/
    public void unregister() {
        if (mPendingIntentList != null) {
            for (int i = 0; i < mPendingIntentList.size(); i++) {
                PendingIntent curIntent = (PendingIntent) mPendingIntentList.get(i);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.removeProximityAlert(curIntent);
                mPendingIntentList.remove(i);
            }
        }

        if (mIntentReceiver != null) {
            unregisterReceiver(mIntentReceiver);
            mIntentReceiver = null;
        }
    }
    //-------------------------------------- gps -----------------------------------------//

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {// 선택된 스피너 아이템 처리
        if(s1.getId() == R.id.year){
            t[0] = s1.getSelectedItem().toString();    //year
        }
        if(s2.getId() == R.id.month){
            t[1] = s2.getSelectedItem().toString();   //month
        }
        if(s3.getId() == R.id.date){
            t[2] = s3.getSelectedItem().toString();   //date
        }
        if(s4.getId() == R.id.hour){
            t[3] = s4.getSelectedItem().toString();   //hour
        }
        if(s5.getId() == R.id.minute){
            t[4] = s5.getSelectedItem().toString();   //minute
         }

    }
    public void onNothingSelected(AdapterView<?> arg0) {}

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {// 호출된 Mpa activity에서 결과 데이터를 setResult로 실어서 되돌려주면 이 메소드가 콜된다
        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            if((requestCode == 1111) && (resultCode == Activity.RESULT_OK));// 작업이 취소되지않고 키값이 1111으로 왔다면..
            {
                Bundle location = data.getExtras();// 되돌려준 데이터를 받아준다
                result = location.getString("address","none");// 키값으로 구별하여 내가 얻어야할 데이터를 얻는다
                lati = location.getString("lati","none");
                longti = location.getString("longti","none");

                //Toast.makeText(this, "좌표= " + result+"\nlati= "+lati+" longti= "+longti, Toast.LENGTH_SHORT).show();// 얻은 결과 데이터를 토스트 메시지로 띄워준다
            }
        }
        catch (Exception e)
        {
            //Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }// onActivityResult

    public void insert() {
        int y = 2016;
        for (int i = 0; i < 10; i++) {// 년
            year[i] = y + " 년";
            y++;
        }
        for (int i = 0; i < 12; i++) {// 달
            month[i] = "" + (i + 1)+" 월";
        }
        for (int i = 0; i < 31; i++) {// 일
            date[i] = "" + (i + 1)+" 일";
        }
        for (int i = 0; i < 24; i++) {// 시
            hour[i] = "" + i+" 시";
        }
        for (int i = 0; i <= 59; i++) {// 분
            minute[i] = "" +i+" 분";
        }
    }


    //++++++++++++++++++++++++++++++++ 위치 접근 리시버 +++++++++++++++++++++++++++++++++++//

    public class LocationReceiver extends BroadcastReceiver {

        SharedPreferences sp;
        SharedPreferences.Editor ed;

        private String mExpectedAction;
        private Intent mLastReceivedIntent;

        public LocationReceiver(String expectedAction) {
            mExpectedAction = expectedAction;
            mLastReceivedIntent = null;
        }

        public IntentFilter getFilter() {
            IntentFilter filter = new IntentFilter(mExpectedAction);
            return filter;
        }

        /**
         * 받았을 때 호출되는 메소드
         *
         * @param context
         * @param intent
         */
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {

                sp = getSharedPreferences("onSpot",MODE_PRIVATE);
                ed = sp.edit();
/*
                AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context, notification);
                r.play();
*/
                mLastReceivedIntent = intent;

                String id = intent.getStringExtra("Alarm1");
                double latitude = intent.getDoubleExtra("latitude", 0.0D);
                double longitude = intent.getDoubleExtra("longitude", 0.0D);

                NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, LoadingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                Notification.Builder builder = new Notification.Builder(context);
                builder.setSmallIcon(R.drawable.icon2).setTicker("HETT").setWhen(System.currentTimeMillis())
                        .setNumber(1).setContentTitle("약속해줘").setContentText("약속 장소에 도착! ")
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS).setContentIntent(pendingIntent).setAutoCancel(true);

                notificationmanager.notify(1, builder.build());

                ed.putInt("key", 1);
                ed.commit();

                new ScreenOn().acquireCpuWakeLock(context);
                Log.d("nnn", "num= "+sp.getInt("key",-99));
               // Toast.makeText(context,"num= "+sp.getInt("key",-99)+ " 도착했다 ~~~!!!! : " + id + ", " + latitude + ", " + longitude, Toast.LENGTH_LONG).show();
                //unregister();
            }
        }

        public Intent getLastReceivedIntent() {
            return mLastReceivedIntent;
        }

        public void clearReceivedIntents() {
            mLastReceivedIntent = null;
        }

    }




    private class MakeGroupAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("Result :", result);
            //Toast.makeText(getBaseContext(), result , Toast.LENGTH_SHORT).show();

                //oast.makeText(getApplicationContext(), "로그인 성공!.", Toast.LENGTH_SHORT).show();
                //Intent main = new Intent(LoginActivity.this,MainActivity.class);
                //2startActivity(main);
        }
    }
    public  String POST(String url) {

        InputStream inputStream = null;
        String result = "";
        //person = new Person();
        //person.setLoginID(loginID.getText().toString());
        //person.setLoginPass(loginPass.getText().toString());


        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            //jsonObject.accumulate("userID", person.getLoginID());
           // Log.d("tag id: ", person.getLoginID());
           // jsonObject.accumulate("password", person.getLoginPass());


            //JSONObject jsonObject1 = new JSONObject();
            Log.d("Before json: ", "--------------------------------");
            JSONArray arr = new JSONArray();
           // ArrayList<String>  userID = new ArrayList<String>();
            String arrr="hh";
            for(int i=0; i<mCount; i++){
               // userID.add(memberID.get(i));
                JSONObject  jsonObject1 = new JSONObject();
                jsonObject1.accumulate("memberID", memberID.get(i).toString());
                Log.d("memberID" +i+" ", memberID.get(i).toString());
                arr.put(jsonObject1);
            }
            Log.d("arr : ", arr.toString());
            jsonObject.accumulate("member", arr);
            jsonObject.accumulate("year", t[0]);
            jsonObject.accumulate("month", t[1]);
            jsonObject.accumulate("date", t[2]);
            jsonObject.accumulate("hour", t[3]);
            jsonObject.accumulate("minute", t[4]);
            jsonObject.accumulate("lati", lati);
            jsonObject.accumulate("longti", longti);
            jsonObject.accumulate("room", et.getText().toString());
            jsonObject.accumulate("count", mCount);

            json = jsonObject.toString();
            Log.d("tag", json);

            httpPost.setEntity(new StringEntity(json.toString(), HTTP.UTF_8));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();


            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }


}
