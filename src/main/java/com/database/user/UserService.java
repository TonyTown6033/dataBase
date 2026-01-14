package com.database.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper,  PasswordEncoder passwordEncoder) {

        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO.UserResp create(UserDTO.CreateReq req) {
        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email());

        String hashedPassword = passwordEncoder.encode(req.password());
        u.setPassword_hash(hashedPassword);

        userMapper.insert(u);
        return toResp(u);
    }

    public UserDTO.UserResp getById(long id) {
        User u = userMapper.findById(id);
        if (u == null){
            throw new NotFoundException("user not found, id is " + id);
        }
        return toResp(u);
    }


    // 这个方法会暴露 password_hash 用于鉴权
    public User getByUsername(String username) {
        User u = userMapper.findByUsername(username);
        if (u == null){
            throw new NotFoundException("user not found, user name is " + username);
        }
        return u;
    }

    public UserDTO.UserResp update(Long id, UserDTO.UpdateReq req) {
        User u = new User();
        u.setId(id);
        u.setName(req.name());
        u.setEmail(req.email());

        int updated = userMapper.update(u);
        if (updated == 0){
            throw new NotFoundException("user not found" + id);
        }
        return getById(id);
    }

    public void delete(Long id) {
        int deleted = userMapper.deleteById(id);
        if (deleted == 0){
            throw new NotFoundException("user not found" + id);
        }
    }

    public UserPageResponse list(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 1;
        if (size > 100) size = 100;

        int offset = (page - 1) * size;
        long total = userMapper.count();
        List<UserDTO.UserResp> items = userMapper.page(size, offset)
                .stream()
                .map(this::toResp)
                .toList();

        return new UserPageResponse(page, size, total, items);
    }

    private UserDTO.UserResp toResp(User u) {
        return new UserDTO.UserResp(u.getId(), u.getName(), u.getEmail(), u.getUsername());
    }
}
