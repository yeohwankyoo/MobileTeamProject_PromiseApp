package com.example.promise;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class MyInformation extends Activity {

    TextView t1,t2,t3;
    SharedPreferences s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        s = getSharedPreferences("goodbad",MODE_PRIVATE);
        int good,bad;
        good = s.getInt("good",0);
        bad = s.getInt("bad",0);

        //// 뒷배경 흐리게 처리 ////
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;// 뒷배경의 어두운 정도
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.activity_my_information);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0x00ff00));

        t1 = (TextView)findViewById(R.id.tv1);
        t2 = (TextView)findViewById(R.id.tv2);
        t3 = (TextView)findViewById(R.id.tv3);

        t1.setText("+ 점수 "+good);
        t2.setText("- 점수 "+bad);
        int temp = good - bad;
        t3.setText("명예지수 : "+temp);
    }
}
