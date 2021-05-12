package com.aman.Mapper;

import com.aman.Representation.Enrolled;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EnrolledMapper implements RowMapper<Enrolled> {

    @Override
    public Enrolled map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Enrolled(rs.getInt("student_ID"), rs.getInt("course_ID"));
    }
}
