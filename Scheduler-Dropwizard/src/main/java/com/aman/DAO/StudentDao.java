package com.aman.DAO;

import com.aman.Mapper.StudentMapper;
import com.aman.Representation.Student;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class StudentDao {

    private final Jdbi JDBI;

    public StudentDao(Jdbi JDBI) {
        this.JDBI = JDBI;
    }

    /* Queries database for all Student records and returns a list */
    public List<Student> listStudents() {
        return JDBI.withHandle(handle -> handle.createQuery("SELECT * FROM Students")
                .map(new StudentMapper()).list());
    }

    public Student getStudentById(int studentID) {
        int exists = JDBI.withHandle(handle -> handle
                .createQuery("SELECT EXISTS (SELECT * FROM Students where studentID = :studentID)")
                .bind("studentID", studentID).mapTo(Integer.class).one());
        return exists == 0 ? null : JDBI.withHandle(handle -> handle
                .createQuery("SELECT * FROM Students WHERE studentID = :studentID")
                .bind("studentID", studentID)
                .map(new StudentMapper()).one());
    }

    public List<Student> getStudentsByLastName(String lastName) {
        return JDBI.withHandle(handle -> handle
                .createQuery("SELECT * FROM Students WHERE lastName = :lastName")
                .bind("lastName", lastName)
                .map(new StudentMapper()).list());
    }

    public Student createStudent(Student student) {
        // Validate whether Student already exists within database.
        int exists = JDBI.withHandle(handle -> handle
                .createQuery("SELECT EXISTS (SELECT * FROM Students WHERE email = :email)")
                .bind("email", student.getEmail())
                .mapTo(Integer.class).one());

        if (exists == 0) {
            // Student does not exist, create it.
            int insert = JDBI.withHandle(handle -> handle
                    .createUpdate("INSERT INTO Students " +
                            "(firstName, lastName, dateOfBirth, address, email, numberOfCourses) " +
                            "VALUES (?, ?, ?, ?, ?, ?)")
                    .bind(0, student.getFirst_name())
                    .bind(1, student.getLast_name())
                    .bind(2, student.getDate_of_birth())
                    .bind(3, student.getAddress())
                    .bind(4, student.getEmail())
                    .bind(5, student.getNumber_of_courses())
                    .execute());

            // Student created, return it.
            if (insert == 1)
                return JDBI.withHandle(handle -> handle
                        .createQuery("SELECT * FROM Students WHERE email = :email")
                        .bind("email", student.getEmail())
                        .map(new StudentMapper()).one());
        }
        // Student already exists.
        return null;
    }

    public Student updateStudent(Student student) {
        // Validate whether Student already exists within database.
        int exists = JDBI.withHandle(handle -> handle
                .createQuery("SELECT EXISTS (SELECT * FROM Students WHERE studentID = :studentID)")
                .bind("studentID", student.getId())
                .mapTo(Integer.class).one());
        if (exists == 1) {
            int update = JDBI.withHandle(handle -> handle
                    .createUpdate("UPDATE Students SET firstName = :firstName, lastName = :lastName," +
                            "dateOfBirth = :dateOfBirth, address = :address, email = :email " +
                            "WHERE studentID = :studentID ")
                    .bind("firstName", student.getFirst_name())
                    .bind("lastName", student.getLast_name())
                    .bind("dateOfBirth", student.getDate_of_birth())
                    .bind("address", student.getAddress())
                    .bind("email", student.getEmail())
                    .bind("studentID", student.getId())
                    .execute());

            // Student updated, return it.
            if (update == 1) {
                return JDBI.withHandle(handle -> handle
                        .createQuery("SELECT * FROM Students WHERE studentID = :studentID")
                        .bind("studentID", student.getId())
                        .map(new StudentMapper()).one());
            }
        }
        // Student does not exist.
        return null;
    }

    public Student deleteStudent(int studentID) {
        Student student;
        try {
            student = JDBI.withHandle(handle -> handle
                    .createQuery("SELECT * FROM Students WHERE studentID = :studentID")
                    .bind("studentID", studentID)
                    .map(new StudentMapper()).one());
            int deleted = JDBI.withHandle(handle -> handle
                    .createUpdate("DELETE FROM Students WHERE studentID = :studentID")
                    .bind("studentID", studentID).execute());
            if (deleted == 1)
                return student;
        } catch (IllegalStateException e) {
            System.out.println("[ERROR]: " + e.getMessage() + " in deleteStudent()");
            System.out.println("[WARN]: Student was not found!");
        }
        return null;
    }
}
