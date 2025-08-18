package net.holosen.dto.site;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class NavDto {
    private Long id;
    private String title;
    private String link;
    private Integer orderNumber;
}
