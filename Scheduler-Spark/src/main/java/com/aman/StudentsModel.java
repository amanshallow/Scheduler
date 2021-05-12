package com.aman;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

/* Student Model */
public class StudentsModel {
	private final String url;
	private final String user;
	private final String password;

	public StudentsModel() {
		url = "jdbc:mysql://localhost:3306/scheduler";
		user = "username";
		password = "secretpassword";
	}

	/*
	 * Retrieve all students from MySQL database.
	 */
	public HashMap<Integer, Student> getAllStudents() {
		HashMap<Integer, Student> allStudents = new HashMap<Integer, Student>();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found! [getAllStudent]");
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				ResultSet resultSet = sqlStatement.executeQuery("SELECT * FROM Students");
				while (resultSet.next()) {
					int id = resultSet.getInt("studentID");
					String firstName = resultSet.getString("firstName");
					String lastName = resultSet.getString("lastName");
					String dateOfBirth = resultSet.getString("dateOfBirth");
					String address = resultSet.getString("address");
					String email = resultSet.getString("email");
					int numberOfCourses = resultSet.getInt("numberOfCourses");
					allStudents.put(id,
							new Student(id, firstName, lastName, dateOfBirth, address, email, numberOfCourses));
				}
				connection.close();
				sqlStatement.close();
				resultSet.close();
				return allStudents;
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [getAllStudents]");
		}
		return null;
	}

	/*
	 * Query MySQL database and return all student record with matching last name.
	 */
	public HashMap<Integer, Student> getStudentsByLastName(String alastName) {
		HashMap<Integer, Student> allStudents = new HashMap<Integer, Student>();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			System.out.println("MySQL JDBC Driver not found! [getStudentsByLastName]");
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				ResultSet resultSet = sqlStatement
						.executeQuery("SELECT * FROM Students WHERE lastName='" + alastName + "'");
				while (resultSet.next()) {
					int id = resultSet.getInt("studentID");
					String firstName = resultSet.getString("firstName");
					String lastName = resultSet.getString("lastName");
					String dateOfBirth = resultSet.getString("dateOfBirth");
					String address = resultSet.getString("address");
					String email = resultSet.getString("email");
					int numberOfCourses = resultSet.getInt("numberOfCourses");
					allStudents.put(id,
							new Student(id, firstName, lastName, dateOfBirth, address, email, numberOfCourses));
				}
				connection.close();
				sqlStatement.close();
				resultSet.close();
				return allStudents;
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [getStudentsByLastName]");
		}
		return null;
	}

	/*
	 * Retrieve one student from the database by ID
	 */
	public Student getOneStudentById(int aId) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			System.out.println("MySQL JDBC Driver not found! [getOneById]");
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				ResultSet resultSet = sqlStatement.executeQuery("SELECT * FROM Students WHERE studentID=" + aId);
				if (resultSet.next()) {
					String firstName = resultSet.getString("firstName");
					String lastName = resultSet.getString("lastName");
					String dateOfBirth = resultSet.getString("dateOfBirth");
					String address = resultSet.getString("address");
					String email = resultSet.getString("email");
					int numberOfCourses = resultSet.getInt("numberOfCourses");
					connection.close();
					sqlStatement.close();
					resultSet.close();
					return new Student(aId, firstName, lastName, dateOfBirth, address, email, numberOfCourses);
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [getOneById]");
		}
		return null;
	}

	/*
	 * Create a new student in the database
	 */
	public Student setOneStudent(Student oneStudent) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found! [setOneStudent]");
			return null;
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				if (!sqlStatement.executeQuery("SELECT * FROM Students WHERE email='" + oneStudent.getEmail() + "'")
						.next()) {
					boolean insert = sqlStatement.execute("INSERT INTO Students "
							+ "(firstName, lastName, dateOfBirth, address, email, numberOfCourses)" + "VALUES ('"
							+ oneStudent.getFirstName() + "', '" + oneStudent.getLastName() + "', '"
							+ oneStudent.getDateOfBirth() + "', '" + oneStudent.getAddress() + "', '"
							+ oneStudent.getEmail() + "', " + oneStudent.getNumberOfCourses() + ")");
					Student aStudent = null;
					ResultSet result = sqlStatement
							.executeQuery("SELECT * FROM Students WHERE email='" + oneStudent.getEmail() + "'");
					if (!insert && result.next())
						aStudent = new Student(result.getInt("studentID"), result.getString("firstName"),
								result.getString("lastName"), result.getString("dateOfBirth"),
								result.getString("address"), result.getString("email"),
								result.getInt("numberOfCourses"));
					connection.close();
					sqlStatement.close();
					result.close();
					return aStudent;
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [setOneStudent]");
		}
		return null;
	}

	/*
	 * Update a particular student in the database
	 */
	public Student updateOneStudent(Student oneStudent) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found! [updateOneStudent]");
			return null;
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				if (sqlStatement.executeQuery("SELECT * FROM Students WHERE studentID=" + oneStudent.getId()).next()) {
					int update = sqlStatement.executeUpdate(
							"UPDATE Students" + " SET firstName='" + oneStudent.getFirstName() + "', " + "lastName='"
									+ oneStudent.getLastName() + "', " + "dateOfBirth='" + oneStudent.getDateOfBirth()
									+ "', " + "address='" + oneStudent.getAddress() + "', " + "email='"
									+ oneStudent.getEmail() + "' WHERE studentID=" + oneStudent.getId());
					Student aStudent = null;
					ResultSet result = sqlStatement
							.executeQuery("SELECT * FROM Students WHERE studentID=" + oneStudent.getId());
					if (update == 1 && result.next())
						aStudent = new Student(result.getInt("studentID"), result.getString("firstName"),
								result.getString("lastName"), result.getString("dateOfBirth"),
								result.getString("address"), result.getString("email"),
								result.getInt("numberOfCourses"));
					connection.close();
					sqlStatement.close();
					result.close();
					return aStudent;
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [updateOneStudent]");
		}
		return null;
	}

	/*
	 * Remove a particular student from the database by ID
	 */
	public Student deleteOneStudentById(int id) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found! [deleteOneStudentById]");
			return null;
		}
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			if (connection != null) {
				Statement sqlStatement = connection.createStatement();
				Student aStudent = null;
				ResultSet results = sqlStatement.executeQuery("SELECT * FROM Students WHERE studentID=" + id);
				if (results.next())
					aStudent = new Student(results.getInt("studentID"), results.getString("firstName"),
							results.getString("lastName"), results.getString("dateOfBirth"),
							results.getString("address"), results.getString("email"),
							results.getInt("numberOfCourses"));
				boolean delete = sqlStatement.execute("DELETE FROM Students WHERE studentID=" + id);
				ResultSet result = sqlStatement.executeQuery("SELECT MAX(studentID) AS LargestID FROM Students");
				if (result.next()) {
					int maxID = result.getInt("LargestID");
					sqlStatement.execute("ALTER TABLE Students AUTO_INCREMENT=" + maxID);
				}
				connection.close();
				sqlStatement.close();
				result.close();
				results.close();
				return !delete ? aStudent : null;
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in [deleteOneStudentById]");
		}
		return null;
	}
}
