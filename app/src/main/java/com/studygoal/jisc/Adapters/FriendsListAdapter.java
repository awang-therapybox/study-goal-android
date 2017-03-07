package com.studygoal.jisc.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Friend;
import com.studygoal.jisc.R;
import com.studygoal.jisc.SettingsActivity;
import com.studygoal.jisc.Utils.CircleTransform;

import java.util.HashMap;
import java.util.List;

public class FriendsListAdapter extends BaseAdapter {

    LayoutInflater inflater;
    public List<Friend> list;
    private Context context;

    public FriendsListAdapter(Context context) {
        this.context = context;
        list = new Select().from(Friend.class).execute();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.friendlist_item, parent, false);
        }
        final Friend attendant = list.get(position);

        if(attendant.profile_pic.equals(""))
            Glide.with(context).load(R.drawable.profilenotfound).transform(new CircleTransform(context)).into(((ImageView) convertView.findViewById(R.id.portrait)));
        else
            Glide.with(context).load(NetworkManager.getInstance().host + attendant.profile_pic).transform(new CircleTransform(context)).placeholder(R.drawable.profilenotfound).into(((ImageView) convertView.findViewById(R.id.portrait)));

        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setTypeface(DataManager.getInstance().myriadpro_regular);
        name.setText(attendant.name);

        final View hide = convertView.findViewById(R.id.hide);
        final View unhide = convertView.findViewById(R.id.unhide);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.friendrequest);

                if(DataManager.getInstance().mainActivity.isLandscape) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = (int) (displaymetrics.widthPixels * 0.4);

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = width;
                    dialog.getWindow().setAttributes(params);
                }

                ((TextView)dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView)dialog.findViewById(R.id.dialog_title)).setText(R.string.friend_privacy_settings);

                ((TextView)dialog.findViewById(R.id.question)).setTypeface(DataManager.getInstance().myriadpro_regular);
                if(attendant.name.equals(" ")) {
                    ((TextView)dialog.findViewById(R.id.question)).setText(context.getString(R.string.what_would_you_like_friend_to_see));
                } else {
                    ((TextView)dialog.findViewById(R.id.question)).setText(context.getString(R.string.what_would_you_like_student_to_see).replace("%s", attendant.name ));
                }


                final SwitchCompat switch_2 = (SwitchCompat)dialog.findViewById(R.id.switch2);
                switch_2.setTypeface(DataManager.getInstance().myriadpro_regular);
                switch_2.setChecked(true);
                final SwitchCompat switch_3 = (SwitchCompat)dialog.findViewById(R.id.switch3);
                switch_3.setTypeface(DataManager.getInstance().myriadpro_regular);
                switch_3.setChecked(true);
                final SwitchCompat switch_4 = (SwitchCompat)dialog.findViewById(R.id.switch4);
                switch_4.setTypeface(DataManager.getInstance().myriadpro_regular);
                switch_4.setChecked(true);
                final SwitchCompat switch_1 = (SwitchCompat)dialog.findViewById(R.id.switch1);
                switch_1.setTypeface(DataManager.getInstance().myriadpro_regular);
                switch_1.setChecked(true);

                switch_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            switch_2.setChecked(true);
                            switch_3.setChecked(true);
                            switch_4.setChecked(true);
                        } else {
                            switch_2.setChecked(false);
                            switch_3.setChecked(false);
                            switch_4.setChecked(false);
                        }
                    }
                });

                dialog.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("student_id", DataManager.getInstance().user.id);
                        params.put("friend_id", attendant.id);
                        params.put("is_result", switch_2.isChecked()?"yes":"no");
                        params.put("is_course_engagement", switch_3.isChecked()?"yes":"no");
                        params.put("is_activity_log", switch_4.isChecked()?"yes":"no");
                        if(NetworkManager.getInstance().changeFriendSettings(params)) {
                            dialog.dismiss();
                            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.successfully_tochangefriend, Snackbar.LENGTH_LONG).show();
                        } else {
                            if(DataManager.getInstance().isLandscape)
                                try {
                                    Snackbar.make(((SettingsActivity) context).findViewById(R.id.whole_container), R.string.failed_tochangefriend, Snackbar.LENGTH_LONG).show();
                                } catch (Exception ignored) {
                                    Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.failed_tochangefriend, Snackbar.LENGTH_LONG).show();
                                }
                            else
                                Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.failed_tochangefriend, Snackbar.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.confirmation_dialog);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (DataManager.getInstance().mainActivity.isLandscape) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = (int) (displaymetrics.widthPixels * 0.45);

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = width;
                    dialog.getWindow().setAttributes(params);
                }
                ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.confirmation);

                ((TextView) dialog.findViewById(R.id.dialog_message)).setTypeface(DataManager.getInstance().myriadpro_regular);
                ((TextView) dialog.findViewById(R.id.dialog_message)).setText(R.string.are_you_sure_you_want_to_delete_this_friend);

                ((TextView) dialog.findViewById(R.id.dialog_no_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                ((TextView) dialog.findViewById(R.id.dialog_no_text)).setText(R.string.no);

                ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setText(R.string.yes);

                dialog.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(DataManager.getInstance().user.isDemo) {
                            attendant.delete();
                            list.remove(attendant);
                            notifyDataSetChanged();
                            return;
                        }

                        dialog.dismiss();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("student_id", DataManager.getInstance().user.id);
                        params.put("friend_id", attendant.id);
                        if(NetworkManager.getInstance().deleteFriend(params)) {
                            attendant.delete();
                            list.remove(attendant);
                            notifyDataSetChanged();
                            if(!DataManager.getInstance().isLandscape)
                                Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.friend_deleted_successfully, Snackbar.LENGTH_LONG).show();
                            else
                                try {
                                    Snackbar.make(((SettingsActivity) context).findViewById(R.id.whole_container), R.string.friend_deleted_successfully, Snackbar.LENGTH_LONG).show();
                                } catch (Exception ignored) {
                                    Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.friend_deleted_successfully, Snackbar.LENGTH_LONG).show();
                                }
                        } else {
                            if(!DataManager.getInstance().isLandscape)
                                Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.not_deleted_friend_message, Snackbar.LENGTH_LONG).show();
                            else
                                try {
                                    Snackbar.make(((SettingsActivity) context).findViewById(R.id.whole_container), R.string.not_deleted_friend_message, Snackbar.LENGTH_LONG).show();
                                } catch (Exception ignored) {
                                    Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout).findViewById(R.id.whole_container), R.string.not_deleted_friend_message, Snackbar.LENGTH_LONG).show();
                                }
                        }
                    }
                });
                dialog.findViewById(R.id.dialog_no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataManager.getInstance().user.isDemo) {
                    attendant.hidden = true;
                    attendant.save();
                    hide.setVisibility(View.INVISIBLE);
                    unhide.setVisibility(View.VISIBLE);
                    return;
                }

                HashMap<String, String> map = new HashMap<>();
                map.put("from_student_id", DataManager.getInstance().user.id);
                map.put("to_student_id", attendant.id);
                if(NetworkManager.getInstance().hideFriend(map)) {
                    attendant.hidden = true;
                    attendant.save();
                    hide.setVisibility(View.INVISIBLE);
                    unhide.setVisibility(View.VISIBLE);
                } else {
                    if(!DataManager.getInstance().mainActivity.isLandscape)
                        Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.failed_to_hide_friend, Snackbar.LENGTH_LONG).show();
                    else
                        try {
                            Snackbar.make(((SettingsActivity) context).findViewById(R.id.whole_container), R.string.failed_to_hide_friend, Snackbar.LENGTH_LONG).show();
                        } catch (Exception ignored) {
                            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.failed_to_hide_friend, Snackbar.LENGTH_LONG).show();
                        }
                }
            }
        });

        unhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(DataManager.getInstance().user.isDemo) {
                    attendant.hidden = false;
                    attendant.save();
                    unhide.setVisibility(View.INVISIBLE);
                    hide.setVisibility(View.VISIBLE);
                    return;
                }

                HashMap<String, String> map = new HashMap<>();
                map.put("from_student_id", DataManager.getInstance().user.id);
                map.put("to_student_id", attendant.id);
                if(NetworkManager.getInstance().unhideFriend(map)) {
                    attendant.hidden = false;
                    attendant.save();
                    unhide.setVisibility(View.INVISIBLE);
                    hide.setVisibility(View.VISIBLE);
                } else {
                    if(!DataManager.getInstance().mainActivity.isLandscape)
                        Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.failed_to_unhide_friend, Snackbar.LENGTH_LONG).show();
                    else
                        try {
                            Snackbar.make(((SettingsActivity) context).findViewById(R.id.whole_container), R.string.failed_to_unhide_friend, Snackbar.LENGTH_LONG).show();
                        } catch (Exception ignored) {
                            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.failed_to_unhide_friend, Snackbar.LENGTH_LONG).show();
                        }
                }
            }
        });

        if(attendant.hidden) {
            unhide.setVisibility(View.VISIBLE);
        } else {
            hide.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}

