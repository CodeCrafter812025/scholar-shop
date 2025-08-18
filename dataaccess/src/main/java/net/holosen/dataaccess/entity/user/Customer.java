package net.holosen.dataaccess.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String firstname;

    @Column(length = 500)
    private String lastname;

    @Column(length = 15)
    private String tel;

    @Column(length = 1000)
    private String address;

    @Column(length = 15)
    private String postalCode;

}
