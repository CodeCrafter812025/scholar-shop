package net.holosen.service.payment.provider.zarinpal.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ZarinPalResponseWrapper {
    private ZarinPalResponse date;
    private Object[] errors;
}
