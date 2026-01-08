package com.database.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDTO {

    public record CreateReq(
            @NotBlank @Size(max = 64) String name,
            @NotBlank @Email @Size(max = 128) String email
    ) {}

    public record UpdateReq(
            @NotBlank @Size(max = 64) String name,
            @NotBlank @Email @Size(max = 128) String email
    ) {}
}
