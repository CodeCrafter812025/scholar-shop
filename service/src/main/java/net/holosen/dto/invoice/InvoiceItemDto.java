package net.holosen.dto.invoice;

import lombok.*;
import net.holosen.dto.product.ColorDto;
import net.holosen.dto.product.ProductDto;
import net.holosen.dto.product.SizeDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class InvoiceItemDto {
    private Long id;
    private ProductDto product;
    private SizeDto size;
    private ColorDto color;
    private Integer quantity;
    private Long price;

}
