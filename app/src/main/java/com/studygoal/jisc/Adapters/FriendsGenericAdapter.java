package com.studygoal.jisc.Adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Friend;
import com.studygoal.jisc.R;
import com.studygoal.jisc.SettingsActivity;

import java.util.HashMap;
import java.util.List;

public class FriendsGenericAdapter extends BaseAdapter {

    public List<Friend> list;
    LayoutInflater inflater;
    Context context;

    public FriendsGenericAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        list = new Select().from(Friend.class).execute();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.friend_item, parent, false);
        }
        final View hide = convertView.findViewById(R.id.hide);
        final View unhide = convertView.findViewById(R.id.unhide);

        final Friend friend = list.get(position);
        TextView textView = (TextView)convertView.findViewById(R.id.dialog_item_name);
        textView.setTypeface(DataManager.getInstance().myriadpro_regular);
        textView.setText(friend.name);


        unhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("from_student_id", DataManager.getInstance().user.id);
                map.put("to_student_id", friend.id);
                if(NetworkManager.getInstance().unhideFriend(map)) {
                    friend.hidden = false;
                    friend.save();
                    unhide.setVisibility(View.INVISIBLE);
                    hide.setVisibility(View.VISIBLE);
                } else {
                    if(!DataManager.getInstance().mainActivity.isLandscape)
                        Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.failed_to_unhide_friend, Snackbar.LENGTH_LONG).show();
                    else
                        Snackbar.make(((SettingsActivity)context).findViewById(R.id.drawer_layout), R.string.failed_to_unhide_friend, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("from_student_id", DataManager.getInstance().user.id);
                map.put("to_student_id", friend.id);
                if(NetworkManager.getInstance().hideFriend(map)) {
                    friend.hidden = true;
                    friend.save();
                    hide.setVisibility(View.INVISIBLE);
                    unhide.setVisibility(View.VISIBLE);
                } else {
                    if(!DataManager.getInstance().mainActivity.isLandscape)
                        Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.failed_to_hide_friend, Snackbar.LENGTH_LONG).show();
                    else
                        Snackbar.make(((SettingsActivity)context).findViewById(R.id.drawer_layout), R.string.failed_to_hide_friend, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        if(friend.hidden) {
            unhide.setVisibility(View.VISIBLE);
        } else {
            hide.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

}
