package com.efl.javafx.desktop.dbserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.efl.javafx.desktop.dbserver.entity.User;
import org.springframework.stereotype.Component;


@Component
public interface UserService extends IService<User> {
    /**
     * 增加用户
     *
     * @param user 待新增的用户
     * @return 增加成功的用户
     */
    boolean create(User user);

    /**
     * 删除用户
     *
     * @param user 待删除的用户
     * @return 删除成功的用户
     */
    boolean delete(User user);

    /**
     * 查找用户
     *
     * @param userName 用户帐号
     * @return 用户帐号对应的用户
     */
    User find(String userName, String password);

    /**
     * 根据userName查找用户
     *
     * @param userName 用户帐号
     * @return 用户帐号对应的用户
     */
    User findByUserName(String userName);


    /**
     * 修改用户
     *
     * @param user 待修改的用户
     * @return 修改成功的用户
     */
    boolean update(User user);

    //boolean selectUserName(String userName);
    //boolean selectUserData(String userName, String password);
}
