package com.database.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT id, name ,email FROM users WHERE id = #{id}")
    User findById(Long id);
}
