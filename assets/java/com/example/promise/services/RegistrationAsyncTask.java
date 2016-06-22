package com.example.promise.services;

/**
 * Created by Yeohwankyoo on 2016-06-14.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.example.promise.SignUpActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.example.promise.ApplicationController;
import com.example.promise.Config;
import com.example.promise.MainActivity;
import com.example.promise.R;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * Async Task to get the token from GCM server and send it to the application server.
 */
public class RegistrationAsyncTask extends AsyncTask<String, Void, Void>{

    Activity mActivity;
    ProgressDialog mProgressDialog;
    String token;

    public RegistrationAsyncTask(Activity activity){
        mActivity = activity;
        if (activity != null){
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("Registering...");
            mProgressDialog.setCancelable(false);
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mProgressDialog != null) mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(String... params) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        String senderId = params[0];
        Log.d("test", "sender id : " + senderId);

        try {
            // Get the token from GCM server.
            InstanceID instanceID = InstanceID.getInstance(mActivity);
            token = instanceID.getToken(senderId,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i("test", "GCM Registration Token: " + token);

            //sendTokenToServer();

            // Storing that the token has already been sent.
            sharedPreferences.edit().putBoolean(Config.KEY_TOKEN_SENT_TO_SEVER, true).apply();

        } catch (Exception e) {
            Log.d("test", "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(Config.KEY_TOKEN_SENT_TO_SEVER, false).apply();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mProgressDialog != null) mProgressDialog.dismiss();

        ((SignUpActivity)mActivity).updateMessage(token);
    }



}
