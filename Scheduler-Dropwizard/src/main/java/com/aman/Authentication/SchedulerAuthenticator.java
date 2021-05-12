package com.aman.Authentication;

import com.aman.DAO.UserDao;
import com.aman.Representation.User;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.jdbi.v3.core.Jdbi;

import java.util.Optional;

public class SchedulerAuthenticator implements Authenticator<BasicCredentials, User> {
    private final Jdbi JDBI;

    public SchedulerAuthenticator(Jdbi jdbi) {
        this.JDBI = jdbi;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) {
        User user = null;
        if (credentials.getUsername().matches("^[a-zA-Z0-9]{8,50}"))
            user = new UserDao(JDBI).findExistingUser(credentials.getUsername());
        if (user != null && user.getName().equalsIgnoreCase(credentials.getUsername())
                && user.getPassword().equals(credentials.getPassword()))
            return Optional.of(new User(credentials.getUsername()));
        return Optional.empty();
    }
}