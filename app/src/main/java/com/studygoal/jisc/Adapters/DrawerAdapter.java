package com.studygoal.jisc.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.CircleTransform;

public class DrawerAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    public String[] values;
    Context context;

    public TextView selected_text;
    public ImageView selected_image;

    public ImageView profile_pic;
    public boolean statsOpened;

    public DrawerAdapter(Context con) {
        context = con;
        inflater = LayoutInflater.from(con);
        statsOpened = false;

        if(DataManager.getInstance().user.isSocial) {
            values = new String[] {"0", con.getString(R.string.feed), con.getString(R.string.log), con.getString(R.string.target), con.getString(R.string.logout)};
        } else {
            values = new String[] {"0",
                    con.getString(R.string.feed),
                    con.getString(R.string.stats),
                    con.getString(R.string.graphs),
                    con.getString(R.string.attainment),
                    con.getString(R.string.points),
                    con.getString(R.string.log),
                    con.getString(R.string.target),
                    con.getString(R.string.logout)};
        }
    }

    //Numarul de rows
    public int getCount() {
        return statsOpened?values.length:values.length-3;
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
            convertView = inflater.inflate(R.layout.nav_header_main, parent, false);
            Glide.with(context).load(R.drawable.tmp_navheader).into((ImageView)convertView.findViewById(R.id.navheader));
            ((ImageView) convertView.findViewById(R.id.navheader)).setColorFilter(0x99ae65d0);

            TextView email = (TextView) convertView.findViewById(R.id.drawer_email);
            TextView studentId = (TextView) convertView.findViewById(R.id.drawer_studentId);
            studentId.setTypeface(DataManager.getInstance().myriadpro_regular);
            studentId.setText(context.getString(R.string.student_id) + " : " + DataManager.getInstance().user.jisc_student_id);
            email.setTypeface(DataManager.getInstance().myriadpro_regular);
            email.setText(DataManager.getInstance().user.email);
            TextView name = (TextView) convertView.findViewById(R.id.drawer_name);
            name.setTypeface(DataManager.getInstance().myriadpro_regular);
            profile_pic = (ImageView) convertView.findViewById(R.id.imageView);
            name.setText(DataManager.getInstance().user.name);
            if(DataManager.getInstance().user.profile_pic.equals(""))
                Glide.with(context).load(R.drawable.profilenotfound2).transform(new CircleTransform(context)).into((ImageView) convertView.findViewById(R.id.imageView));
            else
                Glide.with(context).load(NetworkManager.getInstance().host + DataManager.getInstance().user.profile_pic).transform(new CircleTransform(context)).into((ImageView) convertView.findViewById(R.id.imageView));
        } else {
            if(statsOpened && position > 2 && position < 6) {
                convertView = inflater.inflate(R.layout.nav_item_sub, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.nav_item, parent, false);
            }

            TextView textView;
            ImageView imageView;
            textView = (TextView) convertView.findViewById(R.id.drawer_item_text);
            textView.setTypeface(DataManager.getInstance().myriadpro_regular);
            imageView = (ImageView) convertView.findViewById(R.id.drawer_item_icon);
            imageView.setImageBitmap(null);

            
            ImageView arrow_button = (ImageView) convertView.findViewById(R.id.arrow_button);
            arrow_button.setVisibility(View.GONE);

            if(!statsOpened && position > 2) {
                position += 3;
            }

            textView.setText(values[position]);

            int iconResID = -1;

            if(values[position].equals(context.getString(R.string.feed))) {
                iconResID = R.drawable.feed_icon;
            }
            if(values[position].equals(context.getString(R.string.check_in))) {
                iconResID = R.drawable.checkin;
            }
            if(values[position].equals(context.getString(R.string.stats))) {
                iconResID = R.drawable.stats_icon;
            }
            if(values[position].equals(context.getString(R.string.log))) {
                iconResID = R.drawable.log_icon;
            }
            if(values[position].equals(context.getString(R.string.target))) {
                iconResID = R.drawable.target_icon;
            }
            if(values[position].equals(context.getString(R.string.logout))) {
                iconResID = R.drawable.logout_icon;
            }

            if(values[position].equals(context.getString(R.string.stats))) {
                arrow_button.setVisibility(View.VISIBLE);
                if(statsOpened) {
                    arrow_button.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.arrow_button_up));
                } else {
                    arrow_button.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.arrow_button));
                }
            }

            if(iconResID != -1)
                Glide.with(context).load(iconResID).into(imageView);

            if (DataManager.getInstance().fragment != null) {
                if (DataManager.getInstance().fragment == position) {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.default_blue));
                    imageView.setColorFilter(ContextCompat.getColor(context, R.color.default_blue));
                    selected_image = imageView;
                    selected_text = textView;
                    DataManager.getInstance().fragment = null;
                }
            } else {
                String selected_value = "";
                switch (DataManager.getInstance().home_screen.toLowerCase()) {
                    case "feed": {
                        selected_value = context.getString(R.string.feed);
                        break;
                    }
                    case "stats": {
                        selected_value = context.getString(R.string.stats);
                        break;
                    }
                    case "log": {
                        selected_value = context.getString(R.string.log);
                        break;
                    }
                    case "target": {
                        selected_value = context.getString(R.string.target);
                        break;
                    }
                }
                if (textView.getText().toString().toLowerCase().equals(selected_value.toLowerCase())) {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.default_blue));
                    imageView.setColorFilter(ContextCompat.getColor(context, R.color.default_blue));
                    selected_image = imageView;
                    selected_text = textView;
                }
            }
        }
        return convertView;
    }
}