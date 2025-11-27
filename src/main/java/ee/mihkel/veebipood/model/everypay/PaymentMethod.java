package ee.mihkel.veebipood.model.everypay;

import lombok.Data;

@Data
public class PaymentMethod {
        private String source;
        private String display_name;
        private String country_code;
        private String payment_link;
        private String logo_url;
        private boolean applepay_available;
        private boolean googlepay_available;
        private String wallet_display_name;
        private boolean available;
}
