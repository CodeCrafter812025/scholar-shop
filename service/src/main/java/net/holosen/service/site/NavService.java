package net.holosen.service.site;

import jakarta.transaction.Transactional;
import net.holosen.common.exceptions.NotFoundException;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dataaccess.entity.site.Nav;
import net.holosen.dataaccess.repository.site.NavRepository;
import net.holosen.dto.site.NavDto;
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
public class NavService implements CRUDService<NavDto> , HasValidation<NavDto> {
    private final NavRepository repository;
    private final ModelMapper mapper;

    @Autowired
    public NavService(NavRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<NavDto> readAll(){
        return repository.findAllByEnableIsTrueOrderByOrderNumberAsc()
                .stream().map(x -> mapper.map(x,NavDto.class)).toList();
    }

    @Override
    public Page<NavDto> readAll(Integer page, Integer size){
        if (page == null){
            page = 0;
        }
        if (size == null){
            size = 10;
        }
        return repository.findAll(Pageable.ofSize(size).withPage(page))
                .map(x -> mapper.map(x , NavDto.class));
    }

    @Override
    public NavDto create(NavDto navDto) throws Exception {
        checkValidation(navDto);
        Nav data = mapper.map(navDto , Nav.class);
        data.setEnable(true);
        Integer lastOrderNumber = repository.findLastOrderNumber();
        if (lastOrderNumber == null){
            lastOrderNumber = 0;
        }
        data.setOrderNumber(++lastOrderNumber);
        return mapper.map(repository.save(data) , NavDto.class);
    }

    @Override
    public Boolean delete(Long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public NavDto update(NavDto navDto) throws Exception {
        checkValidation(navDto);
        if (navDto.getId() == null || navDto.getId() < 0){
            throw new ValidationException("Please enter id to update");
        }
        Nav oldData = repository.findById(navDto.getId()).orElseThrow(NotFoundException::new);
        oldData.setOrderNumber(Optional.ofNullable(navDto.getOrderNumber()).orElse(oldData.getOrderNumber()));
        oldData.setLink(Optional.ofNullable(navDto.getLink()).orElse(oldData.getLink()));
        oldData.setTitle(Optional.ofNullable(navDto.getTitle()).orElse(oldData.getTitle()));
        repository.save(oldData);
        return mapper.map(oldData , NavDto.class);
    }

    @Transactional
    public Boolean swapUp(Long id) throws Exception {
        Nav currentNav = repository.findById(id).orElseThrow(NotFoundException::new);
        Optional<Nav> previous = repository.findFirstByOrderNumberLessThanOrderByOrderNumberDesc(currentNav.getOrderNumber());
        if (previous.isPresent()){
            Integer tempOrderNumber = currentNav.getOrderNumber();
            currentNav.setOrderNumber(previous.get().getOrderNumber());
            previous.get().setOrderNumber(tempOrderNumber);
            repository.save(currentNav);
            repository.save(previous.get());
            return true;
        }
        return false;
    }

    @Transactional
    public Boolean swapDown(Long id) throws Exception {
        Nav currentNav = repository.findById(id).orElseThrow(NotFoundException::new);
        Optional<Nav> next = repository.findFirstByOrderNumberGreaterThanOrderByOrderNumberAsc(currentNav.getOrderNumber());
        if (next.isPresent()){
            Integer tempOrderNumber = currentNav.getOrderNumber();
            currentNav.setOrderNumber(next.get().getOrderNumber());
            next.get().setOrderNumber(tempOrderNumber);
            repository.save(currentNav);
            repository.save(next.get());
            return true;
        }
        return false;
    }


    @Override
    public void checkValidation(NavDto navDto) throws ValidationException {
        if (navDto == null){
            throw new ValidationException("Please fill nav data");
        }
        if (navDto.getTitle() == null || navDto.getTitle().isEmpty()){
            throw new ValidationException("Please enter title");
        }
        if (navDto.getLink() == null || navDto.getLink().isEmpty()){
            throw new ValidationException("Please enter link");
        }
    }
}
