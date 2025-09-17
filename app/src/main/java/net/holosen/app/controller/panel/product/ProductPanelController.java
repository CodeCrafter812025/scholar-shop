package net.holosen.app.controller.panel.product;

import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.product.ProductDto;
import net.holosen.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panel/product")
public class ProductPanelController {

    private final ProductService service;

    @Autowired
    public ProductPanelController(ProductService service) {
        this.service = service;
    }

    // لیست
    @GetMapping
    @CheckPermission("list_product")
    public APIPanelResponse<List<ProductDto>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (page == null) page = 0;
        if (size == null) size = 20;
        Page<ProductDto> data = service.readAll(page, size);
        return APIPanelResponse.<List<ProductDto>>builder()
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .message("")
                .build();
    }

    // ایجاد
    @PostMapping
    @CheckPermission("add_product")
    public APIResponse<ProductDto> add(@RequestBody ProductDto dto) throws Exception {
        return APIResponse.<ProductDto>builder()
                .status(APIStatus.Success)
                .data(service.create(dto))
                .message("")
                .build();
    }

    // ویرایش
    @PutMapping
    @CheckPermission("edit_product")
    public APIResponse<ProductDto> edit(@RequestBody ProductDto dto) throws Exception {
        return APIResponse.<ProductDto>builder()
                .status(APIStatus.Success)
                .data(service.update(dto))
                .message("")
                .build();
    }

    // حذف (نرم؛ منطق در سرویس)
    @DeleteMapping("{id}")
    @CheckPermission("delete_product")
    public APIResponse<Boolean> delete(@PathVariable Long id) {
        return APIResponse.<Boolean>builder()
                .status(APIStatus.Success)
                .data(service.delete(id))
                .message("")
                .build();
    }
}
