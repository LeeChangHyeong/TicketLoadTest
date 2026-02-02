package hellojpa.ticketloadtest.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TicketFacade {

    private final RedissonClient redissonClient;
    private final TicketService ticketService;

    public Long reserve(Long userId, Long ticketId) {
        RLock lock = redissonClient.getLock("ticketLock:" + ticketId);

        try {
            // 락 획득 시도 (최대 10초 대기, 락 획득 후 3초 지난 후 자동 해제)
            boolean available = lock.tryLock(10, 3, TimeUnit.SECONDS);

            if (!available) {
                System.out.println("락 획득 실패");
                throw new IllegalStateException("현재 예매량이 많아 잠시 후 다시 시도해주세요.");
            }

            return ticketService.reserve(userId, ticketId);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
