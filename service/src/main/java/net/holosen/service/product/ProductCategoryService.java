package net.holosen.service.product;

import net.holosen.common.exceptions.NotFoundException;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dataaccess.entity.product.ProductCategory;
import net.holosen.dataaccess.repository.product.ProductCategoryRepository;
import net.holosen.dto.product.ProductCategoryDto;
import net.holosen.service.base.CRUDService;
import net.holosen.service.base.HasValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService implements CRUDService<ProductCategoryDto> , HasValidation<ProductCategoryDto> {

    private final ProductCategoryRepository repository;
    private final ModelMapper mapper;

    @Autowired
    public ProductCategoryService(ProductCategoryRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ProductCategoryDto> readAllActive(){
        return repository.findAllByEnableIsTrue()
                .stream().map(x -> mapper.map(x , ProductCategoryDto.class)).toList();
    }


    @Override
    public ProductCategoryDto create(ProductCategoryDto dto) throws Exception {
        checkValidation(dto);
        ProductCategory data = mapper.map(dto , ProductCategory.class);
        return mapper.map(repository.save(data) , ProductCategoryDto.class);
    }

    @Override
    public Boolean delete(Long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public Page<ProductCategoryDto> readAll(Integer page, Integer size) {

        if (page == null){
            page = 0;
        }
        if (size == null){
            size = 10;
        }

        return repository.findAll(Pageable.ofSize(size).withPage(page))
                .map(x -> mapper.map(x , ProductCategoryDto.class));
    }

    @Override
    public ProductCategoryDto update(ProductCategoryDto dto) throws Exception {
        checkValidation(dto);
        if (dto.getId() == null || dto.getId() < 0){
            throw new ValidationException("Please enter id to update");
        }
        ProductCategory oldData = repository.findById(dto.getId()).orElseThrow(NotFoundException::new);
        oldData.setTitle(Optional.ofNullable(dto.getTitle()).orElse(oldData.getTitle()));
        oldData.setDescription(Optional.ofNullable(dto.getDescription()).orElse(oldData.getDescription()));
        if (dto.getImage() != null) {
            oldData.setDescription(Optional.ofNullable(dto.getDescription()).orElse(oldData.getDescription()));
        }
        repository.save(oldData);
        return mapper.map(oldData , ProductCategoryDto.class);
    }

    @Override
    public void checkValidation(ProductCategoryDto dto) throws ValidationException {
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
