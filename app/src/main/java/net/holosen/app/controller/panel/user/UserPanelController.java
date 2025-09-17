package net.holosen.app.controller.panel.user;

import jakarta.servlet.http.HttpServletRequest;
import net.holosen.app.anotation.CheckPermission;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panel/user")
public class UserPanelController {

    private final UserService service;

    @Autowired
    public UserPanelController(UserService service) {
        this.service = service;
    }

    // لیست کاربران
    @GetMapping
    @CheckPermission("list_user")
    public APIPanelResponse<List<UserDto>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (page == null) page = 0;
        if (size == null) size = 20;
        Page<UserDto> data = service.readAll(page, size);
        return APIPanelResponse.<List<UserDto>>builder()
                .status(APIStatus.Success)
                .data(data.getContent())
                .totalCount(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .message("")
                .build();
    }

    // جزئیات کاربر
    @GetMapping("{id}")
    @CheckPermission("info_user")
    public APIResponse<UserDto> getById(@PathVariable Long id) {
        return APIResponse.<UserDto>builder()
                .status(APIStatus.Success)
                .data(service.readById(id))
                .build();
    }

    // ایجاد کاربر
    @PostMapping
    @CheckPermission("add_user")
    public APIResponse<UserDto> add(@RequestBody UserDto dto) throws Exception {
        return APIResponse.<UserDto>builder()
                .status(APIStatus.Success)
                .data(service.create(dto))
                .message("")
                .build();
    }

    // ویرایش کاربر
    @PutMapping
    @CheckPermission("edit_user")
    public APIResponse<UserDto> edit(@RequestBody UserDto dto) throws Exception {
        return APIResponse.<UserDto>builder()
                .status(APIStatus.Success)
                .data(service.update(dto))
                .message("")
                .build();
    }

    // حذف کاربر
    @DeleteMapping("{id}")
    @CheckPermission("delete_user")
    public APIResponse<Boolean> delete(@PathVariable Long id) {
        return APIResponse.<Boolean>builder()
                .status(APIStatus.Success)
                .data(service.delete(id))
                .message("")
                .build();
    }

    // تغییر رمز توسط ادمین
    @PutMapping("change-pass/admin")
    @CheckPermission("change_password_by_admin")
    public APIResponse<UserDto> changePasswordByAdmin(@RequestBody UserDto dto) throws Exception {
        return APIResponse.<UserDto>builder()
                .status(APIStatus.Success)
                .data(service.changePasswordByAdmin(dto))
                .message("")
                .build();
    }

    // تغییر رمز توسط خود کاربر
    @PutMapping("change-pass")
    @CheckPermission("change_password_by_user")
    public APIResponse<UserDto> changePassword(@RequestBody ChangePassDto dto,
                                               HttpServletRequest request) throws Exception {
        UserDto user = (UserDto) request.getAttribute(JwtFilter.CURRENT_USER);
        return APIResponse.<UserDto>builder()
                .status(APIStatus.Success)
                .data(service.changePasswordByUser(dto, user))
                .message("")
                .build();
    }

    // ویرایش پروفایل خود کاربر
    @PutMapping("update-profile")
    @CheckPermission("edit_my_user")
    public APIResponse<UserDto> editProfile(@RequestBody UpdateProfileDto dto,
                                            HttpServletRequest request) throws Exception {
        UserDto user = (UserDto) request.getAttribute(JwtFilter.CURRENT_USER);
        dto.setId(user.getId());
        return APIResponse.<UserDto>builder()
                .status(APIStatus.Success)
                .data(service.updateProfile(dto))
                .message("")
                .build();
    }
}
