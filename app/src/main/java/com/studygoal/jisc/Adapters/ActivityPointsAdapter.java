package com.studygoal.jisc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Models.Attainment;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ActivityPointsAdapter extends BaseAdapter {
    public List<Attainment> list;
    LayoutInflater inflater;

    public ActivityPointsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return DataManager.getInstance().user.points.size()+1;
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

        if(position == 0) {
            convertView = inflater.inflate(R.layout.attainment_item, parent, false);
        } else {
            convertView = inflater.inflate(R.layout.attainment_item, parent, false);


            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView percent = (TextView) convertView.findViewById(R.id.percent);
            name.setTypeface(DataManager.getInstance().myriadpro_regular);
            percent.setTypeface(DataManager.getInstance().myriadpro_regular);

            if(position >= list.size() && DataManager.getInstance().user.affiliation.contains("glos.ac.uk")) {

                name.setText(DataManager.getInstance().mainActivity.getString(R.string.attainment_info));
                percent.setVisibility(View.GONE);

            } else {
                Attainment attainment = list.get(position);
                percent.setVisibility(View.VISIBLE);
                name.setText(Utils.attainmentDate(attainment.date) + " " + attainment.module);
                percent.setText(attainment.percent);
            }
        }


        return convertView;
    }

}
