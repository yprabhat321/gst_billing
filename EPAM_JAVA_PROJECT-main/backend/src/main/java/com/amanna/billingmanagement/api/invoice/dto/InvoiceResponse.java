package com.amanna.billingmanagement.api.invoice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record InvoiceResponse(
		String id,
		String customerGstin,
		String sellerGstin,
		String placeOfSupply,
		List<InvoiceLineItemResponse> lineItems,
		BigDecimal taxableAmount,
		BigDecimal cgstAmount,
		BigDecimal sgstAmount,
		BigDecimal igstAmount,
		BigDecimal totalTaxAmount,
		BigDecimal totalAmount,
		String status,
		Instant createdAt
) {
}

