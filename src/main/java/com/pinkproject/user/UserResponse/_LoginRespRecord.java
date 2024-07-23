package com.pinkproject.user.UserResponse;

import com.pinkproject.user.User;

public record _LoginRespRecord(
        UserRecord user,
        String jwt
) {
    public record UserRecord(
            Integer id,
            String email,
            String password
    ) {
    }
    public static UserRecord fromUser(User user) {
        return new UserRecord(user.getId(), user.getEmail(), user.getPassword());
    }
}