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

public class AttainmentAdapter extends BaseAdapter {
    public List<Attainment> list;
    LayoutInflater inflater;

    public AttainmentAdapter(Context context) {
        this.list = new ArrayList<>();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.attainment_item, parent, false);
        }
        Attainment attainment = list.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView percent = (TextView) convertView.findViewById(R.id.percent);

        name.setTypeface(DataManager.getInstance().myriadpro_regular);
        percent.setTypeface(DataManager.getInstance().myriadpro_regular);
        name.setText(Utils.attainmentDate(attainment.date) + " " + attainment.module);
        percent.setText(attainment.percent);

        return convertView;
    }

}
