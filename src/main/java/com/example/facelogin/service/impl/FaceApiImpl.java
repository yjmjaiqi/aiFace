package com.example.facelogin.service.impl;

import com.example.facelogin.service.FaceApi;
import com.example.facelogin.util.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class FaceApiImpl implements FaceApi {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(FaceApiImpl.class.getName());
    @Scheduled(cron = "0 */10 * * * *") // 每十分钟执行一次，清空static/images路径下的所以图片
    public void deleteImg() {
        String uploadPath = System.getProperty("user.dir");
        uploadPath += "\\src\\main\\resources\\static\\images";
        File imagesFolder = new File(uploadPath);
        if (imagesFolder.exists() && imagesFolder.isDirectory()) {
            File[] files = imagesFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        if (file.delete()) {
                            LOGGER.info("Deleted: " + file.getName());
                        } else {
                            LOGGER.warning("Failed to delete: " + file.getName());
                        }
                    }
                }
            }
        }
    }
    @Override
    public Result detect(String imgUrl) throws Exception {
        Map<String, Object> detect = HttpUtilFunction.detect(imgUrl);
        if(!Objects.isNull(detect) && detect.containsKey("error")){
            return Result.err(detect.get("error"));
        }
        if(!Objects.isNull(detect)){
            try {
                BufferedImage image = ImageIO.read(new File(imgUrl));

                // Parse face rectangle information
                int width = (int) detect.get("width");
                int top = (int) detect.get("top");
                int left = (int) detect.get("left");
                int height = (int) detect.get("height");

                // 在图片上画矩形框
                Graphics2D g2d = image.createGraphics();
                g2d.setColor(Color.RED); // You can choose any color you prefer
                g2d.setStroke(new BasicStroke(2)); // 根据需要调整笔划宽度
                g2d.drawRect(left, top, width, height);
                g2d.dispose();
                String outputImagePath = RootPath.getRootPath("png"); // Replace with the desired output path
                ImageIO.write(image, "jpg", new File(outputImagePath));
                File file = new File(outputImagePath);
                // Get the name of the file
                String fileName = file.getName();
                LOGGER.info("Face rectangle drawn and saved successfully.");
                Files.delete(Paths.get(imgUrl));
                detect.put("imgUrl","http://"+ LocalIp.ip() +":8081/images/"+fileName);
                return Result.success(detect);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Result.err("未检测到人脸");
    }

    @Override
    public Result compare(String face_token1, String face_token2) throws Exception {
        String compare = HttpUtilFunction.compare(face_token1, face_token2);
        if(!compare.isEmpty()){
            return Result.success(compare);
        }
        return Result.err("人脸比对失败");
    }

    @Override
    public Result faceBeauty(String imgUrl,String whitening,String smoothing) throws Exception {
        String face_token = HttpUtil.detect(imgUrl);
        if(!face_token.isEmpty()){
            String base64Png = HttpUtilFunction.faceBeauty(imgUrl,whitening,smoothing);
            Files.delete(Paths.get(imgUrl));
            // 解码base64数据并保存为图像文件
            String path = RootPath.getRootPath("png");
            try {
                byte[] imageBytes = Base64.getDecoder().decode(base64Png);

                OutputStream stream = new FileOutputStream(path); // 更改为保存图像的路径
                stream.write(imageBytes);
                stream.close();
                String[] split = path.split("\\\\");
                return Result.success("http://"+ LocalIp.ip() +":8081/images/" + split[split.length - 1]);
                // 在此可以对图像文件进行进一步处理或其他操作
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.err("未检测到人脸");
    }

    @Override
    public Result faceBeautification(String imgUrl,String whitening,String smoothing,String thinface,String shrink_face,
                                     String enlarge_eye, String remove_eyebrow,String filter_type) throws Exception {
        String face_token = HttpUtil.detect(imgUrl);
        if(!face_token.isEmpty()){
            String base64Png = HttpUtilFunction.faceBeautification(imgUrl,whitening,smoothing,thinface,shrink_face,enlarge_eye,
                    remove_eyebrow,filter_type);
            Files.delete(Paths.get(imgUrl));
            // 解码base64数据并保存为图像文件
            String path = RootPath.getRootPath("png");
            try {
                byte[] imageBytes = Base64.getDecoder().decode(base64Png);

                OutputStream stream = new FileOutputStream(path); // 更改为保存图像的路径
                stream.write(imageBytes);
                stream.close();
                String[] split = path.split("\\\\");
                return Result.success("http://"+ LocalIp.ip() +":8081/images/" + split[split.length - 1]);
                // 在此可以对图像文件进行进一步处理或其他操作
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.err("未检测到人脸");
    }

    @Override
    public Result addFace(String imgUrl) throws Exception {
        String face_token = HttpUtil.detect(imgUrl);
        Files.delete(Paths.get(imgUrl));
        if(!face_token.isEmpty()){
            boolean search = HttpUtil.search(face_token); // 从人脸库搜索人脸信息
            if(search){
                return Result.err("人脸已添加");
            }
            boolean addFace = HttpUtil.addFace(face_token); // 往人脸库添加人脸信息
            if(addFace){
                return Result.success("人脸添加成功");
            }
        }
        return Result.err("人脸添加失败");
    }

    @Override
    public Result searchFace(String imgUrl) throws Exception {
        String face_token = HttpUtil.detect(imgUrl);
        if(!face_token.isEmpty()){
            Map<String, Object> search = HttpUtilFunction.search(face_token);
            if(!Objects.isNull(search) && search.containsKey("ok")){
                return Result.success(search);
            }
        }
        return Result.err("人脸搜索失败");
    }

    @Override
    public Result skinAnalysis(String imgUrl) throws Exception {
        String face_token = HttpUtil.detect(imgUrl);
        if(!face_token.isEmpty()){
            Map<String, Object> map = HttpUtilFunction.skinAnalysis(imgUrl);
            if(!Objects.isNull(map)){
                BufferedImage image = ImageIO.read(new File(imgUrl));

                // Parse face rectangle information
                int width = (int) map.get("width");
                int top = (int) map.get("top");
                int left = (int) map.get("left");
                int height = (int) map.get("height");

                // 在图片上画矩形框
                Graphics2D g2d = image.createGraphics();
                g2d.setColor(Color.RED); // You can choose any color you prefer
                g2d.setStroke(new BasicStroke(2)); // 根据需要调整笔划宽度
                g2d.drawRect(left, top, width, height);
                g2d.dispose();
                String outputImagePath = RootPath.getRootPath("png"); // Replace with the desired output path
                ImageIO.write(image, "jpg", new File(outputImagePath));
                File file = new File(outputImagePath);
                // Get the name of the file
                String fileName = file.getName();
                LOGGER.info("Face rectangle drawn and saved successfully.");
                Files.delete(Paths.get(imgUrl));
                map.put("imgUrl","http://"+ LocalIp.ip() +":8081/images/"+fileName);
                return Result.success(map);
            }
        }
        return Result.err("未检测到人脸皮肤");
    }

    @Override
    public Result gestureRecognition(String imgUrl) throws Exception {
        Map<String, Object> gesture = HttpUtilFunction.gesture(imgUrl);
        if(!Objects.isNull(gesture)){
            BufferedImage image = ImageIO.read(new File(imgUrl));
            // Parse face rectangle information
            int width = (int) gesture.get("width");
            int top = (int) gesture.get("top");
            int left = (int) gesture.get("left");
            int height = (int) gesture.get("height");

            // 在图片上画矩形框
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.RED); // You can choose any color you prefer
            g2d.setStroke(new BasicStroke(2)); // 根据需要调整笔划宽度
            g2d.drawRect(left, top, width, height);
            Font font = new Font("SimSun", Font.BOLD, 100);  // 根据需要选择字体和大小
            g2d.setFont(font);
            g2d.setColor(Color.RED);  // 选择文字颜色
            LOGGER.info("gesture： " + gesture.get("gesture").toString());
            // 获取字体测量对象以获取文字宽度和高度
            FontMetrics fontMetrics = g2d.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(gesture.get("gesture").toString());
            int textHeight = fontMetrics.getHeight();

            // 计算文字位置，使其居中在矩形框中
            int textX = left + (width - textWidth) / 2;
            int textY = top + (height - textHeight) / 2 + fontMetrics.getAscent();  // 使用getAscent()来处理基线偏移

            g2d.drawString(gesture.get("gesture").toString(), textX, textY);// 调整位置
            g2d.dispose();
            String outputImagePath = RootPath.getRootPath("png"); // Replace with the desired output path
            ImageIO.write(image, "jpg", new File(outputImagePath));
            File file = new File(outputImagePath);
            // Get the name of the file
            String fileName = file.getName();
            LOGGER.info("gesture rectangle drawn and saved successfully.");
            Files.delete(Paths.get(imgUrl));
            // gesture.put("imgUrl","http://localhost:8081/images/"+fileName);
            return Result.success("http://"+ LocalIp.ip() +":8081/images/"+fileName);
        }
        return Result.err("未识别到手势");
    }

    @Override
    public Result wordsRecognition(String imgUrl) throws Exception {
        String words = HttpUtilFunction.words(imgUrl);
        if(!words.isEmpty()){
            return Result.success(words);
        }
        return Result.err("文字识别失败");
    }
}
