package com.pinkproject._core.error.exception;


import com.pinkproject._core.utils.ApiUtil;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 인증 안됨
public class Exception401 extends RuntimeException {

    public Exception401(String msg) {
        super(msg);
    }
}