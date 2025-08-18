package net.holosen.app.controller.panel.product;

import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.controller.base.CreateController;
import net.holosen.app.controller.base.ReadController;
import net.holosen.app.controller.base.UpdateController;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.product.ProductCategoryDto;
import net.holosen.service.product.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/panel/product/category")
public class ProductCategoryPanelController implements
        CreateController<ProductCategoryDto>,
        ReadController<ProductCategoryDto>,
        UpdateController<ProductCategoryDto> {

    private final ProductCategoryService service;

    @Autowired
    public ProductCategoryPanelController(ProductCategoryService navService) {
        this.service = navService;
    }


    @Override
    @CheckPermission("add_product_category")
    public APIResponse<ProductCategoryDto> add(ProductCategoryDto dto) throws Exception {
        return APIResponse.<ProductCategoryDto>builder()
                .status(APIStatus.Success)
                .data(service.create(dto))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("list_product_category")
    public APIPanelResponse<List<ProductCategoryDto>> getAll(Integer page, Integer productCategory) {
        Page<ProductCategoryDto> data = service.readAll(page, productCategory);
        return APIPanelResponse.<List<ProductCategoryDto>>builder()
                .message("")
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .build();
    }

    @Override
    @CheckPermission("edit_product_category")
    public APIResponse<ProductCategoryDto> edit(ProductCategoryDto dto) throws Exception {
        return APIResponse.<ProductCategoryDto>builder()
                .status(APIStatus.Success)
                .data(service.update(dto))
                .message("")
                .build();
    }
}
