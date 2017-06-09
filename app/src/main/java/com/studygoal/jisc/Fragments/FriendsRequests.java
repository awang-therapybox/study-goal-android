package com.studygoal.jisc.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.FriendsRequestAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.ReceivedRequest;
import com.studygoal.jisc.R;
import com.studygoal.jisc.SettingsActivity;

public class FriendsRequests extends Fragment {

    FriendsRequestAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        if (!DataManager.getInstance().isLandscape) {
            DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.my_requests_title));
            DataManager.getInstance().mainActivity.hideAllButtons();
            DataManager.getInstance().mainActivity.showCertainButtons(5);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkManager.getInstance().getFriendRequests(DataManager.getInstance().user.id);
                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.list = new Select().from(ReceivedRequest.class).execute();
                            adapter.notifyDataSetChanged();
    //                                DataManager.getInstance().mainActivity.hideProgressBar();
                        }
                    });
                }
            }).start();
        } else {
            try {
                ((SettingsActivity) getActivity()).fragmentTitle.setText(getActivity().getString(R.string.my_requests_title));
            } catch (Exception ignored) {
                DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.my_requests_title));
                DataManager.getInstance().mainActivity.hideAllButtons();
                DataManager.getInstance().mainActivity.showCertainButtons(5);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkManager.getInstance().getFriendRequests(DataManager.getInstance().user.id);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.list = new Select().from(ReceivedRequest.class).execute();
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.friendsrequests, container, false);

//            NetworkManager.getInstance().getFriends(DataManager.getInstance().user.id);
//            NetworkManager.getInstance().getFriendRequests(DataManager.getInstance().user.id);
//            NetworkManager.getInstance().getSentFriendRequests(DataManager.getInstance().user.id);

        final ListView list = (ListView) mainView.findViewById(R.id.list);
        if(DataManager.getInstance().isLandscape)
            adapter = new FriendsRequestAdapter(getActivity());
        else
            adapter = new FriendsRequestAdapter(DataManager.getInstance().mainActivity);
        list.setAdapter(adapter);

        return mainView;
    }
}
