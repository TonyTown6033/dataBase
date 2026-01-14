package com.database.user;

import java.util.List;

public record UserPageResponse(
        int page,
        int size,
        long total,
        List<UserDTO.UserResp> items
) {}
