package net.holosen.service.order;

import net.holosen.common.exceptions.NotFoundException;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dataaccess.entity.order.Invoice;
import net.holosen.dataaccess.entity.order.InvoiceItem;
import net.holosen.dataaccess.enums.OrderStatus;
import net.holosen.dataaccess.repository.order.InvoiceItemRepository;
import net.holosen.dataaccess.repository.order.InvoiceRepository;
import net.holosen.dto.invoice.InvoiceDto;
import net.holosen.dto.product.ProductDto;
import net.holosen.service.base.CreateService;
import net.holosen.service.base.HasValidation;
import net.holosen.service.product.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceService implements CreateService<InvoiceDto> , HasValidation<InvoiceDto> {
    private final InvoiceRepository repository;
    private final InvoiceItemRepository itemRepository;
    private final ProductService productService;
    private final ModelMapper mapper;

    @Autowired
    public InvoiceService(InvoiceRepository repository,
                          InvoiceItemRepository itemRepository,
                          ProductService productService,
                          ModelMapper mapper) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.productService = productService;
        this.mapper = mapper;
    }

    @Override
    public InvoiceDto create(InvoiceDto dto) throws NotFoundException, ValidationException {
        checkValidation(dto);
        Invoice invoice = mapper.map(dto , Invoice.class);
        invoice.setCreateDate(LocalDateTime.now());
        invoice.setPayedDate(null);
        invoice.setStatus(OrderStatus.InProgress);
        long totalAmount = 0L;
        if (invoice.getItems() != null || invoice.getItems().size() > 0){
            for (InvoiceItem ii : invoice.getItems()){
                ProductDto product = productService.read(ii.getProduct().getId());
                ii.setPrice(product.getPrice());
                totalAmount += product.getPrice() * ii.getQuantity();
            }
        }
        invoice.setTotalAmount(totalAmount);
        Invoice savedInvoice = repository.save(invoice);
        return mapper.map(savedInvoice , InvoiceDto.class);
    }

    public List<InvoiceDto> readAllByUserId(Long userId){
        return repository.findAllByUser_id(userId).stream().map(x -> mapper.map(x , InvoiceDto.class)).toList();
    }

    public InvoiceDto read(Long id) throws NotFoundException {
        Invoice invoice = repository.findById(id).orElseThrow(NotFoundException::new);
        return mapper.map(invoice , InvoiceDto.class);
    }


    @Override
    public void checkValidation(InvoiceDto dto) throws ValidationException {
        // TODO : check validations
    }
}
