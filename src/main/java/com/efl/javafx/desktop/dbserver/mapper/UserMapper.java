package com.efl.javafx.desktop.dbserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.efl.javafx.desktop.dbserver.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
