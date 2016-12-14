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
import com.studygoal.jisc.Models.PendingRequest;
import com.studygoal.jisc.Models.ReceivedRequest;
import com.studygoal.jisc.R;
import com.studygoal.jisc.SettingsActivity;
import com.studygoal.jisc.Utils.CircleTransform;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsSearchAdapter extends BaseAdapter {

    LayoutInflater inflater;
    public ArrayList<Friend> list;
    private Context context;

    public FriendsSearchAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();

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
            convertView = inflater.inflate(R.layout.friendsearch_item, parent, false);
        }
        final Friend attendant = list.get(position);

        if(attendant.profile_pic.equals("")) {
            Glide.with(context)
                    .load(R.drawable.profilenotfound)
                    .transform(new CircleTransform(context))
                    .into(((ImageView) convertView.findViewById(R.id.portrait)));
        } else {
            Glide.with(context)
                    .load(NetworkManager.getInstance().host + attendant.profile_pic)
                    .transform(new CircleTransform(context))
                    .placeholder(R.drawable.profilenotfound)
                    .into(((ImageView) convertView.findViewById(R.id.portrait)));
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setTypeface(DataManager.getInstance().myriadpro_regular);
        name.setText(attendant.name);

        final View pendingRequest = convertView.findViewById(R.id.pendingrequest);
        final View sendRequest = convertView.findViewById(R.id.sendrequest);
        final View receivingRequest = convertView.findViewById(R.id.receivingrequest);
        final View friends = convertView.findViewById(R.id.friends);
        View.OnClickListener pendingListener;

        final View.OnClickListener sendListener = new View.OnClickListener() {
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

                ((TextView)dialog.findViewById(R.id.question)).setTypeface(DataManager.getInstance().myriadpro_regular);
                ((TextView)dialog.findViewById(R.id.question)).setText(context.getString(R.string.what_would_you_like_student_to_see).replace("%s", attendant.name));

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
                        Friend attendant1 = list.get(position);

                        HashMap<String, String> params = new HashMap<>();
                        params.put("from_student_id", DataManager.getInstance().user.id);
                        params.put("to_student_id", attendant1.id);
                        params.put("is_result", switch_2.isChecked()?"yes":"no");
                        params.put("is_course_engagement", switch_3.isChecked()?"yes":"no");
                        params.put("is_activity_log", switch_4.isChecked()?"yes":"no");

                        if(NetworkManager.getInstance().sendFriendRequest(params)) {
                            list.remove(attendant1);
                            dialog.dismiss();
                            sendRequest.setVisibility(View.GONE);
                            pendingRequest.setVisibility(View.VISIBLE);
                            notifyDataSetChanged();
                        } else {
                            if(DataManager.getInstance().isLandscape) {
                                try {
                                    Snackbar.make(((SettingsActivity) context).findViewById(R.id.whole_container), R.string.fail_sendFriendRequest, Snackbar.LENGTH_LONG).show();
                                } catch (Exception ignored) {
                                    Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.fail_sendFriendRequest, Snackbar.LENGTH_LONG).show();
                                }
                            } else {
                                Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.fail_sendFriendRequest, Snackbar.LENGTH_LONG).show();
                            }
                            list.remove(attendant1);
                            dialog.dismiss();
                            notifyDataSetChanged();
                        }
                    }
                });
                dialog.show();
            }
        };
        pendingListener = new View.OnClickListener() {
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
                ((TextView) dialog.findViewById(R.id.dialog_message)).setText(R.string.cancel_pending_request);

                ((TextView) dialog.findViewById(R.id.dialog_no_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                ((TextView) dialog.findViewById(R.id.dialog_no_text)).setText(R.string.no);

                ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setText(R.string.yes);

                dialog.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("student_id", DataManager.getInstance().user.id);
                        params.put("friend_id", attendant.id);
                        if(NetworkManager.getInstance().cancelFriendRequest(params)) {
                            dialog.dismiss();
                            notifyDataSetChanged();
                            pendingRequest.setVisibility(View.GONE);
                            sendRequest.setVisibility(View.VISIBLE);
                            receivingRequest.setVisibility(View.GONE);
                            friends.setVisibility(View.GONE);
                        } else {
                            if(DataManager.getInstance().isLandscape) {
                                try {
                                    Snackbar.make(((SettingsActivity) context).findViewById(R.id.whole_container), R.string.fail_cancelFriendRequest, Snackbar.LENGTH_LONG).show();
                                } catch (Exception ignored) {
                                    Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.fail_cancelFriendRequest, Snackbar.LENGTH_LONG).show();
                                }
                            } else {
                                Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.fail_cancelFriendRequest, Snackbar.LENGTH_LONG).show();
                            }

                            dialog.dismiss();
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
        };
        pendingRequest.setOnClickListener(pendingListener);
        sendRequest.setOnClickListener(sendListener);

        if(new Select().from(PendingRequest.class).where("pr_id = ?", attendant.id).exists()) {
            pendingRequest.setVisibility(View.VISIBLE);
            //TODO: DE IMPLEMNETAT CANCEL FRIEND REQUEST - atentie; dau cancel si nu se seteaza listenerurile
            pendingRequest.setOnClickListener(pendingListener);

            sendRequest.setVisibility(View.GONE);
            receivingRequest.setVisibility(View.GONE);
            friends.setVisibility(View.GONE);
        } else if(new Select().from(ReceivedRequest.class).where("re_id = ?", attendant.id).exists()) {
            pendingRequest.setVisibility(View.GONE);
            sendRequest.setVisibility(View.GONE);
            receivingRequest.setVisibility(View.VISIBLE);
            friends.setVisibility(View.GONE);
        } else if(new Select().from(Friend.class).where("friend_id = ?", attendant.id).exists()) {
            pendingRequest.setVisibility(View.GONE);
            sendRequest.setVisibility(View.GONE);
            receivingRequest.setVisibility(View.GONE);
            friends.setVisibility(View.VISIBLE);
        }
        else {
            receivingRequest.setVisibility(View.GONE);
            friends.setVisibility(View.GONE);
            pendingRequest.setVisibility(View.GONE);
            sendRequest.setVisibility(View.VISIBLE);
            sendRequest.setOnClickListener(sendListener);
        }

        return convertView;
    }
}

