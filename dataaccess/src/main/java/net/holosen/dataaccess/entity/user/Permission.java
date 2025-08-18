package net.holosen.dataaccess.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Add_Product

    @Column(columnDefinition = "TEXT")
    private String description; // دسترسی ایجاد محصول جدید

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Permission parent; // root

    @OneToMany(mappedBy = "parent")
    private List<Permission> children = new ArrayList<>();
}
