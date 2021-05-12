package com.aman.Mapper;

import com.aman.Representation.Course;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseMapper implements RowMapper<Course> {

    @Override
    public Course map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Course(rs.getInt("courseID"), rs.getString("courseName"),
                rs.getString("courseDepartment"), rs.getInt("creditHours"));
    }
}
