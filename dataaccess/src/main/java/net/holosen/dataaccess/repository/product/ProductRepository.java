package net.holosen.dataaccess.repository.product;

import net.holosen.dataaccess.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            from Product where enable = true
            and exist = true
            order by visitCount desc
            limit 6
            """)
    List<Product> find6PopularProducts();

    @Query("""
            from Product where enable = true
            and exist = true
            order by addDate desc
            limit 6
            """)
    List<Product> find6NewestProducts();

    @Query("""
            from Product where enable = true
            and exist = true
            order by price asc
            limit 6
            """)
    List<Product> find6CheapestProducts();

    @Query("""
            from Product where enable = true
            and exist = true
            order by price desc
            limit 6
            """)
    List<Product> find6ExpensiveProducts();

    Page<Product> findAllByCategory_Id(Long id , Pageable pageable);

    Page<Product> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // برای پنل: فقط محصولات فعال/موجود
    Page<Product> findByEnableTrueAndExistTrue(Pageable pageable);
}
