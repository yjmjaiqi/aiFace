package com.example.facelogin.service.impl;

import com.example.facelogin.service.LoginRegister;
import com.example.facelogin.util.HttpUtil;
import com.example.facelogin.util.Result;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LoginRegisterImpl implements LoginRegister {
    /**
     * 1、获取图片读取人脸、判断人脸是否有效detect
     * 1.1、包含
     *  比对人脸信息search，
     *  比对成功：
     *      登录成功
     *  比对失败：
     *      登录失败
     * 1.2、不包含
     *  登陆失败，删除照片
     */
    @Override
    public Result login(String imgUrl) throws Exception {
        String face_token = HttpUtil.detect(imgUrl); // 检测人脸信息
        if(!face_token.isEmpty()){
            // Path png = Paths.get(imgUrl);
            // // 删除文件
            // Files.delete(png);
            boolean search = HttpUtil.search(face_token); // 从人脸库搜索人脸信息
            if(search){
                return Result.success("登录成功");
            }
        }
        return Result.err("登录失败");
    }

    @Override
    public Result register(String imgUrl) throws Exception {
        String face_token = HttpUtil.detect(imgUrl); // 检测人脸信息
        if(!face_token.isEmpty()){
            // Path png = Paths.get(imgUrl);
            // // 删除文件
            // Files.delete(png);
            boolean search = HttpUtil.search(face_token); // 从人脸库搜索人脸信息
            if(search){
                return Result.err("已注册");
            }
            boolean detail = HttpUtil.getDetail(); // 从人脸库获取信息
            if(detail){ // 获取则人脸库已存在，直接添加人脸
                boolean addFace = HttpUtil.addFace(face_token); // 往人脸库添加人脸信息
                if(addFace){
                    return Result.success("注册成功");
                }
            }else { // 人脸库不存在，先创建，在添加人脸
                boolean faceSet = HttpUtil.createFaceSet();
                if(faceSet){
                    boolean addFace = HttpUtil.addFace(face_token); // 往人脸库添加人脸信息
                    if(addFace){
                        return Result.success("注册成功");
                    }
                }
            }
        }
        return Result.err("注册失败");
    }
}
