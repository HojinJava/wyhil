package com.macro.mall;

import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 서버 시작 시 초기화 메시지 출력
 */
@Component
public class ServerStartRunner implements ApplicationRunner {
    @Override
    public void run(org.springframework.boot.ApplicationArguments args) throws Exception {
        System.out.println("서버가 실행됩니다");
    }
}
