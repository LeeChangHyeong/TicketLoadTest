package hellojpa.ticketloadtest.controller;

import hellojpa.ticketloadtest.domain.Ticket;
import hellojpa.ticketloadtest.domain.User;
import hellojpa.ticketloadtest.repository.TicketRepository;
import hellojpa.ticketloadtest.repository.UserRepository;
import hellojpa.ticketloadtest.service.TicketFacade;
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

import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TicketService ticketService; // 필요 시 사용
    private final TicketFacade ticketFacade;   // 분산 락 적용된 서비스
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        // 항상 ID 순으로 정렬하여 조회
        List<Ticket> events = ticketRepository.findAllByOrderByIdAsc();
        model.addAttribute("events", events);
        
        try {
            String serverInfo = "Server ID: " + InetAddress.getLocalHost().getHostName();
            model.addAttribute("serverInfo", serverInfo);
            System.out.println("[ACCESS] " + serverInfo);
        } catch (UnknownHostException e) {
            model.addAttribute("serverInfo", "Server ID: Unknown");
        }
        
        return "index";
    }

    @PostMapping("/reserve")
    public String reserve(@RequestParam Long ticketId, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("[RESERVE] Fail: Not Authenticated");
            return "redirect:/login";
        }

        String email = authentication.getName();
        System.out.println("[RESERVE] User: " + email + ", TicketID: " + ticketId);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + email));

        try {
            ticketFacade.reserve(user.getId(), ticketId); // Facade 사용
            System.out.println("[RESERVE] Success for user: " + email);
            redirectAttributes.addFlashAttribute("message", "예매가 완료되었습니다!");
        } catch (Exception e) {
            System.out.println("[RESERVE] Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("message", "예매 실패: " + e.getMessage());
        }
        
        return "redirect:/";
    }
}
