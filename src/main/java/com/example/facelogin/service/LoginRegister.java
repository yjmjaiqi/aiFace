package com.example.facelogin.service;

import com.example.facelogin.util.Result;

public interface LoginRegister {
    Result login(String imgUrl) throws Exception;

    Result register(String imgUrl) throws Exception;
}
