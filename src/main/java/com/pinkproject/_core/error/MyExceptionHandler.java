package com.pinkproject._core.error;


import com.pinkproject._core.error.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
// RuntimeException이 터지면 해당 파일로 오류가 모인다
@ControllerAdvice // 데이터 응답
@RestController
public class MyExceptionHandler {

    @ExceptionHandler(Exception400.class)
    public ResponseEntity<Map<String, String>> ex400(Exception400 e, HttpServletRequest request) {
        log.warn("400 : " + e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("msg", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception401.class)
    public ResponseEntity<Map<String, String>> ex401(Exception401 e, HttpServletRequest request) {
        log.warn("401 : " + e.getMessage()); // ex) 로그인 실패 다이렉트 메세지 [위험도는 낮지만 주의해야 하는 점이 있다 : 강제로 접속하는 인원이 발생]
        log.warn("IP : " + request.getRemoteAddr()); // 누군지 IP 확인
        log.warn("Agent : " + request.getHeader("User-Agent")); // 장비 확인
        Map<String, String> response = new HashMap<>();
        response.put("msg", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception403.class)
    public ResponseEntity<Map<String, String>> ex403(Exception403 e, HttpServletRequest request) {
        log.warn("403 : " + e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("msg", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception404.class)
    public ResponseEntity<Map<String, String>> ex404(Exception404 e, HttpServletRequest request) {
        log.info("404 : " + e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("msg", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception500.class)
    public ResponseEntity<Map<String, String>> ex500(Exception500 e, HttpServletRequest request) {
        log.error("500 : " + e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("msg", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> unknownServerError(Exception e, HttpServletRequest request) {
        log.error("500 : " + e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("msg", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}