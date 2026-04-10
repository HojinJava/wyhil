package com.hnix.sd.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hnix.sd.core.constant.ComConstants;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocsConfig {

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String version) {

        Info info = new Info()
            .title("HAX-WEB API 문서") // 타이틀
            .version(version); // 문서 버전
        SecurityScheme auth = new SecurityScheme()
        	      .type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name(ComConstants.HEADER);
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(ComConstants.HEADER);
        
        return new OpenAPI()
       		.components(new Components().addSecuritySchemes(ComConstants.HEADER, auth))
        	.addSecurityItem(securityRequirement)
            .info(info);
    }
}
