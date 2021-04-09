package com.iceicelee.nppaservice.service;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.dao.UserDao;
import com.iceicelee.nppaservice.entity.TUserEntity;
import com.iceicelee.nppaservice.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
