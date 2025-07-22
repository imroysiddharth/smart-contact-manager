package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PageController {

        @Autowired
        private UserService userService;

        @GetMapping("/")
        public String index() {
                return "redirect:/home";
        }

        @RequestMapping("/home")
        public String home(Model model) {
                model.addAttribute("name", "Home Page");
                model.addAttribute("email", "abc@gmail.com");
                model.addAttribute("github", "https://github.com/imroysiddharth");
                System.out.println("Home page ");
                return "home";
        }

        @GetMapping("/about")
        public String about() {
                // System.out.println("");
                return "about";
        }

        @GetMapping("/services")
        public String service() {
                // System.out.println("");
                return "services";
        }

        @GetMapping("/contact")
        public String contact() {
                // System.out.println("");
                return "contact";
        }

        @GetMapping("/login")
        public String login() {
                // System.out.println("");
                return "login";
        }

        //registration page
        @GetMapping("/register")
        public String register(Model model) {
                UserForm userForm = new UserForm();
                model.addAttribute("userForm", userForm);
                return "register";
        }

        //registration processing
        @PostMapping("/do-register")
        public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult,
                        HttpSession session) {
                System.out.println("Registration Done!");
                System.out.println(userForm.toString());

                if (bindingResult.hasErrors()) {
                        return "register";
                }
                // User user = User.builder()
                // .name(userForm.getName())
                // .email(userForm.getEmail())
                // .password(userForm.getPassword())
                // .about(userForm.getAbout())
                // .phoneNumber(userForm.getPhoneNumber())
                // .profilePic(
                //;
                User newUser = new User();
                newUser.setName(userForm.getName());
                newUser.setEmail(userForm.getEmail());
                newUser.setPassword(userForm.getPassword());
                newUser.setAbout(userForm.getAbout());
                newUser.setPhoneNumber(userForm.getPhoneNumber());
                newUser.setProfilePic(
                                "url.com");

                User savedUser = userService.saveUser(newUser);
                System.out.println(savedUser.getName());

                Message message = Message.builder()
                                .content("Registration Successful")
                                .type(MessageType.green).build();

                session.setAttribute("message", message);
                return "redirect:/register";
        }

}
