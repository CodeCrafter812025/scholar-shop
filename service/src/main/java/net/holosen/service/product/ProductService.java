package net.holosen.service.product;

import net.holosen.common.exceptions.NotFoundException;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dataaccess.entity.file.File;
import net.holosen.dataaccess.entity.product.Product;
import net.holosen.dataaccess.entity.product.ProductCategory;
import net.holosen.dataaccess.repository.product.ProductRepository;
import net.holosen.dataaccess.repository.file.FileRepository;
import net.holosen.dataaccess.repository.product.ProductCategoryRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements CRUDService<ProductDto>, HasValidation<ProductDto> {
    private final ProductRepository repository;
    private final FileRepository fileRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ModelMapper mapper;

    @Autowired
    public ProductService(ProductRepository repository,
                          FileRepository fileRepository,
                          ProductCategoryRepository categoryRepository,
                          ModelMapper mapper) {
        this.repository = repository;
        this.fileRepository = fileRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    public List<LimitedProductDto> read6TopProducts(ProductQueryType type) {
        List<Product> result = new ArrayList<>();
        switch (type) {
            case Popular -> result = repository.find6PopularProducts();
            case Newest  -> result = repository.find6NewestProducts();
            case Cheapest-> result = repository.find6CheapestProducts();
            case Expensive->result = repository.find6ExpensiveProducts();
        }
        return result.stream().map(x -> mapper.map(x, LimitedProductDto.class)).toList();
    }

    public ProductDto read(Long id) throws NotFoundException {
        Product product = repository.findById(id).orElseThrow(NotFoundException::new);
        return mapper.map(product, ProductDto.class);
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto dto) throws Exception {
        checkValidation(dto);

        // —— ولیدیشن FKها: image & category باید وجود داشته باشد
        if (dto.getImage() == null || dto.getImage().getId() == null) {
            throw new ValidationException("Image id is required");
        }
        File img = fileRepository.findById(dto.getImage().getId())
                .orElseThrow(() -> new ValidationException("Image id not found"));

        ProductCategory cat = null;
        if (dto.getCategory() != null && dto.getCategory().getId() != null) {
            cat = categoryRepository.findById(dto.getCategory().getId())
                    .orElseThrow(() -> new ValidationException("Category id not found"));
        }

        Product data = mapper.map(dto, Product.class);
        data.setImage(img);
        data.setCategory(cat);
        data.setVisitCount(0L);
        data.setEnable(true);
        data.setExist(true);
        data.setAddDate(LocalDateTime.now());

        return mapper.map(repository.save(data), ProductDto.class);
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        // حذف نرم همیشه (برای جلوگیری از 500 به‌خاطر FK)
        Product old = repository.findById(id).orElse(null);
        if (old == null) return false;
        old.setEnable(false);
        old.setExist(false);
        repository.save(old);
        return true;
    }

    @Override
    public Page<ProductDto> readAll(Integer page, Integer size) {
        if (page == null) page = 0;
        if (size == null) size = 10;
        return repository.findByEnableTrueAndExistTrue(Pageable.ofSize(size).withPage(page))
                .map(x -> mapper.map(x, ProductDto.class));
    }

    @Override
    @Transactional
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

        if (dto.getImage() != null && dto.getImage().getId() != null) {
            File img = fileRepository.findById(dto.getImage().getId())
                    .orElseThrow(() -> new ValidationException("Image id not found"));
            oldData.setImage(img);
        }
        if (dto.getCategory() != null && dto.getCategory().getId() != null) {
            ProductCategory cat = categoryRepository.findById(dto.getCategory().getId())
                    .orElseThrow(() -> new ValidationException("Category id not found"));
            oldData.setCategory(cat);
        }

        repository.save(oldData);
        return mapper.map(oldData, ProductDto.class);
    }

    public Page<ProductDto> readAllByCategory(Long categoryId , Integer page , Integer size){
        if (page == null) page = 0;
        if (size == null) size = 10;
        return repository.findAllByCategory_Id(categoryId , Pageable.ofSize(size).withPage(page))
                .map(x -> mapper.map(x , ProductDto.class));
    }

    @Override
    public void checkValidation(ProductDto dto) throws ValidationException {
        if (dto == null) throw new ValidationException("Please fill data");
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) throw new ValidationException("Please enter title");
        if (dto.getDescription() == null || dto.getDescription().isEmpty()) throw new ValidationException("Please enter description");
        if (dto.getPrice() == null || dto.getPrice() < 0) throw new ValidationException("Please enter price");
    }

    public Page<LimitedProductDto> search(String q, Pageable pageable) {
        var page = (q == null || q.isBlank()) ? repository.findByEnableTrueAndExistTrue(pageable)
                : repository.findByTitleContainingIgnoreCase(q.trim(), pageable);
        return page.map(p -> mapper.map(p, LimitedProductDto.class));
    }
}
