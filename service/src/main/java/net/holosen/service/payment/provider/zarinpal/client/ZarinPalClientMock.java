package net.holosen.service.payment.provider.zarinpal.client;

import net.holosen.service.payment.provider.zarinpal.request.ZarinPalRequest;
import net.holosen.service.payment.provider.zarinpal.request.ZarinPalVerifyRequest;
import net.holosen.service.payment.provider.zarinpal.response.ZarinPalResponse;
import net.holosen.service.payment.provider.zarinpal.response.ZarinPalVerifyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ZarinPalClientMock {
    @Value("${app.payment-gateway.zarinpal.base-url}")
    private String baseUrl;

    public ZarinPalResponse goToPayment(ZarinPalRequest request){
        return ZarinPalResponse.builder()
                .authority("MOCK_DATA_AUTHORITY_" + new Random().nextInt(100000,999999))
                .code("100")
                .message("MOCK_DATA")
                .build();
    }

    public ZarinPalVerifyResponse verifyPayment(ZarinPalVerifyRequest request){
        return ZarinPalVerifyResponse.builder()
                .code("100")
                .message("Verified")
                .card_hash("1EBE3EBEBE35C7EC0F8D6EE4F2F859107A87822CA179BC9528767EA7B5489B69")
                .card_pan("502229******5995")
                .ref_id("201")
                .fee_type("Merchant")
                .fee(0)
                .build();

    }


}
