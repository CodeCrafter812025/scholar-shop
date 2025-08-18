package net.holosen.service.site;

import net.holosen.common.exceptions.NotFoundException;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dataaccess.entity.site.Blog;
import net.holosen.dataaccess.enums.BlogStatus;
import net.holosen.dataaccess.repository.site.BlogRepository;
import net.holosen.dto.site.BlogDto;
import net.holosen.dto.site.LimitedBlogDto;
import net.holosen.dto.site.SingleBlogDto;
import net.holosen.service.base.CRUDService;
import net.holosen.service.base.HasValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BlogService implements CRUDService<BlogDto> , HasValidation<BlogDto> {
    private final BlogRepository repository;
    private final ModelMapper mapper;

    @Autowired
    public BlogService(BlogRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<LimitedBlogDto> readAllPublished(Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 16;
        }
        return repository.findAllPublished(Pageable.ofSize(size).withPage(page))
                .stream().map(x -> mapper.map(x, LimitedBlogDto.class)).toList();
    }

    @Override
    public Page<BlogDto> readAll(Integer page , Integer size) {
        if (page == null){
            page = 0;
        }
        if (size == null){
            size = 10;
        }
        return repository.findAll(Pageable.ofSize(size).withPage(page))
                .map(x -> mapper.map(x , BlogDto.class));
    }

    public SingleBlogDto read(Long id) throws NotFoundException {
        Blog blog = repository.findById(id).orElseThrow(NotFoundException::new);
        return mapper.map(blog, SingleBlogDto.class);
    }

    @Override
    public BlogDto create(BlogDto dto) throws Exception {
        checkValidation(dto);
        Blog data = mapper.map(dto , Blog.class);
        if (data.getPublishDate() == null){
            data.setPublishDate(LocalDateTime.now());
        }
        if (data.getStatus() == null){
            data.setStatus(BlogStatus.Published);
        }
        data.setVisitCount(0L);
        return mapper.map(repository.save(data) , BlogDto.class);
    }

    @Override
    public Boolean delete(Long id) {
        repository.deleteById(id);
        return true;
    }




    @Override
    public BlogDto update(BlogDto dto) throws Exception {
        checkValidation(dto);
        if (dto.getId() == null || dto.getId() < 0){
            throw new ValidationException("Please enter id to update");
        }
        Blog oldData = repository.findById(dto.getId()).orElseThrow(NotFoundException::new);
        oldData.setTitle(Optional.ofNullable(dto.getTitle()).orElse(oldData.getTitle()));
        oldData.setSubtitle(Optional.ofNullable(dto.getSubtitle()).orElse(oldData.getSubtitle()));
        oldData.setPublishDate(Optional.ofNullable(dto.getPublishDate()).orElse(oldData.getPublishDate()));
        oldData.setStatus(Optional.ofNullable(dto.getStatus()).orElse(oldData.getStatus()));
        oldData.setDescription(Optional.ofNullable(dto.getDescription()).orElse(oldData.getDescription()));
        repository.save(oldData);
        return mapper.map(oldData , BlogDto.class);
    }

    @Override
    public void checkValidation(BlogDto dto) throws ValidationException {
        if (dto == null){
            throw new ValidationException("Pleas fill data");
        }
        if (dto.getTitle() == null || dto.getTitle().isEmpty()){
            throw new ValidationException("Pleas enter title");
        }
        if (dto.getSubtitle() == null || dto.getSubtitle().isEmpty()){
            throw new ValidationException("Pleas enter subtitle");
        }
    }


}
