package com.database.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User create(UserDTO.CreateReq req) {
        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email());
        userMapper.insert(u);
        return u;
    }

    public User getById(long id) {
        User u = userMapper.findById(id);
        if (u == null){
            throw new NotFoundException("user not found, id is " + id);
        }
        return u;
    }

    public User getByUsername(String username) {
        User u = userMapper.findByUsername(username);
        if (u == null){
            throw new NotFoundException("user not found, user name is " + username);
        }
        return u;
    }

    public User update(Long id, UserDTO.UpdateReq req) {
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
        var items = userMapper.page(size, offset);

        return new UserPageResponse(page, size, total, items);
    }
}
