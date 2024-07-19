package com.pinkproject._core.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pinkproject.admin.Admin;
import com.pinkproject.user.SessionUser;
import com.pinkproject.user.User;


import java.util.Date;

public class JwtUtil {

    public static String SECRET_KEY = "default-secret";
    // user 토큰 생성
    public static String create(User user) {

        String jwt = JWT.create()
                .withSubject("stay")
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 365L)) // 1년간 지속
                .withClaim("id", user.getId())
                .withClaim("email", user.getEmail())
                .sign(Algorithm.HMAC512("usertoken")); // 대칭키 사용 나중에 yeoeotteohno 이라 적은 자리에 환경 변수를 넣는다 OS 의 값을 땡겨와야한다!
        return jwt;
    }

    // user 토큰 검증
    public static SessionUser verify(String jwt) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512("usertoken")).build().verify(jwt);
        int id = decodedJWT.getClaim("id").asInt();
        String email = decodedJWT.getClaim("email").asString();

        // 임시 세션을 이용
        return SessionUser.builder()
                .id(id)
                .email(email)
                .build();
    }

    // Admin 토큰 생성
    public static String create(Admin admin) {
        String jwt = JWT.create()
                .withSubject("admin")
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 365L)) // 1년간 지속
                .withClaim("id", admin.getId())
                .sign(Algorithm.HMAC512("admintoken"));
        return jwt;
    }
}