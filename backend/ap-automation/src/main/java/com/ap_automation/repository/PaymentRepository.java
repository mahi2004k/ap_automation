package com.ap_automation.repository;

import com.ap_automation.entity.Payment;
import com.ap_automation.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {


    // ==========================
    // Existing Queries
    // ==========================

    Optional<Payment> findByPaymentNumber(
            String paymentNumber
    );


    Optional<Payment> findByInvoiceId(
            Long invoiceId
    );


    // ==========================
    // Completed Payments
    // ==========================

    long countByStatus(
            PaymentStatus status
    );


    // ==========================
    // Total Paid Amount
    // ==========================

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = :status
            """)
    BigDecimal getTotalPaidAmount(
            @Param("status") PaymentStatus status
    );


    // ==========================
    // Monthly Payment Trend
    // PostgreSQL
    // ==========================

    @Query(value = """
            SELECT
                EXTRACT(MONTH FROM payment_date) AS month_number,
                COALESCE(SUM(amount), 0) AS total_amount
            FROM payments
            WHERE payment_date IS NOT NULL
              AND status = 'PAID'
              AND EXTRACT(YEAR FROM payment_date) =
                  EXTRACT(YEAR FROM CURRENT_DATE)
            GROUP BY EXTRACT(MONTH FROM payment_date)
            ORDER BY month_number
            """,
            nativeQuery = true)
    List<Object[]> getMonthlyPaymentTrend();

}