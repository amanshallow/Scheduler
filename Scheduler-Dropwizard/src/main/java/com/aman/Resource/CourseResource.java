package com.aman.Resource;

import com.aman.DAO.CourseDao;
import com.aman.Representation.Course;
import com.aman.Representation.User;
import io.dropwizard.auth.Auth;
import org.eclipse.jetty.http.HttpStatus;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

@Path("/scheduler")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourseResource {
    private final CourseDao COURSE_DAO;

    public CourseResource(Jdbi jdbi) {
        this.COURSE_DAO = new CourseDao(jdbi);
    }

    /* GET: Return all students from the database. */
    @GET
    @Path("/courses")
    public Response getCourses(@Auth User user) {
        List<Course> allCourses = COURSE_DAO.listCourses();
        if (allCourses.isEmpty())
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "404");
                            put("message", "No courses exist yet");
                        }
                    }).build();
        return Response.ok(allCourses).build();
    }

    /* GET: Return a particular student by ID number or 404. */
    @GET
    @Path("/course/{course_id}")
    public Response getCourseById(@PathParam("course_id") @NotNull int course_id, @Auth User user) {
        if (course_id < 100 || course_id > 999) {
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422, "Improper format")
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "422");
                            put("message", "id must be three digits");
                        }
                    }).build();
        }
        Course course = COURSE_DAO.getCourseById(course_id);
        if (course == null)
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "404");
                            put("message", "Course ID not found");
                        }
                    }).build();
        return Response.ok(course).build();
    }

    @GET
    @Path("/course/search/")
    public Response getCoursesByDepartment(@QueryParam("department") @NotNull String department, @Auth User user) {
        if (!department.matches("[a-zA-Z]+"))
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422,"Improper Format")
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "422");
                            put("message", "Department must only contain letters");
                        }
                    }).build();
        List<Course> allCourses = COURSE_DAO.getCoursesByDepartment(department);
        if (allCourses.isEmpty())
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "404");
                            put("message", "No courses exist with that department");
                        }
                    }).build();
        return Response.ok(allCourses).build();
    }

    @POST
    @Path("/course/{course_id}")
    public Response createCourse(@PathParam("course_id") @NotNull int course_id, Course course, @Auth User user) {
        // Test for missing information
        if (course == null || course.getCourse_name() == null || course.getDepartment() == null)
            return nullObjectResponse();

        // Validate course object
        course.setCourse_id(course_id);
        if (validateInputs(course))
            return improperFormatResponse();

        // Create course object.
        Course createdCourse = COURSE_DAO.createCourse(course);
        if (createdCourse == null)
            return Response.status(HttpStatus.CONFLICT_409)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "409");
                            put("message", "Course already exists");
                        }
                    }).build();
        return Response.status(HttpStatus.CREATED_201).entity(createdCourse).build();
    }

    @PUT
    @Path("/course/{course_id}")
    public Response updateCourse(@PathParam("course_id") @NotNull int course_id, Course course, @Auth User user) {
        // Test for missing information
        if (course == null || course.getCourse_name() == null || course.getDepartment() == null)
            return nullObjectResponse();

        // Validate course object
        course.setCourse_id(course_id);
        if (validateInputs(course))
            return improperFormatResponse();

        // Update course object
        Course createdCourse = COURSE_DAO.updateCourse(course);
        if (createdCourse == null)
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "404");
                            put("message", "Course does not exist");
                        }
                    }).build();
        return Response.ok(createdCourse).build();
    }

    @DELETE
    @Path("/course/{course_id}")
    public Response deleteCourse(@PathParam("course_id") @NotNull int course_id, @Auth User user) {
        if (course_id < 100 || course_id > 999) {
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422, "Improper format")
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "422");
                            put("message", "id must be three digits");
                        }
                    }).build();
        }
        Course course = COURSE_DAO.deleteCourse(course_id);
        if (course == null)
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "404");
                            put("message", "Course with ID [ " + course_id + " ] does not exist");
                        }
                    }).build();
        return Response.ok(course).build();
    }

    private Response nullObjectResponse() {
        return Response.status(HttpStatus.BAD_REQUEST_400)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new HashMap<String, String>() {
                    {
                        put("code", "400");
                        put("message", "JSON body is empty or missing information");
                    }
                }).build();
    }

    private Response improperFormatResponse() {
        return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422, "Improper format")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new HashMap<String, String>() {
                    {
                        put("courseName", "Letters with dash if needed");
                        put("courseDepartment", "Letters without spaces");
                        put("courseID", "Three digits");
                    }
                }).build();
    }

    private static boolean validateInputs(Course course) {
        boolean courseName = course.getCourse_name().matches("[a-zA-Z-]+");
        boolean courseDepartment = course.getDepartment().matches("[a-zA-Z]+");
        boolean courseID = (course.getCourse_id() > 99 && course.getCourse_id() < 1000);
        return !courseName || !courseDepartment || !courseID;
    }
}
