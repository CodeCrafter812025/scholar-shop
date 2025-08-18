package net.holosen.dto.site;

import lombok.*;
import net.holosen.dto.file.FileDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SliderDto {
    private Long id;
    private String title;
    private String link;
    private Integer orderNumber;
    private FileDto image;
}
