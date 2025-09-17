package net.holosen.app.controller.panel.product;

import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.product.ProductCategoryDto;
import net.holosen.service.product.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panel/product/category")
public class ProductCategoryPanelController {

    private final ProductCategoryService service;

    @Autowired
    public ProductCategoryPanelController(ProductCategoryService service) {
        this.service = service;
    }

    @GetMapping
    @CheckPermission("list_product_category")
    public APIPanelResponse<List<ProductCategoryDto>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (page == null) page = 0;
        if (size == null) size = 20;
        Page<ProductCategoryDto> data = service.readAll(page, size);
        return APIPanelResponse.<List<ProductCategoryDto>>builder()
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .message("")
                .build();
    }

    @PostMapping
    @CheckPermission("add_product_category")
    public APIResponse<ProductCategoryDto> add(@RequestBody ProductCategoryDto dto) throws Exception {
        return APIResponse.<ProductCategoryDto>builder()
                .status(APIStatus.Success)
                .data(service.create(dto))
                .message("")
                .build();
    }

    @PutMapping
    @CheckPermission("edit_product_category")
    public APIResponse<ProductCategoryDto> edit(@RequestBody ProductCategoryDto dto) throws Exception {
        return APIResponse.<ProductCategoryDto>builder()
                .status(APIStatus.Success)
                .data(service.update(dto))
                .message("")
                .build();
    }
}
