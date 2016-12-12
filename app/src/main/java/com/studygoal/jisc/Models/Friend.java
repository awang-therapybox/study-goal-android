package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "FriendTabel")
public class Friend extends Model {
    @Column(name = "friend_id")
    public String id;
    @Column(name = "jisc_student_id")
    public String jisc_student_id;
    @Column(name = "pid")
    public String pid;
    @Column(name = "name")
    public String name;
    @Column(name = "email")
    public String email;
    @Column(name = "eppn")
    public String eppn;
    @Column(name = "affiliation")
    public String affiliation;
    @Column(name = "profilepic")
    public String profile_pic;
    @Column(name = "modules")
    public String modules;
    @Column(name = "created_date")
    public String created_date;
    @Column(name = "modified_date")
    public String modified_date;
    @Column(name = "hidden")
    public boolean hidden;

    public Friend(){
        super();

        id="";
        jisc_student_id="";
        pid="";
        name="";
        email="";
        eppn="";
        affiliation="";
        profile_pic="";
        modules="";
        created_date="";
        modified_date="";
        hidden=true;
    }
}
