package com.task4.loginSecurity.controller;

import com.task4.loginSecurity.entity.User;
import com.task4.loginSecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.Optional;

@Controller
public class AppController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping({"/", "/login"})
    public String index() {
        return "index";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user) {
        User userFromDb = null;
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isPresent()) {
            userFromDb = userOptional.get();
        }
        if (userFromDb != null) {
            return "index";
        }

        user.setActive(true);
        user.setEmail(user.getEmail());
        user.setRegistration(new Date());
        userRepository.save(user);
        return "redirect:/index";
    }

}
