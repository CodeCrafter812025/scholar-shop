package net.holosen.dataaccess.entity.site;

import jakarta.persistence.*;
import lombok.*;
import net.holosen.dataaccess.entity.file.File;
import net.holosen.dataaccess.enums.BlogStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String subtitle;

    private LocalDateTime publishDate;
    private BlogStatus status;
    private Long visitCount;

    @ManyToOne
    @JoinColumn(nullable = false)
    private File image;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

}
