package com.example.promise;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class PhoneList implements Runnable {
    private Context mContext;
    private Cursor mCursor;
    final ArrayList<String> pitemName = new ArrayList<String>();
    final ArrayList<String> pitemPhone = new ArrayList<String>();
    //String[] result;


    public PhoneList(Context context) {
        mContext = context;
    }

    @Override
    public void run() {
        mCursor = mContext.getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);

        String name;
        String number;
        int i=0;

        if (mCursor.moveToFirst() && mCursor.getCount() > 0) {
            do {
                name = mCursor.getString(mCursor.getColumnIndex(Phone.DISPLAY_NAME));
                number = mCursor.getString(mCursor.getColumnIndex(Phone.NUMBER));

                Log.d("MyContactList", "Name : " + name + " / Number : " + number);
                //item[i] = name.toString();
                pitemName.add(name);

                StringTokenizer num = new StringTokenizer(number, "-");
                String tmp = "";
                while(num.hasMoreTokens())
                {
                    tmp = tmp+num.nextToken();
                }
                number = tmp;
                pitemPhone.add(number);
                //Toast.makeText(this,"test = "+item[i],Toast.LENGTH_SHORT).show();
                i++;
            } while(mCursor.moveToNext());
        }

        //result = new String[i];
        //for(int k=0;k<i;k++){
        //result[k] = item[k];
        //}

        mCursor.close();
    }


}