package com.pinkproject._core.error.exception;


import com.pinkproject._core.utils.ApiUtil;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 권한 없음
public class Exception403 extends RuntimeException {

    public Exception403(String msg) {
        super(msg);
    }
}