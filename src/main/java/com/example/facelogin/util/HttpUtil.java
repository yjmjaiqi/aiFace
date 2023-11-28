package com.example.facelogin.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.facelogin.constant.Face;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class HttpUtil {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(HttpUtil.class.getName());
    // 人脸检测
    public static String detect(String imageUrl) throws Exception{
        // 设置API请求的URL
        String apiUrl = "https://api-cn.faceplusplus.com/facepp/v3/detect";

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
        // 删除图片
        // Files.delete(Paths.get(imageUrl));
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            String responseBody = response.toString();
            // System.out.println("Response: " + responseBody);
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            JSONArray faces = jsonObject.getJSONArray("faces");
            // 获取第一个face_token的值
            if (faces.size() > 0) {
                JSONObject firstFace = faces.getJSONObject(0);
                String faceToken = firstFace.getString("face_token");
                // System.out.println("Face Token: " + faceToken);
                LOGGER.info("Face Token: " + faceToken);

                connection.disconnect();
                return faceToken;
            }
            // 解析 JSON 响应并提取 face_token 的值
            // 你可以使用 JSON 解析库（比如 org.json）来提取 face_token 的值
        } else {
            LOGGER.warning("Error: " + responseCode);
        }

        connection.disconnect();
        return "";
    }

    // 人脸搜索
    public static boolean search(String face_token) throws Exception{
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
            double thresholds_1e_5 = jsonObject.getJSONObject("thresholds").getDouble("1e-5");

            // System.out.println("confidence = " + confidence);
            // System.out.println("thresholds_1e_5 = " + thresholds_1e_5);
            LOGGER.info("confidence = " + confidence);
            LOGGER.info("thresholds_1e_5 = " + thresholds_1e_5);
            LOGGER.info("response = " + response);
            if(confidence >= thresholds_1e_5){;
                return true;
            }
        } else {
            LOGGER.warning("HTTP Request Error: " + responseCode);
        }
        connection.disconnect();
        return false;
    }

    // 添加人脸（异步）
    public static boolean addFace(String face_token) throws Exception{
        // 设置API请求的URL
        String apiUrl = "https://api-cn.faceplusplus.com/facepp/v3/faceset/async/addface";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String data = "api_key=" + Face.API_KEY +
                "&api_secret=" + Face.API_SECRET +
                "&outer_id=" + Face.OUTER_ID +
                "&face_tokens=" + face_token;
        byte[] postData = data.getBytes(StandardCharsets.UTF_8);

        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.write(postData);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // System.out.println("addFace-Response: " + response);
            LOGGER.info("addFace-Response: " + response);
            return true;
        } else {
            LOGGER.warning("addFace-HTTP Request Error: " + responseCode);
        }
        connection.disconnect();
        return false;
    }

    // 创建人脸库
    public static boolean createFaceSet() throws Exception{
        // 设置API请求的URL
        String apiUrl = "https://api-cn.faceplusplus.com/facepp/v3/faceset/create";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String data = "api_key=" + Face.API_KEY + "&api_secret=" + Face.API_SECRET +"&display_name=" + Face.DISPLAY_NAME + "&outer_id=" + Face.OUTER_ID;
        byte[] postData = data.getBytes(StandardCharsets.UTF_8);

        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.write(postData);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // System.out.println("createFaceSet-Response: " + response);
            LOGGER.info("createFaceSet-Response: " + response);
            return true;
        } else {
            LOGGER.warning("createFaceSet-HTTP Request Error: " + responseCode);
        }
        connection.disconnect();
        return false;
    }

    // 获取人脸库信息
    public static boolean getDetail() throws Exception{
        // 设置API请求的URL
        String apiUrl = "https://api-cn.faceplusplus.com/facepp/v3/faceset/getdetail";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String data = "api_key=" + Face.API_KEY +
                "&api_secret=" + Face.API_SECRET +
                "&outer_id=" + Face.OUTER_ID;
        byte[] postData = data.getBytes(StandardCharsets.UTF_8);

        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.write(postData);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // System.out.println("getDetail-Response: " + response);
            LOGGER.info("getDetail-Response: " + response);
            return true;
        } else {
            LOGGER.warning("getDetail-HTTP Request Error: " + responseCode);
        }
        connection.disconnect();
        return false;
    }
}
