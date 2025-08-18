package net.holosen.dataaccess.repository.payment;

import net.holosen.dataaccess.entity.payment.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction , Long> {
    Optional<Transaction> findFirstByAuthorityEqualsIgnoreCase(String authority);

}
