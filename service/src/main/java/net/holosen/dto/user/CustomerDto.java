package net.holosen.dto.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CustomerDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String tel;
    private String address;
    private String postalCode;

}
