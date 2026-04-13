package com.hnix.sd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.hnix.sd")
@SpringBootApplication
public class MasupportApplication {

	public static void main(String[] args) {
        // 애플리케이션 시작 전에 Tomcat의 인코딩 슬래시 허용 옵션을 설정한다.
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        // HAX-WEB Spring Boot 애플리케이션을 시작한다.
		SpringApplication.run(MasupportApplication.class, args);
	}

}
