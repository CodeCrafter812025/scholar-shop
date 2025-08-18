package net.holosen.dataaccess.entity.order;

import jakarta.persistence.*;
import lombok.*;
import net.holosen.dataaccess.entity.product.Color;
import net.holosen.dataaccess.entity.product.Product;
import net.holosen.dataaccess.entity.product.Size;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Invoice invoice;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Size size;

    @ManyToOne
    private Color color;

    private Integer quantity;
    private Long price;

}
