package com.efl.javafx.desktop.dbserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.efl.javafx.desktop.dbserver.entity.User;
import com.efl.javafx.desktop.dbserver.mapper.UserMapper;
import com.efl.javafx.desktop.dbserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    public static UserServiceImpl userServiceImpl;
    //public static UserMapper userMapper;

    public UserServiceImpl(){
        userServiceImpl = this;
    }

    @Override
    public boolean create(User user) {
        return userServiceImpl.getBaseMapper().insert(user) > 0;
    }

    @Override
    public boolean delete(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserName, user.getUserName());
        return userServiceImpl.getBaseMapper().delete(queryWrapper) > 0;
    }

    @Override
    public User findByUserName(String userName) {
        return userServiceImpl.getBaseMapper().selectOne(new QueryWrapper<User>().lambda().eq(User::getUserName, userName));
    }

    @Override
    public User find(String userName, String password) {
        return userServiceImpl.getBaseMapper().selectOne(
                new QueryWrapper<User>().lambda().eq(User::getUserName, userName).eq(User::getPassword, password));
    }

    @Override
    public boolean update(User user) {
        return userServiceImpl.getBaseMapper().updateById(user) > 0;
    }

    //@Override
    //public boolean appendUserData(String userName, String password) {
    //    User user = new User();
    //    user.setUserName(userName);
    //    user.setPassword(password);
    //    try {
    //        this.save(user);
    //    } catch (Exception e) {
    //        log.error("数据库添加数据异常: ", e);
    //        return false;
    //    }
    //    return true;
    //}

    //@Override
    //public boolean selectUserData(String userName, String passWord) {
    //    QueryWrapper<User> wrapper = new QueryWrapper<>();
    //    wrapper.lambda().eq(User::getUserName, userName).eq(User::getPassword, passWord);
    //    User user = this.getBaseMapper().selectOne(wrapper);
    //    return user != null;
    //}

    //@Override
    //public boolean selectUserName(String userName) {
    //    QueryWrapper<User> wrapper = new QueryWrapper<>();
    //    wrapper.lambda().eq(User::getUserName, userName);
    //    User user = this.getBaseMapper().selectOne(wrapper);
    //    return user != null;
    //}
}
