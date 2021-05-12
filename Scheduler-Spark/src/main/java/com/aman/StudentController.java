package com.aman;

import java.util.HashMap;
import static spark.Spark.*;

/* Student Controller */
public class StudentController {
	private static final StudentsModel EVERYONE = new StudentsModel();
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
				String firstName = request.queryParams("first");
				String lastName = request.queryParams("last");
				String dob = request.queryParams("dob");
				String address = request.queryParams("address");
				String email = request.queryParams("email");

				if (firstName == null || lastName == null || dob == null || address == null || email == null)
					return ANSWER.renderError(UNPROCESSABLE_ENTITY, "Missing required parameter(s)");
				else {
					if (firstName.matches("[a-zA-Z]+") && lastName.matches("[a-zA-Z]+")) {
						if (dob.matches("\\d{2}-\\d{2}-\\d{4}")) {
							if (email.matches("\\w+@oswego.edu$")) {
								Student aStudent = EVERYONE
										.setOneStudent(new Student(0, firstName, lastName, dob, address, email, 0));
								if (aStudent != null && !aStudent.isEmpty())
									return ANSWER.renderSingle(aStudent);
								else
									return ANSWER.renderError(BAD_REQUEST,
											"Student creation failed or already exists.");
							} else
								return ANSWER.renderError(UNPROCESSABLE_ENTITY,
										"Only @oswego.edu email domain allowed.");
						} else
							return ANSWER.renderError(UNPROCESSABLE_ENTITY,
									"Incorrect date format, must be: mm-dd-yyyy");
					} else
						return ANSWER.renderError(UNPROCESSABLE_ENTITY, "Name fields can only contain characters.");
				}
			} else
				return ANSWER.renderError(NOT_FOUND, "Path not found");
		});

		/* READ */
		get("/Scheduler/student/:parameter", (request, response) -> {
			HashMap<Integer, Student> byStudentsLastName;
			String path = request.pathInfo();
			String parameter = request.params(":parameter");
			Student aStudent;

			if (path == null)
				return ANSWER.renderError(NOT_FOUND, "Path not found");
			else {
				if (parameter.matches("\\d+")
						&& (aStudent = EVERYONE.getOneStudentById(Integer.parseInt(parameter))) != null
						&& !aStudent.isEmpty())
					return ANSWER.renderSingle(aStudent);
				else if (parameter.matches("[a-zA-Z]+")
						&& (byStudentsLastName = EVERYONE.getStudentsByLastName(parameter)) != null
						&& !byStudentsLastName.isEmpty())
					return ANSWER.renderMultipleStudents(byStudentsLastName);
				else
					return ANSWER.renderError(NOT_FOUND, "Student not found.");
			}
		});
		
		/* UPDATE */
		put("/Scheduler/student/", (request, response) -> {
			String path = request.pathInfo();
			if (path == null)
				return ANSWER.renderError(NOT_FOUND, "Path not found");
			else {
				String id = request.queryParams("id");
				String firstName = request.queryParams("first");
				String lastName = request.queryParams("last");
				String dob = request.queryParams("dob");
				String address = request.queryParams("address");
				String email = request.queryParams("email");

				if (firstName == null || lastName == null || dob == null || address == null || email == null || id == null)
					return ANSWER.renderError(UNPROCESSABLE_ENTITY, "Missing required parameter(s)");
				else {
					if (firstName.matches("[a-zA-Z]+") && lastName.matches("[a-zA-Z]+")) {
						if (dob.matches("\\d{2}-\\d{2}-\\d{4}")) {
							if (id.matches("\\d+")) {
								if (email.matches("\\w+@oswego.edu$")) {
									Student aStudent = EVERYONE.updateOneStudent(
											new Student(Integer.parseInt(id), firstName, lastName, dob, address, email, 0));
									if (aStudent != null && !aStudent.isEmpty())
										return ANSWER.renderSingle(aStudent);
									else
										return ANSWER.renderError(BAD_REQUEST, "Student update failed.");
								} else
									return ANSWER.renderError(UNPROCESSABLE_ENTITY, "Only @oswego.edu email domain allowed.");
							} else
								return ANSWER.renderError(UNPROCESSABLE_ENTITY, "Student ID must be integers.");
						} else
							return ANSWER.renderError(UNPROCESSABLE_ENTITY, "Incorrect date format, must be: mm-dd-yyyy");
					} else
						return ANSWER.renderError(UNPROCESSABLE_ENTITY, "Name fields can only contain characters.");
				}
			}
		});
		
		/* DELETE */
		delete("/Scheduler/student/:id", (request, response) -> {
			String path = request.pathInfo();
			if (path == null)
				return ANSWER.renderError(NOT_FOUND, "Path not found");
			else {
				if (request.params(":id").matches("\\d+")) {
					Student aStudent = EVERYONE.deleteOneStudentById(Integer.parseInt(request.params(":id")));
					if (aStudent != null && !aStudent.isEmpty())
						return ANSWER.renderSingle(aStudent);
					else
						return ANSWER.renderError(BAD_REQUEST, "Student deletion failed.");
				} else
					return ANSWER.renderError(UNPROCESSABLE_ENTITY, "StudentID must be an integer.");
			}
		});
	}
}
