package com.macro.mall.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MallDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallDemoApplication.class, args);
        System.out.println("서버가 실행됩니다.");
    }
}
