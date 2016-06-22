package com.example.promise;

/**
 * Created by 광선 on 2016-05-07.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListView extends ArrayAdapter<String>{
// 리스트를 만들어주기 위해 ArraryAdapter를 상속받는다

    String[] item;
    Context context;

    public MyListView(Context context, String[] values) {
        super(context, R.layout.rowlayout, values);// 리스트뷰를 위해 따로만든 xml파일을 불러온다
        this.context = context;
        this.item = values;// 파라미터로 받게될 리스트변수
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {// 리스트의 각 row 마다 넣어줄 이미지를 설정해준다
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);// 내가 만들어놓은 rowlayout xml 파일을 연결시겨준다

        // rowlayout xml 파일의 컴포넌트들을 연결 시켜준다
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(item[position]);

        return rowView;
    }


}
