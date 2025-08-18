package net.holosen.dataaccess.entity.payment;

import jakarta.persistence.*;
import lombok.*;
import net.holosen.dataaccess.entity.order.Invoice;
import net.holosen.dataaccess.entity.user.User;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trx")

public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    @ManyToOne
    private Invoice invoice;

    @ManyToOne
    private User customer;

    private String authority;
    private String code;
    private String verifyCode;
    private String description;
    private String resultMessage;
    private String verifyMessage;
    private String cardHash;
    private String cardPan;
    private String refId;

    @ManyToOne
    private Payment payment;


}
