package net.holosen.dto.product;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ColorDto {
    private Long id;
    private String name;
    private String hex;
}
