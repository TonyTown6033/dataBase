package com.database.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "创建用户", description = "创建一个用户并返回用户信息")
    @PostMapping("/users")
    public UserDTO.UserResp createUser(@Valid @RequestBody UserDTO.CreateReq user){
        return userService.create(user);
    }

    @GetMapping("/users/{id}")
    public UserDTO.UserResp getUser(@PathVariable Long id){
        return userService.getById(id);
    }

    @PutMapping("/users/{id}")
    public UserDTO.UserResp updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO.UpdateReq user) {
        return userService.update(id, user);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.delete(id);
    }

    @GetMapping("/users")
    public UserPageResponse listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.list(page, size);
    }
}
