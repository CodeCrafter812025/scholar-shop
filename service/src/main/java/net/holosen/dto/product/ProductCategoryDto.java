package net.holosen.dto.product;

import lombok.*;
import net.holosen.dto.file.FileDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ProductCategoryDto {
    private Long id;
    private String title;
    private String description;
    private FileDto image;

}
