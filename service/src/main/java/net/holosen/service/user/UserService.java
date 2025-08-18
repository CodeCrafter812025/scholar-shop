package net.holosen.service.user;

import lombok.SneakyThrows;
import net.holosen.common.exceptions.NotFoundException;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.common.utils.HashUtil;
import net.holosen.dataaccess.entity.user.Customer;
import net.holosen.dataaccess.entity.user.Role;
import net.holosen.dataaccess.entity.user.User;
import net.holosen.dataaccess.repository.user.CustomerRepository;
import net.holosen.dataaccess.repository.user.RoleRepository;
import net.holosen.dataaccess.repository.user.UserRepository;
import net.holosen.dto.user.*;
import net.holosen.service.base.CRUDService;
import net.holosen.service.base.HasValidation;
import net.holosen.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@Service
public class UserService implements CRUDService<UserDto>, HasValidation<UserDto> {
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper mapper;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       CustomerRepository customerRepository,
                       ModelMapper mapper, JwtUtil jwtUtil) {
        this.repository = userRepository;
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.mapper = mapper;
        this.jwtUtil = jwtUtil;
    }

    public LimitedUserDto login(LoginDto dto) throws Exception {
        String password = HashUtil.sha1Hash(dto.getPassword());
        User user = repository
                .findFirstByUsernameEqualsIgnoreCaseAndPassword(dto.getUsername(), password)
                .orElseThrow(NotFoundException::new);
        if (!user.getEnable()) {
            throw new ValidationException("Your user is disable. contact with support.");
        }
        LimitedUserDto result = mapper.map(user, LimitedUserDto.class);
        result.setToken(jwtUtil.generateToken(result.getUsername()));
        return result;
    }

    @SneakyThrows
    public UserDto readUserByUsername(String username) {
        User user = repository.findFirstByUsername(username).orElseThrow(NotFoundException::new);
        return mapper.map(user, UserDto.class);
    }

    @SneakyThrows
    public UserDto readById(Long id) {
        User user = repository.findById(id).orElseThrow(NotFoundException::new);
        return mapper.map(user, UserDto.class);
    }

    @Override
    public UserDto create(UserDto dto) throws ValidationException {
        checkValidation(dto);
        Optional<User> oldUser = repository.findFirstByUsername(dto.getUsername());
        if (oldUser.isPresent()) {
            throw new ValidationException("کاربری با این نام کاربری ثبت نام کرده. اگر نام کاربری مطعلق به شماست، لطفا ابتدا وارد شوید.");
        }
        Customer customer = customerRepository.save(mapper.map(dto.getCustomer(), Customer.class));
        User user = mapper.map(dto, User.class);
        user.setCustomer(customer);
        user.setPassword(HashUtil.sha1Hash(user.getPassword()));
        user.setRegisterDate(LocalDateTime.now());
        user.setEnable(true);
        Optional<Role> userRole = roleRepository.findFirstByNameEqualsIgnoreCase("user");
        if (userRole.isPresent()) {
            HashSet<Role> roles = new HashSet<>();
            roles.add(userRole.get());
            user.setRoles(roles);
        }
        User savedUser = repository.save(user);
        return mapper.map(savedUser, UserDto.class);
    }

    @Override
    public Boolean delete(Long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public Page<UserDto> readAll(Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }
        return repository.findAll(Pageable.ofSize(size).withPage(page))
                .map(x -> mapper.map(x, UserDto.class));
    }

    @Override
    public UserDto update(UserDto dto) throws Exception {
        checkValidation(dto);
        if (dto.getId() == null || dto.getId() < 0) {
            throw new ValidationException("Please enter id to update");
        }
        User oldData = repository.findById(dto.getId()).orElseThrow(NotFoundException::new);
        oldData.setMobile(Optional.ofNullable(dto.getMobile()).orElse(oldData.getMobile()));
        oldData.setEmail(Optional.ofNullable(dto.getEmail()).orElse(oldData.getEmail()));
        oldData.setEnable(Optional.ofNullable(dto.getEnable()).orElse(oldData.getEnable()));
        if (dto.getCustomer() != null) {
            oldData.setCustomer(Optional.ofNullable(mapper.map(dto.getCustomer(), Customer.class)).orElse(oldData.getCustomer()));
        }
        repository.save(oldData);
        return mapper.map(oldData, UserDto.class);
    }

    public UserDto changePasswordByAdmin(UserDto dto) throws Exception {
        if (dto.getId() == null || dto.getId() < 0){
            throw new ValidationException("Please enter id to update");
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()){
            throw new ValidationException("Please enter new password");
        }
        User oldData = repository.findById(dto.getId()).orElseThrow(NotFoundException::new);
        String password = HashUtil.sha1Hash(dto.getPassword());
        oldData.setPassword(password);
        repository.save(oldData);
        return mapper.map(oldData , UserDto.class);
    }

    public UserDto changePasswordByUser(ChangePassDto dto , UserDto userDto) throws Exception {
        if (dto == null){
            throw new ValidationException("Please fill data");
        }
        if (dto.getOldPassword() == null || dto.getOldPassword().isEmpty()){
            throw new ValidationException("Please enter old password");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().isEmpty()){
            throw new ValidationException("Please enter new password");
        }
        if (dto.getNewPassword2() == null || dto.getNewPassword2().isEmpty()){
            throw new ValidationException("Please enter repeat password");
        }
        User user = repository.findById(userDto.getId()).orElseThrow(NotFoundException::new);
        if (!dto.getOldPassword().equals(dto.getNewPassword2())){
            throw new ValidationException("Incorrect old password");
        }
        user.setPassword(HashUtil.sha1Hash(dto.getNewPassword()));
        repository.save(user);
        return userDto;
    }

    @Override
    public void checkValidation(UserDto dto) throws ValidationException {
        if (dto.getCustomer() == null) {
            throw new ValidationException("please enter customer information");
        }
        if (dto.getCustomer().getFirstname() == null || dto.getCustomer().getFirstname().isEmpty()) {
            throw new ValidationException("Please enter first name");
        }
        if (dto.getCustomer().getLastname() == null || dto.getCustomer().getLastname().isEmpty()) {
            throw new ValidationException("Please enter last name");
        }
        if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new ValidationException("Please enter username");
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new ValidationException("Please enter password");
        }
        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            throw new ValidationException("Please enter email");
        }
        if (dto.getMobile() == null || dto.getMobile().isEmpty()) {
            throw new ValidationException("Please enter mobile");
        }
        if (dto.getCustomer().getTel() == null || dto.getCustomer().getTel().isEmpty()) {
            throw new ValidationException("Please enter tel");
        }
        if (dto.getCustomer().getAddress() == null || dto.getCustomer().getAddress().isEmpty()) {
            throw new ValidationException("Please enter address");
        }
        if (dto.getCustomer().getPostalCode() == null || dto.getCustomer().getPostalCode().isEmpty()) {
            throw new ValidationException("Please enter postalCode");
        }
    }

    public UserDto updateProfile(UpdateProfileDto dto) throws Exception {
        if (dto.getFirstname() == null || dto.getFirstname().isEmpty()){
            throw new ValidationException("please enter first name");
        }
        if (dto.getLastname() == null || dto.getLastname().isEmpty()){
            throw new ValidationException("please enter last name");
        }
        if (dto.getEmail() == null || dto.getEmail().isEmpty()){
            throw new ValidationException("please enter email");
        }
        if (dto.getMobile() == null || dto.getMobile().isEmpty()){
            throw new ValidationException("please enter mobile");
        }
        if (dto.getTell() == null || dto.getTell().isEmpty()){
            throw new ValidationException("please enter tell");
        }
        if (dto.getAddress() == null || dto.getAddress().isEmpty()){
            throw new ValidationException("please enter address");
        }
        if (dto.getPostalCode() == null || dto.getPostalCode().isEmpty()){
            throw new ValidationException("please enter postalCode");
        }
        User oldData = repository.findById(dto.getId()).orElseThrow(NotFoundException::new);
        oldData.setMobile(Optional.ofNullable(dto.getMobile()).orElse(oldData.getMobile()));
        oldData.setEmail(Optional.ofNullable(dto.getEmail()).orElse(oldData.getEmail()));
        oldData.getCustomer().setAddress(Optional.ofNullable(dto.getAddress()).orElse(oldData.getCustomer().getAddress()));
        oldData.getCustomer().setTel(Optional.ofNullable(dto.getTell()).orElse(oldData.getCustomer().getTel()));
        oldData.getCustomer().setFirstname(Optional.ofNullable(dto.getFirstname()).orElse(oldData.getCustomer().getFirstname()));
        oldData.getCustomer().setLastname(Optional.ofNullable(dto.getLastname()).orElse(oldData.getCustomer().getLastname()));
        oldData.getCustomer().setPostalCode(Optional.ofNullable(dto.getPostalCode()).orElse(oldData.getCustomer().getPostalCode()));
        repository.save(oldData);
        return mapper.map(oldData , UserDto.class);

    }

}
