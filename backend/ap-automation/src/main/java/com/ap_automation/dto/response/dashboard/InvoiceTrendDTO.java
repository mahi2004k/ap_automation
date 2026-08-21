package com.ap_automation.dto.response.dashboard;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceTrendDTO {

    private String month;

    private long invoices;

}