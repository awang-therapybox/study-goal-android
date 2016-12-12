package com.studygoal.jisc;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Utils.Utils;

import java.util.HashMap;

public class ForgotPassword extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataManager.getInstance().isLandscape)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.forgotpassword);

        DataManager.getInstance().currActivity = this;

        ((TextView)findViewById(R.id.forgot_title)).setTypeface(DataManager.getInstance().myriadpro_bold);
        ((TextView)findViewById(R.id.forgot_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView)findViewById(R.id.forgot_btn_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((EditText)findViewById(R.id.forgot_edittext)).setTypeface(DataManager.getInstance().myriadpro_regular);

        findViewById(R.id.forgot_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText)findViewById(R.id.forgot_edittext)).getText().toString();
                if(email.length() == 0) {
                    Snackbar.make(findViewById(R.id.container), R.string.invalid_email, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if(!Utils.validate_email(email)) {
                    Snackbar.make(findViewById(R.id.container), R.string.invalid_email, Snackbar.LENGTH_LONG).show();
                    return;
                }
                HashMap<String, String> map = new HashMap<>();
                map.put("email", email);
                String responseCode = NetworkManager.getInstance().forgotPassword(map);
                if(responseCode.equals("200"))
                    ForgotPassword.this.finish();
                else if(responseCode.equals("204"))
                    Snackbar.make(findViewById(R.id.container), R.string.no_user_with_this_email, Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(findViewById(R.id.container), R.string.fail, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
