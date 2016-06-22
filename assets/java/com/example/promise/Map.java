package com.example.promise;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.webkit.GeolocationPermissions;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class Map extends Activity {

    EditText et;
    WebView wv;
    ListView lv;
    ArrayList<String> ListAddr = new ArrayList<String>();
    ArrayList<String> lati = new ArrayList<String>();
    ArrayList<String> longti = new ArrayList<String>();

    private static String TAG = "MainActivity";

    TextView contentsText;
    Geocoder gc;

    EditText edit01;
    EditText edit02;
    EditText edit03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        lv = (ListView)findViewById(R.id.addlv);


        ///-------------------------------------------///
        edit01 = (EditText) findViewById(R.id.edit01);
        contentsText = (TextView) findViewById(R.id.contentsText);


        // 버튼 이벤트 처리
        Button show_btn = (Button) findViewById(R.id.show_btn);
        show_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 사용자가 입력한 주소 정보 확인
                String searchStr = edit01.getText().toString();

                // 주소 정보를 이용해 위치 좌표 찾기 메소드 호출
                searchLocation(searchStr);
            }
        });

        // 버튼 이벤트 처리


        // 지오코더 객체 생성
        gc = new Geocoder(this, Locale.KOREAN);

    }// onCreate

    /**
     * 주소를 이용해 위치 좌표를 찾는 메소드 정의
     */
    private void searchLocation(String searchStr) {
        // 결과값이 들어갈 리스트 선언
        List<Address> addressList = null;

        try {
            String str="";
            addressList = gc.getFromLocationName(searchStr, 15);

            if (addressList != null) {

                // 먼저 관련 검색어 결과와 수를 보여준다
                str = "[" + searchStr + "] 와 관련된 결과 : " + addressList.size()+"\n";
                contentsText.setText(str);
                str="";
               // contentsText.setText(str);
                //--------- 이 위는 걍 텍스트뷰로 -----//


                //--------- 이 아래는 리스트 뷰로 -----//
                ListAddr.clear();// 다음거 검색했을 땐 전에꺼 초기화
                lati.clear();//위도 경도도 마찬가지
                longti.clear();
                for (int i = 0; i < addressList.size(); i++)
                {
                    Address outAddr = addressList.get(i);
                    int addrCount = outAddr.getMaxAddressLineIndex() + 1;
                    StringBuffer outAddrStr = new StringBuffer();

                    for (int k = 0; k < addrCount; k++)
                    {
                        Log.d("eee", "확인= "+outAddr.toString());/////
                        outAddrStr.append(outAddr.getAddressLine(k)).toString();// ()안에 숫자는 0=주소 1=도시명, 그래서 이둘을 append로 붙여줘서
                                                                                // 하나의 완전한 주소 완성
                        if(k == addrCount-1) {// 주소 다 붙였으면
                            ListAddr.add(outAddrStr.toString());// 리스트뷰로 뿌려줄 arraylist에 저장
                            lati.add(""+outAddr.getLatitude());// 위도 경도도 저장
                            longti.add(""+outAddr.getLongitude());
                        }
                        Log.d("ii", "확인= "+outAddr.getLongitude()+"  "+outAddr.getLatitude());/////
                    }
                    final ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1,ListAddr);
                    //lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    lv.setAdapter(aa);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent toPopUp = new Intent(Map.this,PopUp.class);

                            toPopUp.putExtra("address",ListAddr.get(position));// 주소 실어줌
                            toPopUp.putExtra("lati",lati.get(position));
                            toPopUp.putExtra("longti", longti.get(position));
                            setResult(Activity.RESULT_OK, toPopUp);// 결과값 되돌려 보냄

                            //Toast.makeText(Map.this, position + " 번째 눌림 !", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    //outAddrStr.append("\n\tLatitude : " + outAddr.getLatitude());
                    //outAddrStr.append("\n\tLongitude : " + outAddr.getLongitude());
                    //i+=1;
                    //contentsText.append("\n결과 " + i + " : " + outAddrStr.toString());
                    //i-=1;
                }
            }

        } catch(IOException ex) {
            Log.d(TAG, "예외 : " + ex.toString());
        }

    }


}
