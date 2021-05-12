package com.aman;

import java.util.HashMap;
import com.google.gson.Gson;

/* View 'servlet' class */
public class AnswerUserView {
	private Gson gson = new Gson();

	public String renderMultipleCourses(HashMap<Integer, Course> answer) {
		return gson.toJson(answer);
	}

	public String renderMultipleStudents(HashMap<Integer, Student> answer) {
		return gson.toJson(answer);
	}

	public String renderSingle(Object oneStudent) {
		return gson.toJson(oneStudent);
	}

	public String renderError(int statusCode, String error) {
		HashMap<Integer, String> errorOutput = new HashMap<>();
		errorOutput.put(statusCode, error);
		return gson.toJson(errorOutput);
	}
}
