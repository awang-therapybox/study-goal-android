package com.studygoal.jisc.SettingsLandscape;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.R;
import com.studygoal.jisc.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class ListMenuAdapter extends BaseAdapter {

    LayoutInflater inflater;
    SettingsActivity context;
    List<String> list;
    int selected;

    public ListMenuAdapter(SettingsActivity context, int selected) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        list = new ArrayList<>();
        list.add(context.getString(R.string.profile));
        list.add(context.getString(R.string.my_friends));
        list.add(context.getString(R.string.home_screen));
        list.add(context.getString(R.string.trophies));
        list.add(context.getString(R.string.language));
        list.add(context.getString(R.string.report_feature_bug));

        this.selected = selected;
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
        if(convertView == null)
            convertView = inflater.inflate(R.layout.menu_item, parent, false);

        ImageView image = ((ImageView) convertView.findViewById(R.id.image));
        ImageView arrow = ((ImageView) convertView.findViewById(R.id.arrow));
        TextView textView = ((TextView) convertView.findViewById(R.id.text));
        textView.setTypeface(DataManager.getInstance().myriadpro_regular);

        switch (position) {
            case 0: {
                image.setVisibility(View.VISIBLE);
                image.setImageResource(R.drawable.profileicon);
                textView.setText(context.getString(R.string.profile));
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams)textView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                textView.setLayoutParams(layoutParams);
                arrow.setVisibility(View.INVISIBLE);
                if(selected == 0) {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.pink));
                    arrow.setVisibility(View.VISIBLE);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.pink));
                } else {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                    arrow.setVisibility(View.INVISIBLE);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
                break;
            }
            case 1: {
                image.setVisibility(View.VISIBLE);
                image.setImageResource(R.drawable.friends);
                textView.setText(context.getString(R.string.my_friends));
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams)textView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                textView.setLayoutParams(layoutParams);
                arrow.setVisibility(View.INVISIBLE);
                if(selected == 1) {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.pink));
                    arrow.setVisibility(View.VISIBLE);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.pink));
                } else {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                    arrow.setVisibility(View.INVISIBLE);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
                break;
            }
            case 2: {
                image.setVisibility(View.VISIBLE);
                image.setImageResource(R.drawable.home);
                textView.setText(context.getString(R.string.home_screen));
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams)textView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                textView.setLayoutParams(layoutParams);
                arrow.setVisibility(View.INVISIBLE);
                if(selected == 2) {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.pink));
                    arrow.setVisibility(View.VISIBLE);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.pink));
                } else {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                    arrow.setVisibility(View.INVISIBLE);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
                break;
            }
            case 3: {
                image.setVisibility(View.VISIBLE);
                image.setImageResource(R.drawable.trophies);
                textView.setText(context.getString(R.string.trophies));
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams)textView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                textView.setLayoutParams(layoutParams);
                arrow.setVisibility(View.INVISIBLE);
                if(selected == 3) {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.pink));
                    arrow.setVisibility(View.VISIBLE);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.pink));
                } else {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                    arrow.setVisibility(View.INVISIBLE);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
                break;
            }
            case 4: {
                image.setVisibility(View.VISIBLE);
                image.setImageResource(R.drawable.language);
                textView.setText(context.getString(R.string.language));
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams)textView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                textView.setLayoutParams(layoutParams);
                arrow.setVisibility(View.INVISIBLE);
                if(selected == 4) {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.pink));
                    arrow.setVisibility(View.VISIBLE);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.pink));
                } else {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                    arrow.setVisibility(View.INVISIBLE);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
                break;
            }
            case 5: {
                image.setVisibility(View.GONE);
                textView.setText(context.getString(R.string.report_feature_bug));
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams)textView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                textView.setLayoutParams(layoutParams);
                arrow.setVisibility(View.INVISIBLE);
                if(selected == 5) {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.pink));
                    arrow.setVisibility(View.VISIBLE);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.pink));
                } else {
                    image.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                    arrow.setVisibility(View.INVISIBLE);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
                break;
            }
        }
        return convertView;
    }
}
