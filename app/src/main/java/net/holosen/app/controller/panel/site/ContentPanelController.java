package net.holosen.app.controller.panel.site;

import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.controller.base.CreateController;
import net.holosen.app.controller.base.ReadController;
import net.holosen.app.controller.base.UpdateController;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.site.ContentDto;
import net.holosen.service.site.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/panel/content")
public class ContentPanelController implements
        CreateController<ContentDto>,
        UpdateController<ContentDto>,
        ReadController<ContentDto> {
    private final ContentService service;

    @Autowired
    public ContentPanelController(ContentService contentService) {
        this.service = contentService;
    }


    @Override
    @CheckPermission("list_content")
    public APIPanelResponse<List<ContentDto>> getAll(Integer page, Integer size) {
        Page<ContentDto> data = service.readAll(page , size);
        return APIPanelResponse.<List<ContentDto>>builder()
                .message("")
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .build();
    }


    @Override
    @CheckPermission("add_content")
    public APIResponse<ContentDto> add(ContentDto dto) throws Exception {
        return APIResponse.<ContentDto>builder()
                .status(APIStatus.Success)
                .data(service.create(dto))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("edit_content")
    public APIResponse<ContentDto> edit(ContentDto dto) throws Exception {

        return APIResponse.<ContentDto>builder()
                .status(APIStatus.Success)
                .data(service.update(dto))
                .message("")
                .build();
    }
}
