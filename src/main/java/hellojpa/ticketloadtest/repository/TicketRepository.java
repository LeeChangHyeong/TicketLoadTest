package hellojpa.ticketloadtest.repository;

import hellojpa.ticketloadtest.domain.Ticket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}