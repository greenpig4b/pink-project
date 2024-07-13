package com.pinkproject.user.UserResponse;

import lombok.Builder;

@Builder
public record _JoinRespRecord(
        String email,
        String password
) {
}
