package net.holosen.app.controller.panel.order;

import jakarta.servlet.http.HttpServletRequest;
import net.holosen.app.anotation.CheckPermission;
import net.holosen.app.controller.base.CreateController;
import net.holosen.app.controller.base.ReadController;
import net.holosen.app.controller.base.UpdateController;
import net.holosen.app.filter.JwtFilter;
import net.holosen.app.model.APIPanelResponse;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.common.exceptions.ValidationException;
import net.holosen.dto.invoice.InvoiceDto;
import net.holosen.dto.product.ColorDto;
import net.holosen.dto.user.UserDto;
import net.holosen.service.order.InvoiceService;
import net.holosen.service.product.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/panel/invoice")
public class InvoicePanelController {
    private final InvoiceService service;

    @Autowired
    public InvoicePanelController(InvoiceService service) {
        this.service = service;
    }

    @GetMapping("user/{uid}")
    @CheckPermission("list_invoice")
    public APIResponse<List<InvoiceDto>> getAllByUser(@PathVariable Long uid){
        return APIResponse.<List<InvoiceDto>>builder()
                .message("")
                .data(service.readAllByUserId(uid))
                .status(APIStatus.Success)
                .build();
    }

    @GetMapping("{id}")
    @CheckPermission("info_invoice")
    public APIResponse<InvoiceDto> get(@PathVariable Long id){
        try {
            return APIResponse.<InvoiceDto>builder()
                    .message("")
                    .data(service.read(id))
                    .status(APIStatus.Success)
                    .build();
        }catch (Exception e){
            return APIResponse.<InvoiceDto>builder()
                    .message("")
                    .status(APIStatus.Error)
                    .build();
        }
    }

    @GetMapping("mine")
    @CheckPermission("list_my_invoice")
    public APIResponse<List<InvoiceDto>> getMineList(HttpServletRequest request){
        UserDto user = (UserDto) request.getAttribute(JwtFilter.CURRENT_USER);
        return APIResponse.<List<InvoiceDto>>builder()
                .message("")
                .status(APIStatus.Success)
                .data(service.readAllByUserId(user.getId()))
                .build();
    }

    @GetMapping("mine/{id}")
    @CheckPermission("info_invoice")
    public APIResponse<InvoiceDto> getMineInfo(@PathVariable Long id , HttpServletRequest request) {
        UserDto user = (UserDto) request.getAttribute(JwtFilter.CURRENT_USER);
        try {
            InvoiceDto data = service.read(id);
            if (!data.getUser().getId().equals(user.getId())){
                throw new ValidationException("Access Denied to view this invoice!");
            }
            return APIResponse.<InvoiceDto>builder()
                    .message("")
                    .status(APIStatus.Success)
                    .data(data)
                    .build();
        }catch (Exception e){
            return APIResponse.<InvoiceDto>builder()
                    .message(e.getMessage())
                    .status(APIStatus.Error)
                    .build();
        }
    }
}
