package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Institutions")
public class Institution extends Model {

    @Column(name = "name")
    public String name;
    @Column(name = "url")
    public String url;
    @Column(name = "ukprn")
    public Integer ukprn;

    public Institution() {
        super();
    }
}
