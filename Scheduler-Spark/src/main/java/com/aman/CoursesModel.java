package com.aman;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

/* Courses Model */
public class CoursesModel {
	private final String url;
	private final String user;
	private final String password;

	public CoursesModel() {
		url = "jdbc:mysql://localhost:3306/scheduler";
		user = "username";
		password = "secretpassword";
	}

	/*
	 * Retrieve all courses from MySQL database.
	 */
	public HashMap<Integer, Course> getAllCourses() {
		HashMap<Integer, Course> allCourses = new HashMap<Integer, Course>();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found! [getAllCourses]");
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				ResultSet resultSet = sqlStatement.executeQuery("SELECT * FROM Courses");
				while (resultSet.next()) {
					int id = resultSet.getInt("courseID");
					int creditHours = resultSet.getInt("creditHours");
					String courseDepartment = resultSet.getString("courseDepartment");
					String courseName = resultSet.getString("courseName");
					allCourses.put(id, new Course(id, courseName, courseDepartment, creditHours));
				}
				connection.close();
				sqlStatement.close();
				resultSet.close();
				return allCourses;
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [getAllCourses]");
		}
		return null;
	}

	/*
	 * Query MySQL database and return all course records in a department.
	 */
	public HashMap<Integer, Course> getCoursesByDepartment(String department) {
		HashMap<Integer, Course> allCourses = new HashMap<Integer, Course>();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			System.out.println("MySQL JDBC Driver not found! [getCoursesByDepartment]");
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				ResultSet resultSet = sqlStatement
						.executeQuery("SELECT * FROM Courses WHERE courseDepartment='" + department + "'");
				while (resultSet.next()) {
					int id = resultSet.getInt("courseID");
					int creditHours = resultSet.getInt("creditHours");
					String courseDepartment = resultSet.getString("courseDepartment");
					String courseName = resultSet.getString("courseName");
					allCourses.put(id, new Course(id, courseName, courseDepartment, creditHours));
				}
				connection.close();
				sqlStatement.close();
				resultSet.close();
				return allCourses;
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [getCoursesByDepartment]");
		}
		return null;
	}

	/*
	 * Retrieve one course from the database by ID
	 */
	public Course getOneCourseById(int aId) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			System.out.println("MySQL JDBC Driver not found! [getOneCourseById]");
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				ResultSet resultSet = sqlStatement.executeQuery("SELECT * FROM Courses WHERE courseID=" + aId);
				if (resultSet.next()) {
					int creditHours = resultSet.getInt("creditHours");
					String courseDepartment = resultSet.getString("courseDepartment");
					String courseName = resultSet.getString("courseName");
					connection.close();
					sqlStatement.close();
					resultSet.close();
					return new Course(aId, courseName, courseDepartment, creditHours);
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [getOneCourseById]");
		}
		return null;
	}

	/*
	 * Create a new student in the database
	 */
	public Course setOneCourse(Course oneCourse) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found! [setOneCourse]");
			return null;
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				if (!sqlStatement.executeQuery("SELECT * FROM Courses WHERE courseID=" + oneCourse.getCourseID())
						.next()) {
					boolean insert = sqlStatement.execute("INSERT INTO Courses " + "VALUES (" + oneCourse.getCourseID()
							+ ", '" + oneCourse.getCourseName() + "', '" + oneCourse.getCourseDepartment() + "', "
							+ oneCourse.getCreditHours() + ")");
					Course aCourse = null;
					ResultSet result = sqlStatement
							.executeQuery("SELECT * FROM Courses WHERE courseID=" + oneCourse.getCourseID());
					if (!insert && result.next())
						aCourse = new Course(result.getInt("courseID"), result.getString("courseName"),
								result.getString("courseDepartment"), result.getInt("creditHours"));
					connection.close();
					sqlStatement.close();
					result.close();
					return aCourse;
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [setOneCourse]");
		}
		return null;
	}

	/*
	 * Update a particular course in the database
	 */
	public Course updateOneCourse(Course oneCourse) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found! [updateOneCourse]");
			return null;
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				if (sqlStatement.executeQuery("SELECT * FROM Courses WHERE courseID=" + oneCourse.getCourseID())
						.next()) {
					int update = sqlStatement
							.executeUpdate("UPDATE Courses" + " SET courseName='" + oneCourse.getCourseName() + "', "
									+ "courseDepartment='" + oneCourse.getCourseDepartment() + "', " + "creditHours="
									+ oneCourse.getCreditHours() + " WHERE courseID=" + oneCourse.getCourseID());
					Course aCourse = null;
					ResultSet result = sqlStatement
							.executeQuery("SELECT * FROM Courses WHERE courseID=" + oneCourse.getCourseID());
					if (update == 1 && result.next())
						aCourse = new Course(result.getInt("courseID"), result.getString("courseName"),
								result.getString("courseDepartment"), result.getInt("creditHours"));
					connection.close();
					sqlStatement.close();
					result.close();
					return aCourse;
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [updateOneCourse]");
		}
		return null;
	}

	/*
	 * Remove a particular course from the database by ID
	 */
	public Course deleteOneCourseById(int id) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found! [deleteOneCourseById]");
			return null;
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				Course aCourse = null;
				ResultSet result = sqlStatement.executeQuery("SELECT * FROM Courses WHERE courseID=" + id);
				if (result.next())
					aCourse = new Course(result.getInt("courseID"), result.getString("courseName"),
							result.getString("courseDepartment"), result.getInt("creditHours"));
				boolean delete = sqlStatement.execute("DELETE FROM Courses WHERE courseID=" + id);
				connection.close();
				sqlStatement.close();
				result.close();
				return !delete ? aCourse : null;
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [deleteOneCourseById]");
		}
		return null;
	}
}
