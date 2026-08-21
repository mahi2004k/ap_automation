package com.ap_automation.dto.response.dashboard;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTrendDTO {

    private String month;

    private BigDecimal amount;

}