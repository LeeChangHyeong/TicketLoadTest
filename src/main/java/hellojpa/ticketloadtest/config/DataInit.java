package hellojpa.ticketloadtest.config;

import hellojpa.ticketloadtest.domain.Ticket;
import hellojpa.ticketloadtest.domain.User;
import hellojpa.ticketloadtest.repository.TicketRepository;
import hellojpa.ticketloadtest.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;

import hellojpa.ticketloadtest.service.TicketService;

@Component
@RequiredArgsConstructor
public class DataInit {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;
    private final TicketService ticketService;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initData() {
        
        // ... (DB 로그 생략)

        if (userRepository.count() == 0) {
            userRepository.save(new User("테스트유저", "test@test.com", passwordEncoder.encode("123")));
        }

        if (ticketRepository.count() == 0) {
            ticketRepository.save(new Ticket("뮤지컬 <지킬 앤 하이드>", 150000, 3000));
            ticketRepository.save(new Ticket("2026 싸이 흠뻑쇼 - 서울", 145000, 10000));
            ticketRepository.save(new Ticket("임영웅 리사이틀", 160000, 30)); // 적은 재고로 테스트용
        }

        // Cache Warm-up
        ticketService.getAllTickets();
    }
}
