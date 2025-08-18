package net.holosen.app.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.filter.JwtFilter;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.user.PermissionDto;
import net.holosen.dto.user.UserDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class PermissionAspect {
    private final HttpServletRequest request;


    @Autowired
    public PermissionAspect(HttpServletRequest request) {
        this.request = request;
    }

    @SneakyThrows
    @Around("@annotation(checkPermission)")
    public Object checkUserPermission(ProceedingJoinPoint joinPoint, CheckPermission checkPermission) {
        UserDto user = (UserDto) request.getAttribute(JwtFilter.CURRENT_USER);
        if (user == null) {
            return APIResponse.builder()
                    .message("Please Login First!")
                    .status(APIStatus.Forbidden)
                    .build();
        }
        List<String> permissions = user.getRoles().stream().flatMap(x -> x.getPermissions().stream().map(PermissionDto::getName)).toList();
        if (!permissions.contains(checkPermission.value())) {
            return APIResponse.builder()
                    .status(APIStatus.Forbidden)
                    .message("Access Denied!")
                    .build();
        }
        return joinPoint.proceed();
    }
}
