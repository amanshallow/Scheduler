package com.aman;

import java.util.HashMap;
import static spark.Spark.*;

/* Enrolled Courses Model*/
public class EnrolledController {
	private static final EnrolledModel ENROLLED = new EnrolledModel();
	private static final AnswerUserView ANSWER = new AnswerUserView();
	private static final int UNPROCESSABLE_ENTITY = 422;
	private static final int NOT_FOUND = 404;
	private static final int BAD_REQUEST = 400;

	public static void main(String[] args) {
		port(8080);
		/* CREATE */
		post("/Scheduler/student/", (request, response) -> {
			String path = request.pathInfo();
			if (path != null) {
				String studentId = request.queryParams("studentid");
				String courseId = request.queryParams("courseid");

				if (studentId == null || courseId == null)
					return ANSWER.renderError(UNPROCESSABLE_ENTITY, "Missing required parameter(s)");
				else {
					if (studentId.matches("\\d+") && courseId.matches("\\d{3}")) {
						EnrolledCourses enrolling = ENROLLED.setOneEnrolledCourse(Integer.parseInt(studentId),
								Integer.parseInt(courseId));
						if (enrolling != null && !enrolling.isEmpty())
							return ANSWER.renderSingle(enrolling);
						else
							return ANSWER.renderError(BAD_REQUEST, "Enrollment failed or already exists.");
					} else
						return ANSWER.renderError(UNPROCESSABLE_ENTITY, "ID's must be integers.");
				}
			} else
				return ANSWER.renderError(NOT_FOUND, "Path not found");
		});

		/* READ */
		get("/Scheduler/student/:id/courses", (request, response) -> {
			String path = request.pathInfo();
			HashMap<Integer, Course> allEnrolledCourses;
			if (path == null)
				return ANSWER.renderError(NOT_FOUND, "Path not found");
			else {
				if (request.params(":id").matches("\\d+")
						&& (allEnrolledCourses = ENROLLED
								.getAllEnrolledCourses(Integer.parseInt(request.params(":id")))) != null
						&& !allEnrolledCourses.isEmpty())
					return ANSWER.renderSingle(allEnrolledCourses);
				else
					return ANSWER.renderError(NOT_FOUND, "Item not found");
			}
		});

		/* DELETE */
		delete("/Scheduler/student/:student/:course", (request, response) -> {
			String path = request.pathInfo();
			String studentID = request.params(":student");
			String courseID = request.params(":course");
			if (path == null)
				return ANSWER.renderError(NOT_FOUND, "Path not found");
			else {
				if (studentID.matches("\\d+") && courseID.matches("\\d{3}")) {
					EnrolledCourses enrolling = ENROLLED.deleteOneEnrolledCourse(Integer.parseInt(studentID),
							Integer.parseInt(courseID));
					if (enrolling != null && !enrolling.isEmpty())
						return ANSWER.renderSingle(enrolling);
					else
						return ANSWER.renderError(BAD_REQUEST, "Enrollment failed to delete.");
				} else
					return ANSWER.renderError(UNPROCESSABLE_ENTITY, "ID's must be integers.");
			}
		});
	}
}
