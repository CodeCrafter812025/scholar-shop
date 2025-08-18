package net.holosen.app.controller.panel.product;

import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.controller.base.CreateController;
import net.holosen.app.controller.base.ReadController;
import net.holosen.app.controller.base.UpdateController;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.product.SizeDto;
import net.holosen.service.product.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/panel/size")
public class SizePanelController implements
        CreateController<SizeDto>,
        ReadController<SizeDto>,
        UpdateController<SizeDto> {
    private final SizeService service;

    @Autowired
    public SizePanelController(SizeService service) {
        this.service = service;
    }


    @Override
    @CheckPermission("add_size")
    public APIResponse<SizeDto> add(SizeDto dto) throws Exception {
        return APIResponse.<SizeDto>builder()
                .status(APIStatus.Success)
                .data(service.create(dto))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("list_size")
    public APIPanelResponse<List<SizeDto>> getAll(Integer page, Integer size) {
        Page<SizeDto> data = service.readAll(page, size);
        return APIPanelResponse.<List<SizeDto>>builder()
                .message("")
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .build();
    }

    @Override
    @CheckPermission("edit_size")
    public APIResponse<SizeDto> edit(SizeDto dto) throws Exception {
        return APIResponse.<SizeDto>builder()
                .status(APIStatus.Success)
                .data(service.update(dto))
                .message("")
                .build();
    }
}
