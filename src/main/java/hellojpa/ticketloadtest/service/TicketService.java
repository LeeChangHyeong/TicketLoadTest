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

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    /**
     * 기본 예매 로직 (락 없음 - 동시성 문제 발생 가능)
     */
    @Transactional
    public Long reserve(Long userId, Long ticketId) {
        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 2. 티켓 조회 (비관적 락 적용)
        Ticket ticket = ticketRepository.findByIdWithLock(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));

        // 3. 재고 차감 (여기서 동시성 문제 발생 가능)
        ticket.decreaseStock();

        // 4. 예약 정보 생성 및 저장
        Reservation reservation = new Reservation(user, ticket);
        reservationRepository.save(reservation);

        return reservation.getId();
    }
}
