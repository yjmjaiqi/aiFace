package com.example.facelogin.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.facelogin.service.FaceApi;
import com.example.facelogin.util.HttpUtil;
import com.example.facelogin.util.Result;
import com.example.facelogin.util.RootPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/face")
public class Api {
    @Autowired
    private FaceApi faceApi;

    @PostMapping("/detect")
    public Result detect(@RequestPart("image") MultipartFile file){
        if (file.isEmpty()) {
            return Result.err("未检测到图片");
        }
        try {
            String[] split = file.getOriginalFilename().split("\\.");
            String path = RootPath.getRootPath(split[1]);
            file.transferTo(new File(path));
            return faceApi.detect(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.err("人脸检测失败");
    }

    @PostMapping("/search")
    public Result search(@RequestPart("image") MultipartFile file){
        if (file.isEmpty()) {
            return Result.err("未检测到图片");
        }
        try {
            String[] split = file.getOriginalFilename().split("\\.");
            String path = RootPath.getRootPath(split[1]);
            System.out.println(path);
            file.transferTo(new File(path));
            return faceApi.searchFace(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.err("人脸搜索失败");
    }

    @PostMapping("/words")
    public Result words(@RequestPart("image") MultipartFile file){
        if (file.isEmpty()) {
            return Result.err("未检测到图片");
        }
        try {
            String[] split = file.getOriginalFilename().split("\\.");
            String path = RootPath.getRootPath(split[1]);
            System.out.println(path);
            file.transferTo(new File(path));
            return faceApi.wordsRecognition(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.err("文字识别失败");
    }

    @PostMapping("/gesture")
    public Result gesture(@RequestPart("image") MultipartFile file){
        if (file.isEmpty()) {
            return Result.err("未检测到图片");
        }
        try {
            String[] split = file.getOriginalFilename().split("\\.");
            String path = RootPath.getRootPath(split[1]);
            System.out.println(path);
            file.transferTo(new File(path));
            return faceApi.gestureRecognition(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.err("手势识别失败");
    }

    @PostMapping("/addFace")
    public Result addFace(@RequestPart("image") MultipartFile file){
        if (file.isEmpty()) {
            return Result.err("未检测到图片");
        }
        try {
            String[] split = file.getOriginalFilename().split("\\.");
            String path = RootPath.getRootPath(split[1]);
            System.out.println(path);
            file.transferTo(new File(path));
            return faceApi.addFace(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.err("人脸添加失败");
    }

    @PostMapping("/faceBeauty")
    public Result faceBeauty(@RequestPart("image") MultipartFile file,@RequestPart("whitening") String whitening
            ,@RequestPart("smoothing") String smoothing){
        if (file.isEmpty()) {
            return Result.err("未检测到图片");
        }
        try {
            String[] split = file.getOriginalFilename().split("\\.");
            String path = RootPath.getRootPath(split[1]);
            System.out.println(path);
            file.transferTo(new File(path));
            return faceApi.faceBeauty(path,whitening,smoothing);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.err("人脸美颜失败");
    }

    @PostMapping("/faceBeautification")
    public Result faceBeautification(@RequestPart("image") MultipartFile file,@RequestPart("whitening") String whitening
            ,@RequestPart("smoothing") String smoothing,@RequestPart("thinface") String thinface,@RequestPart("shrink_face") String shrink_face,
                                     @RequestPart("enlarge_eye") String enlarge_eye,@RequestPart("remove_eyebrow") String remove_eyebrow,
                                     @RequestPart("filter_type") String filter_type){
        if (file.isEmpty()) {
            return Result.err("未检测到图片");
        }
        try {
            String[] split = file.getOriginalFilename().split("\\.");
            String path = RootPath.getRootPath(split[1]);
            System.out.println(path);
            file.transferTo(new File(path));
            return faceApi.faceBeautification(path,whitening,smoothing,thinface,shrink_face,enlarge_eye,remove_eyebrow,filter_type);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.err("人脸美化失败");
    }

    @PostMapping("/compare")
    public Result compare(@RequestPart("face_token1") String face_token1,
                          @RequestPart("face_token2") String face_token2) throws Exception {
        return faceApi.compare(face_token1,face_token2);
    }
    @PostMapping("/face_token1")
    public Result face_token1(@RequestPart("image") MultipartFile file){
        if (file.isEmpty()) {
            return Result.err("未检测到图片");
        }
        try {
            String[] split = file.getOriginalFilename().split("\\.");
            String path = RootPath.getRootPath(split[1]);
            file.transferTo(new File(path));
            String face_token = HttpUtil.detect(path);
            String imgUrl = "";
            if(!face_token.isEmpty()){
                Map<String,Object> map = new HashMap<>();
                // 使用正则表达式提取 imgUrl 的值
                Pattern pattern = Pattern.compile("imgUrl=([^,}]*)");
                Matcher matcher = pattern.matcher(faceApi.detect(path).getData().toString());
                if (matcher.find()) {
                    imgUrl = matcher.group(1);
                    System.out.println("imgUrl value: " + imgUrl);
                }
                map.put("imgUrl",imgUrl);
                map.put("face_Token",face_token);
                return Result.success(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.err("未检测到人脸");
    }
    @PostMapping("/face_token2")
    public Result face_token2(@RequestPart("image") MultipartFile file){
        if (file.isEmpty()) {
            return Result.err("未检测到图片");
        }
        try {
            String[] split = file.getOriginalFilename().split("\\.");
            String path = RootPath.getRootPath(split[1]);
            file.transferTo(new File(path));
            String face_token = HttpUtil.detect(path);
            String imgUrl = "";
            if(!face_token.isEmpty()){
                Map<String,Object> map = new HashMap<>();
                // 使用正则表达式提取 imgUrl 的值
                Pattern pattern = Pattern.compile("imgUrl=([^,}]*)");
                Matcher matcher = pattern.matcher(faceApi.detect(path).getData().toString());
                if (matcher.find()) {
                    imgUrl = matcher.group(1);
                    System.out.println("imgUrl value: " + imgUrl);
                }
                map.put("imgUrl",imgUrl);
                map.put("face_Token",face_token);
                return Result.success(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.err("未检测到人脸");
    }

    @PostMapping("/skinAnalysis")
    public Result skinAnalysis(@RequestPart("image") MultipartFile file){
        if (file.isEmpty()) {
            return Result.err("未检测到图片");
        }
        try {
            String[] split = file.getOriginalFilename().split("\\.");
            String path = RootPath.getRootPath(split[1]);
            System.out.println(path);
            file.transferTo(new File(path));
            return faceApi.skinAnalysis(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.err("皮肤分析失败");
    }
}
