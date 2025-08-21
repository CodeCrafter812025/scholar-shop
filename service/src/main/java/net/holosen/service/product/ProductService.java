package net.holosen.service.product;

import net.holosen.common.exceptions.NotFoundException;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dataaccess.entity.file.File;
import net.holosen.dataaccess.entity.product.Product;
import net.holosen.dataaccess.repository.product.ProductRepository;
import net.holosen.dto.product.LimitedProductDto;
import net.holosen.dto.product.ProductDto;
import net.holosen.enums.ProductQueryType;
import net.holosen.service.base.CRUDService;
import net.holosen.service.base.HasValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements CRUDService<ProductDto>, HasValidation<ProductDto> {
    private final ProductRepository repository;
    private final ModelMapper mapper;

    @Autowired
    public ProductService(ProductRepository repository,
                          ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<LimitedProductDto> read6TopProducts(ProductQueryType type) {
        List<Product> result = new ArrayList<>();
        switch (type) {
            case Popular -> {
                result = repository.find6PopularProducts();
            }
            case Newest -> {
                result = repository.find6NewestProducts();
            }
            case Cheapest -> {
                result = repository.find6CheapestProducts();
            }
            case Expensive -> {
                result = repository.find6ExpensiveProducts();
            }
        }
        return result.stream().map(x -> mapper.map(x, LimitedProductDto.class)).toList();
    }


    public ProductDto read(Long id) throws NotFoundException {
        Product product = repository.findById(id).orElseThrow(NotFoundException::new);
        return mapper.map(product, ProductDto.class);
    }

    @Override
    public ProductDto create(ProductDto dto) throws Exception {
        checkValidation(dto);
        Product data = mapper.map(dto, Product.class);
        data.setVisitCount(0L);
        data.setEnable(true);
        data.setExist(true);
        data.setAddDate(LocalDateTime.now());
        return mapper.map(repository.save(data), ProductDto.class);
    }

    @Override
    public Boolean delete(Long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public Page<ProductDto> readAll(Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }
        return repository.findAll(Pageable.ofSize(size).withPage(page))
                .map(x -> mapper.map(x, ProductDto.class));
    }

    @Override
    public ProductDto update(ProductDto dto) throws Exception {
        checkValidation(dto);
        if (dto.getId() == null || dto.getId() < 0) {
            throw new ValidationException("Please enter id to update");
        }
        Product oldData = repository.findById(dto.getId()).orElseThrow(NotFoundException::new);
        oldData.setTitle(Optional.ofNullable(dto.getTitle()).orElse(oldData.getTitle()));
        oldData.setDescription(Optional.ofNullable(dto.getDescription()).orElse(oldData.getDescription()));
        oldData.setPrice(Optional.ofNullable(dto.getPrice()).orElse(oldData.getPrice()));
        oldData.setEnable(Optional.ofNullable(dto.getEnable()).orElse(oldData.getEnable()));
        oldData.setExist(Optional.ofNullable(dto.getExist()).orElse(oldData.getExist()));
        if (dto.getImage() != null) {
            oldData.setImage(Optional.ofNullable(mapper.map(dto.getImage(), File.class)).orElse(oldData.getImage()));
        }
        repository.save(oldData);
        return mapper.map(oldData, ProductDto.class);
    }

    public Page<ProductDto> readAllByCategory(Long categoryId , Integer page , Integer size){
        if (page == null){
            page = 0;
        }
        if (size == null){
            size = 10;
        }
        return repository.findAllByCategory_Id(categoryId , Pageable.ofSize(size).withPage(page))
                .map(x -> mapper.map(x , ProductDto.class));

    }

    @Override
    public void checkValidation(ProductDto dto) throws ValidationException {
        if (dto == null) {
            throw new ValidationException("Please fill data");
        }
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new ValidationException("Please enter title");
        }
        if (dto.getPrice() == null || dto.getPrice() < 0) {
            throw new ValidationException("Please enter price");
        }
    }

    public Page<LimitedProductDto> search(String q, Pageable pageable) {
        Page<Product> page;
        if (q == null || q.isBlank()) {
            page = repository.findAll(pageable);
        } else {
            page = repository.findByTitleContainingIgnoreCase(q.trim(), pageable);
        }
        return page.map(p -> mapper.map(p, LimitedProductDto.class));
    }
}
