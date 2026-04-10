package com.hnix.sd.spring.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.util.UrlPathHelper;

import com.hnix.sd.spring.xss.XSSFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	
	private final CommonInterceptor commonInterceptor;
	
	private final long MAX_AGE_SECS = 3600;
	
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		// TODO Auto-generated method stub
	    UrlPathHelper urlPathHelper = new UrlPathHelper();
	    urlPathHelper.setUrlDecode(false);
	    urlPathHelper.setAlwaysUseFullPath(true);
	    configurer.setUrlPathHelper(urlPathHelper);
    
	}


	//클라이언트 요청 시, 해당 경로가 addInterceptor()에 있으면, TestController.java의 Controller로 바로 가지 않고 먼저 TestController.java에 가서 preHandle을 실행한다.
	//특정 경로를 제외하고 싶으면 excludePathPatterns("/*")를 사용한다.

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		InterceptorRegistration reg = registry.addInterceptor(commonInterceptor);

		reg.addPathPatterns("/**");

		reg.excludePathPatterns("/swagger-ui.html");
		reg.excludePathPatterns("/swagger-ui/**");
		reg.excludePathPatterns("/swagger-resources/**");
		reg.excludePathPatterns("/swagger-resources");
		reg.excludePathPatterns("/v3/api-docs/**");

		reg.excludePathPatterns("/member/login/**");
		reg.excludePathPatterns("/member/logout");
		reg.excludePathPatterns("/common/user/consent/**");
		reg.excludePathPatterns("/common/user/certificate/**");
		reg.excludePathPatterns("/work/survey/client/**");
		reg.excludePathPatterns("/common/file/download/**");
		reg.excludePathPatterns("/common/file/list/**");
		reg.excludePathPatterns("/work/certificate/client/**");
		reg.excludePathPatterns("/access/log/store");
		reg.excludePathPatterns("/test/**");
		reg.excludePathPatterns("/rad/**");
		reg.excludePathPatterns("/example");

		reg.excludePathPatterns("/common/mail/user/reset/password");
		reg.excludePathPatterns("/common/userEmail/regist");

	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
		registry.addResourceHandler("swagger-ui.html")
		.addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
		.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
        .maxAge(MAX_AGE_SECS);
	}

	@Bean
	public FilterRegistrationBean<XSSFilter> filterRegistrationBean() {
		FilterRegistrationBean<XSSFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
		filterFilterRegistrationBean.setFilter(new XSSFilter());
		return filterFilterRegistrationBean;
	}
}
