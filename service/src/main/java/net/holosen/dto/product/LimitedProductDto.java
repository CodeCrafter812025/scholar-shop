package net.holosen.dto.product;

import lombok.*;
import net.holosen.dto.file.FileDto;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LimitedProductDto {
    private Long id;
    private String title;
    private Long price;
    private Long visitCount;
    private LocalDateTime addDate;
    private FileDto image;
    private Set<ColorDto> colors;
    private Set<SizeDto> sizes;
    private ProductCategoryDto category;
}
