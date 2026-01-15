package com.database.user;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private String password_hash;
}
