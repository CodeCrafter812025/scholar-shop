package net.holosen.dto.site;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ContentDto {
    private Long id;
    private String keyName;
    private String valueContent;
}
