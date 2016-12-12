package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Targets")
public class Targets extends Model {

    public Targets() {super();}

    @Column(name = "target_id")
    public String target_id;
    @Column(name = "student_id")
    public String student_id;
    @Column(name = "activity_type")
    public String activity_type;
    @Column(name = "activity")
    public String activity;
    @Column(name = "total_time")
    public String total_time;
    @Column(name = "time_span")
    public String time_span;
    @Column(name = "module_id")
    public String module_id;
    @Column(name = "because")
    public String because;
    @Column(name = "status")
    public String status;
    @Column(name = "created_date")
    public String created_date;
    @Column(name = "modified_date")
    public String modified_date;


}

