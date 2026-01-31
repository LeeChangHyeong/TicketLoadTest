package hellojpa.ticketloadtest.controller;

import hellojpa.ticketloadtest.domain.Ticket;
import hellojpa.ticketloadtest.domain.User;
import hellojpa.ticketloadtest.repository.TicketRepository;
import hellojpa.ticketloadtest.repository.UserRepository;
import hellojpa.ticketloadtest.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        // 항상 ID 순으로 정렬하여 조회
        List<Ticket> events = ticketRepository.findAllByOrderByIdAsc();
        model.addAttribute("events", events);
        return "index";
    }

    @PostMapping("/reserve")
    public String reserve(@RequestParam Long ticketId, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + email));

        try {
            ticketService.reserve(user.getId(), ticketId);
            redirectAttributes.addFlashAttribute("message", "예매가 완료되었습니다!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "예매 실패: " + e.getMessage());
        }
        
        return "redirect:/";
    }
}
