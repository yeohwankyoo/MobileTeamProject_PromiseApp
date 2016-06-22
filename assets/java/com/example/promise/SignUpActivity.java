package com.example.promise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.app.Activity;
import android.app.AlertDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.promise.services.RegistrationAsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class SignUpActivity extends Activity implements View.OnClickListener {

    TextView tvIsConnected;
    EditText etName, etUserID, etEmail, etPhone, etPassword, etCheckPass;
    Button btnPost;
    Person  person = new Person();
    String senderId = "6639802369";
    String token = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // get reference to the views
        // tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        etName = (EditText) findViewById(R.id.etName);
        etUserID = (EditText) findViewById(R.id.etUserID);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPhone = (EditText)findViewById(R.id.etPhone);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etCheckPass = (EditText)findViewById(R.id.etCheckPass);

        btnPost = (Button) findViewById(R.id.btnPost);
        etName.requestFocus();
        // check if you are connected or not
        if (isConnected()) {
            //tvIsConnected.setBackgroundColor(0xFF00CC00);
            //tvIsConnected.setText("You are conncted");
        } else {
            //tvIsConnected.setText("You are NOT conncted");
            String alertTitle = "네트워크 문제";
            String buttonMessage = "네트워크 연결을 해주세요";

            new AlertDialog.Builder(SignUpActivity.this).setTitle(alertTitle).setMessage(buttonMessage).show();
        }

        // add click listener to Button "POST"
        btnPost.setOnClickListener(this);

    }

    //회원가입을 하기 위해서 회원가입창에 유효한 값을 넣었으면
    //http 연결을 통해서 서버에 값이 저장이 된다,
    public  String POST(String url) {

        InputStream inputStream = null;
        String result = "";
        //person = new Person();
        person.setName(etName.getText().toString());
        person.setUserID(etUserID.getText().toString());
        person.setEmail(etEmail.getText().toString());
        person.setPhone(etPhone.getText().toString());
        person.setPassword(etPassword.getText().toString());
        person.setCheckPass(etCheckPass.getText().toString());
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();                   //데이터를 서버가 보기 편한 json형식으로 바꾼다.
            jsonObject.accumulate("name", person.getName());
            jsonObject.accumulate("userID", person.getUserID());
            jsonObject.accumulate("email", person.getEmail());
            jsonObject.accumulate("phoneNum", person.getPhone());
            jsonObject.accumulate("password", person.getPassword());
            jsonObject.accumulate("token", token);
            //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
            json = jsonObject.toString();
            Log.d("tag", json);
            //StringEntity se = new StringEntity(json);
            // httpPost.setEntity(se);

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

    //네트워크 연결이 되었는지 확인한다.
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    //회원가입을 할떄, 정보를 다 입력하였는지 판단한다.
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnPost:
                if (!validate()) {
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();

                }
                // call AsynTask to perform network operation on separate thread
                else {
                    new RegistrationAsyncTask(SignUpActivity.this).execute(senderId);
                    // new HttpAsyncTask().execute("http://ec2-52-79-154-139.ap-northeast-2.compute.amazonaws.com:3000/books.json");
                }
                break;
        }

    }

    //AskncTask를 통해서 네트워크에 연결을 한다.
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("Result :", result);
            //Toast.makeText(getBaseContext(), result , Toast.LENGTH_SHORT).show();
            if(result.matches(".*ID is already taken.*"))
            {
                Toast.makeText(getBaseContext(), "이미 존재하는 아이디입니다", Toast.LENGTH_SHORT).show();
                etName.setText("");
                etUserID.setText("");
                etEmail.setText("");
                etPhone.setText("");
                etPassword.setText("");
                etCheckPass.setText("");
                etName.requestFocus();
            }
            else {
                Toast.makeText(getApplicationContext(), "회원가입 성공\n 반가워요 환영합니다.", Toast.LENGTH_SHORT);
                Intent login = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(login);
                finish();
            }

        }
    }

    private boolean validate() {
        int check;  // 0:

        if (etName.getText().toString().trim().equals(""))
            return false;
        else if (etUserID.getText().toString().trim().equals(""))
            return false;
        else if (etEmail.getText().toString().trim().equals(""))
            return false;
        else if (etPhone.getText().toString().trim().equals(""))
            return false;
        else if (etPassword.getText().toString().trim().equals("") || (etPassword.getText().toString().equalsIgnoreCase(etCheckPass.getText().toString())==false))
            return false;
        else
            return true;

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

    //토큰을 받아온다!!
    public void updateMessage(String deviceToken){
        token = deviceToken;
        new HttpAsyncTask().execute("http://xxxxxxxxxxxxxxxxxxxxxxx");
    }

}
