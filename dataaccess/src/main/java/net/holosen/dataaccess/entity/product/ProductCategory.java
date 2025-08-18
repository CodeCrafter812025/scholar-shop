package net.holosen.dataaccess.entity.product;

import jakarta.persistence.*;
import lombok.*;
import net.holosen.dataaccess.entity.file.File;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000 , nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT" , nullable = false)
    private String description;

    private Boolean enable = true;

    @ManyToOne
    @JoinColumn(nullable = false)
    private File image;
}
