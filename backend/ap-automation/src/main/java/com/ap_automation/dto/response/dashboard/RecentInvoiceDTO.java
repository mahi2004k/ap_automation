package com.ap_automation.dto.response.dashboard;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentInvoiceDTO {

    private Long id;

    private String invoiceNumber;

    private String vendorName;

    private BigDecimal amount;

    private String status;

    private LocalDate date;

}