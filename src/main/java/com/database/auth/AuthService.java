package com.database.auth;

import com.database.user.User;
import com.database.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserService userService,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String name, String password) {
        User u = userService.getByName(name);
        if (u == null || !passwordEncoder.matches(password, u.getPassword_hash())){
            throw new UnauthorizedException("Invalid name or password");
        }
        return jwtService.generateToken(name);

    }

}
