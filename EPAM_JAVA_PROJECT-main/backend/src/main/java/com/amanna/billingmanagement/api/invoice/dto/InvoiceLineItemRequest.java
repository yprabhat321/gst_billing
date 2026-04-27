package com.amanna.billingmanagement.api.invoice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InvoiceLineItemRequest(
        @NotBlank(message = "Line item description is required")
        String description,
        @NotBlank(message = "Line item HSN/SAC is required")
        String hsnSac,
        @NotNull(message = "Line item quantity is required")
        @DecimalMin(value = "0.01", message = "Line item quantity must be greater than zero")
        BigDecimal quantity,
        @NotNull(message = "Line item unit price is required")
        @DecimalMin(value = "0.01", message = "Line item unit price must be greater than zero")
        BigDecimal unitPrice,
        @NotNull(message = "Line item GST rate is required")
        @DecimalMin(value = "0.00", message = "Line item GST rate cannot be negative")
        BigDecimal gstRate
) {
}

