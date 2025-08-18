package net.holosen.dto.product;

import jakarta.persistence.*;
import lombok.*;
import net.holosen.dataaccess.entity.file.File;
import net.holosen.dataaccess.entity.product.Color;
import net.holosen.dataaccess.entity.product.ProductCategory;
import net.holosen.dataaccess.entity.product.Size;
import net.holosen.dto.file.FileDto;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ProductDto {
    private Long id;
    private String title;
    private Long price;
    private Long visitCount;
    private LocalDateTime addDate;
    private FileDto image;
    private Set<ColorDto> colors;
    private Set<SizeDto> sizes;
    private ProductCategoryDto category;
    private Boolean enable = true;
    private Boolean exist = true;
    private String description;

}
