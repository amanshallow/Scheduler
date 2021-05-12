package com.aman.Representation;

import com.fasterxml.jackson.annotation.JsonProperty;

/* POJO representing a Course */
public class Course {
    private int course_id;
    private int credit_hours;
    private String department;
    private String course_name;

    public Course() {
    }

    public Course(int course_id, String course_name, String department, int credit_hours) {
        this.course_id = course_id;
        this.credit_hours = credit_hours;
        this.department = department;
        this.course_name = course_name;
    }

    @JsonProperty
    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    @JsonProperty
    public int getCredit_hours() {
        return credit_hours;
    }

    @JsonProperty
    public String getDepartment() {
        return department;
    }

    @JsonProperty
    public String getCourse_name() {
        return course_name;
    }
}
