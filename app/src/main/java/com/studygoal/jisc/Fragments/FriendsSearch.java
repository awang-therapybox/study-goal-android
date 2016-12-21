package com.studygoal.jisc.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.studygoal.jisc.Adapters.FriendsSearchAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Friend;
import com.studygoal.jisc.R;
import com.studygoal.jisc.SettingsActivity;
import com.studygoal.jisc.Utils.Utils;

import java.util.HashMap;

public class FriendsSearch extends Fragment {

    ListView list;
    FriendsSearchAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        if (DataManager.getInstance().mainActivity.isLandscape) {
            try {
                ((SettingsActivity) getActivity()).fragmentTitle.setText(getActivity().getString(R.string.friends));
            } catch (Exception ignored) {
                DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.friends));
                DataManager.getInstance().mainActivity.hideAllButtons();
                DataManager.getInstance().mainActivity.showCertainButtons(7);
            }
        } else {
            DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.friends));
            DataManager.getInstance().mainActivity.hideAllButtons();
            DataManager.getInstance().mainActivity.showCertainButtons(7);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkManager.getInstance().getFriends(DataManager.getInstance().user.id);
                NetworkManager.getInstance().getFriendRequests(DataManager.getInstance().user.id);
                NetworkManager.getInstance().getSentFriendRequests(DataManager.getInstance().user.id);
            }
        }).start();
    }

    @Override
    public void onPause() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.friendssearch_fragment, container, false);

        ((TextView) mainView.findViewById(R.id.send_friend_request_text)).setTypeface(DataManager.getInstance().myriadpro_regular);

        adapter = new FriendsSearchAdapter(getContext());
        list = (ListView)mainView.findViewById(R.id.list);
        list.setAdapter(adapter);

        final EditText search = (EditText) mainView.findViewById(R.id.friends_search_edittext);
        View send_friend_request = mainView.findViewById(R.id.send_friend_request);
        send_friend_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = search.getText().toString();
                if (!Utils.validate_email(email)) {
                    if (DataManager.getInstance().isLandscape) {
                        try {
                            Snackbar.make(getActivity().findViewById(R.id.whole_container), R.string.please_search_for_friend, Snackbar.LENGTH_LONG).show();
                        } catch (Exception ignored) {
                            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.please_search_for_friend, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.please_search_for_friend, Snackbar.LENGTH_LONG).show();
                    }
                } else {
//
                    InputMethodManager imm = (InputMethodManager) FriendsSearch.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);

                    search.clearFocus();
                    final Friend friend = NetworkManager.getInstance().getStudentByEmail(search.getText().toString());

                    if (friend != null) {
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.friendrequest);

                        if (DataManager.getInstance().mainActivity.isLandscape) {
                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                            int width = (int) (displaymetrics.widthPixels * 0.4);

                            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                            params.width = width;
                            dialog.getWindow().setAttributes(params);
                        }

                        ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);

                        ((TextView) dialog.findViewById(R.id.question)).setTypeface(DataManager.getInstance().myriadpro_regular);
                        ((TextView) dialog.findViewById(R.id.question)).setText(getActivity().getString(R.string.what_would_you_like_student_to_see).replace("%s", ""));

                        final SwitchCompat switch_2 = (SwitchCompat) dialog.findViewById(R.id.switch2);
                        switch_2.setTypeface(DataManager.getInstance().myriadpro_regular);
                        switch_2.setChecked(true);
                        final SwitchCompat switch_3 = (SwitchCompat) dialog.findViewById(R.id.switch3);
                        switch_3.setTypeface(DataManager.getInstance().myriadpro_regular);
                        switch_3.setChecked(true);
                        final SwitchCompat switch_4 = (SwitchCompat) dialog.findViewById(R.id.switch4);
                        switch_4.setTypeface(DataManager.getInstance().myriadpro_regular);
                        switch_4.setChecked(true);
                        final SwitchCompat switch_1 = (SwitchCompat) dialog.findViewById(R.id.switch1);
                        switch_1.setTypeface(DataManager.getInstance().myriadpro_regular);
                        switch_1.setChecked(true);
                        switch_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
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
                                params.put("from_student_id", DataManager.getInstance().user.id);
                                params.put("to_student_id", friend.id);
                                params.put("is_result", switch_2.isChecked()?"yes":"no");
                                params.put("is_course_engagement", switch_3.isChecked()?"yes":"no");
                                params.put("is_activity_log", switch_4.isChecked()?"yes":"no");
                                if(NetworkManager.getInstance().sendFriendRequest(params)) {
                                    dialog.dismiss();
                                } else {
                                    if(DataManager.getInstance().isLandscape)
                                        try {
                                            Snackbar.make(getActivity().findViewById(R.id.whole_container), R.string.friend_request_sent, Snackbar.LENGTH_LONG).show();
                                        } catch (Exception ignored) {
                                            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.friend_request_sent, Snackbar.LENGTH_LONG).show();
                                        }
                                    else
                                        Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.fail_sendFriendRequest, Snackbar.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }
                        });
                        dialog.show();
                    } else {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("from_student_id", DataManager.getInstance().user.id);
                        params.put("to_email", email);
                        params.put("language", DataManager.getInstance().language);

                        NetworkManager.getInstance().sendFriendRequest(params);
                        if (DataManager.getInstance().isLandscape) {
                            try {
                                Snackbar.make(getActivity().findViewById(R.id.whole_container), R.string.friend_request_sent, Snackbar.LENGTH_LONG).show();
                            } catch (Exception ignored) {
                                Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.friend_request_sent, Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.friend_request_sent, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        return mainView;
    }
}
