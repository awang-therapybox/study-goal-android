package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Bogdan on 9/22/2016.
 *
 *
 */
@Table(name = "Courses")
public class Courses extends Model{
    public Courses() {super();}

    @Column(name = "course_id")
    public String id;
    @Column(name = "course_name")
    public String name;

}
