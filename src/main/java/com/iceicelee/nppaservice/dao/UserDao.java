package com.iceicelee.nppaservice.dao;

import com.iceicelee.nppaservice.entity.TUserEntity;
import com.iceicelee.nppaservice.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * 直接的JDBC实现
 *
 * @author yaoshuai
 * @date 2021-四月-06
 */
@Repository
public class UserDao implements IUserEntityDao {
    /**
     * jdbc template
     */
    private JdbcTemplate jdbc;

    @Autowired
    public UserDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public TUserEntity queryById(long userId) {
        String sql = "select id, gameId, pi, passportName, realName, idNum, createTime," +
                " authStatus, authTime from t_user where id = ?";
        List<TUserEntity> results = jdbc.query(sql, new TUserEntityMapper(), userId);
        if (CollectionUtils.isEmpty(results)) {
            return null;
        }
        return results.get(0);
    }

    @Override
    public int saveTUserEntity(TUserEntity entity) {
        return jdbc.update("insert into t_user (id, pi, gameId, passportName, " +
                        "realName, idNum, createTime, authStatus, authTime) " +
                        "values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                entity.getId(), entity.getPi(), entity.getGameId(), entity.getPassportName(),
                entity.getRealName(), entity.getIdNumber(), entity.getCreateTime(), entity.getAuthStatus(), entity.getAuthTime());
    }

    @Override
    public int updateTUserEntity(TUserEntity entity) {
        String updateSql = "update t_user set gameId=?, pi=?, passportName=?, realName=?, " +
                "idNum=?, createTime=?, authStatus=?, authTime=? where id = ?";
        return jdbc.update(updateSql, entity.getGameId(), entity.getPi(), entity.getPassportName(), entity.getRealName(),
                entity.getIdNumber(), entity.getCreateTime(), entity.getAuthStatus(), entity.getAuthTime(), entity.getId());
    }

    @Override
    public Collection<TUserEntity> queryByStatus(int authStatus) {
        String sql = "select id, gameId, pi, passportName, realName, idNum, createTime," +
                " authStatus, authTime from t_user where authStatus = ?";
        List<TUserEntity> results = jdbc.query(sql, new TUserEntityMapper(), authStatus);
        if (CollectionUtils.isEmpty(results)) {
            return null;
        }
       return results;
    }

    public List<TUserEntity> findAll() {
        return jdbc.query("select id, pi, passportName, createTime, authStatus, authTime " +
                "from t_user", new TUserEntityMapper());
    }

    static class TUserEntityMapper implements RowMapper<TUserEntity> {
        @Override
        public TUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            TUserEntity userEntity = new TUserEntity();
            userEntity.setId(rs.getLong(1));
            userEntity.setGameId(rs.getInt(2));
            userEntity.setPi(rs.getString(3));
            userEntity.setPassportName(rs.getString(4));
            userEntity.setRealName(rs.getString(5));
            userEntity.setIdNumber(rs.getString(6));
            userEntity.setCreateTime(rs.getTimestamp(7));
            userEntity.setAuthStatus(rs.getByte(8));
            userEntity.setAuthTime(rs.getTimestamp(9));
            return userEntity;
        }
    }
}
