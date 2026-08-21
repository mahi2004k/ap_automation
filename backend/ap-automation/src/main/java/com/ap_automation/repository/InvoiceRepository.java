package com.ap_automation.repository;

import com.ap_automation.entity.Invoice;
import com.ap_automation.enums.InvoiceStatus;
import com.ap_automation.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository
        extends JpaRepository<Invoice, Long> {


    // ==========================
    // Existing Queries
    // ==========================

    long countByStatus(InvoiceStatus status);


    Optional<Invoice> findByInvoiceNumberAndVendorName(
            String invoiceNumber,
            String vendorName
    );


    List<Invoice> findByVendorName(
            String vendorName
    );


    List<Invoice> findByStatus(
            InvoiceStatus status
    );


    // ==========================
    // Recent Invoices
    // ==========================

    List<Invoice> findTop10ByOrderByInvoiceDateDesc();


    // ==========================
    // Financial Analytics
    // ==========================

    @Query("""
            SELECT COALESCE(SUM(i.totalAmount), 0)
            FROM Invoice i
            """)
    BigDecimal getTotalPayableAmount();


    @Query("""
            SELECT COALESCE(SUM(i.totalAmount), 0)
            FROM Invoice i
            WHERE i.paymentStatus IS NULL
               OR i.paymentStatus <> :status
            """)
    BigDecimal getTotalPendingAmount(
            @Param("status") PaymentStatus status
    );


    // ==========================
    // Monthly Invoice Trend
    // PostgreSQL
    // ==========================

    @Query(value = """
            SELECT
                EXTRACT(MONTH FROM invoice_date) AS month_number,
                COUNT(*) AS invoice_count
            FROM invoices
            WHERE invoice_date IS NOT NULL
              AND EXTRACT(YEAR FROM invoice_date) = EXTRACT(YEAR FROM CURRENT_DATE)
            GROUP BY EXTRACT(MONTH FROM invoice_date)
            ORDER BY month_number
            """,
            nativeQuery = true)
    List<Object[]> getMonthlyInvoiceTrend();


    // ==========================
    // Invoice Status Distribution
    // ==========================

    @Query("""
            SELECT i.status, COUNT(i)
            FROM Invoice i
            WHERE i.status IS NOT NULL
            GROUP BY i.status
            ORDER BY i.status
            """)
    List<Object[]> getInvoiceStatusDistribution();

}