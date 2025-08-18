package net.holosen.service.site;

import jakarta.transaction.Transactional;
import net.holosen.common.exceptions.NotFoundException;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dataaccess.entity.file.File;
import net.holosen.dataaccess.entity.site.Nav;
import net.holosen.dataaccess.entity.site.Slider;
import net.holosen.dataaccess.repository.site.SliderRepository;
import net.holosen.dto.site.SliderDto;
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
public class SliderService implements CRUDService<SliderDto> , HasValidation<SliderDto> {
    private final SliderRepository repository;
    private final ModelMapper mapper;

    @Autowired
    public SliderService(SliderRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<SliderDto> readAll(){
        return repository.findAllByEnableIsTrueOrderByOrderNumberAsc()
                .stream().map(x -> mapper.map(x, SliderDto.class)).toList();
    }

    @Override
    public SliderDto create(SliderDto dto) throws Exception {
        checkValidation(dto);
        Slider data = mapper.map(dto , Slider.class);
        data.setEnable(true);
        Integer lastOrderNumber = repository.findLastOrderNumber();
        if (lastOrderNumber == null){
            lastOrderNumber = 0;
        }
        data.setOrderNumber(++lastOrderNumber);
        return mapper.map(repository.save(data) , SliderDto.class);
    }

    @Override
    public Boolean delete(Long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public Page<SliderDto> readAll(Integer page, Integer size) {
        if (page == null){
            page = 0;
        }
        if (size == null){
            size = 10;
        }
        return repository.findAll(Pageable.ofSize(size).withPage(page))
                .map(x -> mapper.map(x , SliderDto.class));
    }

    @Override
    public SliderDto update(SliderDto dto) throws Exception {
        checkValidation(dto);
        if (dto.getId() == null || dto.getId() < 0){
            throw new ValidationException("Please enter id to update");
        }
        Slider oldData = repository.findById(dto.getId()).orElseThrow(NotFoundException::new);
        oldData.setOrderNumber(Optional.ofNullable(dto.getOrderNumber()).orElse(oldData.getOrderNumber()));
        oldData.setLink(Optional.ofNullable(dto.getLink()).orElse(oldData.getLink()));
        oldData.setTitle(Optional.ofNullable(dto.getTitle()).orElse(oldData.getTitle()));
        if (dto.getImage() != null) {
            oldData.setImage(Optional.ofNullable(mapper.map(dto.getImage() , File.class)).orElse(oldData.getImage()));
        }
        repository.save(oldData);
        return mapper.map(oldData , SliderDto.class);
    }

    @Transactional
    public Boolean swapUp(Long id) throws Exception {
        Slider currentSlider = repository.findById(id).orElseThrow(NotFoundException::new);
        Optional<Slider> previous = repository.findFirstByOrderNumberLessThanOrderByOrderNumberDesc(currentSlider.getOrderNumber());
        if (previous.isPresent()){
            Integer tempOrderNumber = currentSlider.getOrderNumber();
            currentSlider.setOrderNumber(previous.get().getOrderNumber());
            previous.get().setOrderNumber(tempOrderNumber);
            repository.save(currentSlider);
            repository.save(previous.get());
            return true;
        }
        return false;
    }

    @Transactional
    public Boolean swapDown(Long id) throws Exception {
        Slider currentSlider = repository.findById(id).orElseThrow(NotFoundException::new);
        Optional<Slider> next = repository.findFirstByOrderNumberGreaterThanOrderByOrderNumberAsc(currentSlider.getOrderNumber());
        if (next.isPresent()){
            Integer tempOrderNumber = currentSlider.getOrderNumber();
            currentSlider.setOrderNumber(next.get().getOrderNumber());
            next.get().setOrderNumber(tempOrderNumber);
            repository.save(currentSlider);
            repository.save(next.get());
            return true;
        }
        return false;
    }

    @Override
    public void checkValidation(SliderDto dto) throws ValidationException {
        if (dto == null){
            throw new ValidationException("Please fill data");
        }
        if (dto.getTitle() == null || dto.getTitle().isEmpty()){
            throw new ValidationException("Please enter title");
        }
        if (dto.getLink() == null || dto.getLink().isEmpty()){
            throw new ValidationException("Please enter link");
        }
    }
}
