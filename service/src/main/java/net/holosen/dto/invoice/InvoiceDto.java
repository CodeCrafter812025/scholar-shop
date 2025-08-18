package net.holosen.dto.invoice;

import lombok.*;
import net.holosen.dataaccess.enums.OrderStatus;
import net.holosen.dto.user.LimitedUserDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class InvoiceDto {
    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime payedDate;
    private OrderStatus status;
    private Long totalAmount;
    private List<InvoiceItemDto> items;
    private LimitedUserDto user;


}
