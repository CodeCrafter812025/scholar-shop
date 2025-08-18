package net.holosen.dto.site;

import lombok.*;
import net.holosen.dto.file.FileDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LimitedBlogDto {
    private Long id;
    private String title;
    private String subtitle;
    private LocalDateTime publishedDate;
    private Long visitCount;
    private FileDto image;
}
