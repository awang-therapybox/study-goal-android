package com.studygoal.jisc.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.studygoal.jisc.Adapters.GenericAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Friend;
import com.studygoal.jisc.Models.TrophyMy;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.CircleTransform;

import java.util.ArrayList;

public class Settings extends Fragment {

    private TextView home_value;
    private TextView language_value;
    private ImageView profile_image;
//    Uri imageUri;
    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.settings));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(7);

        String selected_value = "";
        switch (DataManager.getInstance().home_screen.toLowerCase()) {
            case "feed": {
                selected_value = getActivity().getString(R.string.feed);
                break;
            }
            case "stats": {
                selected_value = getActivity().getString(R.string.stats);
                break;
            }
            case "log": {
                selected_value = getActivity().getString(R.string.log);
                break;
            }
            case "target": {
                selected_value = getActivity().getString(R.string.target);
                break;
            }
        }
        home_value.setText(selected_value.toUpperCase());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Typeface font = DataManager.getInstance().myriadpro_regular;
        View.OnClickListener onClickListener = new SettingsOnClickListener();

        View mainView = inflater.inflate(R.layout.settings_home, container, false);
        TextView friends = (TextView) mainView.findViewById(R.id.friends);
        friends.setTypeface(font);

        TextView friends_value = (TextView) mainView.findViewById(R.id.friends_value);
        friends_value.setTypeface(font);
        friends_value.setText(new Select().from(Friend.class).count() + "");

        TextView home = (TextView) mainView.findViewById(R.id.home);
        home.setTypeface(font);
        home_value = (TextView) mainView.findViewById(R.id.home_value);
        home_value.setTypeface(font);

        TextView trophies = (TextView) mainView.findViewById(R.id.trophies);
        trophies.setTypeface(font);
        TextView trophies_value = (TextView) mainView.findViewById(R.id.trophies_value);
        trophies_value.setTypeface(font);
        trophies_value.setText(new Select().from(TrophyMy.class).count() + "");//);

        TextView language = (TextView) mainView.findViewById(R.id.language);
        language.setTypeface(font);
        language_value = (TextView) mainView.findViewById(R.id.language_value);
        language_value.setTypeface(font);
        language_value.setText(DataManager.getInstance().language.toLowerCase().equals("english")?getString(R.string.english).toUpperCase():getString(R.string.welsh).toUpperCase());

        ((TextView)mainView.findViewById(R.id.email_text)).setTypeface(font);

        TextView privacy = (TextView) mainView.findViewById(R.id.privacy_text);
        privacy.setTypeface(font);

        mainView.findViewById(R.id.email_layout).setOnClickListener(onClickListener);
        mainView.findViewById(R.id.friends_layout).setOnClickListener(onClickListener);
        mainView.findViewById(R.id.trophies_layout).setOnClickListener(onClickListener);
        mainView.findViewById(R.id.home_layout).setOnClickListener(onClickListener);
        mainView.findViewById(R.id.language_layout).setOnClickListener(onClickListener);
        mainView.findViewById(R.id.privacy_layout).setOnClickListener(onClickListener);

        /** Upper Region */
        TextView name = (TextView) mainView.findViewById(R.id.name);
        name.setTypeface(font);
        name.setText(DataManager.getInstance().user.name);
        TextView email = (TextView) mainView.findViewById(R.id.email);
        email.setTypeface(font);
        email.setText(DataManager.getInstance().user.email + " | Student ID : " + DataManager.getInstance().user.jisc_student_id);
        mainView.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(DataManager.getInstance().user.isDemo) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Settings.this.getActivity());
                    alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.demo_mode_updateprofileimage) + "</font>"));
                    alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return;
                }

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(DataManager.getInstance().mainActivity.getString(R.string.select_source));

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                ArrayList<String> list = new ArrayList<>();
                list.add(DataManager.getInstance().mainActivity.getString(R.string.camera));
                list.add(DataManager.getInstance().mainActivity.getString(R.string.library));

                listView.setAdapter(new GenericAdapter(getActivity(), home_value.getText().toString().toUpperCase(), list));
                listView.setOnItemClickListener(new SettingsOnItemClickListener(dialog));
                dialog.show();
            }
        });

        profile_image = (ImageView) mainView.findViewById(R.id.profile_picture);
        Glide.with(DataManager.getInstance().mainActivity).load(NetworkManager.getInstance().host + DataManager.getInstance().user.profile_pic).into(profile_image);
        return mainView;
    }

    public void refresh_image() {
        final DataManager manager = DataManager.getInstance();

        if(manager.user.isSocial) {
            Integer response = NetworkManager.getInstance().loginSocial(manager.user.email, manager.user.password);
            if (response != 200) { return; }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    manager.mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(manager.mainActivity).load(NetworkManager.getInstance().host + manager.user.profile_pic).into(profile_image);
                            Glide.with(manager.mainActivity).load(NetworkManager.getInstance().host + manager.user.profile_pic).transform(new CircleTransform(manager.mainActivity)).into(manager.mainActivity.adapter.profile_pic);

                        }
                    });
                }
            }).start();

        } else {
            if (NetworkManager.getInstance().login()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        manager.mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(manager.mainActivity).load(NetworkManager.getInstance().host + manager.user.profile_pic).into(profile_image);
                                Glide.with(manager.mainActivity).load(NetworkManager.getInstance().host + manager.user.profile_pic).transform(new CircleTransform(manager.mainActivity)).into(manager.mainActivity.adapter.profile_pic);

                            }
                        });
                    }
                }).start();
            }
        }
    }


    private class SettingsOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.email_layout) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "support@jisclearninganalytics.freshdesk.com", null));
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Bug/Feature idea " + DataManager.getInstance().institution);
                emailIntent.putExtra(Intent.EXTRA_TEXT, "+" + getString(R.string.is_this_a_but_or_a_feature) + "\n" +
                        "+"+ getString(R.string.which_part)+"\n" +
                        "+"+ getString(R.string.further_detail));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            } else if (id == R.id.friends_layout) {
                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new Friends())
                        .addToBackStack(null)
                        .commit();
            } else if (id == R.id.trophies_layout) {
                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new Trophies())
                        .addToBackStack(null)
                        .commit();
            } else if (id == R.id.home_layout) {
                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new HomeScreen())
                        .addToBackStack(null)
                        .commit();

            } else if (id == R.id.language_layout) {
                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new LanguageScreen())
                        .addToBackStack(null)
                        .commit();
            } else if (id == R.id.privacy_layout) {
                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new PrivacyWebViewFragment())
                        .addToBackStack(null)
                        .commit();

            }
        }
    }

    private class SettingsOnItemClickListener implements AdapterView.OnItemClickListener {

        private Dialog dialog;

        SettingsOnItemClickListener(Dialog dialog){
            this.dialog = dialog;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {

                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            102);
                } else {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

                    // imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    DataManager.getInstance().mainActivity.startActivityForResult(intent, 100);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 103);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    DataManager.getInstance().mainActivity.startActivityForResult(intent, 101);
                }
            }

            dialog.dismiss();
        }
    }


}
