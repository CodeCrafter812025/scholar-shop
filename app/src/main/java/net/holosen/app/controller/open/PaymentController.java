package net.holosen.app.controller.open;

import jakarta.transaction.Transactional;
import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.dto.payment.GoToPaymentDto;
import net.holosen.dto.payment.PaymentDto;
import net.holosen.service.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService service;

    @Autowired
    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @Transactional
    @PostMapping("goToPayment")
    public APIResponse<String> goToPayment(@RequestBody GoToPaymentDto dto) {
        try {
            return APIResponse.<String>builder()
                    .status(APIStatus.Success)
                    .data(service.goToPayment(dto))
                    .build();
        }catch (Exception e){
            return APIResponse.<String>builder()
                    .status(APIStatus.Error)
                    .message(e.getMessage())
                    .build();

        }
    }

    @GetMapping("gateways")
    public APIResponse<List<PaymentDto>> getAllPaymentGateways(){
        return APIResponse.<List<PaymentDto>>builder()
                .status(APIStatus.Success)
                .data(service.readAllGateways())
                .build();
    }

}
