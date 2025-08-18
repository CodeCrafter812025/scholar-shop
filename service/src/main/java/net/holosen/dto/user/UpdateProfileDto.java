package net.holosen.dto.user;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UpdateProfileDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String mobile;
    private String tell;
    private String address;
    private String postalCode;
}
