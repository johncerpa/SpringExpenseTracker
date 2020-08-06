package com.myproject.expensetrackerapi.repositories;

import com.myproject.expensetrackerapi.domain.User;
import com.myproject.expensetrackerapi.exceptions.EtAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final String SQL_CREATE = "INSERT INTO et_users(user_id, first_name, last_name, email, password) VALUES(NEXTVAL('et_users_seq'), ?, ?, ?, ?)";
    private static final String SQL_COUNT_BY_EMAIL = "SELECT COUNT(*) FROM et_users WHERE email = ?";
    private static final String SQL_FIND_BY_ID = "SELECT user_id, first_name, last_name, email, password FROM et_users WHERE user_id = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer create(String firstName, String lastName, String email, String password) throws EtAuthException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        SQL_CREATE,
                        Statement.RETURN_GENERATED_KEYS
                );

                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, email);
                ps.setString(4, password);

                return ps;
            }, keyHolder);

            return (Integer) keyHolder.getKeys().get("user_id");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new EtAuthException("Invalid details. Failed to create account");
        }
    }

    @Override
    public User findByEmailAndPassword(String email, String password) throws EtAuthException {
        return null;
    }

    @Override
    public Integer getCountByEmail(String email) {
        return jdbcTemplate.queryForObject(SQL_COUNT_BY_EMAIL, new Object[]{ email }, Integer.class);
    }

    @Override
    public User findById(Integer userId) {
        return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[]{ userId }, userRowMapper);
    }

    public RowMapper<User> userRowMapper = ((rs, rowNum) -> {
        return new User(
                rs.getInt("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("password")
        );
    });
}
