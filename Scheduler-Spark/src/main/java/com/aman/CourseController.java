package com.aman;

import static spark.Spark.*;

import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;

/* Course Controller */
public class CourseController {
	private static final CoursesModel EVERY_COURSE = new CoursesModel();
	private static final AnswerUserView ANSWER = new AnswerUserView();
	private static final int UNPROCESSABLE_ENTITY = 422;
	private static final int NOT_FOUND = 404;
	private static final int BAD_REQUEST = 400;

	public static void main(String[] args) {
		port(8080);
		BasicConfigurator.configure();

		/* CREATE */
		post("/Scheduler/course/", (request, response) -> {
			String path = request.pathInfo();
			if (path != null) {
				String courseID = request.queryParams("id");
				String creditHours = request.queryParams("credits");
				String courseDepartment = request.queryParams("department");
				String courseName = request.queryParams("name");

				if (courseID == null || creditHours == null || courseDepartment == null || courseName == null) {
					response.status(422);
					return ANSWER.renderError(UNPROCESSABLE_ENTITY, "Missing required parameter(s)");
				} else {
					if (courseName.matches("[a-zA-Z-]+") && courseDepartment.matches("[a-zA-Z]+")) {
						if (courseID.matches("\\d{3}") && creditHours.matches("\\d")) {
							Course aCourse = EVERY_COURSE.setOneCourse(new Course(Integer.parseInt(courseID),
									courseName, courseDepartment, Integer.parseInt(creditHours)));
							if (aCourse != null && !aCourse.isEmpty())
								return ANSWER.renderSingle(aCourse);
							else {
								response.status(BAD_REQUEST);
								return ANSWER.renderError(BAD_REQUEST, "Course creation failed or already exists.");
							}
						} else {
							response.status(UNPROCESSABLE_ENTITY);
							return ANSWER.renderError(UNPROCESSABLE_ENTITY,
									"Course ID (###) and credit hours (#) must be integers.");
						}
					} else {
						response.status(UNPROCESSABLE_ENTITY);
						return ANSWER.renderError(UNPROCESSABLE_ENTITY, "Name fields can only contain characters.");
					}
				}
			} else {
				response.status(NOT_FOUND);
				return ANSWER.renderError(NOT_FOUND, "Path not found");
			}
		});

		/* READ */
		get("/Scheduler/course/:parameter", (request, response) -> {
			Course aCourse;
			String path = request.pathInfo();
			String parameter = request.params(":parameter");
			HashMap<Integer, Course> courseByDepartment;
			if (path == null)
				return ANSWER.renderError(NOT_FOUND, "Path not found");
			else {
				if (parameter.matches("\\d{3}")
						&& (aCourse = EVERY_COURSE.getOneCourseById(Integer.parseInt(parameter))) != null
						&& !aCourse.isEmpty())
					return ANSWER.renderSingle(aCourse);
				else if (request.params(":parameter").matches("[a-zA-Z]+")
						&& (courseByDepartment = EVERY_COURSE.getCoursesByDepartment(parameter)) != null)
					return ANSWER.renderMultipleCourses(courseByDepartment);
				else
					return ANSWER.renderError(NOT_FOUND, "Item not found");
			}
		});
		/* UPDATE */
		put("/Scheduler/course/", (request, response) -> {
			String path = request.pathInfo();
			if (path == null)
				return ANSWER.renderError(NOT_FOUND, "Path not found");
			else {
				String courseID = request.queryParams("id");
				String creditHours = request.queryParams("credits");
				String courseDepartment = request.queryParams("department");
				String courseName = request.queryParams("name");

				if (courseID == null || creditHours == null || courseDepartment == null || courseName == null)
					return ANSWER.renderError(UNPROCESSABLE_ENTITY, "Missing required parameter(s)");
				else {
					if (courseName.matches("[a-zA-Z-]+") && courseDepartment.matches("[a-zA-Z]+")) {
						if (courseID.matches("\\d{3}") && creditHours.matches("\\d+")) {
							Course aCourse = EVERY_COURSE.updateOneCourse(new Course(Integer.parseInt(courseID),
									courseName, courseDepartment, Integer.parseInt(creditHours)));
							if (aCourse != null && !aCourse.isEmpty())
								return ANSWER.renderSingle(aCourse);
							else
								return ANSWER.renderError(BAD_REQUEST, "Course update failed.");
						} else
							return ANSWER.renderError(NOT_FOUND, "Course ID (###) and credit hours (#) must be integers.");
					} else
						return ANSWER.renderError(UNPROCESSABLE_ENTITY, "Name fields can only contain characters.");
				}
			}
		});

		/* UPDATE */
		delete("/Scheduler/course/:id", (request, response) -> {
			String path = request.pathInfo();
			if (path == null)
				return ANSWER.renderError(NOT_FOUND, "Path not found");
			else {
				if (request.params(":id").matches("\\d+")) {
					Course aCourse = EVERY_COURSE.deleteOneCourseById(Integer.parseInt(request.params(":id")));
					if (aCourse != null && !aCourse.isEmpty())
						return ANSWER.renderSingle(aCourse);
					else
						return ANSWER.renderError(BAD_REQUEST, "Course deletion failed.");
				} else
					return ANSWER.renderError(UNPROCESSABLE_ENTITY, "CourseID must be an integer.");
			}
		});
	}
}
