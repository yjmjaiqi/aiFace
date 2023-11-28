package com.example.facelogin;

import com.example.facelogin.util.HttpUtil;
import com.example.facelogin.util.HttpUtilFunction;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class TestDemo {
    private static final Logger LOGGER = Logger.getLogger(TestDemo.class.getName());
    public static void main(String[] args) throws Exception {
        String imageUrl = "C:\\Users\\DELL\\Pictures\\Saved Pictures\\yjm.jpg";  // 替换为你的图像文件路径
        String faceToken = HttpUtil.detect(imageUrl);
        if(!faceToken.equals("")){
            System.out.println("Face Token: " + faceToken);
        }
        // boolean faceSet = HttpUtil.createFaceSet();
        // System.out.println(faceSet);
        LOGGER.info(faceToken);
    }

    @Test
    public void detect() throws Exception {
        String imageUrl = "C:\\Users\\DELL\\Pictures\\Saved Pictures\\yjm2.jpg";
        Map<String, Object> detect = HttpUtilFunction.detect(imageUrl);
        if(!Objects.isNull(detect)){
            System.out.println(detect);
        }
        try {
            File file = new File(imageUrl);

            // Get the name of the file
            String fileName = file.getName();
            System.out.println(fileName);
            // BufferedImage image = ImageIO.read(new File(imageUrl));
            //
            // // Parse face rectangle information
            // int width = detect.get("width");
            // int top = detect.get("top");
            // int left = detect.get("left");
            // int height = detect.get("height");
            //
            // // 在图片上画矩形框
            // Graphics2D g2d = image.createGraphics();
            // g2d.setColor(Color.RED); // You can choose any color you prefer
            // g2d.setStroke(new BasicStroke(2)); // 根据需要调整笔划宽度
            // g2d.drawRect(left, top, width, height);
            // g2d.dispose();
            //
            // // Save the modified image
            // String outputImagePath = "C:\\Users\\DELL\\Pictures\\Saved Pictures\\yjm2"+"-detect"+".jpg"; // Replace with the desired output path
            // ImageIO.write(image, "jpg", new File(outputImagePath));
            //
            // System.out.println("Face rectangle drawn and saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void imgPath(){
        String currentWorkingDir = System.getProperty("user.dir");
        currentWorkingDir += "\\src\\main\\resources\\static\\images\\"+"a.png";
        System.out.println(currentWorkingDir);
        // 删除图片
        try {
            Path path = Paths.get(currentWorkingDir);
            System.out.println(path);
            // System.out.println(!Objects.isNull());
            // 删除文件
            // Files.delete(path);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("图片删除失败: " + e.getMessage());
        }
    }
    @Test
    public void searchFace() throws Exception {
        String face_token = HttpUtil.detect("C:\\Users\\DELL\\Pictures\\Saved Pictures\\yjm2.jpg");
        if(!face_token.isEmpty()){
            Map<String, Object> map = HttpUtilFunction.search(face_token);
        }
    }
    @Test
    public void words() throws Exception {
        String words = HttpUtilFunction.words("C:\\Users\\DELL\\Pictures\\Saved Pictures\\wz.png");
        if(!words.isEmpty()){
            LOGGER.info("content: " + words);
        }
    }

    @Test
    public void gesture() throws Exception {
        Map<String, Object> gesture = HttpUtilFunction.gesture("C:\\Users\\DELL\\Pictures\\手势\\9567195e88dc3aed044f2f8f39ee3dc.jpg");
        if(!Objects.isNull(gesture)){
            LOGGER.info("gesture: " + gesture);
        }
    }

    @Test
    public void faceBeauty() throws Exception {
        String s = HttpUtilFunction.faceBeauty("C:\\Users\\DELL\\Pictures\\Saved Pictures\\微信图片_20231028182624.jpg",
                "100","100");
    }

    @Test
    public void faceBeautification() throws Exception {
        String s = HttpUtilFunction.faceBeautification("C:\\Users\\DELL\\Pictures\\Saved Pictures\\微信图片_20231028182624.jpg",
                "100","100","100","100","100","100","black_white");
    }

    //5b4e09b1855471162da320e8c7975714
    //636422d64b3b00fb993f26dae918b976
    @Test
    public void compare() throws Exception {
        String compare = HttpUtilFunction.compare("6a606785b80877cfdfaf1c84ceaf65ee", "640ed986b3c02a2f286d86985751e3d5");
        System.out.println(compare);
    }

    @Test
    public void skinAnalysis() throws Exception {
        Map<String, Object> compare = HttpUtilFunction.skinAnalysis("C:\\Users\\DELL\\Pictures\\Saved Pictures\\微信图片_20231028182624.jpg");
        System.out.println(compare);
    }
}