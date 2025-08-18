package net.holosen.dataaccess.repository.order;

import net.holosen.dataaccess.entity.order.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem , Long> {
}
