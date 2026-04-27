package com.amanna.billingmanagement.infrastructure.persistence.entity;

import com.amanna.billingmanagement.domain.invoice.InvoiceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "invoices")
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "customer_gstin", nullable = false)
    private String customerGstin;

    @Column(name = "seller_gstin", nullable = false)
    private String sellerGstin;

    @Column(name = "place_of_supply", nullable = false)
    private String placeOfSupply;

    @Column(name = "line_items_json", nullable = false, columnDefinition = "CLOB")
    private String lineItemsJson;

    @Column(name = "taxable_amount", nullable = false)
    private BigDecimal taxableAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerGstin() {
        return customerGstin;
    }

    public void setCustomerGstin(String customerGstin) {
        this.customerGstin = customerGstin;
    }

    public String getSellerGstin() {
        return sellerGstin;
    }

    public void setSellerGstin(String sellerGstin) {
        this.sellerGstin = sellerGstin;
    }

    public String getPlaceOfSupply() {
        return placeOfSupply;
    }

    public void setPlaceOfSupply(String placeOfSupply) {
        this.placeOfSupply = placeOfSupply;
    }

    public String getLineItemsJson() {
        return lineItemsJson;
    }

    public void setLineItemsJson(String lineItemsJson) {
        this.lineItemsJson = lineItemsJson;
    }

    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(BigDecimal taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}


