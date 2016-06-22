package com.example.promise.services;

/**
 * Created by Yeohwankyoo on 2016-06-14.
 */
import com.google.android.gms.iid.InstanceIDListenerService;

/**

 *
 * Service is invoked when token(device ID for GCM) is updated.
 * Could be called when security of the token is compromised.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService{

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // Fetch updated token from the server again and send it to application server.
        new RegistrationAsyncTask(null).execute();
    }
}

