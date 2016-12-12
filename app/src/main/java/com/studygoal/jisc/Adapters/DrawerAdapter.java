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

    public DrawerAdapter(Context con) {
        context = con;
        inflater = LayoutInflater.from(con);
        values = new String[] {"0", con.getString(R.string.feed), con.getString(R.string.stats), con.getString(R.string.log), con.getString(R.string.target), con.getString(R.string.logout)};
    }

    //Numarul de rows
    public int getCount() {
        return values.length;
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
            convertView = inflater.inflate(R.layout.nav_item, parent, false);
            TextView textView;
            ImageView imageView;
            switch (position) {
                case 1: {
                    textView = (TextView) convertView.findViewById(R.id.drawer_item_text);
                    textView.setTypeface(DataManager.getInstance().myriadpro_regular);
                    textView.setText(context.getString(R.string.feed));
                    imageView = (ImageView) convertView.findViewById(R.id.drawer_item_icon);
                    Glide.with(context).load(R.drawable.feed_icon).into(imageView);
                    break;
                }
                case 2: {
                    textView = (TextView) convertView.findViewById(R.id.drawer_item_text);
                    textView.setTypeface(DataManager.getInstance().myriadpro_regular);
                    textView.setText(context.getString(R.string.stats));
                    imageView = (ImageView) convertView.findViewById(R.id.drawer_item_icon);
                    Glide.with(context).load(R.drawable.stats_icon).into(imageView);
                    break;
                }
                case 3: {
                    textView = (TextView) convertView.findViewById(R.id.drawer_item_text);
                    textView.setTypeface(DataManager.getInstance().myriadpro_regular);
                    textView.setText(context.getString(R.string.log));
                    imageView = (ImageView) convertView.findViewById(R.id.drawer_item_icon);
                    Glide.with(context).load(R.drawable.log_icon).into(imageView);
                    break;
                }
                case 4: {
                    textView = (TextView) convertView.findViewById(R.id.drawer_item_text);
                    textView.setTypeface(DataManager.getInstance().myriadpro_regular);
                    textView.setText(context.getString(R.string.target));
                    imageView = (ImageView) convertView.findViewById(R.id.drawer_item_icon);
                    Glide.with(context).load(R.drawable.target_icon).into(imageView);
                    break;
                }
                default: {
                    textView = (TextView) convertView.findViewById(R.id.drawer_item_text);
                    textView.setTypeface(DataManager.getInstance().myriadpro_regular);
                    textView.setText(context.getString(R.string.logout));
                    imageView = (ImageView) convertView.findViewById(R.id.drawer_item_icon);
                    Glide.with(context).load(R.drawable.logout_icon).into(imageView);
                    break;
                }
            }


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