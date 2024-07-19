package com.pinkproject.user.UserResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public record _KakaoUserRecord(
        Long id,
        @JsonProperty("connected_at")
        Timestamp connectedAt,
        Properties properties
) {
    public record Properties(
            String nickname
    ){
    }
}
