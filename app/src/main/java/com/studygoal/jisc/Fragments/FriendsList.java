package com.studygoal.jisc.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.FriendsListAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Friend;
import com.studygoal.jisc.R;
import com.studygoal.jisc.SettingsActivity;

public class FriendsList extends Fragment {

    FriendsListAdapter adapter;
    ListView list;

    @Override
    public void onResume() {
        super.onResume();
        if(!DataManager.getInstance().isLandscape) {
            DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.my_friends_title));
            DataManager.getInstance().mainActivity.hideAllButtons();
            DataManager.getInstance().mainActivity.showCertainButtons(5);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkManager.getInstance().getFriends(DataManager.getInstance().user.id);

                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new FriendsListAdapter(DataManager.getInstance().mainActivity);
                            list.setAdapter(adapter);
                        }
                    });
                }
            }).start();
        } else {
            try {
                ((SettingsActivity) getActivity()).fragmentTitle.setText(getActivity().getString(R.string.my_friends_title));
            } catch (Exception ignored) {
                DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.my_friends_title));
                DataManager.getInstance().mainActivity.hideAllButtons();
                DataManager.getInstance().mainActivity.showCertainButtons(5);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkManager.getInstance().getFriends(DataManager.getInstance().user.id);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new FriendsListAdapter(getActivity());
                            list.setAdapter(adapter);
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void onPause() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.friendslist, container, false);


        EditText search = (EditText) mainView.findViewById(R.id.friends_search_edittext);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.list.clear();
                adapter.list = new Select().from(Friend.class).where("name LIKE ?", "%" + s + "%").execute();
                adapter.notifyDataSetChanged();
            }
        });

        list = (ListView) mainView.findViewById(R.id.list);

        return mainView;
    }
}
