package net.holosen.dataaccess.repository.product;

import net.holosen.dataaccess.entity.product.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
}
