package com.studygoal.jisc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.studygoal.jisc.Adapters.InstitutionsAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Institution;
import com.studygoal.jisc.Utils.Utils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.ImageValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.UUID;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TWITTER_KEY = "M0NKXVGquYoclGTcG81u49hka";
    private static final String TWITTER_SECRET = "CCpca8rm2GuJFkuHmTdTiwBsTcWdv7Ybi5Qqi7POIA6BvCObY6";
    TwitterAuthClient mTwitterAuthClient;

    CallbackManager callbackManager;

    int socialType;

    String email;
    String socialID;

    private WebView webView;

    LinearLayout loginContent;

    LinearLayout loginStep1;
    LinearLayout loginStep2;
    LinearLayout loginStep3;

    boolean isStaff;
    boolean rememberMe;

    Institution selectedInstitution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataManager.getInstance().context = getApplicationContext();
        DataManager.getInstance().init();
        DataManager.getInstance().currActivity = this;

        isStaff = false;
        rememberMe = false;
        selectedInstitution = null;

        if (getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            DataManager.getInstance().isLandscape = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DataManager.getInstance().isLandscape = false;
        }

        setContentView(R.layout.activity_login);

        ActiveAndroid.initialize(this);
        DataManager.getInstance().context = this;
        DataManager.getInstance().loadFonts();

        if (DataManager.getInstance().toast) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.session_expired_message);
            alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.session_expired_title) + "</font>"));
            alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            DataManager.getInstance().toast = false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkManager.getInstance().getAllTrophies();
            }
        }).start();

        if (!getSharedPreferences("jisc", Context.MODE_PRIVATE).contains("guid")) {
            getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("guid", UUID.randomUUID().toString().toUpperCase()).apply();
        }

        DataManager.getInstance().guid = getSharedPreferences("jisc", Context.MODE_PRIVATE).getString("guid", "");

        loginContent = (LinearLayout) findViewById(R.id.login_content);
        loginStep1 = (LinearLayout) findViewById(R.id.login_step_1);
        loginStep2 = (LinearLayout) findViewById(R.id.login_step_2);
        loginStep3 = (LinearLayout) findViewById(R.id.login_step_3);

        loginContent.setVisibility(View.VISIBLE);
        loginStep1.setVisibility(View.VISIBLE);
        loginStep2.setVisibility(View.GONE);
        loginStep3.setVisibility(View.GONE);

        ((TextView) findViewById(R.id.login_logo_text)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mmrtext.ttf"));
        ((TextView) findViewById(R.id.login_step_1_imastudent)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) findViewById(R.id.login_step_1_imastaff)).setTypeface(DataManager.getInstance().myriadpro_regular);

        findViewById(R.id.login_step_1_imastudent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.isStaff = false;

                loginContent.setVisibility(View.VISIBLE);
                loginStep1.setVisibility(View.GONE);
                loginStep2.setVisibility(View.VISIBLE);
                loginStep3.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.login_step_1_imastaff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.isStaff = true;

                loginContent.setVisibility(View.VISIBLE);
                loginStep1.setVisibility(View.GONE);
                loginStep2.setVisibility(View.VISIBLE);
                loginStep3.setVisibility(View.GONE);
            }
        });

        ((CheckBox) findViewById(R.id.login_check_rememberme)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((CheckBox) findViewById(R.id.login_check_dontrememberme)).setTypeface(DataManager.getInstance().myriadpro_regular);

        findViewById(R.id.login_check_rememberme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.rememberMe = true;

                loginContent.setVisibility(View.GONE);
                loginStep1.setVisibility(View.GONE);
                loginStep2.setVisibility(View.GONE);
                loginStep3.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.login_check_dontrememberme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.rememberMe = false;

                loginContent.setVisibility(View.GONE);
                loginStep1.setVisibility(View.GONE);
                loginStep2.setVisibility(View.GONE);
                loginStep3.setVisibility(View.VISIBLE);
            }
        });

        ((TextView) findViewById(R.id.login_searchinstitution_title)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((EditText) findViewById(R.id.search_field)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) findViewById(R.id.login_institutionnotlisted)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) findViewById(R.id.login_signinwith)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) findViewById(R.id.login_demomode)).setTypeface(DataManager.getInstance().myriadpro_regular);

        findViewById(R.id.login_demomode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE0ODgzNjU2NzcsImp0aSI6IjFtbjhnU3YrWk9mVzJlYXV1NmVrN0Rzbm1MUjA0dDRyT0V0SEQ5Z1BGdk09IiwiaXNzIjoiaHR0cDpcL1wvc3AuZGF0YVwvYXV0aCIsIm5iZiI6MTQ4ODM2NTY2NywiZXhwIjoxNjYyNTY0NTY2NywiZGF0YSI6eyJlcHBuIjoiIiwicGlkIjoiZGVtb3VzZXJAZGVtby5hYy51ayIsImFmZmlsaWF0aW9uIjoic3R1ZGVudEBkZW1vLmFjLnVrIn19.xM6KkBFvHW7vtf6dF-X4f_6G3t_KGPVNylN_rMJROsh1MXIg9sK5j77L0Jzg1JR8fhXZf-0jFMnZz6FMotAeig";
//                String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE0ODg0NDkxNzksImp0aSI6IjdnOHFHVWlDKzRIdTdyN2ZUcTBOcldjaUpGTzByR1wvdUhpZVhvN0NBSjZvPSIsImlzcyI6Imh0dHA6XC9cL3NwLmRhdGFcL2F1dGgiLCJuYmYiOjE0ODg0NDkxNjksImV4cCI6MTQ5MjU5NjM2OSwiZGF0YSI6eyJlcHBuIjoiIiwicGlkIjoiczE1MTI0OTNAZ2xvcy5hYy51ayIsImFmZmlsaWF0aW9uIjoic3RhZmZAZ2xvcy5hYy51ayJ9fQ.xO_Yk6ZgTWgg0UHVXglFKD1tMP2wq98b8IU4alaGQvjtlYcjoz5W8gZbAX0Gcktl0nDs_bkvsB1g5OaYkkY6yg";
                DataManager.getInstance().set_jwt(token);

                if (NetworkManager.getInstance().checkIfUserRegistered()) {
                    if (NetworkManager.getInstance().login()) {
                        DataManager.getInstance().institution = "1";
                        DataManager.getInstance().user.isDemo = true;
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                }
            }
        });

        webView = (WebView) findViewById(R.id.webview);
        webView.setVisibility(View.INVISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (url.contains("?{")) {
                    webView.setVisibility(View.INVISIBLE);
                    String json = url.split("\\?")[1];
                    try {
                        JSONObject jsonObject = new JSONObject(java.net.URLDecoder.decode(json, "UTF-8"));

                        // Token can be replaced here for testing individuals.
                        String token = jsonObject.getString("jwt");
                        DataManager.getInstance().set_jwt(token);

                        if(LoginActivity.this.rememberMe) {
                            getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("jwt", DataManager.getInstance().get_jwt()).apply();
                            getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("is_checked", "yes").apply();
                            if(LoginActivity.this.isStaff) {
                                getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("is_staff", "yes").apply();
                            }
                        }

                        if(LoginActivity.this.isStaff) {
                            if (NetworkManager.getInstance().checkIfStaffRegistered()) {
                                if (NetworkManager.getInstance().loginStaff()) {
                                    DataManager.getInstance().institution = selectedInstitution.name;
                                    getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("is_institution", DataManager.getInstance().institution).apply();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                } else {
                                    //TODO: complete login staff workflow
                                }
                            } else {
                                //TODO: register staff
                            }
                        } else {
                            if (NetworkManager.getInstance().checkIfUserRegistered()) {
                                if (NetworkManager.getInstance().login()) {
                                    DataManager.getInstance().institution = selectedInstitution.name;
                                    getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("is_institution", DataManager.getInstance().institution).apply();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                } else {
                                    //TODO: Need more information about the register flow so i can deal with other situations
                                }

                            } else {
                                //TODO: register student
                                webView.loadUrl("https://sp.data.alpha.jisc.ac.uk/Shibboleth.sso/Login?entityID=https://" + selectedInstitution.url +
                                        "&target=https://sp.data.alpha.jisc.ac.uk/secure/register/form.php?u=" + DataManager.getInstance().get_jwt());
                                webView.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        webView.setVisibility(View.INVISIBLE);
                        hideProgressBar();
                    }
                }
            }
        });

        final InstitutionsAdapter institutionsAdapter = new InstitutionsAdapter(LoginActivity.this);
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(institutionsAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(NetworkManager.getInstance().downloadInstitutions()) {
                    institutionsAdapter.institutions = new Select().from(Institution.class).orderBy("name").execute();
                    institutionsAdapter.notifyDataSetChanged();
                } else {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                            alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.no_internet) + "</font>"));
                            alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            DataManager.getInstance().toast = false;
                        }
                    });
                }
            }
        }).start();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                showProgressBar();

                if (getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                webView.loadUrl("about:blank");

                LoginActivity.this.selectedInstitution = (Institution) view.getTag();

                String url = "https://sp.data.alpha.jisc.ac.uk/Shibboleth.sso/Login?entityID=https://" +
                        selectedInstitution.url + "&target=https://sp.data.alpha.jisc.ac.uk/secure/auth.php?u=" +
                        DataManager.getInstance().guid;

                if(LoginActivity.this.rememberMe) {
                    url += "&lt=true";
                }

                webView.setVisibility(View.VISIBLE);
                webView.clearCache(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(url);
            }
        });

        EditText editText = (EditText) findViewById(R.id.search_field);
        editText.setTypeface(DataManager.getInstance().myriadpro_regular);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                institutionsAdapter.institutions = new Select()
                        .from(Institution.class)
                        .where("name LIKE ?", "%" + s.toString() + "%")
                        .orderBy("name ASC")
                        .execute();
                institutionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //check if REMEMBER ME is active
        String jwt = getSharedPreferences("jisc", Context.MODE_PRIVATE).getString("jwt","");
        String is_checked = getSharedPreferences("jisc", Context.MODE_PRIVATE).getString("is_checked","");
        String is_staff = getSharedPreferences("jisc", Context.MODE_PRIVATE).getString("is_staff","");
        String is_institution = getSharedPreferences("jisc", Context.MODE_PRIVATE).getString("is_institution","");
        if(is_checked.equals("yes") && jwt.length() > 0 ) {
            try {
                String jwtDecoded = Utils.jwtDecoded(jwt);
                JSONObject json = new JSONObject(jwtDecoded);

                Long expiration = Long.parseLong(json.optString("exp"));
                Long timestamp = System.currentTimeMillis()/1000;

                if(expiration < timestamp) {
                    // it is expired
                    getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("jwt", "").apply();
                    getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("is_checked", "").apply();
                    getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("is_staff", "").apply();
                    getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("is_institution", "").apply();
                } else {
                    //continue with login process
                    DataManager.getInstance().set_jwt(jwt);

                    if(is_staff.equals("yes")) {
                        if (NetworkManager.getInstance().checkIfStaffRegistered()) {
                            if (NetworkManager.getInstance().loginStaff()) {
                                DataManager.getInstance().institution = is_institution;
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else {
                                //TODO: complete login staff workflow
                            }
                        } else {
                            //TODO: register staff workflow
                        }
                    } else {
                        if (NetworkManager.getInstance().checkIfUserRegistered()) {
                            if (NetworkManager.getInstance().login()) {
                                DataManager.getInstance().institution = is_institution;
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else {
                                //TODO: Need more information about the register flow so i can deal with other situations
                            }

                        } else {
                            //TODO: register student worflow
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        ImageView login_with_google = (ImageView)findViewById(R.id.login_with_google);
        login_with_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socialType = 3;

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, 5005);
            }
        });

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        mTwitterAuthClient= new TwitterAuthClient();
        ImageView login_with_twitter = (ImageView)findViewById(R.id.login_with_twitter);
        login_with_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socialType = 2;
                socialID = "";
                email = "";

                mTwitterAuthClient.authorize(LoginActivity.this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        // Success
                        socialID = ""+twitterSessionResult.data.getUserId();
                        mTwitterAuthClient.requestEmail(twitterSessionResult.data, new Callback<String>() {
                            @Override
                            public void success(Result<String> result) {
                                email = result.data;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginSocial();
                                    }
                                });
                            }

                            @Override
                            public void failure(TwitterException exception) {
                                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                                alertDialogBuilder.setMessage(R.string.facebook_error_email);
                                alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        });
                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        callbackManager = CallbackManager.Factory.create();

        ImageView login_with_facebook = (ImageView)findViewById(R.id.login_with_facebook);
        login_with_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socialType = 1;
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email"));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                if(!loginResult.getRecentlyGrantedPermissions().contains("email")) {
                    android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                    alertDialogBuilder.setMessage(R.string.facebook_error_email);
                    alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return;
                }

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e("LoginActivity", response.toString());

                                try {
                                    socialID = object.getString("id");
                                    email = object.getString("email");

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loginSocial();
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (webView.getVisibility() == View.VISIBLE) {
            webView.setVisibility(View.INVISIBLE);
            webView.loadUrl("about:blank");
            hideProgressBar();
        } else {
            super.onBackPressed();
        }
    }

    public void showProgressBar() {
        findViewById(R.id.blackout).setVisibility(View.VISIBLE);
        findViewById(R.id.blackout).setOnClickListener(null);
    }

    public void hideProgressBar() {
        findViewById(R.id.blackout).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(socialType == 1) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (socialType == 2) {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        } else if (socialType == 3) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            email = acct.getEmail();
            socialID = acct.getId();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loginSocial();
                }
            });

        } else {
            Log.e("JISC", "handleSignInResult:" + result.getStatus().getResolution());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("JISC","connection result: "+connectionResult);
    }

    void loginSocial() {

        Integer response = NetworkManager.getInstance().loginSocial(email,socialID);

        if(response == 200) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
            return;
        }

        if (response == 403) {
            android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
            alertDialogBuilder.setMessage(R.string.social_login_error);
            alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
            alertDialogBuilder.setMessage(R.string.something_went_wrong);
            alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}