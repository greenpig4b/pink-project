package com.pinkproject._core.error.exception;


import com.pinkproject._core.utils.ApiUtil;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 찾을 수 없음
public class Exception404 extends RuntimeException {

    public Exception404(String msg) {
        super(msg);
    }
}