package com.database.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserMapper userMapper;
    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id){
        return userMapper.findById(id);
    }
}
