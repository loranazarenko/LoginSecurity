package com.task4.loginSecurity.controller;

import com.task4.loginSecurity.entity.User;
import com.task4.loginSecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private HttpServletResponse httpServletResponse;

    @GetMapping("/users")
    public String showAllUsers(Model model) {
        model.addAttribute("title", "All Users");
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("allusers", users);
        return "users";
    }

    public String userBlock(String[] usersId) {
        boolean isFlag = false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        for (String index : usersId) {
            User user = getUser(index);
            if (user == null) {
                return "index";
            }
            user.setActive(false);
            userRepository.save(user);

            if (user.getUsername().equals(auth.getName())) {
                isFlag = true;
            }
        }
        if (isFlag) {
            new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, auth);
            return "redirect:/login?logout";
        }
        return "redirect:/users";
    }

    public String userDelete(String[] usersId) {
        for (String index : usersId) {
            User user = getUser(index);
            if (user == null) {
                return "index";
            }
            userRepository.delete(user);
        }
        return "redirect:/users";
    }

    public String userUnblock(String[] usersId) {
        for (String index : usersId) {
            User user = getUser(index);
            if (user == null) {
                return "index";
            }
            user.setActive(true);
            userRepository.save(user);
        }
        return "redirect:/users";
    }

    private User getUser(String index) {
        Long id = Long.parseLong(index);
        User user = null;
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            user = userOptional.get();
        }
        return user;
    }

    @RequestMapping(value = "/useraction", method = RequestMethod.GET)
    public String userTarget(HttpServletRequest request) {

        String[] checkeds = request.getParameterValues("isChecked");
        String[] deletes = request.getParameterValues("delete");
        String[] blocked = request.getParameterValues("block");
        String[] unblocked = request.getParameterValues("unblock");

        if (checkeds == null) {
            return "redirect:/users";
        } else if (deletes == null) {
            if (blocked == null) {
                if (unblocked == null) {
                    return "redirect:/users";
                } else {
                    userUnblock(checkeds);
                    return "redirect:/users";
                }
            } else {
                userBlock(checkeds);
                return "redirect:/users";
            }
        } else {
            userDelete(checkeds);
            return "redirect:/users";
        }
    }
}
