package com.iceicelee.nppaservice.service;

import com.iceicelee.nppaservice.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author yaoshuai
 *
 */
@Repository
public class UserRepository {
    /**
     * jdbc template
     */
    private JdbcTemplate jdbc;

    @Autowired
    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<User> findAll() {
        return jdbc.query("select id, pi, passportName, createTime, authStatus, authTime " +
                "from t_user", new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId((int) rs.getLong(1));
                user.setPi(rs.getString(2));
                user.setPassportName(rs.getString(3));
                user.setCreateTime(rs.getTimestamp(4));
                user.setAuthStatus(rs.getByte(5));
                user.setAuthTime(rs.getTimestamp(6));
                return user;
            }
        });
    }

    public void save(User user) {
        jdbc.update("insert into t_user (id, pi, passportName, createTime, authStatus, authTime) " +
                        "values (?, ?, ?, ?, ?, ?)",
                user.getId(), user.getPi(), user.getPassportName(), user.getCreateTime(),
                user.getAuthStatus(), user.getAuthTime());
    }
}
