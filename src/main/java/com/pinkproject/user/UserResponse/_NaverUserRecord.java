package com.pinkproject.user.UserResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public record _NaverUserRecord(
        @JsonProperty("response")
        Response response
) {
    public record Response(
            @JsonProperty("id")
            String id,
            @JsonProperty("email")
            String email,
            @JsonProperty("nickname")
            String nickname
    ) {
    }
}
