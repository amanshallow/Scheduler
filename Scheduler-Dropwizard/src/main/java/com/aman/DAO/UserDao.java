package com.aman.DAO;

import com.aman.Mapper.StudentMapper;
import com.aman.Mapper.UserMapper;
import com.aman.Representation.User;
import org.jdbi.v3.core.Jdbi;

import java.util.HashMap;

public class UserDao {

    private final Jdbi JDBI;

    public UserDao(Jdbi JDBI) {
        this.JDBI = JDBI;
    }

    public User findExistingUser(String username) {
        try {
            return JDBI.withHandle(handle -> handle
                    .createQuery("SELECT * FROM Users WHERE username = :username")
                    .bind("username", username).map(new UserMapper()).one());
        } catch (IllegalStateException e) {
            System.out.println("[ERROR]: " + e.getMessage() + " in findExistingUser()");
            System.out.println("[WARN]: User was not found!");
        }
        return null;
    }

    public HashMap<String, String> createUser(User user) {
        // Validate whether User already exists within database.
        int exists = JDBI.withHandle(handle -> handle
                .createQuery("SELECT EXISTS (SELECT * FROM Users WHERE username = :username)")
                .bind("username", user.getName())
                .mapTo(Integer.class).one());

        if (exists == 0) {
            // User does not exist, create it.
            int insert = JDBI.withHandle(handle -> handle
                    .createUpdate("INSERT INTO Users VALUES (?, ?)")
                    .bind(0, user.getName())
                    .bind(1, user.getPassword())
                    .execute());

            // User created, return it.
            if (insert == 1)
                return new HashMap<>() {{
                    put("code", "200");
                    put("message", "user successfully created");
                }};
        }
        // User already exists.
        return null;
    }
}
