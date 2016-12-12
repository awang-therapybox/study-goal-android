package com.studygoal.jisc;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Utils.CircleTransform;

import junit.framework.Assert;

/**
 * Created by MarcelC on 1/14/16.
 *
 *
 */
public class TrophyDetails extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (DataManager.getInstance().isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.trophy_details);
        DataManager.getInstance().currActivity = this;
        Bundle bundle = getIntent().getExtras();

        String days = " " + getString(R.string.in) + " " + bundle.getString("days") + " " + getString(R.string.days);

        String type = bundle.getString("type");
        String statement = bundle.getString("statement");

        TextView title_view = (TextView) findViewById(R.id.main_screen_title);
        title_view.setTypeface(DataManager.getInstance().myriadpro_regular);
        title_view.setText(bundle.getString("title"));

        TextView trophy_details_text = (TextView) findViewById(R.id.trophy_details_text);
        trophy_details_text.setTypeface(DataManager.getInstance().myriadpro_regular);
        trophy_details_text.setText(statement);

        TextView trophy_details_type = (TextView) findViewById(R.id.trophy_details_type);
        trophy_details_type.setTypeface(DataManager.getInstance().myriadpro_regular);

        if (bundle.getString("type").contains("Silver")){
            trophy_details_type.setText(getString(R.string.silver));
        }else{
            trophy_details_type.setText(getString(R.string.gold));
        }

        ImageView image = (ImageView) findViewById(R.id.trophy_details_image);

        String imageName = bundle.getString("image");

        Assert.assertNotNull(this);
        Assert.assertNotNull(imageName);

        Glide.with(this)
                .load(this.getResources().getIdentifier(imageName, "drawable", this.getPackageName()))
                .transform(new CircleTransform(this))
                .into(image);

        RelativeLayout close_button = (RelativeLayout) findViewById(R.id.close_button);
        close_button.setOnClickListener(this);

        assert type != null;
        if (type.equals("Gold")) {
            findViewById(R.id.circle).setBackground(ContextCompat.getDrawable(this, R.drawable.circle_gold));
            trophy_details_type.setTextColor(Color.parseColor("#f19001"));
        }

    }

    @Override
    public void onClick(View v) {
        this.finish();
    }
}