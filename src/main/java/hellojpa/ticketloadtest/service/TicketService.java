package hellojpa.ticketloadtest.service;

import hellojpa.ticketloadtest.domain.Reservation;
import hellojpa.ticketloadtest.domain.Ticket;
import hellojpa.ticketloadtest.domain.User;
import hellojpa.ticketloadtest.repository.ReservationRepository;
import hellojpa.ticketloadtest.repository.TicketRepository;
import hellojpa.ticketloadtest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "tickets", key = "'all'")
    @Transactional(readOnly = true)
    public List<Ticket> getAllTickets() {
        try {
            // 인위적인 병목 시뮬레이션 (무거운 쿼리 가정)
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ticketRepository.findAllByOrderByIdAsc();
    }

    @CacheEvict(value = "tickets", allEntries = true)
    @Transactional
    public Long reserve(Long userId, Long ticketId) {
        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 2. 티켓 조회
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));

        // 3. 재고 차감
        ticket.decreaseStock();

        // 4. 예약 정보 생성 및 저장
        Reservation reservation = new Reservation(user, ticket);
        reservationRepository.save(reservation);

        return reservation.getId();
    }
}
