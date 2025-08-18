package net.holosen.dataaccess.repository.site;

import net.holosen.dataaccess.entity.site.Nav;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NavRepository extends JpaRepository<Nav, Long> {

    List<Nav> findAllByEnableIsTrueOrderByOrderNumberAsc();

    @Query("""
              select orderNumber from Nav order by orderNumber desc limit 1
            """)
    Integer findLastOrderNumber();


    Optional<Nav> findFirstByOrderNumberLessThanOrderByOrderNumberDesc(Integer orderNumber);

    Optional<Nav> findFirstByOrderNumberGreaterThanOrderByOrderNumberAsc(Integer orderNumber);


}
