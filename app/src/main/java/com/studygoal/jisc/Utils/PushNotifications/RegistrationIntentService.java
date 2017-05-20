package com.studygoal.jisc.Utils.PushNotifications;


import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.studygoal.jisc.R;

import static com.studygoal.jisc.Managers.DataManager.UPDATE_DEVICE;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(
                    R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            sharedPreferences.edit().putString("push_token", token).apply();
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putString("push_token", "").apply();
        }

        sendRegistrationToServer();
    }

    private void sendRegistrationToServer() {
        // Add custom implementation, as needed.
        sendBroadcast(new Intent(UPDATE_DEVICE));
    }
}