package com.studygoal.jisc.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.studygoal.jisc.Fragments.TrophiesMy;
import com.studygoal.jisc.Models.TrophyMy;
import com.studygoal.jisc.R;

import java.util.List;

/**
 * Created by MarcelC on 1/14/16.
 */
public class TrophiesMyAdapter extends BaseAdapter implements View.OnClickListener {

    LayoutInflater inflater;
    public List<TrophyMy> list;
    private Context context;
    Fragment fragment;

    public TrophiesMyAdapter(Context context, Fragment fr) {
        this.context = context;
        list = new Select().from(TrophyMy.class).execute();
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

        TrophyMy trophy = list.get(position);
        Glide.with(context).load(trophy.getImageDrawable(context)).into(image);

        TextView text_silver = (TextView) convertView.findViewById(R.id.total_gold);
        TextView text_gold = (TextView) convertView.findViewById(R.id.total_silver);
        text_gold.setVisibility(View.GONE);
        text_silver.setVisibility(View.GONE);
        text_gold.setText(trophy.total);
        text_silver.setText(trophy.total);

        if(trophy.trophy_type.equals("silver"))
        {
            text_gold.setVisibility(View.VISIBLE);
        }
        else
        {
            text_silver.setVisibility(View.VISIBLE);

        }

        convertView.setTag(""+position);
        convertView.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        TrophyMy trophy = list.get(Integer.parseInt((String) v.getTag()));
        ((TrophiesMy) fragment).showTrophy(trophy);
    }
}