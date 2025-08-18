package net.holosen.dto.user;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ChangePassDto {
    private String oldPassword;
    private String newPassword;
    private String newPassword2;
}
