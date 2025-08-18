package net.holosen.dto.user;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserDto {
    private Long id;
    private String username;
    private String mobile;
    private String password;
    private String email;
    private LocalDateTime registerDate;
    private Boolean enable = true;
    private Set<RoleDto> roles;
    private CustomerDto customer;
}
