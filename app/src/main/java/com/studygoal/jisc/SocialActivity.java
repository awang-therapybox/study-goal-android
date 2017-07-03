package com.studygoal.jisc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

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
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class SocialActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "8sRjZ9CSN89N1iHMyMZjfedeE";
    private static final String TWITTER_SECRET = "GPRDOuvl9HBoIZVYZo7r5XlverGN6HAGNouJYhCDOVIYrd7lcT";

    TwitterAuthClient mTwitterAuthClient;

    CallbackManager callbackManager;

    int socialType;

    String email;
    String socialID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            DataManager.getInstance().isLandscape = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DataManager.getInstance().isLandscape = false;
        }

        setContentView(R.layout.activity_social);

        email = "";
        socialID = "";

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        TextView login_with_google = (TextView)findViewById(R.id.login_with_google);
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

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        Twitter.getSessionManager().clearActiveSession();
        Twitter.logOut();

        mTwitterAuthClient= new TwitterAuthClient();
        TextView login_with_twitter = (TextView)findViewById(R.id.login_with_twitter);
        login_with_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CookieSyncManager.createInstance(SocialActivity.this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeSessionCookie();
                Twitter.getSessionManager().clearActiveSession();
                Twitter.logOut();

                socialType = 2;
                socialID = "";
                email = "";

                mTwitterAuthClient.authorize(SocialActivity.this, new Callback<TwitterSession>() {
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
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SocialActivity.this);
                                alertDialogBuilder.setMessage(R.string.facebook_error_email);
                                alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                                exception.printStackTrace();
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

        TextView login_with_facebook = (TextView)findViewById(R.id.login_with_facebook);
        login_with_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socialType = 1;
                LoginManager.getInstance().logInWithReadPermissions(SocialActivity.this, Arrays.asList("email"));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                if(!loginResult.getRecentlyGrantedPermissions().contains("email")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SocialActivity.this);
                    alertDialogBuilder.setMessage(R.string.facebook_error_email);
                    alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return;
                }

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

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
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SocialActivity.this, LoginActivity.class);
        SocialActivity.this.startActivity(intent);
        SocialActivity.this.finish();
    }

    void loginSocial() {

        Integer response = NetworkManager.getInstance().loginSocial(email,socialID);

        if(response == 200) {
            Intent intent = new Intent(SocialActivity.this, MainActivity.class);
            startActivity(intent);
            SocialActivity.this.finish();
            return;
        }

        if (response == 403) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SocialActivity.this);
            alertDialogBuilder.setMessage(R.string.social_login_error);
            alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SocialActivity.this);
            alertDialogBuilder.setMessage(R.string.something_went_wrong);
            alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}