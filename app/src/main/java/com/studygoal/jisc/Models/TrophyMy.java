package com.studygoal.jisc.Models;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import junit.framework.Assert;

/**
 * Created by MarcelC on 1/14/16.
 */

@Table(name = "TrophyMy")
public class TrophyMy extends Model {
    public TrophyMy() {super();}

    @Column(name = "trophy_id")
    public String trophy_id;
    @Column(name = "trophy_name")
    public String trophy_name;
    @Column(name = "trophy_type")
    public String trophy_type;
    @Column(name = "activity_name")
    public String activity_name;
    @Column(name = "count")
    public String count;
    @Column(name = "days")
    public String days;
    @Column(name = "total")
    public String total;

    public String getImageName()
    {
        if (this.trophy_id.equals("1")) {
            return "library_hawk_silver_big";
        } else
        if (this.trophy_id.equals("2")) {
            return "library_hawk_gold_big";
        } else
        if (this.trophy_id.equals("3")) {
            return "tackle_silver_big";
        } else
        if (this.trophy_id.equals("4")) {
            return "tackle_gold_big";
        } else
        if (this.trophy_id.equals("5")) {
            return "hard_tackle_silver_big";
        } else
        if (this.trophy_id.equals("6")) {
            return "hard_tackle_gold_big";
        } else
        if (this.trophy_id.equals("7")) {
            return "bonus_miles_silver_big";
        } else
        if (this.trophy_id.equals("8")) {
            return "bonus_miles_gold_big";
        } else
        if (this.trophy_id.equals("9")) {
            return "even_madder_scientist_silver_big";
        } else
        if (this.trophy_id.equals("10")) {
            return "even_madder_scientist_gold_big";
        } else
        if (this.trophy_id.equals("11")) {
            return "homework_hound_silver_big";
        } else
        if (this.trophy_id.equals("12")) {
            return "homework_hound_gold_big";
        } else
        if (this.trophy_id.equals("13")) {
            return "top_dog_silver_big";
        } else
        if (this.trophy_id.equals("14")) {
            return "top_dog_gold_big";
        } else
        if (this.trophy_id.equals("15")) {
            return "hercule_silver_big";
        } else
        if (this.trophy_id.equals("16")) {
            return "hercule_gold_big";
        } else
        if (this.trophy_id.equals("17")) {
            return "sherlock_silver_big";
        } else
        if (this.trophy_id.equals("18")) {
            return "sherlock_gold_big";
        } else
        if (this.trophy_id.equals("19")) {
            return "exam_veteran_silver_big";
        } else
        if (this.trophy_id.equals("20")) {
            return "exam_veteran_gold_big";
        } else
        if (this.trophy_id.equals("21")) {
            return "extrovert_silver_big";
        } else
        if (this.trophy_id.equals("22")) {
            return "extrovert_gold_big";
        } else
        if (this.trophy_id.equals("23")) {
            return "team_player_silver_big";
        } else
        if (this.trophy_id.equals("24")) {
            return "team_player_gold_big";
        } else
        if (this.trophy_id.equals("25")) {
            return "cramped_hand_silver_big";
        } else
        if (this.trophy_id.equals("26")) {
            return "cramped_hand_gold_big";
        } else
        if (this.trophy_id.equals("27")) {
            return "carpal_tunnel_silver_big";
        } else
        if (this.trophy_id.equals("28")) {
            return "carpal_tunnel_gold_big";
        } else
        if (this.trophy_id.equals("29")) {
            return "t_square_silver_big";
        } else
        if (this.trophy_id.equals("30")) {
            return "t_square_gold_big";
        } else
        if (this.trophy_id.equals("31")) {
            return "artists_desk_silver_big";
        } else
        if (this.trophy_id.equals("32")) {
            return "artists_desk_gold_big";
        } else
        if (this.trophy_id.equals("33")) {
            return "loudspeaker_silver_big";
        } else
        if (this.trophy_id.equals("34")) {
            return "loudspeaker_gold_big";
        } else
        if (this.trophy_id.equals("35")) {
            return "fog_horn_silver_big";
        } else
        if (this.trophy_id.equals("36")) {
            return "fog_horn_gold_big";
        } else
        if (this.trophy_id.equals("37")) {
            return "deja_vu_silver_big";
        } else
        if (this.trophy_id.equals("38")) {
            return "deja_vu_gold_big";
        } else
        if (this.trophy_id.equals("39")) {
            return "deja_vu_again_silver_big";
        } else
        if (this.trophy_id.equals("40")) {
            return "deja_vu_again_gold_big";
        } else
        if (this.trophy_id.equals("41")) {
            return "roving_reporter_silver_big";
        } else
        if (this.trophy_id.equals("42")) {
            return "roving_reporter_gold_big";
        } else
        if (this.trophy_id.equals("43")) {
            return "media_mogul_silver_big";
        } else
        if (this.trophy_id.equals("44")) {
            return "media_mogul_gold_big";
        } else
        if (this.trophy_id.equals("45")) {
            return "poster_boy_silver_big";
        } else
        if (this.trophy_id.equals("46")) {
            return "poster_boy_gold_big";
        } else
        if (this.trophy_id.equals("47")) {
            return "pusher_silver_big";
        } else
        if (this.trophy_id.equals("48")) {
            return "pusher_gold_big";
        } else
        if (this.trophy_id.equals("49")) {
            return "reacher_silver_big";
        } else
        if (this.trophy_id.equals("50")) {
            return "reacher_gold_big";
        } else
        if (this.trophy_id.equals("51")) {
            return "frequent_flyer_silver_big";
        } else
        if (this.trophy_id.equals("52")) {
            return "frequent_flyer_gold_big";
        } else
        if (this.trophy_id.equals("53")) {
            return "bibliophile_silver_big";
        } else
        if (this.trophy_id.equals("54")) {
            return "bibliophile_gold_big";
        } else
        if (this.trophy_id.equals("55")) {
            return "bookworm_silver_big";
        } else
        if (this.trophy_id.equals("56")) {
            return "bookworm_gold_big";
        } else
        if (this.trophy_id.equals("57")) {
            return "desk_jockey_silver_big";
        } else
        if (this.trophy_id.equals("58")) {
            return "desk_jockey_gold_big";
        } else
        if (this.trophy_id.equals("59")) {
            return "key_puncher_silver_big";
        } else
        if (this.trophy_id.equals("60")) {
            return "key_puncher_gold_big";
        } else
        if (this.trophy_id.equals("61")) {
            return "campus_captain_silver_big";
        } else
        if (this.trophy_id.equals("62")) {
            return "campus_captain_gold_big";
        }

        return "";
    }

    public int getImageDrawable(Context context) {

        String imageName = this.getImageName();

        Assert.assertNotNull(context);
        Assert.assertNotNull(imageName);

        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }


}
