package net.holosen.app.controller.panel.site;

import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.controller.base.CRUDController;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.site.BlogDto;
import net.holosen.service.site.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/panel/blog")
public class BlogPanelController implements CRUDController<BlogDto> {
    private final BlogService service;

    @Autowired
    public BlogPanelController(BlogService blogService) {
        this.service = blogService;
    }


    @Override
    @CheckPermission("add_blog")
    public APIResponse<BlogDto> add(BlogDto dto) throws Exception {
        return APIResponse.<BlogDto>builder()
                .status(APIStatus.Success)
                .data(service.create(dto))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("delete_blog")
    public APIResponse<Boolean> delete(Long id) {
        return APIResponse.<Boolean>builder()
                .status(APIStatus.Success)
                .data(service.delete(id))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("list_blog")
    public APIPanelResponse<List<BlogDto>> getAll(Integer page, Integer size) {
        Page<BlogDto> data = service.readAll(page, size);
        return APIPanelResponse.<List<BlogDto>>builder()
                .message("")
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .build();
    }

    @Override
    @CheckPermission("edit_blog")
    public APIResponse<BlogDto> edit(BlogDto dto) throws Exception {
        return APIResponse.<BlogDto>builder()
                .status(APIStatus.Success)
                .data(service.update(dto))
                .message("")
                .build();
    }
}
