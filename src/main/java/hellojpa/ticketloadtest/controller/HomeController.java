package hellojpa.ticketloadtest.controller;

import hellojpa.ticketloadtest.domain.Ticket;
import hellojpa.ticketloadtest.domain.User;
import hellojpa.ticketloadtest.repository.TicketRepository;
import hellojpa.ticketloadtest.repository.UserRepository;
import hellojpa.ticketloadtest.service.ReservationProducer;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TicketService ticketService;
    private final TicketFacade ticketFacade;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ReservationProducer reservationProducer;

    @GetMapping("/")
    public String home(Model model) {
        List<Ticket> events = ticketService.getAllTickets();
        model.addAttribute("events", events);
        
        try {
            String serverInfo = "Server ID: " + InetAddress.getLocalHost().getHostName();
            model.addAttribute("serverInfo", serverInfo);
        } catch (UnknownHostException e) {
            model.addAttribute("serverInfo", "Server ID: Unknown");
        }
        
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
            // Kafka로 비동기 전송하고 내 앞의 대기 인원 받기
            Long waitingAhead = reservationProducer.sendReservation(user.getId(), ticketId);
            redirectAttributes.addFlashAttribute("message", "예매 요청이 접수되었습니다. 내 앞의 대기 인원: " + waitingAhead + "명");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "접수 실패: " + e.getMessage());
        }
        
        return "redirect:/";
    }

    @PostMapping("/test/reserve")
    @ResponseBody
    public String testReserve(@RequestParam Long ticketId) {
        Long testUserId = 1L;
        try {
            // Kafka로 비동기 전송하고 내 앞의 대기 인원 받기
            Long waitingAhead = reservationProducer.sendReservation(testUserId, ticketId);
            return "QUEUED: " + waitingAhead + " people ahead";
        } catch (Exception e) {
            return "FAIL: " + e.getMessage();
        }
    }
}
