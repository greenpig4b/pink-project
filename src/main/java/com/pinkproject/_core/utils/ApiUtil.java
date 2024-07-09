package com.pinkproject._core.utils;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
public class ApiUtil<T> {
    private Integer status;
    private Boolean success;
    private T response;
    private String errorMessage;

    public ApiUtil(T response) {
        this.status = 200;
        this.success = true;
        this.response = response;
        this.errorMessage = null;
    }

    public ApiUtil(Integer status, String errorMessage) {
        this.status = status;
        this.success = false;
        this.response = null;
        this.errorMessage = errorMessage;
    }
}