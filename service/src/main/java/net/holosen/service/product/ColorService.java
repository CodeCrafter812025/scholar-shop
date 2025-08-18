package net.holosen.service.product;

import net.holosen.common.exceptions.NotFoundException;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dataaccess.entity.product.Color;
import net.holosen.dataaccess.repository.product.ColorRepository;
import net.holosen.dto.product.ColorDto;
import net.holosen.service.base.CRUDService;
import net.holosen.service.base.HasValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ColorService implements CRUDService<ColorDto> , HasValidation<ColorDto> {

    private final ColorRepository repository;
    private final ModelMapper mapper;

    @Autowired
    public ColorService(ColorRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ColorDto create(ColorDto dto) throws Exception {
        checkValidation(dto);
        Color data = mapper.map(dto, Color.class);
        return mapper.map(repository.save(data) , ColorDto.class);
    }

    @Override
    public Boolean delete(Long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public Page<ColorDto> readAll(Integer page, Integer size) {
        if (page == null){
            page = 0;
        }
        if (size == null){
            size = 10;
        }
        return repository.findAll(Pageable.ofSize(size).withPage(page))
                .map(x -> mapper.map(x , ColorDto.class));
    }

    @Override
    public ColorDto update(ColorDto dto) throws Exception {
        checkValidation(dto);
        if (dto.getId() == null || dto.getId() < 0){
            throw new ValidationException("Please enter id to update");
        }
        Color oldData = repository.findById(dto.getId()).orElseThrow(NotFoundException::new);
        oldData.setName(Optional.ofNullable(dto.getName()).orElse(oldData.getName()));
        oldData.setHex(Optional.ofNullable(dto.getHex()).orElse(oldData.getHex()));
        repository.save(oldData);
        return mapper.map(oldData , ColorDto.class);
    }

    @Override
    public void checkValidation(ColorDto dto) throws ValidationException {
        if (dto == null){
            throw new ValidationException("Please fill data");
        }
        if (dto.getName() == null || dto.getName().isEmpty()){
            throw new ValidationException("Please enter name");
        }
        if (dto.getHex() == null || dto.getHex().isEmpty()){
            throw new ValidationException("Please enter hex");
        }
    }
}
