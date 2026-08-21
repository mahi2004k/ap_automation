package com.ap_automation.dto.response;

import com.ap_automation.dto.response.dashboard.InvoiceStatusDTO;
import com.ap_automation.dto.response.dashboard.InvoiceTrendDTO;
import com.ap_automation.dto.response.dashboard.PaymentTrendDTO;
import com.ap_automation.dto.response.dashboard.RecentInvoiceDTO;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    // ==========================
    // Existing Statistics
    // ==========================

    private long totalInvoices;

    private long matchedInvoices;

    private long pendingInvoices;

    private long needsReviewInvoices;

    private long totalPurchaseOrders;

    private long totalReceivingReports;

    private long totalUsers;


    // ==========================
    // Payment Statistics
    // ==========================

    private long completedPayments;

    private BigDecimal totalPayableAmount;

    private BigDecimal totalPaidAmount;

    private BigDecimal totalPendingAmount;


    // ==========================
    // Analytics
    // ==========================

    private List<InvoiceTrendDTO> invoiceTrend;

    private List<PaymentTrendDTO> paymentTrend;

    private List<InvoiceStatusDTO> invoiceStatus;


    // ==========================
    // Recent Invoices
    // ==========================

    private List<RecentInvoiceDTO> recentInvoices;

}