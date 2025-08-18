package net.holosen.dto.payment;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PaymentDto {
    private Long id;
    private String name;
    private String description;
}
