package com.studygoal.jisc.Models;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import junit.framework.Assert;

/**
 * Created by MarcelC on 1/14/16.
 *
 *
 */
@Table(name = "Trophy")
public class Trophy extends Model{

    public Trophy() {super();}

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
    @Column(name = "statement")
    public String statement;

    public String getImageName()
    {
        switch (this.trophy_id) {
            case "1":
                return "library_hawk_silver_big";
            case "2":
                return "library_hawk_gold_big";
            case "3":
                return "tackle_silver_big";
            case "4":
                return "tackle_gold_big";
            case "5":
                return "hard_tackle_silver_big";
            case "6":
                return "hard_tackle_gold_big";
            case "7":
                return "bonus_miles_silver_big";
            case "8":
                return "bonus_miles_gold_big";
            case "9":
                return "even_madder_scientist_silver_big";
            case "10":
                return "even_madder_scientist_gold_big";
            case "11":
                return "homework_hound_silver_big";
            case "12":
                return "homework_hound_gold_big";
            case "13":
                return "top_dog_silver_big";
            case "14":
                return "top_dog_gold_big";
            case "15":
                return "hercule_silver_big";
            case "16":
                return "hercule_gold_big";
            case "17":
                return "sherlock_silver_big";
            case "18":
                return "sherlock_gold_big";
            case "19":
                return "exam_veteran_silver_big";
            case "20":
                return "exam_veteran_gold_big";
            case "21":
                return "extrovert_silver_big";
            case "22":
                return "extrovert_gold_big";
            case "23":
                return "team_player_silver_big";
            case "24":
                return "team_player_gold_big";
            case "25":
                return "cramped_hand_silver_big";
            case "26":
                return "cramped_hand_gold_big";
            case "27":
                return "carpal_tunnel_silver_big";
            case "28":
                return "carpal_tunnel_gold_big";
            case "29":
                return "t_square_silver_big";
            case "30":
                return "t_square_gold_big";
            case "31":
                return "artists_desk_silver_big";
            case "32":
                return "artists_desk_gold_big";
            case "33":
                return "loudspeaker_silver_big";
            case "34":
                return "loudspeaker_gold_big";
            case "35":
                return "fog_horn_silver_big";
            case "36":
                return "fog_horn_gold_big";
            case "37":
                return "deja_vu_silver_big";
            case "38":
                return "deja_vu_gold_big";
            case "39":
                return "deja_vu_again_silver_big";
            case "40":
                return "deja_vu_again_gold_big";
            case "41":
                return "roving_reporter_silver_big";
            case "42":
                return "roving_reporter_gold_big";
            case "43":
                return "media_mogul_silver_big";
            case "44":
                return "media_mogul_gold_big";
            case "45":
                return "poster_boy_silver_big";
            case "46":
                return "poster_boy_gold_big";
            case "47":
                return "pusher_silver_big";
            case "48":
                return "pusher_gold_big";
            case "49":
                return "reacher_silver_big";
            case "50":
                return "reacher_gold_big";
            case "51":
                return "frequent_flyer_silver_big";
            case "52":
                return "frequent_flyer_gold_big";
            case "53":
                return "bibliophile_silver_big";
            case "54":
                return "bibliophile_gold_big";
            case "55":
                return "bookworm_silver_big";
            case "56":
                return "bookworm_gold_big";
            case "57":
                return "desk_jockey_silver_big";
            case "58":
                return "desk_jockey_gold_big";
            case "59":
                return "key_puncher_silver_big";
            case "60":
                return "key_puncher_gold_big";
            case "61":
                return "campus_captain_silver_big";
            case "62":
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
