package com.macro.mall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.macro.mall")
public class MallSearchApplication {

    public static void main(String[] args) {
        // 검색 API 애플리케이션을 시작한다.
        SpringApplication.run(MallSearchApplication.class, args);
    }
}
