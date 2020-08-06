package com.myproject.expensetrackerapi.repositories;

import com.myproject.expensetrackerapi.domain.Category;
import com.myproject.expensetrackerapi.exceptions.EtBadRequestException;
import com.myproject.expensetrackerapi.exceptions.EtResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private static final String SQL_CREATE = "INSERT INTO et_categories(category_id, user_id, title, description) " +
            "VALUES(NEXTVAL('et_categories_seq'), ?, ?, ?)";

    private static final String SQL_FIND_BY_ID = "SELECT C.category_id, C.user_id, C.title, C.description, " +
            "COALESCE(SUM(T.amount), 0) total_expense " +
            "FROM et_transactions T " +
            "RIGHT OUTER JOIN et_categories C " +
            "on C.category_id = T.category_id " +
            "WHERE C.user_id = ? AND C.category_id = ? " +
            "GROUP BY C.category_id";

    private static final String SQL_FIND_ALL = "SELECT C.category_id, C.user_id, C.title, C.description, " +
            "COALESCE(SUM(T.amount), 0) total_expense " +
            "FROM et_transactions T " +
            "RIGHT OUTER JOIN et_categories C " +
            "on C.category_id = T.category_id " +
            "WHERE C.user_id = ? " +
            "GROUP BY C.category_id";

    private static final String SQL_UPDATE = "UPDATE et_categories " +
            "SET title = ?, description = ? " +
            "WHERE user_id = ? AND category_id = ?";

    private static final String SQL_DELETE_CATEGORY = "DELETE FROM et_categories WHERE user_id = ? AND category_id = ?";

    private static final String SQL_DELETE_TRANSACTIONS = "DELETE FROM et_transactions WHERE category_id = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Category> findAll(Integer userId) throws EtResourceNotFoundException {
        // 2nd argument params for query C.user_id = ? => C.user_id = userId
        return jdbcTemplate.query(SQL_FIND_ALL, new Object[] { userId }, categoryRowMapper);
    }

    @Override
    public Category findById(Integer userId, Integer categoryId) throws EtResourceNotFoundException {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[]{ userId, categoryId }, categoryRowMapper);
        } catch(Exception e) {
            throw new EtResourceNotFoundException("Category not found");
        }
    }

    @Override
    public Integer create(Integer userId, String title, String description) throws EtBadRequestException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, userId);
                ps.setString(2, title);
                ps.setString(3, description);
                return ps;
            }, keyHolder);

            return (Integer) keyHolder.getKeys().get("category_id");
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public void update(Integer userId, Integer categoryId, Category category) throws EtBadRequestException {
        try {
            jdbcTemplate.update(SQL_UPDATE, new Object[] {
                    category.getTitle(),
                    category.getDescription(),
                    userId,
                    categoryId
            });
        } catch(Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public void removeById(Integer userId, Integer categoryId) {
        this.removeAllTransactions(categoryId);
        jdbcTemplate.update(SQL_DELETE_CATEGORY, new Object[]{ userId, categoryId });
    }

    private void removeAllTransactions(Integer categoryId) {
        jdbcTemplate.update(SQL_DELETE_TRANSACTIONS, new Object[]{ categoryId });
    }

    private RowMapper<Category> categoryRowMapper = (rs, i) -> {
        return new Category(
                rs.getInt("category_id"),
                rs.getInt("user_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDouble("total_expense")
        );
    };

}
