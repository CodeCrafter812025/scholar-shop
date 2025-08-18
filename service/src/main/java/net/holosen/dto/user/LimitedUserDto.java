package net.holosen.dto.user;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LimitedUserDto {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String token;

    public String fullName() {
        return firstname + " " + lastname;
    }
}
