package net.holosen.dataaccess.repository.site;

import net.holosen.dataaccess.entity.site.Blog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Query("""
            from Blog where status = net.holosen.dataaccess.enums.BlogStatus.Published
            and publishDate <= CURRENT_TIMESTAMP
            order by publishDate desc
            """)
    List<Blog> findAllPublished(Pageable pageable);


}
