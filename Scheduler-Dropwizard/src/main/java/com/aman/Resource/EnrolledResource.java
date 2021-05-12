package com.aman.Resource;

import com.aman.DAO.EnrolledDao;
import com.aman.Representation.Course;
import com.aman.Representation.Enrolled;
import com.aman.Representation.User;
import io.dropwizard.auth.Auth;
import org.eclipse.jetty.http.HttpStatus;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/scheduler")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnrolledResource {
    private final EnrolledDao ENROLLED_DAO;

    public EnrolledResource(Jdbi jdbi) {
        this.ENROLLED_DAO = new EnrolledDao(jdbi);
    }

    @GET
    @Path("/student/{id}/courses")
    public Response getEnrolledCourses(@PathParam("id") @NotNull int student_id, @Auth User user) {
        if (student_id < 0) {
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422, "Improper format")
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "422");
                            put("message", "id must be positive");
                        }
                    }).build();
        }
        HashMap<Integer, Course> allEnrolled = ENROLLED_DAO.listEnrolledCourses(student_id);
        if (allEnrolled.isEmpty())
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "404");
                            put("message", "Student is not enrolled in any courses");
                        }
                    }).build();
        return Response.ok(allEnrolled).build();
    }

    @POST
    @Path("/student/{id}/course/{course_id}")
    public Response createEnrollment(@PathParam("id") @NotNull int student_id,
                                     @PathParam("course_id") @NotNull int course_id, @Auth User user) {
        if (student_id < 0 || course_id < 100 || course_id > 999)
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422, "Improper format")
                    .entity(new HashMap<String, String>() {
                        {
                            put("id", "A positive digit");
                            put("course_id", "Three digits");
                        }
                    }).build();

        // Create student object.
        Enrolled enrolled = ENROLLED_DAO.createEnrollment(new Enrolled(student_id, course_id));
        if (enrolled == null)
            return Response.status(HttpStatus.BAD_REQUEST_400)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "400");
                            put("message", "Either enrollment exists or course doesn't exist");
                        }
                    }).build();
        return Response.status(HttpStatus.CREATED_201).entity(enrolled).build();
    }

    @DELETE
    @Path("/student/{id}/course/{course_id}")
    public Response deleteEnrollment(@PathParam("id") @NotNull int student_id,
                                     @PathParam("course_id") @NotNull int course_id, @Auth User user) {
        if (student_id < 0 || course_id < 100 || course_id > 999)
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422, "Improper format")
                    .entity(new HashMap<String, String>() {
                        {
                            put("id", "A positive digit");
                            put("course_id", "Three digits");
                        }
                    }).build();

        // Delete the enrollment
        Enrolled enrolled = ENROLLED_DAO.deleteEnrolledCourse(new Enrolled(student_id, course_id));
        if (enrolled == null)
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "404");
                            put("message", "Enrollment does not exist");
                        }
                    }).build();
        return Response.ok(enrolled).build();
    }
}
