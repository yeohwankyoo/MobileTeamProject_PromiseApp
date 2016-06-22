package com.example.promise;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import android.provider.ContactsContract.Contacts;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 200;

    int count,index_count;

    ListView list;
    PhoneList pl = new PhoneList(this);
    ArrayList<String> people = new ArrayList<String>();
    ArrayList<String> peopleID = new ArrayList<String>();
    CallFriendList serverList = new CallFriendList(this);
    String ownID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent myIntent = getIntent();
        Bundle myData = myIntent.getExtras();
        ownID = myData.getString("ownerID");
        list = (ListView)findViewById(R.id.list);

        /////////// 연락처 불러오는 부분 ////////
        showContacts();
        showMyFriend();

        ///////////// 연락처 /////////

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//


    }//** onCreate **


    public  void makePhoneList(){

        final ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice,serverList.sitemName);
        //final ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,pl.item);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setAdapter(aa);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // SparseBooleanArrary = 인덱스에 해당하는 아이템들의 체크여부를 true, false로 저장함
                SparseBooleanArray checkedItems = list.getCheckedItemPositions();
                ArrayList<String> index = new ArrayList<String>();// index = 선택된 사람의 이름 저장
                ArrayList<String> index2 = new ArrayList<String>();
                count = aa.getCount();// 리스트에서 아이템의 수
                for(int i=0;i<count;i++) {
                    if(checkedItems.get(i)){// 아이템마다 보면서 선택된것일때 (해당 인덱스가 true일 때)
                        //Toast.makeText(MainActivity.this,"test = "+pl.item.get(i),Toast.LENGTH_SHORT).show();
                        index.add(serverList.sitemName.get(i));// 선택된건 따로 저장
                        index2.add(serverList.sitemID.get(i));
                    }
                }
                people.clear();
                peopleID.clear();
                index_count = index.size();
                for(int i=0;i<index.size();i++){

                    people.add(index.get(i));// 선택된 사람의 이름들만 따로 담아놓음
                    peopleID.add(index2.get(i));
                }                           // 방만들기 메뉴 눌렀을때 사용하기 위함
            }                       // index랑 people 두개쓰는 이유 -- index는 로컬에 선언(선택하고 다시 눌러서 취소했을경우 누를 떄마다 체크된것만 배열에 넣어주기위해),
            // people은 글로벌선언(아래 옵션메뉴 클릭 메소드에서 써야되는데 index는 로컬변수, 그래서 people에 옮겨담음)
        });
        ///////////// 연락처 /////////

    }
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            pl.run();
        }
    }

    private void showMyFriend(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
        else {
            /////////////////////////////////
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {

                serverList = new CallFriendList(this, pl.pitemName, pl.pitemPhone);
                serverList.execute("");

            } else {
                // display error
                Toast.makeText(this, "네트워크 상태를 확인하십시오", Toast.LENGTH_SHORT).show();
            }
        }
        /////////////////////////////////

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode ==PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE)
        {

        }
    }


    ////// 연락처 목록에서 선택된 아이템 이벤트 처리
    public void onListItemClick(ListView parent, View v, int position, long id) {


    }
    //-----------------------------------------------------------------------------------//
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    @Override /////// *********************** 액션바 메뉴 선택되었을때 ************************
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.make_promise_room)// 방만들기 눌렀을때
        {
            if(people.size() == 0)// 선택된게 없을 경우 알림창 띄워줌
            {
                new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("").setMessage("약속 대상을 선택해주세요!").setPositiveButton(
                        "", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick( DialogInterface dialog, int which )
                            {
                                //finish();
                            }
                        }).setNegativeButton( "OK!", null ).show();
            }
            else// 선택되었을 경우
            {
                //Intent make_room = new Intent(this, Room.class);
                Intent popup = new Intent(this,PopUp.class);

                for (int i = 0; i < index_count; i++) {
                    popup.putExtra("item" + i, people.get(i));
                    popup.putExtra("itemID" + i, peopleID.get(i));
                }
                popup.putExtra("count", index_count);// 선택된 친구 목록과 수를 popup activity 열기전에 담아줌

                for (int i = 0; i < list.getCount(); i++) {// 약속방 창띄우기 전에 체크된것 다 해제
                    list.setItemChecked(i, false);
                }
                people.clear();// 정보 넘겨줬으면 초기화
                peopleID.clear();
                popup.putExtra("owerID", ownID);
                startActivity(popup);
                //startActivity(make_room);// 체크된 목록 정보와 함께 약속방 창띄움
            }
            return true;

        }
        if(id == R.id.search)// 검색 눌렀을때
        {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.information)
        {
            Intent info = new Intent(this,MyInformation.class);
            startActivity(info);
        }
        else if (id == R.id.room)
        {
            Intent room = new Intent(this,ShowRoomState.class);
            startActivity(room);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //------------------------------------------------------------------------//

    public boolean onKeyDown(int keyCode, KeyEvent event)// back 버튼으로 종료할시, 바로 끄지않고 종료확인 메시지를 한번 더 띄워준다다
    {
        if( keyCode == KeyEvent.KEYCODE_BACK )
        {
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Quit?").setMessage("정말 그만하시겠습니까?").setPositiveButton("예", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick( DialogInterface dialog, int which )
                {
                    finish();
                }
            }).setNegativeButton( "아니요", null ).show();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}