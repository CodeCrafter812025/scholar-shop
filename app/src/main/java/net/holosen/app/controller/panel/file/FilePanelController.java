package net.holosen.app.controller.panel.file;

import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.controller.base.DeleteController;
import net.holosen.app.controller.base.ReadController;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dto.file.FileDto;
import net.holosen.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/panel/file")
public class FilePanelController implements
        ReadController<FileDto>,
        DeleteController<FileDto> {
    private final FileService service;

    @Autowired
    public FilePanelController(FileService service) {
        this.service = service;
    }

    @PostMapping("upload")
    @CheckPermission("add_file")
    public APIResponse<FileDto> upload(@RequestParam("file") MultipartFile file) throws Exception {
        return APIResponse.<FileDto>builder()
                .status(APIStatus.Success)
                .data(service.upload(file))
                .message("")
                .build();
    }


    @Override
    @CheckPermission("delete_file")
    public APIResponse<Boolean> delete(Long id) {
        return APIResponse.<Boolean>builder()
                .status(APIStatus.Success)
                .data(service.delete(id))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("list_file")
    public APIPanelResponse<List<FileDto>> getAll(Integer page, Integer size) {
        Page<FileDto> data = service.readAll(page, size);
        return APIPanelResponse.<List<FileDto>>builder()
                .message("")
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .build();
    }
}
