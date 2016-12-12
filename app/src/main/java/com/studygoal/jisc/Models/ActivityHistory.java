package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "ActivityHistory")
public class ActivityHistory extends Model {

    @Column(name = "log_id")
    public String id;
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
    @Column(name = "note")
    public String note;
    @Column(name = "created_date")
    public String created_date;
    @Column(name = "modified_date")
    public String modified_date;

    public ActivityHistory() {super();}
}
