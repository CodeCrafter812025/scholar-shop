package net.holosen.service.payment.provider.zarinpal.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ZarinPalVerifyResponseWrapper {
    private ZarinPalVerifyResponse date;
    private Object[] errors;
}
