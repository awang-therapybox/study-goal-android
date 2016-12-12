package com.studygoal.jisc;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.lb.auto_fit_textview.AutoResizeTextView;
import com.studygoal.jisc.Adapters.DrawerAdapter;
import com.studygoal.jisc.Fragments.AddTarget;
import com.studygoal.jisc.Fragments.FeedFragment;
import com.studygoal.jisc.Fragments.Friends;
import com.studygoal.jisc.Fragments.LogActivityHistory;
import com.studygoal.jisc.Fragments.LogNewActivity;
import com.studygoal.jisc.Fragments.Settings;
import com.studygoal.jisc.Fragments.Stats;
import com.studygoal.jisc.Fragments.Stats2;
import com.studygoal.jisc.Fragments.TargetFragment;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.ActivityHistory;
import com.studygoal.jisc.Models.CurrentUser;
import com.studygoal.jisc.Models.ReceivedRequest;
import com.studygoal.jisc.Models.RunningActivity;
import com.studygoal.jisc.Utils.CircleTransform;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class MainActivity extends FragmentActivity {

    public DrawerLayout drawer;
    public RelativeLayout friend, settings, addTarget, send, timer, back;
    Settings settings_fragment;
    LogActivityHistory logFragment;
    public FeedFragment feedFragment;
    public boolean isLandscape = DataManager.getInstance().isLandscape;
    private int selectedPosition;
    ListView navigationView;
    public DrawerAdapter adapter;
    View menu, blackout;
    ProgressDialog progress;

    @Override
    protected void onResume() {
        super.onResume();
        DataManager.getInstance().checkForbidden = true;

        try {
            Glide.with(DataManager.getInstance().mainActivity).load(NetworkManager.getInstance().host + DataManager.getInstance().user.profile_pic).transform(new CircleTransform(DataManager.getInstance().mainActivity)).into(DataManager.getInstance().mainActivity.adapter.profile_pic);
        } catch (Exception ignored) {
        }
    }

    public void refreshDrawer() {
        if (adapter != null) {
            adapter.values = new String[]{"0", getString(R.string.feed), getString(R.string.stats), getString(R.string.log), getString(R.string.target), getString(R.string.logout)};
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isLandscape = DataManager.getInstance().isLandscape;
        DataManager.getInstance().checkForbidden = true;
        super.onCreate(savedInstanceState);
        DataManager.getInstance().currActivity = this;
        if (getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isLandscape = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isLandscape = false;
        }

        if (DataManager.isTestBuild)
            CrashManager.register(this, "ff42f3b167f44d7e87edf69ffc1c7cbb", new CrashManagerListener() {
                public boolean shouldAutoUploadCrashes() {
                    return true;
                }
            });

        setContentView(R.layout.activity_main);

        blackout = findViewById(R.id.blackout);

        back = (RelativeLayout) findViewById(R.id.main_screen_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        timer = (RelativeLayout) findViewById(R.id.main_screen_running);
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        send = (RelativeLayout) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedFragment != null) {
                    feedFragment.post();
                }
            }
        });

        friend = (RelativeLayout) findViewById(R.id.main_screen_friend);
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getTag() != null && v.getTag().equals("from_list")) {

                    v.setTag("");
                    final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.confirmation_dialog);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    if (DataManager.getInstance().mainActivity.isLandscape) {
                        DisplayMetrics displaymetrics = new DisplayMetrics();
                        DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                        int width = (int) (displaymetrics.widthPixels * 0.45);

                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        params.width = width;
                        dialog.getWindow().setAttributes(params);
                    }

                    ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                    ((TextView) dialog.findViewById(R.id.dialog_title)).setText("");

                    ((TextView) dialog.findViewById(R.id.dialog_message)).setTypeface(DataManager.getInstance().myriadpro_regular);
                    ((TextView) dialog.findViewById(R.id.dialog_message)).setText(R.string.do_you_want_to_respond_now);

                    ((TextView) dialog.findViewById(R.id.dialog_no_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                    ((TextView) dialog.findViewById(R.id.dialog_no_text)).setText(R.string.no);

                    ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                    ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setText(R.string.yes);

                    dialog.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Friends friendsFragment = new Friends();

                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment, friendsFragment)
                                    .addToBackStack(null)
                                    .commit();

                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.dialog_no).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    Friends friendsFragment = new Friends();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment, friendsFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
        settings = (RelativeLayout) findViewById(R.id.main_screen_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLandscape) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                } else {
                    settings_fragment = new Settings();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment, settings_fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
        addTarget = (RelativeLayout) findViewById(R.id.main_screen_addtarget);


        DataManager.getInstance().mainActivity = this;
        NetworkManager.getInstance().getAppSettings(DataManager.getInstance().user.id);


        NetworkManager.getInstance().getMyTrophies();

        //Decide where to place it
        if (new Select().from(ActivityHistory.class).count() == 0) {
            NetworkManager.getInstance().getActivityHistory(DataManager.getInstance().user.id);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                NetworkManager.getInstance().getModules();
                NetworkManager.getInstance().getStretchTargets(DataManager.getInstance().user.id);
                NetworkManager.getInstance().getFriends(DataManager.getInstance().user.id);
                NetworkManager.getInstance().getFriendRequests(DataManager.getInstance().user.id);
            }
        }).start();


        Intent intentService = new Intent(this, Syncronize.class);
        startService(intentService);


        if (DataManager.getInstance().home_screen == null) {
            DataManager.getInstance().home_screen = "feed";
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, new FeedFragment())
                    .commit();
        } else if (DataManager.getInstance().home_screen.equals("")) {
            DataManager.getInstance().home_screen = "feed";
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, new FeedFragment())
                    .commit();
        }
        if (DataManager.getInstance().home_screen.toLowerCase().equals("feed")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, new FeedFragment())
                    .commit();
        } else if (DataManager.getInstance().home_screen.toLowerCase().equals("stats")) {
            if (isLandscape)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new Stats())
                        .commit();
            else
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new Stats2())
                        .commit();
        } else if (DataManager.getInstance().home_screen.toLowerCase().equals("log")) {
            logFragment = new LogActivityHistory();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, logFragment)
                    .commit();
        } else if (DataManager.getInstance().home_screen.toLowerCase().equals("target")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, new TargetFragment())
                    .commit();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (DataManager.getInstance().language.toLowerCase().equals("english") || DataManager.getInstance().language.toLowerCase().equals("SAESNEG".toLowerCase())) {
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        } else if (DataManager.getInstance().language.toLowerCase().equals("welsh") || DataManager.getInstance().language.toLowerCase().equals("CYMRAEG".toLowerCase())) {
            Locale locale = new Locale("cy");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        DataManager.getInstance().context = this;
        DataManager.getInstance().reload();

        ((TextView) findViewById(R.id.main_screen_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
        menu = findViewById(R.id.main_screen_menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                switch (selectedPosition) {
                    case 1: {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_fragment, new FeedFragment())
                                .commit();
                        break;
                    }
                    case 2: {
                        if (isLandscape)
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment, new Stats())
                                    .commit();
                        else
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment, new Stats2())
                                    .commit();
                        break;
                    }
                    case 3: {
                        logFragment = new LogActivityHistory();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_fragment, logFragment)
                                .commit();
                        break;
                    }
                    case 4: {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_fragment, new TargetFragment())
                                .commit();
                        break;
                    }
                    case 5: {
                        break;
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        adapter = new DrawerAdapter(this);
        navigationView = (ListView) findViewById(R.id.nav_view);
        navigationView.setAdapter(adapter);
        navigationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position != 0) {
                    adapter.selected_image.setColorFilter(0x00FFFFFF);
                    adapter.selected_text.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.light_grey));
                    switch (position) {
                        case 1: {
                            adapter.selected_image = (ImageView) view.findViewById(R.id.drawer_item_icon);
                            adapter.selected_text = (TextView) view.findViewById(R.id.drawer_item_text);
                            adapter.selected_image.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.default_blue));
                            adapter.selected_text.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.default_blue));
                            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                                getSupportFragmentManager().popBackStackImmediate();
                            }
                            selectedPosition = 1;
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        }
                        case 2: {
                            adapter.selected_image = (ImageView) view.findViewById(R.id.drawer_item_icon);
                            adapter.selected_text = (TextView) view.findViewById(R.id.drawer_item_text);
                            adapter.selected_image.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.default_blue));
                            adapter.selected_text.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.default_blue));
                            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                                getSupportFragmentManager().popBackStackImmediate();
                            }
                            selectedPosition = 2;
                            drawer.closeDrawer(GravityCompat.START);

                            break;
                        }
                        case 3: {
                            adapter.selected_image = (ImageView) view.findViewById(R.id.drawer_item_icon);
                            adapter.selected_text = (TextView) view.findViewById(R.id.drawer_item_text);
                            adapter.selected_image.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.default_blue));
                            adapter.selected_text.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.default_blue));
                            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                                getSupportFragmentManager().popBackStackImmediate();
                            }
                            selectedPosition = 3;
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        }
                        case 4: {
                            adapter.selected_image = (ImageView) view.findViewById(R.id.drawer_item_icon);
                            adapter.selected_text = (TextView) view.findViewById(R.id.drawer_item_text);
                            adapter.selected_image.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.default_blue));
                            adapter.selected_text.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.default_blue));
                            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                                getSupportFragmentManager().popBackStackImmediate();
                            }
                            selectedPosition = 4;
                            drawer.closeDrawer(GravityCompat.START);
                            break;
                        }
                        case 5: {
                            final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.confirmation_dialog);
                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            if (DataManager.getInstance().mainActivity.isLandscape) {
                                DisplayMetrics displaymetrics = new DisplayMetrics();
                                DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                                int width = (int) (displaymetrics.widthPixels * 0.45);

                                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                                params.width = width;
                                dialog.getWindow().setAttributes(params);
                            }

                            ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                            ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.confirm);

                            ((TextView) dialog.findViewById(R.id.dialog_message)).setTypeface(DataManager.getInstance().myriadpro_regular);
                            ((TextView) dialog.findViewById(R.id.dialog_message)).setText(R.string.confirm_logout_message);

                            ((TextView) dialog.findViewById(R.id.dialog_no_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                            ((TextView) dialog.findViewById(R.id.dialog_no_text)).setText(R.string.no);

                            ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                            ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setText(R.string.yes);

                            dialog.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    drawer.closeDrawer(GravityCompat.START);
                                    dialog.dismiss();
                                    android.webkit.CookieManager.getInstance().removeAllCookies(null);
                                    DataManager.getInstance().checkForbidden = false;
                                    DataManager.getInstance().set_jwt("");
                                    new Delete().from(CurrentUser.class).execute();
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    MainActivity.this.finish();
                                }
                            });
                            dialog.findViewById(R.id.dialog_no).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                            break;
                        }
                    }
                }

            }
        });
    }

    public void setTitle(String title) {
        ((TextView) findViewById(R.id.main_screen_title)).setText(title);
        ((AutoResizeTextView) findViewById(R.id.main_screen_title)).setLines(1);
        ((AutoResizeTextView) findViewById(R.id.main_screen_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
    }

    int backpressed = 0;

    @Override
    public void onBackPressed() {
        if (DataManager.getInstance().fromTargetItem) {
            DataManager.getInstance().fragment = 3;
            selectedPosition = 3;
            DataManager.getInstance().fromTargetItem = false;
            adapter.notifyDataSetChanged();
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStackImmediate();
            }
            logFragment = new LogActivityHistory();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, logFragment)
                    .commit();
            return;
        }
        if (DataManager.getInstance().addTarget == 1) {
            selectedPosition = 4;
            DataManager.getInstance().fragment = 4;
            adapter.notifyDataSetChanged();
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStackImmediate();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, new TargetFragment())
                    .commit();
            DataManager.getInstance().addTarget = 0;
            return;
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                backpressed = 0;
                getSupportFragmentManager().popBackStackImmediate();
            } else if (backpressed == 0) {
                backpressed = 1;
                Snackbar.make(findViewById(R.id.main_fragment), R.string.press_back_again_to_exit_app, Snackbar.LENGTH_LONG).show();
            } else
                super.onBackPressed();
        }
    }


    public void showProgressBar(@Nullable String text) {
        blackout.setVisibility(View.VISIBLE);
        blackout.requestLayout();
        blackout.setOnClickListener(null);
    }

    public void showProgressBar2(@Nullable String text) {
        blackout.setVisibility(View.VISIBLE);
        blackout.findViewById(R.id.progressbar).setVisibility(View.GONE);
        blackout.requestLayout();
        blackout.setOnClickListener(null);
    }

    public void hideProgressBar() {
        try {
            blackout.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        } catch (Exception ignored) {
        }
        blackout.setVisibility(View.GONE);
    }

    public void hideAllButtons() {
        friend.setVisibility(View.GONE);
        settings.setVisibility(View.GONE);
        addTarget.setVisibility(View.GONE);
        send.setVisibility(View.GONE);
        timer.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        menu.setVisibility(View.GONE);
    }

    public void showCertainButtons(int fragment) {
        switch (fragment) {
            case 1: {
                menu.setVisibility(View.VISIBLE);
                friend.setVisibility(View.VISIBLE);
                settings.setVisibility(View.VISIBLE);
                int count = new Select().from(ReceivedRequest.class).count();
                if (count > 0) {
                    findViewById(R.id.incoming_fr).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.incoming_fr_text)).setText(count + "");
                } else
                    findViewById(R.id.incoming_fr).setVisibility(View.GONE);
                break;
            }
            case 3: {
                settings.setVisibility(View.VISIBLE);
                addTarget.setVisibility(View.VISIBLE);
                menu.setVisibility(View.VISIBLE);

                if (new Select().from(RunningActivity.class).exists()) {
                    addTarget.setVisibility(View.GONE);
                    timer.setVisibility(View.VISIBLE);

                    timer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment, new LogNewActivity(), "newActivity")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                } else {
                    addTarget.setVisibility(View.VISIBLE);
                    timer.setVisibility(View.GONE);

                    addTarget.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (logFragment != null) {
                                logFragment.showDialog();
                            }
                        }
                    });
                }
                break;
            }
            case 4: {
                menu.setVisibility(View.VISIBLE);
                addTarget.setVisibility(View.VISIBLE);
                settings.setVisibility(View.VISIBLE);

                addTarget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_fragment, new AddTarget())
                                .addToBackStack(null)
                                .commit();
                    }
                });
                break;
            }
            case 5: {
                menu.setVisibility(View.VISIBLE);
                settings.setVisibility(View.VISIBLE);
                break;
            }
            case 6: {
                send.setVisibility(View.VISIBLE);
                break;
            }
            case 7: {
                back.setVisibility(View.VISIBLE);
                break;
            }
            case 8: {
                back.setVisibility(View.VISIBLE);
                settings.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 100) {
            final String imagePath = getRealPathFromURI(MainActivity.this, intent.getData());
            showProgressBar(null);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Log.e("Jisc","Image Path: "+imagePath);

                    if (NetworkManager.getInstance().updateProfileImage(imagePath)) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                settings_fragment.refresh_image();
                                hideProgressBar();
                            }
                        });
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressBar();
                        }
                    });

                }
            }).start();
        } else if (requestCode == 101) {

            if (intent != null) {

                final String imagePath = getRealPathFromURI(MainActivity.this, intent.getData());
                showProgressBar(null);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (NetworkManager.getInstance().updateProfileImage(imagePath)) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    settings_fragment.refresh_image();
                                    hideProgressBar();
                                }
                            });
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressBar();
                            }
                        });
                    }
                }).start();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


}
