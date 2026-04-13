package com.macro.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动入口
 * Created by macro on 2018/4/26.
 */
@SpringBootApplication
public class MallAdminApplication {
    public static void main(String[] args) {
        // 관리자 백엔드 애플리케이션을 시작한다.
        SpringApplication.run(MallAdminApplication.class, args);
    }
}
