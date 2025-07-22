package com.scm.controllers;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.GetMapping;




@Controller
@RequestMapping("/user")
public class UserController {

   

   
    //User dashboard page 
    @GetMapping("/dashboard")
    public String userDashboard() {

        return "user/dashboard";
    }
    @GetMapping("/profile")
    public String userProfile() {
       
        return "user/profile";
    }
    
}
