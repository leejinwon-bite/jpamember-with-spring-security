package com.jpamember.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberLoginController {
    
    @GetMapping("/login")
//    매개에 String logout은 /member/login?logout parameter.
    public String loginGET(String error, String logout) {
        
        log.info("login get..................");
        log.info("logout: "+logout);
        
        return "member/login";
    }
    
//    @PostMapping("/login2")
//    public String loginPost() {
//        
//        return "redirect:/";
//    }
	
	
}
