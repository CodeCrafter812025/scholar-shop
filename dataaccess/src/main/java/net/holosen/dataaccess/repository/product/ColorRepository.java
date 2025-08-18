package net.holosen.dataaccess.repository.product;

import net.holosen.dataaccess.entity.product.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color , Long> {
}
