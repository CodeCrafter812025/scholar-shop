package net.holosen.app.controller.panel.user;

import jakarta.servlet.http.HttpServletRequest;
import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.controller.base.CRUDController;
import net.holosen.app.filter.JwtFilter;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.user.ChangePassDto;
import net.holosen.dto.user.UpdateProfileDto;
import net.holosen.dto.user.UserDto;
import net.holosen.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panel/user")
public class UserPanelController implements CRUDController<UserDto> {

    private final UserService service;

    @Autowired
    public UserPanelController(UserService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    @CheckPermission("info_user")
    public APIResponse<UserDto> getById(Long id , HttpServletRequest request){
        return APIResponse.<UserDto>builder()
                .data(service.readById(id))
                .status(APIStatus.Success)
                .build();
    }

    @Override
    @CheckPermission("add_user")
    public APIResponse<UserDto> add(UserDto dto) throws Exception {
        return APIResponse.<UserDto>builder()
                .status(APIStatus.Success)
                .data(service.create(dto))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("delete_user")
    public APIResponse<Boolean> delete(Long id) {
        return APIResponse.<Boolean>builder()
                .status(APIStatus.Success)
                .data(service.delete(id))
                .message("")
                .build();
    }

    @Override
    @CheckPermission("list_user")
    public APIPanelResponse<List<UserDto>> getAll(Integer page, Integer size) {
        Page<UserDto> data = service.readAll(page, size);
        return APIPanelResponse.<List<UserDto>>builder()
                .message("")
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .build();
    }

    @Override
    @CheckPermission("edit_user")
    public APIResponse<UserDto> edit(UserDto dto) throws Exception {
        return APIResponse.<UserDto>builder()
                .status(APIStatus.Success)
                .data(service.update(dto))
                .message("")
                .build();
    }


    @PutMapping("change-pass/admin")
    @CheckPermission("change_password_by_admin")
    public APIResponse<UserDto> changePasswordByAdmin(UserDto dto) throws Exception {
        return APIResponse.<UserDto>builder()
                .status(APIStatus.Success)
                .data(service.changePasswordByAdmin(dto))
                .message("")
                .build();
    }

    @PutMapping("change-pass")
    @CheckPermission("change_password_by_user")
    public APIResponse<UserDto> changePassword(ChangePassDto dto , HttpServletRequest request) throws Exception {
        UserDto user = (UserDto) request.getAttribute(JwtFilter.CURRENT_USER);
        return APIResponse.<UserDto>builder()
                .status(APIStatus.Success)
                .data(service.changePasswordByUser(dto , user))
                .message("")
                .build();
    }


    @PutMapping("update-profile")
    @CheckPermission("edit_my_user")
    public APIResponse<UserDto> editProfile(@RequestBody UpdateProfileDto dto , HttpServletRequest request) throws Exception {
        UserDto user = (UserDto) request.getAttribute(JwtFilter.CURRENT_USER);
        dto.setId(user.getId());
        return APIResponse.<UserDto>builder()
                .status(APIStatus.Success)
                .data(service.updateProfile(dto))
                .message("")
                .build();
    }
}
