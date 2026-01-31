package hellojpa.ticketloadtest.controller;

import hellojpa.ticketloadtest.domain.Ticket;
import hellojpa.ticketloadtest.repository.TicketRepository;
import hellojpa.ticketloadtest.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<Ticket> events = ticketRepository.findAll();
        model.addAttribute("events", events);
        return "index";
    }
    
    @PostMapping("/reserve")
    public String reserve(@RequestParam Long ticketId, Model model) {
        // 테스트를 위해 임시로 ID가 1인 유저를 사용 (로그인 구현 전 단계이므로)
        // 실제로는 SecurityContextHolder 등에서 유저 정보를 가져와야 함
        Long tempUserId = 1L;
        
        try {
            ticketService.reserve(tempUserId, ticketId);
            model.addAttribute("message", "예매가 완료되었습니다!");
        } catch (Exception e) {
            model.addAttribute("message", "예매 실패: " + e.getMessage());
        }
        
        // 데이터 갱신 후 다시 목록 보여주기
        List<Ticket> events = ticketRepository.findAll();
        model.addAttribute("events", events);
        
        return "index";
    }
}
