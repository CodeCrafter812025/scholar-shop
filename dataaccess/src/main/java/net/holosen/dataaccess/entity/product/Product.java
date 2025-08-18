package net.holosen.dataaccess.entity.product;

import jakarta.persistence.*;
import lombok.*;
import net.holosen.dataaccess.entity.file.File;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000 , nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private Boolean enable = true;
    private Boolean exist = true;
    private Long price;
    private Long visitCount;
    private LocalDateTime addDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private File image;

    @ManyToMany
    @JoinTable(name = "product_color",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "color_id"))
    private Set<Color> colors;

    @ManyToMany
    @JoinTable(name = "product_size",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "size_id"))
    private Set<Size> sizes;

    @ManyToOne
    private ProductCategory category;

}
