package net.holosen.app.controller.panel.product;

import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.controller.base.CRUDController;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.product.ProductDto;
import net.holosen.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panel/product")
public class ProductPanelController implements CRUDController<ProductDto> {
    private final ProductService service;


    @Autowired
    public ProductPanelController(ProductService service) {
        this.service = service;
    }


    @Override
    @CheckPermission("add_data")
    public APIResponse<ProductDto> add(ProductDto dto) throws Exception {
        return APIResponse.<ProductDto>builder()
                .status(APIStatus.Success)
                .data(service.create(dto))
                .message("")
                .build();
    }

    // حذف سخت با fallback به حذف نرم (همان متد قبلی)
    @Override
    @CheckPermission("delete_data")
    @DeleteMapping("{id}")
    public APIResponse<Boolean> delete(@PathVariable Long id) {
        try {
            Boolean ok = service.delete(id);
            return APIResponse.<Boolean>builder()
                    .status(APIStatus.Success)
                    .data(ok)
                    .message(ok ? "" : "Soft delete failed")
                    .build();
        } catch (Exception e) {
            return APIResponse.<Boolean>builder()
                    .status(APIStatus.Error)
                    .data(false)
                    .message(e.getMessage())
                    .build();
        }
    }


    // حذف نرم صریح
    @CheckPermission("delete_data")
    @PatchMapping("disable/{id}")
    public APIResponse<Boolean> disable(@PathVariable Long id) {
        try {
            Boolean ok = service.softDelete(id);
            return APIResponse.<Boolean>builder()
                    .status(APIStatus.Success)
                    .data(ok)
                    .message("")
                    .build();
        } catch (Exception e) {
            return APIResponse.<Boolean>builder()
                    .status(APIStatus.Error)
                    .data(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    @CheckPermission("list_product")
    public APIPanelResponse<List<ProductDto>> getAll(Integer page, Integer size) {
        Page<ProductDto> data = service.readAll(page, size);
        return APIPanelResponse.<List<ProductDto>>builder()
                .message("")
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .build();
    }

    @Override
    @CheckPermission("edit_product")
    public APIResponse<ProductDto> edit(ProductDto dto) throws Exception {
        return APIResponse.<ProductDto>builder()
                .status(APIStatus.Success)
                .data(service.update(dto))
                .message("")
                .build();
    }
}
