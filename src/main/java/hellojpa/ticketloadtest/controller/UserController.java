package hellojpa.ticketloadtest.controller;

import hellojpa.ticketloadtest.dto.UserForm;
import hellojpa.ticketloadtest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(UserForm form, Model model) {
        try {
            userService.join(form.getName(), form.getEmail(), form.getPassword());
            return "redirect:/login";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }
}
