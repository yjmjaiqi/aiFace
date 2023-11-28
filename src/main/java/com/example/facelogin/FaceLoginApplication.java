package com.example.facelogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FaceLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceLoginApplication.class, args);
    }

}
