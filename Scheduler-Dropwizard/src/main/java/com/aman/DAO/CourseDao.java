package com.aman.DAO;

import com.aman.Mapper.CourseMapper;
import com.aman.Representation.Course;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class CourseDao {

    private final Jdbi JDBI;

    public CourseDao(Jdbi JDBI) {
        this.JDBI = JDBI;
    }

    /* Queries database for all Course records and returns a list */
    public List<Course> listCourses() {
        return JDBI.withHandle(handle -> handle.createQuery("SELECT * FROM Courses")
                .map(new CourseMapper()).list());
    }

    public Course getCourseById(int courseID) {
        try {
            return JDBI.withHandle(handle -> handle
                    .createQuery("SELECT * FROM Courses WHERE courseID = :courseID")
                    .bind("courseID", courseID)
                    .map(new CourseMapper()).one());
        } catch (IllegalStateException e) {
            System.out.println("[ERROR]: " + e.getMessage() + " in getCourseById()");
            System.out.println("[WARN]: Course was not found!");
        }
        return null;
    }

    public List<Course> getCoursesByDepartment(String courseDepartment) {
        return JDBI.withHandle(handle -> handle
                .createQuery("SELECT * FROM Courses WHERE courseDepartment = :courseDepartment")
                .bind("courseDepartment", courseDepartment)
                .map(new CourseMapper()).list());
    }

    public Course createCourse(Course course) {
        // Validate whether Course already exists within database.
        int exists = JDBI.withHandle(handle -> handle
                .createQuery("SELECT EXISTS (SELECT * FROM Courses WHERE courseID = :courseID)")
                .bind("courseID", course.getCourse_id())
                .mapTo(Integer.class).one());

        if (exists == 0) {
            // Course does not exist, create it.
            int insert = JDBI.withHandle(handle -> handle
                    .createUpdate("INSERT INTO Courses VALUES (?, ?, ?, ?)")
                    .bind(0, course.getCourse_id())
                    .bind(1, course.getCourse_name())
                    .bind(2, course.getDepartment())
                    .bind(3, course.getCredit_hours())
                    .execute());

            // Course created, return it.
            if (insert == 1)
                return JDBI.withHandle(handle -> handle
                        .createQuery("SELECT * FROM Courses WHERE courseID = :courseID")
                        .bind("courseID", course.getCourse_id())
                        .map(new CourseMapper()).one());
        }
        // Course already exists.
        return null;
    }

    public Course updateCourse(Course course) {
        // Validate whether Course already exists within database.
        int exists = JDBI.withHandle(handle -> handle
                .createQuery("SELECT EXISTS (SELECT * FROM Courses WHERE courseID = :courseID)")
                .bind("courseID", course.getCourse_id())
                .mapTo(Integer.class).one());
        if (exists == 1) {
            int update = JDBI.withHandle(handle -> handle
                    .createUpdate("UPDATE Courses SET courseName = :courseName, " +
                            "courseDepartment = :courseDepartment, creditHours = :creditHours" +
                            " WHERE courseID = :courseID")
                    .bind("courseName", course.getCourse_name())
                    .bind("courseDepartment", course.getDepartment())
                    .bind("creditHours", course.getCredit_hours())
                    .bind("courseID", course.getCourse_id())
                    .execute());

            // Course updated, return it.
            if (update == 1) {
                return JDBI.withHandle(handle -> handle
                        .createQuery("SELECT * FROM Courses WHERE courseID = :courseID")
                        .bind("courseID", course.getCourse_id())
                        .map(new CourseMapper()).one());
            }
        }
        // Course does not exist.
        return null;
    }

    public Course deleteCourse(int courseID) {
        Course course;
        try {
            course = JDBI.withHandle(handle -> handle
                    .createQuery("SELECT * FROM Courses WHERE courseID = :courseID")
                    .bind("courseID", courseID)
                    .map(new CourseMapper()).one());
            int deleted = JDBI.withHandle(handle -> handle
                    .createUpdate("DELETE FROM Courses WHERE courseID = :courseID")
                    .bind("courseID", courseID).execute());
            if (deleted == 1)
                return course;
        } catch (IllegalStateException e) {
            System.out.println("[ERROR]: " + e.getMessage() + " in deleteCourse()");
            System.out.println("[WARN]: Course was not found!");
        }
        return null;
    }
}
