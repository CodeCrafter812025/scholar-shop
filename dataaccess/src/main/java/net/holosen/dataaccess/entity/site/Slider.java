package net.holosen.dataaccess.entity.site;

import jakarta.persistence.*;
import lombok.*;
import net.holosen.dataaccess.entity.file.File;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Slider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100 , nullable = false)
    private String title;

    @Column(length = 1000 , nullable = false)
    private String link;

    private Boolean enable = true;
    private Integer orderNumber;

    @ManyToOne
    @JoinColumn(nullable = false)
    private File image;

}
