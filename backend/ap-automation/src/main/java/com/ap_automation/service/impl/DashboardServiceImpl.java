package com.ap_automation.service.impl;

import com.ap_automation.dto.response.DashboardResponse;
import com.ap_automation.dto.response.dashboard.InvoiceStatusDTO;
import com.ap_automation.dto.response.dashboard.InvoiceTrendDTO;
import com.ap_automation.dto.response.dashboard.PaymentTrendDTO;
import com.ap_automation.dto.response.dashboard.RecentInvoiceDTO;
import com.ap_automation.entity.Invoice;
import com.ap_automation.enums.InvoiceStatus;
import com.ap_automation.enums.PaymentStatus;
import com.ap_automation.repository.InvoiceRepository;
import com.ap_automation.repository.PaymentRepository;
import com.ap_automation.repository.PurchaseOrderRepository;
import com.ap_automation.repository.ReceivingReportRepository;
import com.ap_automation.repository.UserRepository;
import com.ap_automation.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl
        implements DashboardService {


    private final InvoiceRepository invoiceRepository;

    private final PaymentRepository paymentRepository;

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final ReceivingReportRepository receivingReportRepository;

    private final UserRepository userRepository;


    @Override
    public DashboardResponse getDashboard() {


        // ==========================================
        // Basic Statistics
        // ==========================================

        long totalInvoices =
                invoiceRepository.count();


        long matchedInvoices =
                invoiceRepository.countByStatus(
                        InvoiceStatus.MATCHED
                );


        long pendingInvoices =
                invoiceRepository.countByStatus(
                        InvoiceStatus.PENDING
                );


        long needsReviewInvoices =
                invoiceRepository.countByStatus(
                        InvoiceStatus.NEEDS_REVIEW
                );


        long totalPurchaseOrders =
                purchaseOrderRepository.count();


        long totalReceivingReports =
                receivingReportRepository.count();


        long totalUsers =
                userRepository.count();


        // ==========================================
        // Payment Statistics
        // ==========================================

        long completedPayments =
                paymentRepository.countByStatus(
                        PaymentStatus.PAID
                );


        BigDecimal totalPayableAmount =
                defaultBigDecimal(
                        invoiceRepository.getTotalPayableAmount()
                );


        BigDecimal totalPaidAmount =
                defaultBigDecimal(
                        paymentRepository.getTotalPaidAmount(
                                PaymentStatus.PAID
                        )
                );


        BigDecimal totalPendingAmount =
                defaultBigDecimal(
                        invoiceRepository.getTotalPendingAmount(
                                PaymentStatus.PAID
                        )
                );


        // ==========================================
        // Invoice Trend
        // ==========================================

        List<InvoiceTrendDTO> invoiceTrend =
                buildInvoiceTrend();


        // ==========================================
        // Payment Trend
        // ==========================================

        List<PaymentTrendDTO> paymentTrend =
                buildPaymentTrend();


        // ==========================================
        // Invoice Status
        // ==========================================

        List<InvoiceStatusDTO> invoiceStatus =
                buildInvoiceStatus();


        // ==========================================
        // Recent Invoices
        // ==========================================

        List<RecentInvoiceDTO> recentInvoices =
                buildRecentInvoices();


        // ==========================================
        // Response
        // ==========================================

        return DashboardResponse.builder()

                // Existing
                .totalInvoices(totalInvoices)

                .matchedInvoices(matchedInvoices)

                .pendingInvoices(pendingInvoices)

                .needsReviewInvoices(
                        needsReviewInvoices
                )

                .totalPurchaseOrders(
                        totalPurchaseOrders
                )

                .totalReceivingReports(
                        totalReceivingReports
                )

                .totalUsers(totalUsers)


                // Payments
                .completedPayments(
                        completedPayments
                )

                .totalPayableAmount(
                        totalPayableAmount
                )

                .totalPaidAmount(
                        totalPaidAmount
                )

                .totalPendingAmount(
                        totalPendingAmount
                )


                // Charts
                .invoiceTrend(invoiceTrend)

                .paymentTrend(paymentTrend)

                .invoiceStatus(invoiceStatus)


                // Table
                .recentInvoices(recentInvoices)


                .build();

    }


    // =====================================================
    // Invoice Trend
    // =====================================================

    private List<InvoiceTrendDTO> buildInvoiceTrend() {


        List<Object[]> results =
                invoiceRepository.getMonthlyInvoiceTrend();


        Map<Integer, Long> monthlyData =
                new HashMap<>();


        for (Object[] row : results) {

            Integer month =
                    ((Number) row[0]).intValue();

            Long count =
                    ((Number) row[1]).longValue();

            monthlyData.put(
                    month,
                    count
            );

        }


        List<InvoiceTrendDTO> trend =
                new ArrayList<>();


        for (int month = 1; month <= 12; month++) {

            trend.add(
                    InvoiceTrendDTO.builder()

                            .month(
                                    Month.of(month)
                                            .getDisplayName(
                                                    java.time.format.TextStyle.SHORT,
                                                    Locale.ENGLISH
                                            )
                            )

                            .invoices(
                                    monthlyData.getOrDefault(
                                            month,
                                            0L
                                    )
                            )

                            .build()
            );

        }


        return trend;

    }


    // =====================================================
    // Payment Trend
    // =====================================================

    private List<PaymentTrendDTO> buildPaymentTrend() {


        List<Object[]> results =
                paymentRepository.getMonthlyPaymentTrend();


        Map<Integer, BigDecimal> monthlyData =
                new HashMap<>();


        for (Object[] row : results) {

            Integer month =
                    ((Number) row[0]).intValue();


            BigDecimal amount =
                    convertToBigDecimal(row[1]);


            monthlyData.put(
                    month,
                    amount
            );

        }


        List<PaymentTrendDTO> trend =
                new ArrayList<>();


        for (int month = 1; month <= 12; month++) {

            trend.add(
                    PaymentTrendDTO.builder()

                            .month(
                                    Month.of(month)
                                            .getDisplayName(
                                                    java.time.format.TextStyle.SHORT,
                                                    Locale.ENGLISH
                                            )
                            )

                            .amount(
                                    monthlyData.getOrDefault(
                                            month,
                                            BigDecimal.ZERO
                                    )
                            )

                            .build()
            );

        }


        return trend;

    }


    // =====================================================
    // Invoice Status
    // =====================================================

    private List<InvoiceStatusDTO> buildInvoiceStatus() {


        List<Object[]> results =
                invoiceRepository.getInvoiceStatusDistribution();


        List<InvoiceStatusDTO> statusList =
                new ArrayList<>();


        for (Object[] row : results) {

            InvoiceStatus status =
                    (InvoiceStatus) row[0];


            long count =
                    ((Number) row[1]).longValue();


            statusList.add(

                    InvoiceStatusDTO.builder()

                            .name(
                                    formatStatus(
                                            status.name()
                                    )
                            )

                            .value(count)

                            .build()

            );

        }


        return statusList;

    }


    // =====================================================
    // Recent Invoices
    // =====================================================

    private List<RecentInvoiceDTO> buildRecentInvoices() {


        List<Invoice> invoices =
                invoiceRepository
                        .findTop10ByOrderByInvoiceDateDesc();


        return invoices.stream()

                .map(invoice ->

                        RecentInvoiceDTO.builder()

                                .id(
                                        invoice.getId()
                                )

                                .invoiceNumber(
                                        invoice.getInvoiceNumber()
                                )

                                .vendorName(
                                        invoice.getVendorName()
                                )

                                .amount(
                                        defaultBigDecimal(
                                                invoice.getTotalAmount()
                                        )
                                )

                                .status(
                                        invoice.getStatus() != null
                                                ? formatStatus(
                                                invoice.getStatus().name()
                                        )
                                                : "Unknown"
                                )

                                .date(
                                        invoice.getInvoiceDate()
                                )

                                .build()

                )

                .toList();

    }


    // =====================================================
    // Helpers
    // =====================================================

    private BigDecimal defaultBigDecimal(
            BigDecimal value
    ) {

        return value != null
                ? value
                : BigDecimal.ZERO;

    }


    private BigDecimal convertToBigDecimal(
            Object value
    ) {

        if (value == null) {

            return BigDecimal.ZERO;

        }


        if (value instanceof BigDecimal) {

            return (BigDecimal) value;

        }


        return new BigDecimal(
                value.toString()
        );

    }


    private String formatStatus(
            String status
    ) {

        if (status == null || status.isBlank()) {

            return "Unknown";

        }


        String formatted =
                status
                        .replace("_", " ")
                        .toLowerCase();


        return Character.toUpperCase(
                formatted.charAt(0)
        )
                + formatted.substring(1);

    }

}