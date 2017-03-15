package com.studygoal.jisc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.InstitutionsAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Institution;
import com.studygoal.jisc.Utils.Utils;

import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class LoginActivity extends Activity {

    private TextView choose_institution;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataManager.getInstance().context = getApplicationContext();
        DataManager.getInstance().init();
        DataManager.getInstance().currActivity = this;
        if (getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            DataManager.getInstance().isLandscape = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DataManager.getInstance().isLandscape = false;
        }

        setContentView(R.layout.activity_login);

        //Init the database; Might be moved to a Splash screen if there is to be one
        ActiveAndroid.initialize(this);
        DataManager.getInstance().context = this;
        DataManager.getInstance().loadFonts();

        webView = (WebView) findViewById(R.id.webview);

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

        //Init the user
        if (!getSharedPreferences("jisc", Context.MODE_PRIVATE).contains("guid")) {
            getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("guid", UUID.randomUUID().toString().toUpperCase()).apply();
        }

        ((TextView)findViewById(R.id.activity_login_demo_mode)).setTypeface(DataManager.getInstance().myriadpro_regular);
        findViewById(R.id.activity_login_demo_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE0ODgzNjU2NzcsImp0aSI6IjFtbjhnU3YrWk9mVzJlYXV1NmVrN0Rzbm1MUjA0dDRyT0V0SEQ5Z1BGdk09IiwiaXNzIjoiaHR0cDpcL1wvc3AuZGF0YVwvYXV0aCIsIm5iZiI6MTQ4ODM2NTY2NywiZXhwIjoxNjYyNTY0NTY2NywiZGF0YSI6eyJlcHBuIjoiIiwicGlkIjoiZGVtb3VzZXJAZGVtby5hYy51ayIsImFmZmlsaWF0aW9uIjoic3R1ZGVudEBkZW1vLmFjLnVrIn19.xM6KkBFvHW7vtf6dF-X4f_6G3t_KGPVNylN_rMJROsh1MXIg9sK5j77L0Jzg1JR8fhXZf-0jFMnZz6FMotAeig";
                DataManager.getInstance().set_jwt(token);

                if (NetworkManager.getInstance().checkIfUserRegistered()) {
                    if (NetworkManager.getInstance().login()) {
                        DataManager.getInstance().institution = "1";
                        DataManager.getInstance().user.isDemo = true;
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    } else {
                        //TODO: Need more information about the register flow so i can deal with other situations
                    }

                }
            }
        });


        DataManager.getInstance().guid = getSharedPreferences("jisc", Context.MODE_PRIVATE).getString("guid", "");

        webView.setVisibility(View.INVISIBLE);
        choose_institution = (TextView) findViewById(R.id.choose_institution);
        //Fonts
        ((TextView) findViewById(R.id.choose_institution)).setTypeface(DataManager.getInstance().myriadpro_regular);
        choose_institution.setTypeface(DataManager.getInstance().myriadpro_regular);

        //Downloading institutions
        if (NetworkManager.getInstance().downloadInstitutions()) {
            final View institution_layout = findViewById(R.id.institutions);
            institution_layout.setOnClickListener(null);

            final AppCompatTextView appCompatTextView = (AppCompatTextView) findViewById(R.id.appCompatTextView);
            appCompatTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                    alphaAnimation.setDuration(800);
                    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            institution_layout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    institution_layout.startAnimation(alphaAnimation);
                }
            });

            final List<Institution> institutionList = new Select().from(Institution.class).orderBy("name").execute();
            final InstitutionsAdapter institutionsAdapter = new InstitutionsAdapter(LoginActivity.this);
            institutionsAdapter.institutions = new Select().from(Institution.class).where("name LIKE ?", "%" + "" + "%").orderBy("name ASC").execute();

            final ListView list = (ListView) findViewById(R.id.list);
            list.setAdapter(institutionsAdapter);

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
                                //TODO: register staff
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
                                //TODO:REGISTER
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    if(view.getTag().equals("no institution")) {

                        Intent newIntent = new Intent(LoginActivity.this, SocialActivity.class);
                        LoginActivity.this.startActivity(newIntent);
                        LoginActivity.this.finish();
                        return;
                    }

                    if(view.getTag().equals("demo")) {
                        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE0ODgzNjU2NzcsImp0aSI6IjFtbjhnU3YrWk9mVzJlYXV1NmVrN0Rzbm1MUjA0dDRyT0V0SEQ5Z1BGdk09IiwiaXNzIjoiaHR0cDpcL1wvc3AuZGF0YVwvYXV0aCIsIm5iZiI6MTQ4ODM2NTY2NywiZXhwIjoxNjYyNTY0NTY2NywiZGF0YSI6eyJlcHBuIjoiIiwicGlkIjoiZGVtb3VzZXJAZGVtby5hYy51ayIsImFmZmlsaWF0aW9uIjoic3R1ZGVudEBkZW1vLmFjLnVrIn19.xM6KkBFvHW7vtf6dF-X4f_6G3t_KGPVNylN_rMJROsh1MXIg9sK5j77L0Jzg1JR8fhXZf-0jFMnZz6FMotAeig";
                        DataManager.getInstance().set_jwt(token);

                        if (NetworkManager.getInstance().checkIfUserRegistered()) {
                            if (NetworkManager.getInstance().login()) {
                                DataManager.getInstance().institution = "1";
                                DataManager.getInstance().user.isDemo = true;
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else {
                                //TODO: Need more information about the register flow so i can deal with other situations
                            }

                        }
                        return;
                    }

                    showProgressBar();

                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }

                    webView.loadUrl("about:blank");

                    final Institution institution = (Institution) view.getTag();

                    final CheckBox checkBoxLogged = (CheckBox) findViewById(R.id.choose_keeplogged);

                    String url = "https://sp.data.alpha.jisc.ac.uk/Shibboleth.sso/Login?entityID=https://" +
                            institution.url + "&target=https://sp.data.alpha.jisc.ac.uk/secure/auth.php?u=" +
                            DataManager.getInstance().guid;

                    if(checkBoxLogged.isChecked()) {
                        url += "&lt=true";
                    }

                    webView.setVisibility(View.VISIBLE);
                    webView.clearCache(true);
                    webView.getSettings().setJavaScriptEnabled(true);
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

                                    CheckBox checkBox = (CheckBox) findViewById(R.id.choose_staff);

                                    if(checkBoxLogged.isChecked()) {
                                        getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("jwt", DataManager.getInstance().get_jwt()).apply();
                                        getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("is_checked", "yes").apply();
                                        if(checkBox.isChecked()) {
                                            getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("is_staff", "yes").apply();
                                        }
                                    }

                                    if(checkBox.isChecked()) {
                                        if (NetworkManager.getInstance().checkIfStaffRegistered()) {
                                            if (NetworkManager.getInstance().loginStaff()) {
                                                DataManager.getInstance().institution = institutionList.get(position).name;
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
                                                DataManager.getInstance().institution = institutionList.get(position).name;
                                                getSharedPreferences("jisc", Context.MODE_PRIVATE).edit().putString("is_institution", DataManager.getInstance().institution).apply();
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                LoginActivity.this.finish();
                                            } else {
                                                //TODO: Need more information about the register flow so i can deal with other situations
                                            }

                                        } else {
                                            //TODO:REGISTER
                                            webView.loadUrl("https://sp.data.alpha.jisc.ac.uk/Shibboleth.sso/Login?entityID=https://" + institutionList.get(position).url +
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
                    webView.loadUrl(url);

                    AlphaAnimation animation = new AlphaAnimation(1, 0);
                    animation.setDuration(400);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            choose_institution.setText(institution.name);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            institution_layout.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    institution_layout.startAnimation(animation);

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
            findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    AlphaAnimation animation = new AlphaAnimation(1, 0);
                    animation.setDuration(400);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            institution_layout.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    institution_layout.startAnimation(animation);
                }
            });
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

}
