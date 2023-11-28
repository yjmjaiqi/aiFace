package com.example.facelogin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.facelogin.constant.Face;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class HttpUtilFunction {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(HttpUtil.class.getName());
    // 人脸检测
    public static Map<String,Object> detect(String imageUrl) throws Exception{
        // 设置API请求的URL
        String apiUrl = "https://api-cn.faceplusplus.com/facepp/v3/detect";
        String returnAttributes = "gender,age,smiling,headpose,emotion,skinstatus";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        String boundary = Long.toHexString(System.currentTimeMillis());
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        String CRLF = "\r\n";
        OutputStream output = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);

        // 添加api_key参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_key\"").append(CRLF);
        writer.append(CRLF).append(Face.API_KEY).append(CRLF).flush();

        // 添加api_secret参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_secret\"").append(CRLF);
        writer.append(CRLF).append(Face.API_SECRET).append(CRLF).flush();

        // 添加image_file参数
        writer.append("--" + boundary).append(CRLF);
        File file = new File(imageUrl);

        // Get the name of the file
        String fileName = file.getName();
        String[] split = fileName.split("\\.");
        LOGGER.info("fileName: " + fileName);
        LOGGER.info("split[1]: " + split[1]);
        writer.append("Content-Disposition: form-data; name=\"image_file\"; filename="+fileName).append(CRLF);
        writer.append("Content-Type: image/"+split[1]).append(CRLF);
        writer.append(CRLF).flush();
        Files.copy(Paths.get(imageUrl), output);
        output.flush();
        writer.append(CRLF).flush();

        // 添加return_attributes参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"return_attributes\"").append(CRLF);
        writer.append(CRLF).append(returnAttributes).append(CRLF).flush();
        writer.append("--" + boundary + "--").append(CRLF).flush();


        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            String responseBody = response.toString();
            System.out.println("Response: " + responseBody);
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            Integer face_num = jsonObject.getInteger("face_num");
            JSONArray faces = jsonObject.getJSONArray("faces");
            if(face_num > 0){
                Object face_rectangle = faces.getJSONObject(0).get("face_rectangle");
                Object attributes = faces.getJSONObject(0).get("attributes");
                // 获取第一个face_token的值
                if (!Objects.isNull(face_rectangle) && !Objects.isNull(attributes)) {
                    Map<String, Object> map = new HashMap<>();
                    JSONObject object = JSONObject.parseObject(face_rectangle.toString());
                    JSONObject parseObject = JSONObject.parseObject(attributes.toString());
                    Object gender = parseObject.getJSONObject("gender").get("value");
                    Object age = parseObject.getJSONObject("age").get("value");
                    double threshold = parseObject.getJSONObject("smile").getDouble("threshold");
                    double value = parseObject.getJSONObject("smile").getDouble("value");
                    JSONObject skinstatus = parseObject.getJSONObject("skinstatus");
                    Double dark_circle = skinstatus.getDouble("dark_circle");
                    Double acne = skinstatus.getDouble("acne");
                    Double health = skinstatus.getDouble("health");
                    Double stain = skinstatus.getDouble("stain");
                    map.put("dark_circle",dark_circle);
                    map.put("acne",acne);
                    map.put("health",health);
                    map.put("stain",stain);
                    JSONObject emotion = parseObject.getJSONObject("emotion");
                    Double surprise = emotion.getDouble("surprise");
                    Double happiness = emotion.getDouble("happiness");
                    Double neutral = emotion.getDouble("neutral");
                    Double sadness = emotion.getDouble("sadness");
                    Double disgust = emotion.getDouble("disgust");
                    Double anger = emotion.getDouble("anger");
                    Double fear = emotion.getDouble("fear");
                    map.put("surprise",surprise);
                    map.put("happiness",happiness);
                    map.put("neutral",neutral);
                    map.put("sadness",sadness);
                    map.put("disgust",disgust);
                    map.put("anger",anger);
                    map.put("fear",fear);
                    JSONObject headpose = parseObject.getJSONObject("headpose");
                    Double yaw_angle = headpose.getDouble("yaw_angle");
                    Double roll_angle = headpose.getDouble("roll_angle");
                    Double pitch_angle = headpose.getDouble("pitch_angle");
                    map.put("yaw_angle",yaw_angle);
                    map.put("roll_angle",roll_angle);
                    map.put("pitch_angle",pitch_angle);
                    if(value > threshold){
                        map.put("smile","有笑容");
                    }else {
                        map.put("smile","无笑容");
                    }
                    LOGGER.info("gender: " + gender);
                    LOGGER.info("attributes: " + attributes);
                    Integer width = object.getInteger("width");
                    Integer top = object.getInteger("top");
                    Integer left = object.getInteger("left");
                    Integer height = object.getInteger("height");
                    // System.out.println("Face Token: " + faceToken);
                    LOGGER.info("Face width: " + width);
                    LOGGER.info("Face top: " + top);
                    LOGGER.info("Face left: " + left);
                    LOGGER.info("Face height: " + height);
                    map.put("width",width);
                    map.put("top",top);
                    map.put("left",left);
                    map.put("height",height);
                    map.put("gender",gender);
                    map.put("age",age);
                    connection.disconnect();
                    return map;
                }
            }
        }else if(responseCode == 400){
            Map<String,Object> map = new HashMap<>();
            map.put("error","图像文件、或有数据破损、或图片文件格式不符合要求");
            return map;
        }else {
            LOGGER.warning("Error: " + responseCode);
            Files.delete(Paths.get(imageUrl));
        }

        connection.disconnect();
        return null;
    }

    // 人脸搜索
    public static Map<String,Object> search(String face_token) throws Exception{
        // 设置API请求的URL
        String apiUrl = "https://api-cn.faceplusplus.com/facepp/v3/search";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";

        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (OutputStream output = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)) {

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"api_key\"").append(CRLF).append(CRLF);
            writer.append(Face.API_KEY).append(CRLF);

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"api_secret\"").append(CRLF).append(CRLF);
            writer.append(Face.API_SECRET).append(CRLF);

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"face_token\"").append(CRLF).append(CRLF);
            writer.append(face_token).append(CRLF);

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"outer_id\"").append(CRLF).append(CRLF);
            writer.append(Face.OUTER_ID).append(CRLF);

            writer.append("--" + boundary + "--").append(CRLF);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Map<String, Object> map = new HashMap<>();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject jsonObject = JSONObject.parseObject(response.toString());
            // 提取confidence和1e-5的值
            double confidence = jsonObject.getJSONArray("results").getJSONObject(0).getDouble("confidence");
            String face_token1 = jsonObject.getJSONArray("results").getJSONObject(0).getString("face_token");
            double thresholds_1e_3 = jsonObject.getJSONObject("thresholds").getDouble("1e-3");
            double thresholds_1e_4 = jsonObject.getJSONObject("thresholds").getDouble("1e-4");
            double thresholds_1e_5 = jsonObject.getJSONObject("thresholds").getDouble("1e-5");

            LOGGER.info("confidence = " + confidence);
            LOGGER.info("face_token = " + face_token1);
            LOGGER.info("thresholds_1e_3 = " + thresholds_1e_3);
            LOGGER.info("thresholds_1e_4 = " + thresholds_1e_4);
            LOGGER.info("thresholds_1e_5 = " + thresholds_1e_5);
            LOGGER.info("response = " + response);
            map.put("confidence",confidence);
            map.put("face_token",face_token);
            map.put("thresholds_1e_3",thresholds_1e_3);
            map.put("thresholds_1e_4",thresholds_1e_4);
            map.put("thresholds_1e_5",thresholds_1e_5);
            if(confidence > thresholds_1e_5){
                map.put("ok",true);
            }
            return map;
        } else {
            LOGGER.warning("HTTP Request Error: " + responseCode);
        }
        connection.disconnect();
        return null;
    }

    // 通用文字识别
    public static String words(String imageUrl) throws Exception{
        // 设置API请求的URL
        String apiUrl = "https://api-cn.faceplusplus.com/imagepp/v2/generalocr";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        String boundary = Long.toHexString(System.currentTimeMillis());
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        String CRLF = "\r\n";
        OutputStream output = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);

        // 添加api_key参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_key\"").append(CRLF);
        writer.append(CRLF).append(Face.API_KEY).append(CRLF).flush();

        // 添加api_secret参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_secret\"").append(CRLF);
        writer.append(CRLF).append(Face.API_SECRET).append(CRLF).flush();

        // 添加image_file参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"image_file\"; filename=\"image.jpg\"").append(CRLF);
        writer.append("Content-Type: image/jpeg").append(CRLF);
        writer.append(CRLF).flush();
        Files.copy(Paths.get(imageUrl), output);
        output.flush();
        writer.append(CRLF).flush();
        writer.append("--" + boundary + "--").append(CRLF).flush();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            LOGGER.info("Response: " + response);
            // 解析 JSON
            JSONObject jsonObject = JSON.parseObject(response.toString());
            JSONArray textInfoArray = jsonObject.getJSONArray("text_info");

            String content = "";
            // 遍历每个 "text_info" 中的 "line_content" 值
            for (int i = 0; i < textInfoArray.size(); i++) {
                JSONObject textInfo = textInfoArray.getJSONObject(i);
                content += textInfo.getString("line_content") + "\r\n";
            }
            return content;
        } else {
            LOGGER.warning("Error: " + responseCode);
        }

        connection.disconnect();
        return "";
    }

    // 手势识别
    public static Map<String,Object> gesture(String imageUrl) throws Exception{
        // 设置API请求的URL
        String apiUrl = "https://api-cn.faceplusplus.com/humanbodypp/v1/gesture";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        String boundary = Long.toHexString(System.currentTimeMillis());
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        String CRLF = "\r\n";
        OutputStream output = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);

        // 添加api_key参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_key\"").append(CRLF);
        writer.append(CRLF).append(Face.API_KEY).append(CRLF).flush();

        // 添加api_secret参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_secret\"").append(CRLF);
        writer.append(CRLF).append(Face.API_SECRET).append(CRLF).flush();

        // 添加image_file参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"image_file\"; filename=\"image.jpg\"").append(CRLF);
        writer.append("Content-Type: image/jpeg").append(CRLF);
        writer.append(CRLF).flush();
        Files.copy(Paths.get(imageUrl), output);
        output.flush();
        writer.append(CRLF).flush();
        writer.append("--" + boundary + "--").append(CRLF).flush();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            LOGGER.info("Response: " + response);
            // 解析 JSON
            JSONObject jsonObject = JSON.parseObject(response.toString());
            JSONArray handsArray = jsonObject.getJSONArray("hands");
            if(!(handsArray.size() > 0)){
                return null;
            }
            // 遍历每个 "hands" 中的 "gesture" 值
            for (int i = 0; i < handsArray.size(); i++) {
                Map<String,Object> map = new HashMap<>();
                JSONObject hand = handsArray.getJSONObject(i);
                JSONObject gesture = hand.getJSONObject("gesture");
                // 如果你想将手势的值存储在变量中，可以使用以下方法
                map.put("unknown",gesture.getInteger("unknown"));
                map.put("heart_a",gesture.getInteger("heart_a"));
                map.put("heart_b",gesture.getInteger("heart_b"));
                map.put("heart_c",gesture.getInteger("heart_c"));
                map.put("heart_d",gesture.getInteger("heart_d"));
                map.put("hand_open",gesture.getInteger("hand_open"));
                map.put("index_finger_up",gesture.getInteger("index_finger_up"));
                map.put("double_finger_up",gesture.getInteger("double_finger_up"));
                map.put("namaste",gesture.getInteger("namaste"));
                map.put("ok",gesture.getInteger("ok"));
                map.put("palm_up",gesture.getInteger("palm_up"));
                map.put("fist",gesture.getInteger("fist"));
                map.put("rock",gesture.getInteger("rock"));
                map.put("thanks",gesture.getInteger("thanks"));
                map.put("thumb_down",gesture.getInteger("thumb_down"));
                map.put("thumb_up",gesture.getInteger("thumb_up"));
                map.put("victory",gesture.getInteger("victory"));
                map.put("big_v",gesture.getInteger("big_v"));
                map.put("phonecall",gesture.getInteger("phonecall"));
                map.put("beg",gesture.getInteger("beg"));
                String gestures = "";
                // 使用 Stream 和 Comparator 找到最大值的 Entry
                Optional<Map.Entry<String, Object>> maxEntry = map.entrySet()
                        .stream()
                        .max((entry1, entry2) -> {
                            // 假设值是 Comparable 的
                            Comparable<Object> value1 = (Comparable<Object>) entry1.getValue();
                            Comparable<Object> value2 = (Comparable<Object>) entry2.getValue();
                            return value1.compareTo(value2);
                        });

                Map.Entry<String, Object> stringObjectEntry = maxEntry.orElse(null);
                if(!Objects.isNull(stringObjectEntry)){
                    String key = stringObjectEntry.getKey();
                    String chineseExplanation = getChineseExplanation(key);
                    JSONObject hand_rectangle = hand.getJSONObject("hand_rectangle");
                    map.clear();
                    map.put("gesture",chineseExplanation);
                    map.put("top",hand_rectangle.getInteger("top"));
                    map.put("left",hand_rectangle.getInteger("left"));
                    map.put("width",hand_rectangle.getInteger("width"));
                    map.put("height",hand_rectangle.getInteger("height"));
                    return map;
                }
            }
        } else {
            LOGGER.warning("Error: " + responseCode);
        }

        connection.disconnect();
        return null;
    }

    // 人脸美颜
    public static String faceBeauty(String imageUrl,String whitening,String smoothing) throws Exception{
        // 设置API请求的URL
        String apiUrl = "https://api-cn.faceplusplus.com/facepp/v1/beautify";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        String boundary = Long.toHexString(System.currentTimeMillis());
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        String CRLF = "\r\n";
        OutputStream output = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);

        // 添加api_key参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_key\"").append(CRLF);
        writer.append(CRLF).append(Face.API_KEY).append(CRLF).flush();

        // 添加api_secret参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_secret\"").append(CRLF);
        writer.append(CRLF).append(Face.API_SECRET).append(CRLF).flush();

        // 添加image_file参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"image_file\"; filename=\"image.jpg\"").append(CRLF);
        writer.append("Content-Type: image/jpeg").append(CRLF);
        writer.append(CRLF).flush();
        Files.copy(Paths.get(imageUrl), output);
        output.flush();
        writer.append(CRLF).flush();

        // 添加whitening参数[0,100]
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"whitening\"").append(CRLF);
        writer.append(CRLF).append(whitening).append(CRLF).flush();

        // 添加smoothing参数[0,100]
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"smoothing\"").append(CRLF);
        writer.append(CRLF).append(smoothing).append(CRLF).flush();
        writer.append("--" + boundary + "--").append(CRLF).flush();


        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            String responseBody = response.toString();
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            Object result = jsonObject.get("result");
            return result.toString();
        }else {
            LOGGER.warning("Error: " + responseCode);
        }

        connection.disconnect();
        return null;
    }

    // 人脸美化
    public static String faceBeautification(String imageUrl,String whitening,String smoothing,String thinface,
                                            String shrink_face,String enlarge_eye,
                                            String remove_eyebrow,String filter_type) throws Exception{
        // 设置API请求的URL
        String apiUrl = "https://api-cn.faceplusplus.com/facepp/v2/beautify";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        String boundary = Long.toHexString(System.currentTimeMillis());
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        String CRLF = "\r\n";
        OutputStream output = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);

        // 添加api_key参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_key\"").append(CRLF);
        writer.append(CRLF).append(Face.API_KEY).append(CRLF).flush();

        // 添加api_secret参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_secret\"").append(CRLF);
        writer.append(CRLF).append(Face.API_SECRET).append(CRLF).flush();

        // 添加image_file参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"image_file\"; filename=\"image.jpg\"").append(CRLF);
        writer.append("Content-Type: image/jpeg").append(CRLF);
        writer.append(CRLF).flush();
        Files.copy(Paths.get(imageUrl), output);
        output.flush();
        writer.append(CRLF).flush();

        // 添加whitening参数[0,100]
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"whitening\"").append(CRLF);
        writer.append(CRLF).append(whitening).append(CRLF).flush();
        // 添加smoothing参数[0,100]
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"smoothing\"").append(CRLF);
        writer.append(CRLF).append(smoothing).append(CRLF).flush();
        // thinface[0,100]
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"thinface\"").append(CRLF);
        writer.append(CRLF).append(thinface).append(CRLF).flush();
        // shrink_face[0,100]
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"shrink_face\"").append(CRLF);
        writer.append(CRLF).append(shrink_face).append(CRLF).flush();
        // enlarge_eye[0,100]
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"enlarge_eye\t\"").append(CRLF);
        writer.append(CRLF).append(enlarge_eye	).append(CRLF).flush();
        // remove_eyebrow[0,100]
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"remove_eyebrow\"").append(CRLF);
        writer.append(CRLF).append(remove_eyebrow).append(CRLF).flush();
        // filter_type
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"filter_type\"").append(CRLF);
        writer.append(CRLF).append(filter_type).append(CRLF).flush();
        writer.append("--" + boundary + "--").append(CRLF).flush();


        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            String responseBody = response.toString();
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            Object result = jsonObject.get("result");
            LOGGER.info("Response: " + responseBody);
            return result.toString();
        }else {
            LOGGER.warning("Error: " + responseCode);
        }

        connection.disconnect();
        return null;
    }

    // 人脸比对
    public static String compare(String face_token1,String face_token2) throws Exception{
        // 设置API请求的URL
        String apiUrl = "https://api-cn.faceplusplus.com/facepp/v3/compare";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        String boundary = Long.toHexString(System.currentTimeMillis());
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        String CRLF = "\r\n";
        OutputStream output = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);

        // 添加api_key参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_key\"").append(CRLF);
        writer.append(CRLF).append(Face.API_KEY).append(CRLF).flush();

        // 添加api_secret参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_secret\"").append(CRLF);
        writer.append(CRLF).append(Face.API_SECRET).append(CRLF).flush();

        // 添加face_token1参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"face_token1\"").append(CRLF);
        writer.append(CRLF).append(face_token1).append(CRLF).flush();
        // 添加face_token2参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"face_token2\"").append(CRLF);
        writer.append(CRLF).append(face_token2).append(CRLF).flush();
        writer.append("--" + boundary + "--").append(CRLF).flush();


        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            String responseBody = response.toString();
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            double thresholds_1e_5 = jsonObject.getJSONObject("thresholds").getDouble("1e-5");
            double confidence = jsonObject.getDouble("confidence");
            LOGGER.info("thresholds_1e_5: " + thresholds_1e_5);
            LOGGER.info("confidence: " + confidence);
            LOGGER.info("Response: " + responseBody);
            if(confidence > thresholds_1e_5){
                return "人脸相似度高";
            }else {
                return "人脸不相似";
            }
        }else {
            LOGGER.warning("Error: " + responseCode);
        }

        connection.disconnect();
        return "";
    }

    // 皮肤分析
    public static Map<String,Object> skinAnalysis(String imageUrl) throws Exception{
        // 设置API请求的URL
        String apiUrl = "https://api-cn.faceplusplus.com/facepp/v1/skinanalyze";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        String boundary = Long.toHexString(System.currentTimeMillis());
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        String CRLF = "\r\n";
        OutputStream output = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);

        // 添加api_key参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_key\"").append(CRLF);
        writer.append(CRLF).append(Face.API_KEY).append(CRLF).flush();

        // 添加api_secret参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"api_secret\"").append(CRLF);
        writer.append(CRLF).append(Face.API_SECRET).append(CRLF).flush();

        // 添加image_file参数
        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"image_file\"; filename=\"image.jpg\"").append(CRLF);
        writer.append("Content-Type: image/jpeg").append(CRLF);
        writer.append(CRLF).flush();
        Files.copy(Paths.get(imageUrl), output);
        output.flush();
        writer.append(CRLF).flush();
        writer.append("--" + boundary + "--").append(CRLF).flush();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            Map<String,Object> map = new HashMap<>();
            JSONObject jsonObject = JSONObject.parseObject(response.toString());
            Integer width = jsonObject.getJSONObject("face_rectangle").getInteger("width");
            Integer top = jsonObject.getJSONObject("face_rectangle").getInteger("top");
            Integer height = jsonObject.getJSONObject("face_rectangle").getInteger("height");
            Integer left = jsonObject.getJSONObject("face_rectangle").getInteger("left");
            map.put("width",width);
            map.put("top",top);
            map.put("height",height);
            map.put("left",left);
            Integer left_eyelids = jsonObject.getJSONObject("result").getJSONObject("left_eyelids").getInteger("value");
            if(left_eyelids == 0){
                map.put("left_eyelids","单眼皮");
            }else if(left_eyelids == 1){
                map.put("left_eyelids","平行双眼皮");
            }else if(left_eyelids == 2){
                map.put("left_eyelids","扇形双眼皮");
            }
            Integer right_eyelids = jsonObject.getJSONObject("result").getJSONObject("right_eyelids").getInteger("value");
            if(right_eyelids == 0){
                map.put("right_eyelids","单眼皮");
            }else if(right_eyelids == 1){
                map.put("right_eyelids","平行双眼皮");
            }else if(right_eyelids == 2){
                map.put("right_eyelids","扇形双眼皮");
            }
            Integer eye_pouch = jsonObject.getJSONObject("result").getJSONObject("eye_pouch").getInteger("value");
            if(eye_pouch == 0){
                map.put("eye_pouch","无眼袋");
            }else if(eye_pouch == 1){
                map.put("eye_pouch","有眼袋");
            }
            Integer dark_circle = jsonObject.getJSONObject("result").getJSONObject("dark_circle").getInteger("value");
            if(dark_circle == 0){
                map.put("dark_circle","无黑眼圈");
            }else if(dark_circle == 1){
                map.put("dark_circle","有黑眼圈");
            }
            Integer forehead_wrinkle = jsonObject.getJSONObject("result").getJSONObject("forehead_wrinkle").getInteger("value");
            if(forehead_wrinkle == 0){
                map.put("forehead_wrinkle","无抬头纹");
            }else if(forehead_wrinkle == 1){
                map.put("forehead_wrinkle","有抬头纹");
            }
            Integer crows_feet = jsonObject.getJSONObject("result").getJSONObject("crows_feet").getInteger("value");
            if(crows_feet == 0){
                map.put("crows_feet","无鱼尾纹");
            }else if(crows_feet == 1){
                map.put("crows_feet","有鱼尾纹");
            }
            Integer eye_finelines = jsonObject.getJSONObject("result").getJSONObject("eye_finelines").getInteger("value");
            if(eye_finelines == 0){
                map.put("eye_finelines","无眼部细纹");
            }else if(eye_finelines == 1){
                map.put("eye_finelines","有眼部细纹");
            }
            Integer glabella_wrinkle = jsonObject.getJSONObject("result").getJSONObject("glabella_wrinkle").getInteger("value");
            if(glabella_wrinkle == 0){
                map.put("glabella_wrinkle","无眉间纹");
            }else if(glabella_wrinkle == 1){
                map.put("glabella_wrinkle","有眉间纹");
            }
            Integer nasolabial_fold = jsonObject.getJSONObject("result").getJSONObject("nasolabial_fold").getInteger("value");
            if(nasolabial_fold == 0){
                map.put("nasolabial_fold","无法令纹");
            }else if(nasolabial_fold == 1){
                map.put("nasolabial_fold","有法令纹");
            }
            Integer skin_type = jsonObject.getJSONObject("result").getJSONObject("skin_type").getInteger("skin_type");
            if(skin_type == 0){
                map.put("skin_type","油性皮肤");
            }else if(skin_type == 1){
                map.put("skin_type","干性皮肤");
            }else if(skin_type == 2){
                map.put("skin_type","中性皮肤");
            }else if(skin_type == 3){
                map.put("skin_type","混合性皮肤");
            }
            Integer pores_forehead = jsonObject.getJSONObject("result").getJSONObject("pores_forehead").getInteger("value");
            if(pores_forehead == 0){
                map.put("pores_forehead","无毛孔粗大");
            }else if(pores_forehead == 1){
                map.put("pores_forehead","有毛孔粗大");
            }
            Integer pores_left_cheek = jsonObject.getJSONObject("result").getJSONObject("pores_left_cheek").getInteger("value");
            if(pores_left_cheek == 0){
                map.put("pores_left_cheek","无毛孔粗大");
            }else if(pores_left_cheek == 1){
                map.put("pores_left_cheek","有毛孔粗大");
            }
            Integer pores_right_cheek = jsonObject.getJSONObject("result").getJSONObject("pores_right_cheek").getInteger("value");
            if(pores_right_cheek == 0){
                map.put("pores_right_cheek","无毛孔粗大");
            }else if(pores_right_cheek == 1){
                map.put("pores_right_cheek","有毛孔粗大");
            }
            Integer pores_jaw = jsonObject.getJSONObject("result").getJSONObject("pores_jaw").getInteger("value");
            if(pores_jaw == 0){
                map.put("pores_jaw","无毛孔粗大");
            }else if(pores_jaw == 1){
                map.put("pores_jaw","有毛孔粗大");
            }
            Integer blackhead = jsonObject.getJSONObject("result").getJSONObject("blackhead").getInteger("value");
            if(blackhead == 0){
                map.put("blackhead","无黑头");
            }else if(blackhead == 1){
                map.put("blackhead","有黑头");
            }
            Integer acne = jsonObject.getJSONObject("result").getJSONObject("acne").getInteger("value");
            if(acne == 0){
                map.put("acne","无痘痘");
            }else if(acne == 1){
                map.put("acne","有痘痘");
            }
            Integer mole = jsonObject.getJSONObject("result").getJSONObject("mole").getInteger("value");
            if(mole == 0){
                map.put("mole","无痣");
            }else if(mole == 1){
                map.put("mole","有痣");
            }
            Integer skin_spot = jsonObject.getJSONObject("result").getJSONObject("skin_spot").getInteger("value");
            if(skin_spot == 0){
                map.put("skin_spot","无斑点");
            }else if(skin_spot == 1){
                map.put("skin_spot","有斑点");
            }
            LOGGER.info("Response: " + response);
            return map;
        } else {
            LOGGER.warning("Error: " + responseCode);
        }

        connection.disconnect();
        return null;
    }


    // 获取中文解释
    private static String getChineseExplanation(String key) {
        Map<String, String> explanationMap = new HashMap<>();
        explanationMap.put("unknown", "未定义手势");
        explanationMap.put("heart_a", "比心 A");
        explanationMap.put("heart_b", "比心 B");
        explanationMap.put("heart_c", "比心 C");
        explanationMap.put("heart_d", "比心 D");
        explanationMap.put("ok", "OK");
        explanationMap.put("hand_open", "手张开");
        explanationMap.put("thumb_up", "大拇指向上");
        explanationMap.put("thumb_down", "大拇指向下");
        explanationMap.put("rock", "ROCK");
        explanationMap.put("namaste", "合十");
        explanationMap.put("palm_up", "手心向上");
        explanationMap.put("fist", "握拳");
        explanationMap.put("index_finger_up", "食指朝上");
        explanationMap.put("double_finger_up", "双指朝上");
        explanationMap.put("victory", "胜利");
        explanationMap.put("big_v", "大 V 字");
        explanationMap.put("phonecall", "打电话");
        explanationMap.put("beg", "作揖");
        explanationMap.put("thanks", "感谢");

        return explanationMap.get(key);
    }
}
