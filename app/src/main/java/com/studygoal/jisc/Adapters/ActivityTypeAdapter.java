package com.studygoal.jisc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.R;

import java.util.ArrayList;

public class ActivityTypeAdapter extends BaseAdapter {
    public ArrayList<String> activityTypeList;
    LayoutInflater inflater;
    String selected;

    public ActivityTypeAdapter(Context context, String selected) {
        this.selected = selected;
        this.activityTypeList = DataManager.getInstance().activity_type;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return activityTypeList.size();
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
            convertView = inflater.inflate(R.layout.custom_spinner_layout_item, parent, false);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.dialog_item_name);
        textView.setTypeface(DataManager.getInstance().myriadpro_regular);
        textView.setText(activityTypeList.get(position));

        if(activityTypeList.get(position).equals(selected)) {
            convertView.findViewById(R.id.dialog_item_selected).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.dialog_item_selected).setVisibility(View.GONE);
        }
        return convertView;
    }

}
