package com.iceicelee.nppaservice.dao;

import com.iceicelee.nppaservice.entity.TUserEntity;

import java.util.Collection;

/**
 * @author: Yao Shuai
 * @date: 2021/4/9 19:51
 */
public interface IUserEntityDao {

    TUserEntity queryById(long userId);

    int saveTUserEntity(TUserEntity entity);

    int updateTUserEntity(TUserEntity entity);

    Collection<TUserEntity> queryByStatus(int authStatus);
}
