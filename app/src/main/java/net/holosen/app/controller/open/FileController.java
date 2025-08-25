package net.holosen.app.controller.open;


import com.mysql.cj.util.TimeUtil;
import lombok.SneakyThrows;
import net.holosen.common.exceptions.NotFoundException;
import net.holosen.dto.file.FileDto;
import net.holosen.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${app.file.upload.path}")
    private String uploadPath;


    private final FileService service;

    @Autowired
    public FileController(FileService service) {
        this.service = service;
    }

    @GetMapping("{name}")
    public ResponseEntity<InputStreamResource> getFileByName(@PathVariable String name) {
        try {
            FileDto fileDto = service.readByName(name);
            File file = new File(uploadPath + File.separator + fileDto.getPath());
            if (!file.exists()) {
                throw new NotFoundException();
            }
            InputStream inputStream = new FileInputStream(file);
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(fileDto.getContentType()));
            if (fileDto.getSize() != null) {
                headers.setContentLength(fileDto.getSize());
            }
            return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
        } catch (NotFoundException | FileNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
