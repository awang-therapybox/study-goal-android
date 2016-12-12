package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Module")
public class Module extends Model {


    @Column(name = "module_id")
    public String id;
    @Column(name = "module_name")
    public String name;

    public Module() {
        super();
        id = "";
        name = "";
    }
}
