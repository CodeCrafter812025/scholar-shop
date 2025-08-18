package net.holosen.dto.site;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import net.holosen.dataaccess.entity.file.File;
import net.holosen.dataaccess.enums.BlogStatus;
import net.holosen.dto.file.FileDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BlogDto {
    private Long id;
    private String title;
    private String subtitle;
    private LocalDateTime publishDate;
    private Long visitCount;
    private FileDto image;
    private BlogStatus status;
    private String description;
}
