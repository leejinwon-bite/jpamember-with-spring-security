package com.jpamember.springboot.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Controller;

import com.jpamember.springboot.security.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@RequiredArgsConstructor
// 특정 경로에만 spring security 적용시키기 위한 에널 테이션
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {
    
//    remember-me라고 자동 로그인 처리하는거 할려고, 쿠키를 발행 해야함. 쿠키관련 객체들임. 맛있겠다.
    private final DataSource dataSource;
    private final CustomUserDetailsService userDetailsService;
	
//	모든 프로젝트 자원을 개발할때 로그인 을 요구하지 않도록 하는 메서드 -> 나중에 개발 후엔 주석 처리 
//	This is not recommended -- please use permitAll via HttpSecurity#authorizeHttpRequests instead.
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		log.info("------------configure------------------");
		
		/*
		 * http.formLogin()는 /login 치면 white error 뜨던걸 다시 로그인 페이지가 나오도록 하는 메서드임.
		 * loginPage("/member/login")는 spring security에서 제공하는 login page가 아닌, custom
		 * login page를 login page로 대체.
		 */
		http.formLogin().loginPage("/member/login");
		
//		csrf 설정이 복잡해서 그냥 에러뜨니깐 비활성화 시킨거임.
		http.csrf().disable();
		
//		쿠키 생성시 인코딩 하기위한 key, 토큰을 저장할 장소인 tokenRepository, 토큰 유효기간 custom config.
		http.rememberMe()
		    .key("12345678")
		    .tokenRepository(persistentTokenRepository())
		    .userDetailsService(userDetailsService)
//		    60seconds * 60minutes * 24hours * 30days -> 30일간 유효기간
		    .tokenValiditySeconds(60*60*24*30);
		
		return http.build();
	}
	
//	정적 파일인 css, js 파일등에도 쓸데없이 DefaultSecurityFilterChain이 적용 되지 않도록 제외 시키는 메서등
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		
		log.info("-------------------web configure----------------------");
		
		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().
				atCommonLocations());
	}
	
	
//	PasswordEncoder 사용을 위한 빈 등록 config.
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
//	토큰 관련 객체 Bean 등록 config for remember-me(자동 로그인 기능).
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
	    
	    JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
	    repo.setDataSource(dataSource);
	    return repo;
	}

}
