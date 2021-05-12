package com.aman;

public class EnrolledCourses {
	private int studentId;
	private int courseId;

	public EnrolledCourses(int studentId, int courseId) {
		super();
		this.studentId = studentId;
		this.courseId = courseId;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public boolean isEmpty() {
		if (getCourseId() == 0)
			return true;
		return false;
	}
}
