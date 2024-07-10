package com.pinkproject.user.UserResponse;

public record _LoginRespRecord(
        UserRecord user,
        String jwt
) {
    public record UserRecord(
            String email,
            String password
    ) {
    }
}