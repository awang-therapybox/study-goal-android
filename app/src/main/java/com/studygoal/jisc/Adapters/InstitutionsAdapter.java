package com.studygoal.jisc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Models.Institution;
import com.studygoal.jisc.R;

import java.util.ArrayList;
import java.util.List;

public class InstitutionsAdapter extends BaseAdapter {

    public List<Institution> institutions;
    LayoutInflater inflater;
    Context context;

    public InstitutionsAdapter(Context c) {
        context = c;
        institutions = new ArrayList<>();
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return institutions.size()+2;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.institution_item, viewGroup, false);
        }

        if(i < institutions.size()) {
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setTypeface(DataManager.getInstance().myriadpro_regular);
            name.setText(institutions.get(i).name);
            name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            view.setTag(institutions.get(i));
        } else if(i == institutions.size()){
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setTypeface(DataManager.getInstance().myriadpro_bold);
            name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            name.setText(context.getString(R.string.institution_no_listed));

            view.setTag("no institution");
        }
        else {
        TextView name = (TextView) view.findViewById(R.id.name);
        name.setTypeface(DataManager.getInstance().myriadpro_bold);
        name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        name.setText(context.getString(R.string.demo_mode));

        view.setTag("demo");
    }


    return view;
    }
}
