package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Mark")
public class Mark extends Model {
    public Mark() {super();}

    @Column(name = "student_id")
    public String id;
    @Column(name = "assignment")
    public String assigment;
    @Column(name = "module_instance")
    public String module_instance;
    @Column(name = "module")
    public String module;
    @Column(name = "mark")
    public String mark;

}
