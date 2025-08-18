package net.holosen.service.payment.provider.zarinpal.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ZarinPalRequest {
    private String merchant_id;
    private Integer amount;
    private String currency;
    private String description;
    private String callback_url;
    private MetaData metadata;

    @Getter
    @Setter
    @Builder
    public static class MetaData{
        private String mobile;
        private String email;
        private String order_id;
    }
}
