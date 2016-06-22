package com.example.promise;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.promise.services.RegistrationAsyncTask;

import org.antlr.v4.tool.BuildDependencyGenerator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoginActivity extends Activity {

    Button buttonLogin;
    Button buttonSignUp;
    EditText loginID;
    EditText loginPass;
    String ownID;
    Person  person = new Person();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        buttonSignUp = (Button)findViewById(R.id.buttonSignUp);
        loginID = (EditText)findViewById(R.id.loginID);
        loginPass = (EditText)findViewById(R.id.loginPass);
    }

    public void onClickLogin(View v)
    {
        if (isConnected()) {

            if (!validate()) {
                Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();

            }
            // call AsynTask to perform network operation on separate thread
            else {
                new LoginAsyncTask().execute("http://xxxxxxxxxxxxxxxxxxxxxxxxx");

            }


        } else {

            String alertTitle = "네트워크 문제";
            String buttonMessage = "네트워크 연결을 해주세요";

            new AlertDialog.Builder(LoginActivity.this).setTitle(alertTitle).setMessage(buttonMessage).show();
        }
    }

    public void onClickSign(View v)
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class LoginAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("Result :", result);
            //로그인 실패했을 경우
            if(result.matches(".*Incorrect username or password.*"))
            {
                Toast.makeText(getBaseContext(), "잘못된 아이디 혹은 비밀번호 입력", Toast.LENGTH_SHORT).show();
                loginID.setText("");
                loginPass.setText("");
            }
            else {
                    //로그인 성공 했을경우
                Intent main = new Intent(LoginActivity.this,MainActivity.class);

                Bundle myData = new Bundle();
                myData.putString("ownerID", loginID.getText().toString());
                main.putExtras(myData);
                startActivity(main);
            }

        }
    }
    public  String POST(String url) {

        InputStream inputStream = null;
        String result = "";
        //person = new Person();
        person.setLoginID(loginID.getText().toString());       // 로그인 아이디를 입력한다.
        person.setLoginPass(loginPass.getText().toString());   // 로그인 비밀번호를 입력한다

        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("userID", person.getLoginID());     //json 형식으로 바꾼다.
            Log.d("tag id: ", person.getLoginID());
            jsonObject.accumulate("password", person.getLoginPass());
            ;
            JSONObject jsonObject1 = new JSONObject();


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

    //로그인을 할떄, 아이디와 비밀번호를 입력했는지 체크한다.
    private boolean validate() {
        int check;  // 0:

        if (loginID.getText().toString().trim().equals(""))
            return false;
        else if (loginPass.getText().toString().trim().equals(""))
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

}

