package com.amanna.billingmanagement.domain.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Invoice {

    private static final int MONEY_SCALE = 2;

    private final String id;
    private final String customerGstin;
    private final String sellerGstin;
    private final String placeOfSupply;
    private final List<InvoiceLineItem> lineItems;
    private final BigDecimal taxableAmount;
    private final Instant createdAt;
    private final InvoiceStatus status;

    private Invoice(
            String id,
            String customerGstin,
            String sellerGstin,
            String placeOfSupply,
            List<InvoiceLineItem> lineItems,
            BigDecimal taxableAmount,
            Instant createdAt,
            InvoiceStatus status
    ) {
        this.id = id;
        this.customerGstin = customerGstin;
        this.sellerGstin = sellerGstin;
        this.placeOfSupply = placeOfSupply;
        this.lineItems = List.copyOf(lineItems);
        this.taxableAmount = taxableAmount;
        this.createdAt = createdAt;
        this.status = status;
    }

    public static Invoice draft(String customerGstin, String sellerGstin, String placeOfSupply, List<InvoiceLineItem> lineItems) {
        validateInputs(customerGstin, sellerGstin, placeOfSupply, lineItems);
        return new Invoice(
                UUID.randomUUID().toString(),
                customerGstin.trim(),
                sellerGstin.trim(),
                normalizeStateCode(placeOfSupply),
                lineItems,
                sumTaxableAmount(lineItems),
                Instant.now(),
                InvoiceStatus.DRAFT
        );
    }

    public static Invoice reconstruct(
            String id,
            String customerGstin,
            String sellerGstin,
            String placeOfSupply,
            List<InvoiceLineItem> lineItems,
            BigDecimal taxableAmount,
            InvoiceStatus status,
            Instant createdAt
    ) {
        return new Invoice(id, customerGstin, sellerGstin, placeOfSupply, lineItems, taxableAmount, createdAt, status);
    }

    public String id() {
        return id;
    }

    public String customerGstin() {
        return customerGstin;
    }

    public String sellerGstin() {
        return sellerGstin;
    }

    public String placeOfSupply() {
        return placeOfSupply;
    }

    public List<InvoiceLineItem> lineItems() {
        return lineItems;
    }

    public BigDecimal taxableAmount() {
        return taxableAmount;
    }

    public BigDecimal cgstAmount() {
        if (isInterStateSupply()) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }
        return totalTaxAmount().divide(new BigDecimal("2"), MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal sgstAmount() {
        if (isInterStateSupply()) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }
        return totalTaxAmount().subtract(cgstAmount()).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal igstAmount() {
        if (isInterStateSupply()) {
            return totalTaxAmount();
        }
        return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal totalTaxAmount() {
        return lineItems.stream()
                .map(item -> item.taxableAmount().multiply(item.gstRate()).setScale(MONEY_SCALE, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP), BigDecimal::add)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal totalAmount() {
        return taxableAmount.add(totalTaxAmount()).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public InvoiceStatus status() {
        return status;
    }

    public Invoice cancel() {
        if (status == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Invoice is already cancelled");
        }
        return new Invoice(id, customerGstin, sellerGstin, placeOfSupply, lineItems, taxableAmount, createdAt, InvoiceStatus.CANCELLED);
    }

    public Invoice issue() {
        if (status == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled invoice cannot be issued");
        }
        if (status == InvoiceStatus.ISSUED) {
            throw new IllegalStateException("Invoice is already issued");
        }
        return new Invoice(id, customerGstin, sellerGstin, placeOfSupply, lineItems, taxableAmount, createdAt, InvoiceStatus.ISSUED);
    }

    public Invoice update(String customerGstin, String sellerGstin, String placeOfSupply, List<InvoiceLineItem> lineItems) {
        if (status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Only draft invoices can be updated");
        }
        validateInputs(customerGstin, sellerGstin, placeOfSupply, lineItems);
        return new Invoice(
                id,
                customerGstin.trim(),
                sellerGstin.trim(),
                normalizeStateCode(placeOfSupply),
                lineItems,
                sumTaxableAmount(lineItems),
                createdAt,
                status
        );
    }

    private static void validateInputs(String customerGstin, String sellerGstin, String placeOfSupply, List<InvoiceLineItem> lineItems) {
        if (customerGstin == null || customerGstin.isBlank()) {
            throw new IllegalArgumentException("Customer GSTIN is required");
        }
        if (sellerGstin == null || sellerGstin.isBlank()) {
            throw new IllegalArgumentException("Seller GSTIN is required");
        }
        if (placeOfSupply == null || placeOfSupply.isBlank()) {
            throw new IllegalArgumentException("Place of supply is required");
        }
        String stateCode = normalizeStateCode(placeOfSupply);
        if (stateCode.length() != 2) {
            throw new IllegalArgumentException("Place of supply must be a 2-digit state code");
        }
        if (lineItems == null || lineItems.isEmpty()) {
            throw new IllegalArgumentException("At least one line item is required");
        }
        lineItems.forEach(Objects::requireNonNull);
    }

    private static BigDecimal sumTaxableAmount(List<InvoiceLineItem> lineItems) {
        BigDecimal total = lineItems.stream()
                .map(InvoiceLineItem::taxableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.signum() <= 0) {
            throw new IllegalArgumentException("Taxable amount must be greater than zero");
        }
        return total.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private boolean isInterStateSupply() {
        return !extractStateCode(sellerGstin).equals(normalizeStateCode(placeOfSupply));
    }

    private static String extractStateCode(String gstin) {
        if (gstin == null || gstin.length() < 2) {
            throw new IllegalArgumentException("GSTIN must contain a valid state code");
        }
        return gstin.substring(0, 2);
    }

    private static String normalizeStateCode(String placeOfSupply) {
        return placeOfSupply == null ? null : placeOfSupply.trim();
    }
}

