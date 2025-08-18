package net.holosen.service.payment.provider.zarinpal.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ZarinPalVerifyResponse {
    private String code;
    private String message;
    private String ref_id;
    private String card_pan;
    private String card_hash;
    private String fee_type;
    private Integer fee;

}
