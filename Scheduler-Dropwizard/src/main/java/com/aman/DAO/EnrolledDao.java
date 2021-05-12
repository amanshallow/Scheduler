package com.aman.DAO;

import com.aman.Mapper.CourseMapper;
import com.aman.Mapper.EnrolledMapper;
import com.aman.Representation.Course;
import com.aman.Representation.Enrolled;
import org.jdbi.v3.core.Jdbi;

import java.util.HashMap;
import java.util.List;

public class EnrolledDao {

    private final Jdbi JDBI;

    public EnrolledDao(Jdbi JDBI) {
        this.JDBI = JDBI;
    }

    /* Queries database for all Course records and returns a list */
    public HashMap<Integer, Course> listEnrolledCourses(int studentID) {
        List<Course> enrolled = JDBI.withHandle(handle -> handle.createQuery("SELECT courseID, " +
                "courseName, courseDepartment, creditHours FROM EnrolledCourses INNER JOIN Courses " +
                "ON EnrolledCourses.course_ID = Courses.courseID WHERE student_ID = :studentID")
                .bind("studentID", studentID).map(new CourseMapper()).list());
        HashMap<Integer, Course> allEnrolled = new HashMap<>();
        for (Course course : enrolled)
            allEnrolled.put(course.getCourse_id(), course);
        return allEnrolled;
    }

    public Enrolled createEnrollment(Enrolled enrolled) {
        // Validate whether Course already exists within database.
        int courseExists = JDBI.withHandle(handle -> handle
                .createQuery("SELECT EXISTS (SELECT * FROM Courses WHERE courseID = :courseID)")
                .bind("courseID", enrolled.getCourse_id())
                .mapTo(Integer.class).one());
        int enrollmentExists = JDBI.withHandle(handle -> handle
                .createQuery("SELECT EXISTS (SELECT * FROM EnrolledCourses WHERE student_ID = :studentID" +
                        " AND course_ID = :courseID)")
                .bind("studentID", enrolled.getStudent_id())
                .bind("courseID", enrolled.getCourse_id())
                .mapTo(Integer.class).one());

        // Enrollment does not exist, create it.
        if (courseExists == 1 && enrollmentExists == 0) {
            int insert = JDBI.withHandle(handle -> handle
                    .createUpdate("INSERT INTO EnrolledCourses VALUES (?, ?)")
                    .bind(0, enrolled.getStudent_id())
                    .bind(1, enrolled.getCourse_id())
                    .execute());

            // Enrollment created, return it.
            if (insert == 1) {
                JDBI.withHandle(handle -> handle.execute("UPDATE Students SET numberOfCourses" +
                        " = ((SELECT numberOfCourses FROM (SELECT MAX(numberOfCourses) " +
                        "FROM Students) AS totalCourses) + 1) WHERE studentID = " + enrolled.getStudent_id()));
                return JDBI.withHandle(handle -> handle
                        .createQuery("SELECT * FROM EnrolledCourses WHERE student_ID = :studentID" +
                                " AND course_ID = :courseID")
                        .bind("studentID", enrolled.getStudent_id())
                        .bind("courseID", enrolled.getCourse_id())
                        .map(new EnrolledMapper()).one());
            }
        }
        // Enrollment already exists.
        return null;
    }

    public Enrolled deleteEnrolledCourse(Enrolled enrolled) {
        Enrolled deletedEnrollment;
        try {
            deletedEnrollment = JDBI.withHandle(handle -> handle
                    .createQuery("SELECT * FROM EnrolledCourses WHERE student_ID = :studentID" +
                            " AND course_ID = :courseID")
                    .bind("studentID", enrolled.getStudent_id())
                    .bind("courseID", enrolled.getCourse_id())
                    .map(new EnrolledMapper()).one());
            int deleted = JDBI.withHandle(handle -> handle
                    .createUpdate("DELETE FROM EnrolledCourses WHERE student_ID = :studentID" +
                            " AND course_ID = :courseID")
                    .bind("studentID", enrolled.getStudent_id())
                    .bind("courseID", enrolled.getCourse_id())
                    .execute());
            if (deleted == 1) {
                JDBI.withHandle(handle -> handle
                        .execute("UPDATE Students SET numberOfCourses = " +
                                "IF(Students.numberOfCourses > 0, numberOfCourses - 1, 0) " +
                                "WHERE studentID = " + enrolled.getStudent_id()));
                return deletedEnrollment;
            }
        } catch (IllegalStateException e) {
            System.out.println("[ERROR]: " + e.getMessage() + " in deleteEnrolledCourse()");
            System.out.println("[WARN]: Enrollment was not found!");
        }
        return null;
    }
}
