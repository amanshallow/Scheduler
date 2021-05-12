package com.aman;


public class Course {
	private int courseID;
	private int creditHours;
	private String courseDepartment;
	private String courseName;

	public Course(int courseID, String courseName, String courseDepartment, int creditHours) {
		this.courseID = courseID;
		this.creditHours = creditHours;
		this.courseDepartment = courseDepartment;
		this.courseName = courseName;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	public int getCreditHours() {
		return creditHours;
	}

	public void setCreditHours(int creditHours) {
		this.creditHours = creditHours;
	}

	public String getCourseDepartment() {
		return courseDepartment;
	}

	public void setCourseDepartment(String courseDepartment) {
		this.courseDepartment = courseDepartment;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public boolean isEmpty() {
		if (getCourseID() == 0)
			return true;
		return false;
	}
}
