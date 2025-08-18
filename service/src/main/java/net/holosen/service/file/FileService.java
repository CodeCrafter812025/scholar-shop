package net.holosen.service.file;

import net.holosen.common.exceptions.NotFoundException;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dataaccess.entity.file.File;
import net.holosen.dataaccess.repository.file.FileRepository;
import net.holosen.dto.file.FileDto;
import net.holosen.service.base.DeleteService;
import net.holosen.service.base.ReadService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileService implements ReadService<FileDto> , DeleteService<FileDto> {
    private final FileRepository repository;
    private final ModelMapper mapper;

    @Value("${app.file.upload.path}")
    private String uploadPath;


    @Autowired
    public FileService(FileRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Boolean delete(Long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public Page<FileDto> readAll(Integer page, Integer size) {
        if (page == null){
            page = 0;
        }
        if (size == null){
            size = 10;
        }

        return repository.findAll(Pageable.ofSize(size).withPage(page))
                .map(x -> mapper.map(x , FileDto.class));
    }

    public FileDto upload(@RequestBody MultipartFile file) throws Exception {
        if (file == null){
            throw new ValidationException("Please Select File To Upload");
        }
        String head = Objects.requireNonNull(file.getOriginalFilename()).substring(0 , file.getOriginalFilename().lastIndexOf("."));
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String filename = head + "." + extension;
        File entity = File.builder()
                .createDate(LocalDateTime.now())
                .extension(extension)
                .name(head)
                .path(filename)
                .uuid(UUID.randomUUID().toString())
                .size(file.getSize())
                .build();

        String filePath = uploadPath + java.io.File.separator + filename;
        Path savePath = Paths.get(filePath);
        java.nio.file.Files.write(savePath , file.getBytes());

        File savedFile = repository.save(entity);
        return mapper.map(savedFile , FileDto.class);
    }

    public FileDto readByName(String name) throws NotFoundException {
        File file = repository.findFirstByNameEqualsIgnoreCase(name).orElseThrow(NotFoundException::new);
        return mapper.map(file , FileDto.class);
    }
}
