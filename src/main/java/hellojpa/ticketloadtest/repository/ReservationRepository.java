package hellojpa.ticketloadtest.repository;

import hellojpa.ticketloadtest.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
