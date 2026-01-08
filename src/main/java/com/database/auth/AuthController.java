package com.database.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    public record LoginReq(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginReq loginReq) {

        String token = authService.login(loginReq.username, loginReq.password);
        return Map.of("token", token);

    }
}
