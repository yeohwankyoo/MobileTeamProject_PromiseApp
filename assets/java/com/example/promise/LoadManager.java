package com.example.promise;

/**
 * Created by Yeohwankyoo on 2016-06-11.
 */
        import android.widget.Toast;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;

public class LoadManager {
    URL url;            //접속대상 서버주소를 가진 객체
    HttpURLConnection conn;   //통신을 담당하는 객체
    BufferedReader buffer=null;

    //필요한 객체 초기화
    public LoadManager() {
        try {

            url = new URL("http://xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            conn = (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String request(){
        String data="";
        try {
            conn.connect();         //웹서버에 요청하는 시점
            InputStream is = conn.getInputStream();   //웹서버로부터 전송받을 데이터에 대한 스트림 얻기

            //1byte기반의 바이트스트림이므로 한글이 깨진다.
            //따라서 버퍼처리된 문자기반의 스트림으로 업그레이드 해야 된다.
            buffer = new BufferedReader(new InputStreamReader(is));

            //스트림을 얻어왔으므로, 문자열로 반환
            StringBuffer str = new StringBuffer();
            String d=null;
            while( (d=buffer.readLine()) != null){
                str.append(d);
            }

            data = str.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(buffer!=null){
                try {
                    buffer.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return data;

    }

}

