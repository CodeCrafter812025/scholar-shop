package net.holosen.app.controller.open;

import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.common.exceptions.NotFoundException;
import net.holosen.dto.site.ContentDto;
import net.holosen.service.site.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/content")
public class ContentController {
    private final ContentService service;

    @Autowired
    public ContentController(ContentService service) {
        this.service = service;
    }

    @GetMapping("")
    public APIResponse<List<ContentDto>> getAll(){
        return APIResponse.<List<ContentDto>>builder()
                .status(APIStatus.Success)
                .data(service.readAll())
                .build();
    }

    @GetMapping("{key}")
    public APIResponse<ContentDto> getByKey(@PathVariable String key){
        try {
            return APIResponse.<ContentDto>builder()
                    .status(APIStatus.Success)
                    .data(service.readByKey(key))
                    .build();
        } catch (NotFoundException e) {
            return APIResponse.<ContentDto>builder()
                    .status(APIStatus.Error)
                    .message(e.getMessage())
                    .build();
        }
    }

}
