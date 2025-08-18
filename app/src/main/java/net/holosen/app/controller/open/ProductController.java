package net.holosen.app.controller.open;

import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.common.exceptions.NotFoundException;
import net.holosen.dto.product.LimitedProductDto;
import net.holosen.dto.product.ProductCategoryDto;
import net.holosen.dto.product.ProductDto;
import net.holosen.enums.ProductQueryType;
import net.holosen.service.product.ProductCategoryService;
import net.holosen.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService service;
    private final ProductCategoryService categoryService;

    @Autowired
    public ProductController(ProductService service,
                             ProductCategoryService categoryService) {
        this.service = service;
        this.categoryService = categoryService;
    }

    @GetMapping("category")
    public APIResponse<List<ProductCategoryDto>> getAllCategories(){
        return APIResponse.<List<ProductCategoryDto>>builder()
                .status(APIStatus.Success)
                .data(categoryService.readAllActive())
                .build();
    }


    @GetMapping("top/{type}")
    public APIResponse<List<LimitedProductDto>> getTopProducts(@PathVariable ProductQueryType type){
        return APIResponse.<List<LimitedProductDto>>builder()
                .status(APIStatus.Success)
                .data(service.read6TopProducts(type))
                .build();
    }

    @GetMapping("{id}")
    public APIResponse<ProductDto> getById(@PathVariable Long id){
        try {
            return APIResponse.<ProductDto>builder()
                    .status(APIStatus.Success)
                    .data(service.read(id))
                    .build();
        } catch (NotFoundException e) {
            return APIResponse.<ProductDto>builder()
                    .status(APIStatus.Error)
                    .message(e.getMessage())
                    .build();
        }
    }

}
