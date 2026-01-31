package hellojpa.ticketloadtest.repository;

import hellojpa.ticketloadtest.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;



import java.util.List;

import java.util.Optional;



public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByOrderByIdAsc();



    @Lock(LockModeType.PESSIMISTIC_WRITE)

    @Query("select t from Ticket t where t.id = :id")

    Optional<Ticket> findByIdWithLock(@Param("id") Long id);

}
