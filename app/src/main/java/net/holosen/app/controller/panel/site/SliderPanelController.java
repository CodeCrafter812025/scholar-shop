package net.holosen.app.controller.panel.site;

import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.controller.base.CRUDController;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.site.SliderDto;
import net.holosen.service.site.SliderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/panel/slider")
public class SliderPanelController implements CRUDController<SliderDto> {
    private final SliderService service;

    @Autowired
    public SliderPanelController(SliderService navService) {
        this.service = navService;
    }


    @Override
    @CheckPermission("add_slider")
    public APIResponse<SliderDto> add(SliderDto dto) throws Exception {
        return APIResponse.<SliderDto>builder()
                .status(APIStatus.Success)
                .data(service.create(dto))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("delete_slider")
    public APIResponse<Boolean> delete(Long id) {
        return APIResponse.<Boolean>builder()
                .status(APIStatus.Success)
                .data(service.delete(id))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("list_slider")
    public APIPanelResponse<List<SliderDto>> getAll(Integer page, Integer size) {
        Page<SliderDto> data = service.readAll(page , size);
        return APIPanelResponse.<List<SliderDto>>builder()
                .message("")
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .build();
    }

    @Override
    @CheckPermission("edit_slider")
    public APIResponse<SliderDto> edit(SliderDto dto) throws Exception {
        return APIResponse.<SliderDto>builder()
                .status(APIStatus.Success)
                .data(service.update(dto))
                .message("")
                .build();
    }

    @CheckPermission("edit_slider")
    @PutMapping("swap-up/{id}")
    public APIResponse<Boolean> swapUp(@PathVariable Long id) throws Exception {
        return APIResponse.<Boolean>builder()
                .status(APIStatus.Success)
                .data(service.swapUp(id))
                .message("")
                .build();
    }

    @CheckPermission("edit_nav")
    @PutMapping("swap-down/{id}")
    public APIResponse<Boolean> swapDown(@PathVariable Long id) throws Exception {
        return APIResponse.<Boolean>builder()
                .status(APIStatus.Success)
                .data(service.swapDown(id))
                .message("")
                .build();
    }
}
