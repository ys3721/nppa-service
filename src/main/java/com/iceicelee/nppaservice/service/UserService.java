package com.iceicelee.nppaservice.service;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.dao.UserDao;
import com.iceicelee.nppaservice.entity.TUserEntity;
import com.iceicelee.nppaservice.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author: Yao Shuai
 * @date: 2021/4/9 19:45
 */
@Service
public class UserService implements IUserService {

    private UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 通过userid来查找user实体，
     * 按照id找这个人 如果没有的话就
     * @param userId
     *  平台id
     * @return
     *  如果没有返回空哇
     */
    @Override
    public User findUserByPassportId(Long userId) {
        TUserEntity userEntity = userDao.queryById(userId);
        if (userEntity != null) {
            return this.fromEntity(userEntity);
        } else {
            return null;
        }
    }

    public User fromEntity(TUserEntity entity) {
        User user = new User(entity.getId());
        user.setGameId(entity.getGameId());
        user.setPi(entity.getPi());
        user.setPassportName(entity.getPassportName());
        user.setRealName(entity.getRealName());
        user.setIdNumber(entity.getIdNumber());
        user.setCreateTime(entity.getCreateTime());
        user.setAuthStatus(AuthenticationStatus.codeOf(entity.getAuthStatus()));
        user.setAuthTime(entity.getAuthTime());
        return user;
    }

    public TUserEntity toEntity(User user) {
        TUserEntity entity = new TUserEntity();
        entity.setId(user.getId());
        entity.setGameId(user.getGameId());
        entity.setPi(user.getPi());
        entity.setPassportName(user.getPassportName());
        entity.setRealName(user.getRealName());
        entity.setIdNumber(user.getIdNumber());
        entity.setCreateTime(user.getCreateTime());
        entity.setAuthStatus((byte) user.getAuthStatus().getCode());
        entity.setAuthTime(user.getAuthTime());
        return entity;
    }

    public void createUserAndSave(long userId, int gameId, String pi, String passportName, String realName,
                                  String idCard, int reqTimeSec, int status, long now) {
        User user = new User();
        user.setId(userId);
        user.setGameId(gameId);
        user.setPi(pi);
        user.setPassportName(passportName);
        user.setRealName(realName);
        user.setIdNumber(idCard);
        user.setCreateTime(new Timestamp(reqTimeSec * 1000L));
        user.setAuthStatus(AuthenticationStatus.codeOf((byte) status));
        user.setAuthTime(new Timestamp(now));
        this.userDao.saveTUserEntity(this.toEntity(user));
    }

    @Override
    public void saveOrUpdateUser(User user) {
        User existUser = this.findUserByPassportId(user.getId());
        if (existUser == null) {
            this.userDao.saveTUserEntity(this.toEntity(user));
        } else {
            user.setCreateTime(existUser.getCreateTime());
            this.userDao.updateTUserEntity(this.toEntity(user));
        }
    }

    @Override
    public Collection<User> findUsersByStatus(AuthenticationStatus status) {
        Collection<TUserEntity> userEntity = userDao.queryByStatus(status.getCode());
        Collection<User> users = new ArrayList<>(userEntity.size());
        for (TUserEntity entity : userEntity) {
            users.add(this.fromEntity(entity));
        }
        return users;
    }
}
