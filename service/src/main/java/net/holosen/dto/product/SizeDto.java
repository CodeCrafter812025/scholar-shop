package net.holosen.dto.product;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SizeDto {
    private Long id;
    private String title;
    private String description;
}
