package hellojpa.ticketloadtest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tickets")
@Getter @Setter
@NoArgsConstructor
public class Ticket {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private Integer price;
    
    private Integer stock;

    public Ticket(String name, Integer price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    
    // 재고 감소 로직 (동시성 문제 발생 유도용 - 동기화 없음)
    public void decreaseStock() {
        if (this.stock > 0) {
            this.stock--;
        } else {
            throw new IllegalStateException("재고가 부족합니다.");
        }
    }
}
