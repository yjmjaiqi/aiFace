package com.example.facelogin.controller;

import com.example.facelogin.constant.Face;
import com.example.facelogin.service.LoginRegister;
import com.example.facelogin.util.HttpUtil;
import com.example.facelogin.util.Result;
import com.example.facelogin.util.RootPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.Base64;

@RestController
@RequestMapping("/face")
public class Login {
    @Autowired
    private LoginRegister loginRegister;

    @PostMapping("/login")
    public Result login(@RequestBody String base64Png) throws Exception {
        if (base64Png.isEmpty()) {
            return Result.err("登录失败");
        }
        // 去除data URL部分，只保留base64编码的图像数据
        String imageData = base64Png.replaceFirst("data:image/.*;base64,", "");
        // 解码base64数据并保存为图像文件
        String path = RootPath.getRootPath("png");
        try {
            byte[] imageBytes = Base64.getDecoder().decode(imageData);

            OutputStream stream = new FileOutputStream(path); // 更改为保存图像的路径
            stream.write(imageBytes);
            stream.close();
            // 在此可以对图像文件进行进一步处理或其他操作
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loginRegister.login(path);
    }


    @PostMapping("/register")
    public Result register(@RequestBody String base64Png) throws Exception {
        if (base64Png.isEmpty()) {
            return Result.err("注册失败");
        }
        // 去除data URL部分，只保留base64编码的图像数据
        String imageData = base64Png.replaceFirst("data:image/.*;base64,", "");
        String path = RootPath.getRootPath("png");
        // 解码base64数据并保存为图像文件
        try {
            byte[] imageBytes = Base64.getDecoder().decode(imageData);
            OutputStream stream = new FileOutputStream(path); // 更改为保存图像的路径
            stream.write(imageBytes);
            stream.close();
            // 在此可以对图像文件进行进一步处理或其他操作
        } catch (IOException e) {
            e.printStackTrace();
            return Result.err("登录失败");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginRegister.register(path);
    }
}
