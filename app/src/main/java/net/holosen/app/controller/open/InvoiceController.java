package net.holosen.app.controller.open;

import jakarta.servlet.http.HttpServletRequest;
import net.holosen.app.filter.JwtFilter;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.invoice.InvoiceDto;
import net.holosen.dto.invoice.InvoiceItemDto;
import net.holosen.dto.product.ProductDto;
import net.holosen.dto.user.LimitedUserDto;
import net.holosen.dto.user.UserDto;
import net.holosen.service.order.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * کنترلر عمومی برای ایجاد و مشاهده‌ی فاکتورهای کاربر.
 */
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * ایجاد فاکتور جدید بر اساس لیست آیتم‌ها برای کاربر جاری.
     *
     * @param request    بدنه‌ی شامل آیتم‌ها
     * @param httpRequest برای استخراج کاربر جاری از JwtFilter
     * @return APIResponse با فاکتور ایجاد شده یا خطا
     */
    @PostMapping
    public APIResponse<InvoiceDto> createInvoice(@RequestBody CreateInvoiceRequest request,
                                                 HttpServletRequest httpRequest) {
        try {
            UserDto currentUser = (UserDto) httpRequest.getAttribute(JwtFilter.CURRENT_USER);
            if (currentUser == null) {
                return APIResponse.<InvoiceDto>builder()
                        .status(APIStatus.Error)
                        .message("User not authenticated")
                        .build();
            }

            // تبدیل آیتم‌های ورودی به InvoiceItemDto
            List<InvoiceItemDto> items = request.getItems().stream().map(item ->
                    InvoiceItemDto.builder()
                            .product(ProductDto.builder().id(item.getProductId()).build())
                            .quantity(item.getQuantity())
                            .build()
            ).toList();

            // ایجاد فاکتور
            InvoiceDto invoice = invoiceService.create(
                    InvoiceDto.builder()
                            .user(LimitedUserDto.builder().id(currentUser.getId()).build())
                            .items(items)
                            .build()
            );
            return APIResponse.<InvoiceDto>builder()
                    .status(APIStatus.Success)
                    .data(invoice)
                    .build();
        } catch (Exception e) {
            return APIResponse.<InvoiceDto>builder()
                    .status(APIStatus.Error)
                    .message(e.getMessage())
                    .build();
        }
    }

    /**
     * دریافت لیست فاکتورهای کاربر جاری.
     *
     * @param httpRequest برای استخراج کاربر جاری از JwtFilter
     * @return APIResponse شامل فهرست فاکتورها
     */
    @GetMapping
    public APIResponse<List<InvoiceDto>> getUserInvoices(HttpServletRequest httpRequest) {
        try {
            UserDto currentUser = (UserDto) httpRequest.getAttribute(JwtFilter.CURRENT_USER);
            if (currentUser == null) {
                return APIResponse.<List<InvoiceDto>>builder()
                        .status(APIStatus.Error)
                        .message("User not authenticated")
                        .build();
            }

            List<InvoiceDto> invoices = invoiceService.readAllByUserId(currentUser.getId());
            return APIResponse.<List<InvoiceDto>>builder()
                    .status(APIStatus.Success)
                    .data(invoices)
                    .build();
        } catch (Exception e) {
            return APIResponse.<List<InvoiceDto>>builder()
                    .status(APIStatus.Error)
                    .message(e.getMessage())
                    .build();
        }
    }

    /** کلاس داخلی برای ساختار درخواست ایجاد فاکتور */
    public static class CreateInvoiceRequest {
        private List<Item> items;

        public List<Item> getItems() {
            return items;
        }
        public void setItems(List<Item> items) {
            this.items = items;
        }

        public static class Item {
            private Long productId;
            private Integer quantity;

            public Long getProductId() {
                return productId;
            }
            public void setProductId(Long productId) {
                this.productId = productId;
            }
            public Integer getQuantity() {
                return quantity;
            }
            public void setQuantity(Integer quantity) {
                this.quantity = quantity;
            }
        }
    }
}
