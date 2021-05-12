package com.aman;

import java.util.HashMap;
import static spark.Spark.*;

/* Controller for all students */
public class StudentControllerAll {
	private static final StudentsModel EVERYONE = new StudentsModel();
	private static final AnswerUserView ANSWER = new AnswerUserView();
	private static final int NOT_FOUND = 404;

	public static void main(String[] args) {
		port(8080);
		get("/Scheduler/student/all", (request, response) -> {
			HashMap<Integer, Student> allStudents;
			if ((allStudents = EVERYONE.getAllStudents()) != null)
				return ANSWER.renderSingle(allStudents);
			else
				return ANSWER.renderError(NOT_FOUND, "Students not found");
		});
	}
}
