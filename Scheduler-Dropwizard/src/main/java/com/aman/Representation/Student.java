package com.aman.Representation;

import com.fasterxml.jackson.annotation.JsonProperty;

/* POJO representing a Student */
public class Student {

    private int id;
    private String first_name;
    private String last_name;
    private String date_of_birth;
    private String address;
    private String email;
    private int number_of_courses;

    public Student() {
    }

    public Student(int id, String first_name, String last_name, String date_of_birth, String address, String email,
                   int number_of_courses) {
        super();
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.date_of_birth = date_of_birth;
        this.address = address;
        this.email = email;
        this.number_of_courses = number_of_courses;
    }

    @JsonProperty
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty
    public String getFirst_name() {
        return first_name;
    }

    @JsonProperty
    public String getLast_name() {
        return last_name;
    }

    @JsonProperty
    public String getDate_of_birth() {
        return date_of_birth;
    }

    @JsonProperty
    public String getAddress() {
        return address;
    }

    @JsonProperty
    public int getNumber_of_courses() {
        return number_of_courses;
    }

    public void setNumber_of_courses(int number_of_courses) {
        this.number_of_courses = number_of_courses;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }
}
