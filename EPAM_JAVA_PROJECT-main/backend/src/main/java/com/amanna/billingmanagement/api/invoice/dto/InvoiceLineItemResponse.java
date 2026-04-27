package com.amanna.billingmanagement.api.invoice.dto;

import java.math.BigDecimal;

public record InvoiceLineItemResponse(
        String description,
        String hsnSac,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal gstRate,
        BigDecimal taxableAmount
) {
}

