package net.holosen.dataaccess.repository.file;

import net.holosen.dataaccess.entity.file.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File , Long> {
    Optional<File> findFirstByNameEqualsIgnoreCase(String name);

}
