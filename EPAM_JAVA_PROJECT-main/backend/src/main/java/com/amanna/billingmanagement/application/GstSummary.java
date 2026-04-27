package com.amanna.billingmanagement.application;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GstSummary(
        LocalDate fromDate,
        LocalDate toDate,
        int invoiceCount,
        BigDecimal taxableAmount,
        BigDecimal cgstAmount,
        BigDecimal sgstAmount,
        BigDecimal igstAmount,
        BigDecimal totalTaxAmount,
        BigDecimal totalAmount
) {
}

