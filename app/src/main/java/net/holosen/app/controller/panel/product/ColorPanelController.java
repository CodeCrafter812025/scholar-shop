package net.holosen.app.controller.panel.product;

import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.controller.base.ReadController;
import net.holosen.app.controller.base.CreateController;
import net.holosen.app.controller.base.UpdateController;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.product.ColorDto;
import net.holosen.service.product.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/panel/color")
public class ColorPanelController implements
        CreateController<ColorDto>,
        UpdateController<ColorDto>,
        ReadController<ColorDto>
{
    private final ColorService service;

    @Autowired
    public ColorPanelController(ColorService service) {
        this.service = service;
    }


    @Override
    @CheckPermission("add_color")
    public APIResponse<ColorDto> add(ColorDto dto) throws Exception {
        return APIResponse.<ColorDto>builder()
                .status(APIStatus.Success)
                .data(service.create(dto))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("list_color")
    public APIPanelResponse<List<ColorDto>> getAll(Integer page, Integer color) {
        Page<ColorDto> data = service.readAll(page, color);
        return APIPanelResponse.<List<ColorDto>>builder()
                .message("")
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .build();
    }

    @Override
    @CheckPermission("edit_color")
    public APIResponse<ColorDto> edit(ColorDto dto) throws Exception {
        return APIResponse.<ColorDto>builder()
                .status(APIStatus.Success)
                .data(service.update(dto))
                .message("")
                .build();
    }
}
