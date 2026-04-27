package com.amanna.billingmanagement.domain.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record InvoiceLineItem(
        String description,
        String hsnSac,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal gstRate
) {

    public InvoiceLineItem {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Line item description is required");
        }
        if (hsnSac == null || hsnSac.isBlank()) {
            throw new IllegalArgumentException("Line item HSN/SAC is required");
        }
        if (quantity == null || quantity.signum() <= 0) {
            throw new IllegalArgumentException("Line item quantity must be greater than zero");
        }
        if (unitPrice == null || unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("Line item unit price must be greater than zero");
        }
        if (gstRate == null || gstRate.signum() < 0) {
            throw new IllegalArgumentException("Line item GST rate cannot be negative");
        }
    }

    public BigDecimal taxableAmount() {
        return quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
    }
}

