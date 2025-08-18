package net.holosen.app.controller.panel.site;

import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.controller.base.CRUDController;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.site.NavDto;
import net.holosen.service.site.NavService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/panel/nav")
public class NavPanelController implements CRUDController<NavDto> {
    private final NavService service;

    @Autowired
    public NavPanelController(NavService navService) {
        this.service = navService;
    }


    @Override
    @CheckPermission("add_nav")
    public APIResponse<NavDto> add(NavDto dto) throws Exception {
        return APIResponse.<NavDto>builder()
                .status(APIStatus.Success)
                .data(service.create(dto))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("delete_nav")
    public APIResponse<Boolean> delete(Long id) {
        return APIResponse.<Boolean>builder()
                .status(APIStatus.Success)
                .data(service.delete(id))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("list_nav")
    public APIPanelResponse<List<NavDto>> getAll(Integer page, Integer size) {
        Page<NavDto> data = service.readAll(page , size);
        return APIPanelResponse.<List<NavDto>>builder()
                .message("")
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .build();
    }

    @Override
    @CheckPermission("edit_nav")
    public APIResponse<NavDto> edit(NavDto dto) throws Exception {
        return APIResponse.<NavDto>builder()
                .status(APIStatus.Success)
                .data(service.update(dto))
                .message("")
                .build();
    }

    @CheckPermission("edit_nav")
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
