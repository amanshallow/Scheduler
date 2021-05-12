package com.aman;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class EnrolledModel {
	private final String url;
	private final String user;
	private final String password;

	public EnrolledModel() {
		url = "jdbc:mysql://localhost:3306/scheduler";
		user = "username";
		password = "secretpassword";
	}

	/*
	 * Query MySQL database and retrieve all courses student is enrolled in.
	 */
	public HashMap<Integer, Course> getAllEnrolledCourses(int studentId) {
		HashMap<Integer, Course> allEnrolledCourses = new HashMap<Integer, Course>();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found! [getAllEnrolledCourses]");
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				ResultSet resultSet = sqlStatement
						.executeQuery("SELECT course_ID, courseName, courseDepartment, creditHours"
								+ " FROM EnrolledCourses INNER JOIN Courses ON EnrolledCourses.course_ID ="
								+ " Courses.courseID WHERE student_ID =" + studentId);
				while (resultSet.next()) {
					int id = resultSet.getInt("course_ID");
					int creditHours = resultSet.getInt("creditHours");
					String courseDepartment = resultSet.getString("courseDepartment");
					String courseName = resultSet.getString("courseName");
					allEnrolledCourses.put(id, new Course(id, courseName, courseDepartment, creditHours));
				}
				connection.close();
				sqlStatement.close();
				resultSet.close();
				return allEnrolledCourses;
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [getAllEnrolledCourses]");
		}
		return null;
	}

	/*
	 * Create a new enrolled course in the database.
	 */
	public EnrolledCourses setOneEnrolledCourse(int studentId, int courseId) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found! [setOneEnrolledCourse]");
			return null;
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				ResultSet result;
				EnrolledCourses enrolled = null;
				Statement sqlStatement = connection.createStatement();
				if (!sqlStatement.executeQuery(
						"SELECT * FROM EnrolledCourses WHERE student_ID=" + studentId + " AND course_ID=" + courseId)
						.next()
						&& sqlStatement.executeQuery("SELECT courseID from Courses WHERE courseID=" + courseId)
								.next()) {
					boolean insert = sqlStatement
							.execute("INSERT INTO EnrolledCourses " + "VALUES (" + studentId + ", " + courseId + ")");
					int update = sqlStatement
							.executeUpdate("UPDATE Students set numberOfCourses = ((SELECT numberOfCourses "
									+ "FROM (SELECT MAX(numberOfCourses) FROM Students) AS totalCourses) + 1) WHERE studentID="
									+ studentId);
					if (!insert && update == 1) {
						result = sqlStatement.executeQuery("SELECT * FROM EnrolledCourses WHERE student_ID=" + studentId
								+ " AND course_ID=" + courseId);
						if (result.next())
							enrolled = new EnrolledCourses(result.getInt("student_ID"), result.getInt("course_ID"));
						result.close();
					}
					connection.close();
					sqlStatement.close();
					return enrolled;
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [setOneEnrolledCourse]");
		}
		return null;
	}

	/*
	 * Remove an enrolled course from the database by ID
	 */
	public EnrolledCourses deleteOneEnrolledCourse(int studentId, int courseId) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found! [deleteOneEnrolledCourse]");
			return null;
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				EnrolledCourses enrolled = null;

				ResultSet result = sqlStatement.executeQuery(
						"SELECT * FROM EnrolledCourses WHERE student_ID=" + studentId + " AND course_ID=" + courseId);
				if (result.next())
					enrolled = new EnrolledCourses(result.getInt("student_ID"), result.getInt("course_ID"));

				boolean delete = sqlStatement.execute(
						"DELETE FROM EnrolledCourses WHERE student_ID=" + studentId + " AND course_ID=" + courseId);
				int update = sqlStatement.executeUpdate("UPDATE Students "
						+ "SET numberOfCourses = IF(Students.numberOfCourses > 0, numberOfCourses - 1, 0) "
						+ "WHERE studentID=" + studentId);
				connection.close();
				sqlStatement.close();
				result.close();
				return (!delete && update == 1) ? enrolled : null;
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [deleteOneEnrolledCourse]");
		}
		return null;
	}
}
