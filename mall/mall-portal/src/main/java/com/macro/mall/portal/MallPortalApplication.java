package com.macro.mall.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.macro.mall")
public class MallPortalApplication {

    public static void main(String[] args) {
        // 포털 API 애플리케이션을 시작한다.
        SpringApplication.run(MallPortalApplication.class, args);
    }

}
