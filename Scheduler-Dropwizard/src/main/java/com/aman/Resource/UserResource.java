package com.aman.Resource;

import com.aman.DAO.UserDao;
import com.aman.Representation.User;
import org.eclipse.jetty.http.HttpStatus;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private final UserDao USER_DAO;

    public UserResource(Jdbi jdbi) {
        this.USER_DAO = new UserDao(jdbi);
    }

    @POST
    @Path("/user")
    public Response createUser(User user) {
        if (user == null || user.getName() == null || user.getPassword() == null)
            return Response.status(HttpStatus.BAD_REQUEST_400)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "400");
                            put("message", "JSON body is empty or missing information");
                        }
                    }).build();

        if (user.getPassword().length() < 12 || !user.getName().matches("^[a-zA-Z0-9]{8,50}"))
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "422");
                            put("name", "Eight to 50 letters");
                            put("password", "Mix of letters, digits or symbols");
                        }
                    }).build();

        HashMap<String, String> createdUser = USER_DAO.createUser(user);
        if (createdUser == null)
            return Response.status(HttpStatus.CONFLICT_409)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new HashMap<String, String>() {
                        {
                            put("code", "409");
                            put("message", "User already exists");
                        }
                    }).build();
        return Response.status(HttpStatus.CREATED_201).entity(createdUser).build();
    }
}
