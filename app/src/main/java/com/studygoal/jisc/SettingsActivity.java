package com.studygoal.jisc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.studygoal.jisc.Fragments.Friends;
import com.studygoal.jisc.Fragments.HomeScreen;
import com.studygoal.jisc.Fragments.LanguageScreen;
import com.studygoal.jisc.Fragments.Trophies;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.SettingsLandscape.ListMenuAdapter;
import com.studygoal.jisc.SettingsLandscape.ProfileFragment;

import java.io.ByteArrayOutputStream;

public class SettingsActivity extends AppCompatActivity {

    public TextView fragmentTitle;
    public ProfileFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_settings);
        DataManager.getInstance().currActivity = this;
        findViewById(R.id.main_screen_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        ((TextView)findViewById(R.id.settings_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
        (fragmentTitle = (TextView)findViewById(R.id.fragment_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);

        final ListView list_menu = (ListView) findViewById(R.id.list_menu);
        final ListMenuAdapter[] adapter = {new ListMenuAdapter(this, 0)};
        list_menu.setAdapter(adapter[0]);

        list_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 5) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "learning.analytics@jisc.ac.uk", null));
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Bug/Feature idea " + DataManager.getInstance().institution);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "+ Bug or feature?\n" +
                            "+ Which parts of the app are affected (feeds, stats, logs, targets, other)??\n" +
                            "+ Further Detail: ");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } else {
                    adapter[0] = new ListMenuAdapter(SettingsActivity.this, position);
                    list_menu.setAdapter(adapter[0]);

                    switch (position) {
                        case 0: {
                            fragment = new ProfileFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, fragment)
                                    .commit();
                            break;
                        }
                        case 1: {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new Friends())
                                    .commit();
                            break;
                        }
                        case 2: {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new HomeScreen())
                                    .commit();
                            break;
                        }
                        case 3: {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new Trophies())
                                    .commit();
                            break;
                        }
                        case 4: {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new LanguageScreen())
                                    .commit();
                            break;
                        }
                    }

                }
            }
        });

    }

    public void showProgressBar(@Nullable String text) {
        findViewById(R.id.blackout).setVisibility(View.VISIBLE);
        findViewById(R.id.blackout).setOnClickListener(null);
    }

    public void hideProgressBar() {
        findViewById(R.id.blackout).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 100) {

            if (intent != null) {

                final String imagePath = getRealPathFromURI(fragment.imageUri);
                showProgressBar(null);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(NetworkManager.getInstance().updateProfileImage(imagePath)) {
                            SettingsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fragment.refresh_image();
                                    hideProgressBar();
                                }
                            });
                        }
                        SettingsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressBar();
                            }
                        });
                    }
                }).start();

            }
        } else if (requestCode == 101) {

            if (intent != null) {

                Uri pickedImage = intent.getData();

                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = this.getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();

                final String imagePathh = cursor.getString(cursor.getColumnIndex(filePath[0]));

                showProgressBar(null);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(NetworkManager.getInstance().updateProfileImage(imagePathh)) {
                            SettingsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fragment.refresh_image();
                                    hideProgressBar();
                                }
                            });
                        }
                        SettingsActivity.this.runOnUiThread(new Runnable() {
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

    public String getRealPathFromURI(Uri uri) {

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}

