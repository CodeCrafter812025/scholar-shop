package net.holosen.service.payment;

import net.holosen.common.exceptions.NotFoundException;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dataaccess.entity.order.Invoice;
import net.holosen.dataaccess.entity.payment.Payment;
import net.holosen.dataaccess.entity.payment.Transaction;
import net.holosen.dataaccess.entity.user.User;
import net.holosen.dataaccess.repository.payment.PaymentRepository;
import net.holosen.dataaccess.repository.payment.TransactionRepository;
import net.holosen.dto.invoice.InvoiceDto;
import net.holosen.dto.invoice.InvoiceItemDto;
import net.holosen.dto.payment.GoToPaymentDto;
import net.holosen.dto.payment.PaymentDto;
import net.holosen.dto.product.ColorDto;
import net.holosen.dto.product.ProductDto;
import net.holosen.dto.product.SizeDto;
import net.holosen.dto.user.CustomerDto;
import net.holosen.dto.user.LimitedUserDto;
import net.holosen.dto.user.UserDto;
import net.holosen.service.order.InvoiceService;
import net.holosen.service.payment.provider.zarinpal.provider.ZarinPalProvider;
import net.holosen.service.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final TransactionRepository trxRepository;
    private final InvoiceService invoiceService;
    private final UserService userService;
    private final ModelMapper mapper;
    private final ZarinPalProvider zarinPalProvider;

    @Autowired
    public PaymentService(PaymentRepository repository,
                          TransactionRepository trxRepository,
                          InvoiceService invoiceService,
                          UserService userService,
                          ModelMapper mapper,
                          ZarinPalProvider zarinPalProvider) {
        this.repository = repository;
        this.trxRepository = trxRepository;
        this.invoiceService = invoiceService;
        this.userService = userService;
        this.mapper = mapper;
        this.zarinPalProvider = zarinPalProvider;
    }

    @Transactional
    public String goToPayment(GoToPaymentDto dto) throws Exception {


        /*
        *   1- Validation
            2- Create New User
            3- Create New Invoice + Invoice Items
            4- Calculate Total Amount
            5- Create New Transaction
            6- Send Request To Bank IPG
            7- Receive IPG Response
            8- Update Transaction => Save IPG Token
            9- Redirect User to IPG URL
        * */

        checkValidation(dto);

        UserDto user = userService.create(UserDto.builder()
                .username(dto.getUsername())
                .mobile(dto.getMobile())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .customer(CustomerDto.builder()
                        .firstname(dto.getFirstname())
                        .lastname(dto.getLastname())
                        .tel(dto.getTel())
                        .address(dto.getAddress())
                        .postalCode(dto.getPostalCode())
                        .build())
                .build());

        InvoiceDto invoice = invoiceService.create(InvoiceDto.builder()
                .user(LimitedUserDto.builder().id(user.getId()).build())
                .items(dto.getItems().stream().map(x -> InvoiceItemDto.builder()
                        .product(ProductDto.builder().id(x.getProductId()).build())
                        .color(ColorDto.builder().id(x.getColorId()).build())
                        .size(SizeDto.builder().id(x.getSizeId()).build())
                        .quantity(x.getQuantity())
                        .build()).toList())
                .build());

        String result = "";

            Payment gateway = repository.findFirstByNameEqualsIgnoreCase(dto.getGateway().toString()).orElseThrow(NotFoundException::new);

            Transaction trx = Transaction.builder()
                    .amount(invoice.getTotalAmount())
                    .payment(gateway)
                    .description("")
                    .customer(mapper.map(user, User.class))
                    .invoice(mapper.map(invoice, Invoice.class))
                    .build();


            switch (dto.getGateway()) {
                case Zarinpal -> {
                    result = zarinPalProvider.goToPayment(trx);
                }
                case CardToCard -> {
                }
                case MellatBank -> {
                }
                case TejaratBank -> {
                }
                case PasargardBank -> {
                }
            }
            trxRepository.save(trx);
            
        return result;

    }

    public String verify(String authority , String status) throws NotFoundException {
        if (status == null || status.isEmpty() || status.equalsIgnoreCase("NOK")){
            return "NOK";
        }
        if (status.equalsIgnoreCase("OK")){
            Transaction trx = trxRepository.findFirstByAuthorityEqualsIgnoreCase(authority).orElseThrow(NotFoundException::new);
            Transaction verifiedTrx = zarinPalProvider.verify(trx);
            trxRepository.save(verifiedTrx);
            return "OK";
        }
        return "NOK";
    }


    private static void checkValidation(GoToPaymentDto dto) throws ValidationException {
        if (dto.getGateway() == null) {
            throw new ValidationException("Please select payment gateway");
        }
        if (dto.getFirstname() == null || dto.getFirstname().isEmpty()) {
            throw new ValidationException("Please enter firstname");
        }
        if (dto.getLastname() == null || dto.getLastname().isEmpty()) {
            throw new ValidationException("Please enter lastname");
        }
        if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new ValidationException("Please enter username");
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new ValidationException("Please enter password");
        }
        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            throw new ValidationException("Please enter email");
        }
        if (dto.getMobile() == null || dto.getMobile().isEmpty()) {
            throw new ValidationException("Please enter mobile");
        }
        if (dto.getTel() == null || dto.getTel().isEmpty()) {
            throw new ValidationException("Please enter tel");
        }
        if (dto.getAddress() == null || dto.getAddress().isEmpty()) {
            throw new ValidationException("Please enter address");
        }
        if (dto.getPostalCode() == null || dto.getPostalCode().isEmpty()) {
            throw new ValidationException("Please enter postalCode");
        }
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new ValidationException("Please add at least one product to your basket");
        }
    }

    public List<PaymentDto> readAllGateways(){
        return repository.findAllByEnableIsTrue().stream().map(x -> mapper.map(x , PaymentDto.class)).toList();
    }

}
