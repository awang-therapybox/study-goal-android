package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Activity")
public class Activity extends Model {

    @Column(name = "student_id")
    public String student_id;
    @Column(name = "module_id")
    public String module_id;
    @Column(name = "activity_type")
    public String activity_type;
    @Column(name = "activity")
    public String activity;
    @Column(name = "activity_date")
    public String activity_date;
    @Column(name = "time_spent")
    public String time_spent;

    public Activity() {super();}
}

