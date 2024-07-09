package com.pinkproject._core.error.exception;

import com.pinkproject._core.utils.ApiUtil;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 서버 에러
public class Exception500 extends RuntimeException {

    public Exception500(String msg) {
        super(msg);
    }
}