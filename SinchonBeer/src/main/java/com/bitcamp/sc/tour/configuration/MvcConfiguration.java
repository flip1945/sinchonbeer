package com.bitcamp.sc.tour.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bitcamp.sc.tour.interceptor.LoggerInterceptor;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	LoggerInterceptor loggerInterceptor = new LoggerInterceptor();
		registry.addInterceptor(loggerInterceptor).addPathPatterns(loggerInterceptor.loginEssential).excludePathPatterns(loggerInterceptor.loginInessential);
		
	}
	
}
