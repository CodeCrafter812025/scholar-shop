package net.holosen.dataaccess.repository.order;

import net.holosen.dataaccess.entity.order.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findAllByUser_id(Long id);
}
