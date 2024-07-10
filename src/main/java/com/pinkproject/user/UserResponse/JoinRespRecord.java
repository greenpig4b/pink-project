package com.pinkproject.user.UserResponse;

import lombok.Builder;

@Builder
public record JoinRespRecord(
        String email,
        String password
) {
}
