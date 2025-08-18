package net.holosen.dataaccess.entity.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500 , nullable = false)
    private String title;

    @Column(length = 1000 , nullable = false)
    private String description;



}
