package com.example.facelogin.service;

import com.example.facelogin.util.Result;

public interface FaceApi {
    // 人脸检测
    Result detect(String imgUrl) throws Exception;
    // 人脸对比
    Result compare(String face_token1,String face_token2) throws Exception;
    // 人脸美颜
    Result faceBeauty(String imgUrl,String whitening,String smoothing) throws Exception;
    // 人脸美化
    Result faceBeautification(String imgUrl,String whitening,String smoothing,String thinface,String shrink_face,String enlarge_eye,
                              String remove_eyebrow,String filter_type) throws Exception;
    // 人脸添加
    Result addFace(String imgUrl) throws Exception;
    // 人脸搜索
    Result searchFace(String imgUrl) throws Exception;
    // 皮肤分析
    Result skinAnalysis(String imgUrl) throws Exception;
    // 手势识别
    Result gestureRecognition(String imgUrl) throws Exception;
    // 文字识别
    Result wordsRecognition(String imgUrl) throws Exception;
}
