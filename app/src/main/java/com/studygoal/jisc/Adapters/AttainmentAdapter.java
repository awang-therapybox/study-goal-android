package com.studygoal.jisc.Adapters;

import android.content.Context;
import android.graphics.Color;
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

public class AttainmentAdapter extends BaseAdapter {
    public List<Attainment> list;
    LayoutInflater inflater;

    public AttainmentAdapter(Context context) {
        this.list = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int size = list.size();
        if(DataManager.getInstance().user.affiliation.contains("glos.ac.uk")) {
            size++;
        }
        return size;
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
            convertView = inflater.inflate(R.layout.attainment_item, parent, false);
        }

        if(position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#e3f0ff"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#f0f7ff"));
        }


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
        return convertView;
    }

}
