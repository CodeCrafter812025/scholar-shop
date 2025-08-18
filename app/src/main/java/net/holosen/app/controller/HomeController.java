package net.holosen.app.controller;

import net.holosen.app.model.APIResponse;
import net.holosen.app.model.enums.APIStatus;
import net.holosen.service.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
public class HomeController {

    @Value("${app.payment-gateway.zarinpal.callback-url}")
    private String callBackUrl;

    private final PaymentService paymentService;

    @Autowired
    public HomeController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("verify")
    public APIResponse<String> verify(@RequestParam String Authority , @RequestParam String Status){
        try {
            return APIResponse.<String>builder()
                    .status(APIStatus.Success)
                    .data(paymentService.verify(Authority , Status))
                    .build();
        }catch (Exception e){
            return APIResponse.<String>builder()
                    .status(APIStatus.Error)
                    .message(e.getMessage())
                    .build();
        }
    }

    @GetMapping("pg/StartPay/{Authority}")
    public APIResponse<String> startPay(@PathVariable String Authority){
        return APIResponse.<String>builder()
                .status(APIStatus.Success)
                .data(callBackUrl + "?Authority" + "&Status=OK")
                .build();
    }

}
