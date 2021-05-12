package com.aman.Resource;

import com.aman.DAO.StudentDao;
import com.aman.Representation.Student;
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
public class StudentResource {
    private final StudentDao STUDENT_DAO;

    public StudentResource(Jdbi jdbi) {
        this.STUDENT_DAO = new StudentDao(jdbi);
    }

    /* GET: Return all students from the database. */
    @GET
    @Path("/students")
    public Response getStudents(@Auth User user) {
        List<Student> allStudents = STUDENT_DAO.listStudents();
        if (allStudents.isEmpty())
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "404");
                            put("message", "No students exist yet");
                        }
                    }).build();
        return Response.ok(allStudents).build();
    }

    /* GET: Return a particular student by ID number or 404. */
    @GET
    @Path("/student/{id}")
    public Response getStudentById(@PathParam("id") @NotNull int student_id, @Auth User user) {
        if (student_id < 0) {
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422, "Improper format")
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "422");
                            put("message", "id must be positive");
                        }
                    }).build();
        }
        Student student = STUDENT_DAO.getStudentById(student_id);
        if (student == null)
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "404");
                            put("message", "Student ID not found");
                        }
                    }).build();
        return Response.ok(student).build();
    }

    @GET
    @Path("/student/search/")
    public Response getStudentsByLastName(@QueryParam("last_name") @NotNull String lastName, @Auth User user) {
        if (!lastName.matches("[a-zA-Z]+"))
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422, "Improper Format")
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "422");
                            put("message", "last_name must only contain letters");
                        }
                    }).build();
        List<Student> allStudents = STUDENT_DAO.getStudentsByLastName(lastName);
        if (allStudents.isEmpty())
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "404");
                            put("message", "No students exist with that last name");
                        }
                    }).build();
        return Response.ok(allStudents).build();
    }

    @POST
    @Path("/student")
    public Response createStudent(Student student, @Auth User user) {
        // Test student object for missing required information
        if (student == null || student.getFirst_name() == null || student.getLast_name() == null
                || student.getDate_of_birth() == null || student.getAddress() == null
                || student.getEmail() == null)
            return nullObjectResponse();

        // Validate student object
        student.setNumber_of_courses(0);
        if (validateInputs(student))
            return improperFormatResponse();

        // Create student object.
        Student createdStudent = STUDENT_DAO.createStudent(student);
        if (createdStudent == null)
            return Response.status(HttpStatus.CONFLICT_409)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "409");
                            put("message", "Student already exists");
                        }
                    }).build();
        return Response.status(HttpStatus.CREATED_201).entity(createdStudent).build();
    }

    @PUT
    @Path("/student/{id}")
    public Response updateStudent(@PathParam("id") @NotNull int studentID, Student student, @Auth User user) {
        // Test student object for missing required information
        if (student == null || student.getFirst_name() == null || student.getLast_name() == null
                || student.getDate_of_birth() == null || student.getAddress() == null
                || student.getEmail() == null)
            return nullObjectResponse();

        // Validate student object
        student.setId(studentID);
        if (validateInputs(student))
            return improperFormatResponse();

        // Update student object
        Student updateStudent = STUDENT_DAO.updateStudent(student);
        if (updateStudent == null)
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "404");
                            put("message", "Student does not exist");
                        }
                    }).build();
        return Response.ok(updateStudent).build();
    }

    @DELETE
    @Path("/student/{id}")
    public Response deleteStudent(@PathParam("id") @NotNull int studentID, @Auth User user) {
        if (studentID < 0) {
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422, "Improper format")
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "422");
                            put("message", "id must be positive");
                        }
                    }).build();
        }
        Student student = STUDENT_DAO.deleteStudent(studentID);
        if (student == null)
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "404");
                            put("message", "Student with ID [ " + studentID + " ] does not exist");
                        }
                    }).build();
        return Response.ok(student).build();
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
                        put("id", "Positive digit");
                        put("firstName", "Letters");
                        put("lastName", "Letters");
                        put("dateOfBirth", "MM-DD-YYYY");
                        put("email", "email@oswego.edu");
                        put("address", "Letters and digits");
                    }
                }).build();
    }

    private static boolean validateInputs(Student student) {
        boolean first = student.getFirst_name().matches("[a-zA-Z]+");
        boolean last = student.getLast_name().matches("[a-zA-Z]+");
        boolean dob = student.getDate_of_birth().matches("\\d{2}-\\d{2}-\\d{4}");
        boolean email = student.getEmail().matches("[a-zA-Z]+@oswego.edu$");
        boolean address = student.getAddress().matches("[a-zA-Z0-9,\\s]*");
        boolean id = student.getId() >= 0;
        return !first || !last || !dob || !email || !address || !id;
    }
}
