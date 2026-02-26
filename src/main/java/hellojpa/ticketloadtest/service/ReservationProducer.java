package hellojpa.ticketloadtest.service;

import hellojpa.ticketloadtest.dto.ReservationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationProducer {

    private final KafkaTemplate<String, ReservationMessage> kafkaTemplate;
    private final StringRedisTemplate redisTemplate; // 추가
    private static final String TOPIC = "reservation-topic";

    public Long sendReservation(Long userId, Long ticketId) {
        // 1. 내가 몇 번째로 줄을 섰는지 (절대 번호)
        Long rank = redisTemplate.opsForValue().increment("waiting:count:" + ticketId);
        
        // 2. 현재 몇 번까지 처리가 완료되었는지 확인
        String processedStr = redisTemplate.opsForValue().get("processed:count:" + ticketId);
        Long processedCount = (processedStr == null) ? 0L : Long.parseLong(processedStr);
        
        ReservationMessage message = new ReservationMessage(userId, ticketId);
        kafkaTemplate.send(TOPIC, message);
        
        // 3. 내 앞의 대기 인원 = 절대 번호 - 처리 완료 번호 - 1 (나 자신 제외)
        // 만약 음수가 나오면 0으로 처리
        long waitingAhead = Math.max(0, rank - processedCount - 1);
        return waitingAhead;
    }
}
