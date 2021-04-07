package com.iceicelee.nppaservice.dao;

import com.iceicelee.nppaservice.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author yaoshuai
 * @date 2021-四月-06
 */
@Repository
public class UserDao {

    /**
     * jdbc template
     */
    private JdbcTemplate jdbc;

    @Autowired
    public UserDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public User findById(long userId) {
        String sql = "select id, pi, passportName, createTime, authStatus, authTime from t_user where id = ?";
        List<User> results = jdbc.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId((int) rs.getLong(1));
            user.setPi(rs.getString(2));
            user.setPassportName(rs.getString(3));
            user.setCreateTime(rs.getTimestamp(4));
            user.setAuthStatus(rs.getByte(5));
            user.setAuthTime(rs.getTimestamp(6));
            return user;
        }, userId);
        if (CollectionUtils.isEmpty(results)) {
            return null;
        }
        return results.get(0);
    }

    public List<User> findAll() {
        return jdbc.query("select id, pi, passportName, createTime, authStatus, authTime " +
                "from t_user", (rs, rowNum) -> {
                    User user = new User();
                    user.setId((int) rs.getLong(1));
                    user.setPi(rs.getString(2));
                    user.setPassportName(rs.getString(3));
                    user.setCreateTime(rs.getTimestamp(4));
                    user.setAuthStatus(rs.getByte(5));
                    user.setAuthTime(rs.getTimestamp(6));
                    return user;
                });
    }

    /**
     * 存储这个User
     *
     * @param user
     */
    public void save(User user) {
        jdbc.update("insert into t_user (id, pi, gameId, passportName, realName, idNum, createTime, authStatus, authTime) " +
                        "values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                user.getId(), user.getPi(), user.getGameId(), user.getPassportName(), user.getRealName(), user.getIdNumber(), user.getCreateTime(),
                user.getAuthStatus(), user.getAuthTime());
    }

}
