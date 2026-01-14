package com.database.user;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT id, name ,email FROM users WHERE id = #{id}")
    User findById(Long id);

    @Select("""
        SELECT id, username, password_hash
        FROM users
        WHERE username = #{username}
    """)
    User findByUsername(@Param("username") String username);

    @Insert("INSERT INTO users(name,email,password_hash) VALUES (#{name}, #{email}, #{password_hash})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET name = #{name}, email = #{email} WHERE id = #{id}")
    int update(User user);

    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);

    @Select("""
        SELECT id, name, email
        FROM users
        ORDER BY id DESC
        LIMIT #{limit} OFFSET #{offset}
    """)
    List<User> page(@Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM users")
    long count();
}
