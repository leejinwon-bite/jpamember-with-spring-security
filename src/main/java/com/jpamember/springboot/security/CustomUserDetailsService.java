package com.jpamember.springboot.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
// UserDetailsService는 spring security에서 제공하는 인터페이스임.
public class CustomUserDetailsService implements UserDetailsService {
	
	
//	실제 인증을 처리하는 메서드임. spring security에서 가장 중요한 객체임. 
//	스프링 시큐리티 login 인증 단계 2가지 -> 1. 사용자의 정보를 loading, 2. loaded user 정보를 이용해서 pw 검증
//	아래는 1 단계 인증 부분임. 
//	적용시 console 부분에 나오던 test 용 pw가 안나옴.
	@Override
//	UserDetails 타입은 return 부분 적어주면 에러 사라짐. import도 이때 가능해짐.
//	loadUserByUsername는 UserDetailsService interface가 가지는 단 1개의 메서드라서 이름 틀리면 에러남.
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		
		log.info("load User By Username: "+username);
		
//		Test용으로 id, pw를 custom으로 생성하는 애임.
//		User class는 UserDetails의 구현체, 제공되는 애임.
		UserDetails userDetails = User.builder()
//				사용자의 id를 spring security에서는 username이라는 것을 이용함. 
				.username("user1")
				.password("1111")
				.authorities("ROLE_USER")
				.build();
		
		return userDetails;
	}

}
