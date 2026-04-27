package com.amanna.billingmanagement.api.invoice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;

import java.util.List;

public record UpdateInvoiceRequest(
		@NotBlank(message = "Customer GSTIN is required")
		String customerGstin,
		@NotBlank(message = "Seller GSTIN is required")
		String sellerGstin,
		@NotBlank(message = "Place of supply is required")
		String placeOfSupply,
		@NotEmpty(message = "At least one line item is required")
		List<@Valid InvoiceLineItemRequest> lineItems
) {
}

