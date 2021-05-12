package com.aman.Representation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Enrolled {
    private int student_id;
    private int course_id;

    public Enrolled() {
    }

    public Enrolled(int student_id, int course_id) {
        super();
        this.student_id = student_id;
        this.course_id = course_id;
    }

    @JsonProperty
    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    @JsonProperty
    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }
}
