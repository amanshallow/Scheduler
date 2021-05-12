package com.aman;

import java.util.HashMap;
import static spark.Spark.*;

public class CourseControllerAll {
	private static final CoursesModel EVERY_COURSE = new CoursesModel();
	private static final AnswerUserView ANSWER = new AnswerUserView();
	private static final int NOT_FOUND = 404;

	public static void main(String[] args) {
		port(8080);
		get("/Scheduler/course/all", (request, response) -> {
			HashMap<Integer, Course> allCourses;
			if ((allCourses = EVERY_COURSE.getAllCourses()) != null)
				return ANSWER.renderMultipleCourses(allCourses);
			else
				return ANSWER.renderError(NOT_FOUND, "Courses not found");
		});
	}
}
