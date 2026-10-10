package com.shop.laptopshop.controller;

import com.shop.laptopshop.model.User;
import com.shop.laptopshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String success, Model model) {
        if ("true".equals(success)) {
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập");
        }
        return "login"; // Spring sẽ tìm file login.html
    }
    
    @PostMapping("/login")
public String loginPost(@RequestParam String username, 
                        @RequestParam String password, 
                        Model model, HttpSession session) {
    try {
        User user = userService.loginUser(username, password);
        
        if (user != null) {
            session.setAttribute("user", user); // Lưu toàn bộ đối tượng user
            return "redirect:/";
        } else {
            model.addAttribute("loginError", "Sai tên đăng nhập hoặc mật khẩu");
            return "login";
        }
    } catch (RuntimeException e) {
        model.addAttribute("loginError", e.getMessage());
        return "login";
    }
}
    @GetMapping("/register")
    public String register() {
        return "register"; // Spring sẽ tìm file register.html
    }
    
    @PostMapping("/register")
    public String registerPost(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String phone,
                              @RequestParam String password,
                              @RequestParam String confirmPassword,
                              Model model) {
        try {
            // Kiểm tra mật khẩu khớp
            if (!password.equals(confirmPassword)) {
                model.addAttribute("registrationError", "Mật khẩu không khớp");
                return "register";
            }
            
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(password);
            
            userService.registerUser(user);
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập");
            return "redirect:/login?success=true";
        } catch (RuntimeException e) {
            model.addAttribute("registrationError", e.getMessage());
            return "register";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}