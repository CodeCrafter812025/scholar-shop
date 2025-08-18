package net.holosen.dataaccess.repository.site;

import net.holosen.dataaccess.entity.site.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    Optional<Content> findFirstByKeyNameEqualsIgnoreCase(String key);

}
