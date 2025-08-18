package net.holosen.dto.user;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RoleDto {
    private Long id;
    private String name;
    private String description;
    private Set<PermissionDto> permissions;
}
