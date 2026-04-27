package com.amanna.billingmanagement.api.invoice;

import com.amanna.billingmanagement.api.invoice.dto.CreateInvoiceRequest;
import com.amanna.billingmanagement.api.invoice.dto.InvoiceLineItemRequest;
import com.amanna.billingmanagement.api.invoice.dto.InvoiceLineItemResponse;
import com.amanna.billingmanagement.api.invoice.dto.InvoiceResponse;
import com.amanna.billingmanagement.api.invoice.dto.UpdateInvoiceRequest;
import com.amanna.billingmanagement.application.InvoiceService;
import com.amanna.billingmanagement.domain.invoice.Invoice;
import com.amanna.billingmanagement.domain.invoice.InvoiceLineItem;
import com.amanna.billingmanagement.domain.invoice.InvoiceStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@CrossOrigin(originPatterns = {"*"})
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponse create(@Valid @RequestBody CreateInvoiceRequest request) {
        return toResponse(invoiceService.createDraft(
                request.customerGstin(),
                request.sellerGstin(),
                request.placeOfSupply(),
                toDomainLineItems(request.lineItems())
        ));
    }

    @GetMapping("/{id}")
    public InvoiceResponse getById(@PathVariable String id) {
        return toResponse(invoiceService.getById(id));
    }

    @GetMapping
    public List<InvoiceResponse> list(
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) String customerGstin
    ) {
        return invoiceService.list(status, customerGstin).stream()
            .map(this::toResponse)
            .toList();
    }

    @PostMapping("/{id}/cancel")
    public InvoiceResponse cancel(@PathVariable String id) {
        return toResponse(invoiceService.cancel(id));
    }

    @PostMapping("/{id}/issue")
    public InvoiceResponse issue(@PathVariable String id) {
        return toResponse(invoiceService.issue(id));
    }

    @PostMapping("/{id}/update")
    public InvoiceResponse update(@PathVariable String id, @Valid @RequestBody UpdateInvoiceRequest request) {
        return toResponse(invoiceService.update(
                id,
                request.customerGstin(),
                request.sellerGstin(),
                request.placeOfSupply(),
                toDomainLineItems(request.lineItems())
        ));
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
            invoice.id(),
            invoice.customerGstin(),
            invoice.sellerGstin(),
            invoice.placeOfSupply(),
            toResponseLineItems(invoice.lineItems()),
            invoice.taxableAmount(),
            invoice.cgstAmount(),
            invoice.sgstAmount(),
            invoice.igstAmount(),
            invoice.totalTaxAmount(),
            invoice.totalAmount(),
            invoice.status().name(),
            invoice.createdAt()
        );
    }

    private List<InvoiceLineItem> toDomainLineItems(List<InvoiceLineItemRequest> requests) {
        List<InvoiceLineItem> items = new ArrayList<>();
        for (InvoiceLineItemRequest request : requests) {
            items.add(new InvoiceLineItem(
                    request.description(),
                    request.hsnSac(),
                    request.quantity(),
                    request.unitPrice(),
                    request.gstRate().divide(new java.math.BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP)
            ));
        }
        return items;
    }

    private List<InvoiceLineItemResponse> toResponseLineItems(List<InvoiceLineItem> items) {
        List<InvoiceLineItemResponse> responses = new ArrayList<>();
        for (InvoiceLineItem item : items) {
            responses.add(new InvoiceLineItemResponse(
                    item.description(),
                    item.hsnSac(),
                    item.quantity(),
                    item.unitPrice(),
                    item.gstRate().multiply(new java.math.BigDecimal("100")).setScale(2, java.math.RoundingMode.HALF_UP),
                    item.taxableAmount()
            ));
        }
        return responses;
    }
}

