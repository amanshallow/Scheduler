package com.aman.Mapper;

import com.aman.Representation.Student;
import org.jdbi.v3.core.mapper.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentMapper implements RowMapper<Student> {

    @Override
    public Student map(ResultSet rs, org.jdbi.v3.core.statement.StatementContext ctx) throws SQLException {
        return new Student(rs.getInt("studentID"), rs.getString("firstName"),
                rs.getString("lastName"), rs.getString("dateOfBirth"),
                rs.getString("address"), rs.getString("email"),
                rs.getInt("numberOfCourses"));
    }
}
