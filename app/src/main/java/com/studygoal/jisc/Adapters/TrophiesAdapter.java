package com.studygoal.jisc.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.studygoal.jisc.Fragments.TrophiesAll;
import com.studygoal.jisc.Models.Trophy;
import com.studygoal.jisc.R;

import java.util.List;

public class TrophiesAdapter extends BaseAdapter implements View.OnClickListener {

    LayoutInflater inflater;
    public List<Trophy> list;
    private Context context;
    Fragment fragment;

    public TrophiesAdapter(Context context,Fragment fr) {
        this.context = context;
        list = new Select().from(Trophy.class).execute();
        inflater = LayoutInflater.from(context);
        fragment = fr;
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.trophies_item, parent, false);
        } else {
            ImageView image = (ImageView) convertView.findViewById(R.id.trophy_image);
            image.setImageDrawable(null);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.trophy_image);
        image.setImageDrawable(null);

        Trophy trophy = list.get(position);
        Glide.with(context).load(trophy.getImageDrawable(context)).into(image);

        convertView.setTag("" + position);
        convertView.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {

        Trophy trophy = list.get(Integer.parseInt((String) v.getTag()));
        ((TrophiesAll)fragment).showTrophy(trophy);

    }

}
