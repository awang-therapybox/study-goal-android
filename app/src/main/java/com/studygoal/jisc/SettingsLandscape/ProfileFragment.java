package com.studygoal.jisc.SettingsLandscape;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.studygoal.jisc.Adapters.GenericAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.R;
import com.studygoal.jisc.SettingsActivity;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    ImageView profile_image;
    public Uri imageUri;

    @Override
    public void onResume() {
        super.onResume();
        ((SettingsActivity)getActivity()).fragmentTitle.setText("Profile");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.profile_screen, container, false);

        TextView name = (TextView) mainView.findViewById(R.id.name);
        name.setTypeface(DataManager.getInstance().myriadpro_regular);
        name.setText(DataManager.getInstance().user.name);

        TextView email = (TextView) mainView.findViewById(R.id.email);
        email.setTypeface(DataManager.getInstance().myriadpro_regular);
        email.setText(DataManager.getInstance().user.email + " | " + getContext().getString(R.string.student_id) + " : " + DataManager.getInstance().user.jisc_student_id);


        mainView.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if(DataManager.getInstance().mainActivity.isLandscape) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = (int) (displaymetrics.widthPixels * 0.3);

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = width;
                    dialog.getWindow().setAttributes(params);
                }

                ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(getContext().getString(R.string.select_source));

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                ArrayList<String> list = new ArrayList<>();
                list.add(getContext().getString(R.string.camera));
                list.add(getContext().getString(R.string.library));

                listView.setAdapter(new GenericAdapter(getActivity(), "", list));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                    || (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                            102);
                                }
                            } else {
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                                imageUri = getActivity().getContentResolver().insert(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                DataManager.getInstance().mainActivity.startActivityForResult(intent, 100);
                            }
                        } else {
                            if (ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            103);
                                }
                            } else {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                DataManager.getInstance().mainActivity.startActivityForResult(intent, 101);
                            }
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        profile_image = (ImageView) mainView.findViewById(R.id.profile_picture);
        Glide.with(DataManager.getInstance().mainActivity).load(NetworkManager.getInstance().host + DataManager.getInstance().user.profile_pic).into(profile_image);

        return mainView;
    }
    public void refresh_image() {
//        NetworkManager.getInstance().fn_view_student(DataManager.getInstance().user.id);
        if(NetworkManager.getInstance().login()) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(DataManager.getInstance().mainActivity).load(NetworkManager.getInstance().host + DataManager.getInstance().user.profile_pic).into(profile_image);
                    }
                });
            }
        }).start();
        }
    }
}
