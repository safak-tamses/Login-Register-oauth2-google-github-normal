package com.example.web.controller;


import com.example.web.dto.UserLoginDTO;

import com.example.web.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {
    private UserService userService;
    @ModelAttribute("user")
    public UserLoginDTO userLoginDTO(){
        return new UserLoginDTO();
    }
    @GetMapping
    public String login(){
        return "login";
    }
    @PostMapping
    public void loginUser(@ModelAttribute("user") UserLoginDTO userLoginDTO){
        userService.loadUserByUsername(userLoginDTO.getUsername());
    }
}
