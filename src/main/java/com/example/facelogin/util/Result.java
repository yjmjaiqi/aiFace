package com.example.facelogin.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }
    public static <T> Result<T> err(T data) {
        return new Result(404, "error", data);
    }
    public static <T> Result<T> fail(T data) {
        return new Result(500, "fail", data);
    }
}

