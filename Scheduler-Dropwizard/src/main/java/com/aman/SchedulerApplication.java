package com.aman;

import com.aman.Authentication.SchedulerAuthenticator;
import com.aman.Representation.User;
import com.aman.Resource.CourseResource;
import com.aman.Resource.EnrolledResource;
import com.aman.Resource.StudentResource;
import com.aman.Resource.UserResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class SchedulerApplication extends Application<SchedulerConfiguration> {
    public static void main(String[] args) throws Exception {
        new SchedulerApplication().run(args);
    }

    @Override
    public void run(SchedulerConfiguration configuration, Environment environment) {
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(),
                "scheduler");
        jdbi.installPlugin(new SqlObjectPlugin());
        environment.jersey().register(jdbi);

        // Resources
        environment.jersey().register(new StudentResource(jdbi));
        environment.jersey().register(new CourseResource(jdbi));
        environment.jersey().register(new EnrolledResource(jdbi));
        environment.jersey().register(new UserResource(jdbi));

        // Basic HTTP Authentication
        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(new SchedulerAuthenticator(jdbi))
                        .setRealm("SCHEDULER-REALM")
                        .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    }
}
