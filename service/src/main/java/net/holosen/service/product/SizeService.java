package net.holosen.service.product;

import net.holosen.common.exceptions.NotFoundException;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dataaccess.entity.product.Size;
import net.holosen.dataaccess.repository.product.SizeRepository;
import net.holosen.dto.product.SizeDto;
import net.holosen.service.base.CRUDService;
import net.holosen.service.base.HasValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SizeService implements CRUDService<SizeDto> , HasValidation<SizeDto> {

    private final SizeRepository repository;
    private final ModelMapper mapper;

    @Autowired
    public SizeService(SizeRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public SizeDto create(SizeDto dto) throws Exception {
        checkValidation(dto);
        Size data = mapper.map(dto , Size.class);
        return mapper.map(repository.save(data) , SizeDto.class);
    }

    @Override
    public Boolean delete(Long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public Page<SizeDto> readAll(Integer page, Integer size) {
        if (page == null){
            page = 0;
        }
        if (size == null){
            size = 10;
        }

        return repository.findAll(Pageable.ofSize(size).withPage(page))
                .map(x -> mapper.map(x , SizeDto.class));
    }

    @Override
    public SizeDto update(SizeDto dto) throws Exception {
        checkValidation(dto);
        if (dto.getId() == null || dto.getId() < 0){
            throw new ValidationException("Please enter id ti update");
        }
        Size oldData = repository.findById(dto.getId()).orElseThrow(NotFoundException::new);
        oldData.setTitle(Optional.ofNullable(dto.getTitle()).orElse(oldData.getTitle()));
        oldData.setDescription(Optional.ofNullable(dto.getDescription()).orElse(oldData.getDescription()));
        repository.save(oldData);
        return mapper.map(oldData , SizeDto.class);
    }

    @Override
    public void checkValidation(SizeDto dto) throws ValidationException {
        if (dto == null){
            throw new ValidationException("Please fill data");
        }
        if (dto.getTitle() == null || dto.getTitle().isEmpty()){
            throw new ValidationException("Please enter title");
        }
        if (dto.getDescription() == null || dto.getDescription().isEmpty()){
            throw new ValidationException("Please enter description");
        }
    }
}
