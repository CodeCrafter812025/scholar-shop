package net.holosen.dataaccess.entity.order;

import jakarta.persistence.*;
import lombok.*;
import net.holosen.dataaccess.entity.user.User;
import net.holosen.dataaccess.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createDate;
    private LocalDateTime payedDate;
    private OrderStatus status;
    private Long totalAmount;

    @OneToMany(mappedBy = "invoice")
    private List<InvoiceItem> items;

    @ManyToOne
    private User user;

}
