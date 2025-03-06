package com.jpamember.springboot.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class CustomSecurityConfig {
	
	
//	모든 프로젝트 자원을 개발할때 로그인 을 요구하지 않도록 하는 메서드 -> 나중에 개발 후엔 주석 처리 
//	This is not recommended -- please use permitAll via HttpSecurity#authorizeHttpRequests instead.
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		log.info("------------configure------------------");
		
//		/login 치면 white error 뜨던걸 다시 로그인 페이지가 나오도록 하는 메서드임.
		http.formLogin();
		
		return http.build();
	}
	
//	정적 파일인 css, js 파일등에도 쓸데없이 DefaultSecurityFilterChain이 적용 되지 않도록 제외 시키는 메서등
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		
		log.info("-------------------web configure----------------------");
		
		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().
				atCommonLocations());
	}

}
