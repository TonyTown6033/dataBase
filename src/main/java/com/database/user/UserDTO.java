package com.database.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDTO {

    public record CreateReq(
            @NotBlank @Size(max = 64) String name,
            @NotBlank @Email @Size(max = 128) String email,
            @NotBlank @Size(max = 64, min = 8) String password
    ) {}

    public record UpdateReq(
            @NotBlank @Size(max = 64) String name,
            @NotBlank @Email @Size(max = 128) String email
    ) {}

    // 这里增加一个返回userDTO 是为了避免password_hash被泄露做离线破解
    public record UserResp (
        Long id,
        String name,
        String email
    ) {}
}
