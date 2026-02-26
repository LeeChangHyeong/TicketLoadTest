package hellojpa.ticketloadtest.service;

import hellojpa.ticketloadtest.dto.ReservationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationConsumer {

    private final TicketFacade ticketFacade;
    private final StringRedisTemplate redisTemplate; // 추가

    @KafkaListener(topics = "reservation-topic", groupId = "ticket-group")
    public void consume(ReservationMessage message) {
        try {
            ticketFacade.reserve(message.getUserId(), message.getTicketId());
            
            // 처리 완료 후 "현재 처리된 번호"를 1 증가시킴
            redisTemplate.opsForValue().increment("processed:count:" + message.getTicketId());
            
        } catch (Exception e) {
            System.err.println("[KAFKA CONSUMER] Fail: " + e.getMessage());
        }
    }
}
