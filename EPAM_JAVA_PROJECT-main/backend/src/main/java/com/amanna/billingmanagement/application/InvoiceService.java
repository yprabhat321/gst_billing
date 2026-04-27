package com.amanna.billingmanagement.application;

import com.amanna.billingmanagement.domain.invoice.Invoice;
import com.amanna.billingmanagement.domain.invoice.InvoiceLineItem;
import com.amanna.billingmanagement.domain.invoice.InvoiceStatus;
import com.amanna.billingmanagement.infrastructure.InvoiceMapper;
import com.amanna.billingmanagement.infrastructure.persistence.repository.InvoiceJpaRepository;
import com.amanna.billingmanagement.infrastructure.persistence.entity.InvoiceEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class InvoiceService {

	private final InvoiceJpaRepository invoiceJpaRepository;
	private final InvoiceMapper invoiceMapper;
	private final AuditLogService auditLogService;

	public InvoiceService(
			InvoiceJpaRepository invoiceJpaRepository,
			InvoiceMapper invoiceMapper,
			AuditLogService auditLogService
	) {
		this.invoiceJpaRepository = invoiceJpaRepository;
		this.invoiceMapper = invoiceMapper;
		this.auditLogService = auditLogService;
	}

	public Invoice createDraft(String customerGstin, String sellerGstin, String placeOfSupply, List<InvoiceLineItem> lineItems) {
		Invoice invoice = Invoice.draft(customerGstin, sellerGstin, placeOfSupply, lineItems);
		InvoiceEntity saved = invoiceJpaRepository.save(invoiceMapper.toEntity(invoice));
		Invoice createdInvoice = invoiceMapper.toDomain(saved);
		auditLogService.logInvoiceAction("CREATE", createdInvoice.id());
		return createdInvoice;
	}

	public Invoice getById(String id) {
		InvoiceEntity entity = invoiceJpaRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found: " + id));
		return invoiceMapper.toDomain(entity);
	}

	public List<Invoice> list(InvoiceStatus status, String customerGstin) {
		String normalizedCustomerGstin = normalizeCustomerGstin(customerGstin);
		List<InvoiceEntity> entities;
		if (status == null && normalizedCustomerGstin == null) {
			entities = invoiceJpaRepository.findAllByOrderByCreatedAtAsc();
		} else if (status != null && normalizedCustomerGstin == null) {
			entities = invoiceJpaRepository.findByStatusOrderByCreatedAtAsc(status);
		} else if (status == null) {
			entities = invoiceJpaRepository.findByCustomerGstinOrderByCreatedAtAsc(normalizedCustomerGstin);
		} else {
			entities = invoiceJpaRepository.findByStatusAndCustomerGstinOrderByCreatedAtAsc(status, normalizedCustomerGstin);
		}
		return entities.stream().map(invoiceMapper::toDomain).toList();
	}

	private String normalizeCustomerGstin(String customerGstin) {
		if (customerGstin == null || customerGstin.isBlank()) {
			return null;
		}
		return customerGstin.trim();
	}

	public Invoice issue(String id) {
		Invoice invoice = getById(id).issue();
		InvoiceEntity saved = invoiceJpaRepository.save(invoiceMapper.toEntity(invoice));
		Invoice issuedInvoice = invoiceMapper.toDomain(saved);
		auditLogService.logInvoiceAction("ISSUE", issuedInvoice.id());
		return issuedInvoice;
	}

	public Invoice cancel(String id) {
		Invoice invoice = getById(id).cancel();
		InvoiceEntity saved = invoiceJpaRepository.save(invoiceMapper.toEntity(invoice));
		Invoice cancelledInvoice = invoiceMapper.toDomain(saved);
		auditLogService.logInvoiceAction("CANCEL", cancelledInvoice.id());
		return cancelledInvoice;
	}

	public Invoice update(String id, String customerGstin, String sellerGstin, String placeOfSupply, List<InvoiceLineItem> lineItems) {
		Invoice invoice = getById(id).update(customerGstin, sellerGstin, placeOfSupply, lineItems);
		InvoiceEntity saved = invoiceJpaRepository.save(invoiceMapper.toEntity(invoice));
		Invoice updatedInvoice = invoiceMapper.toDomain(saved);
		auditLogService.logInvoiceAction("UPDATE", updatedInvoice.id());
		return updatedInvoice;
	}

	public GstSummary gstSummary(LocalDate fromDate, LocalDate toDate) {
		if (fromDate == null || toDate == null) {
			throw new IllegalArgumentException("Both from and to dates are required");
		}
		if (toDate.isBefore(fromDate)) {
			throw new IllegalArgumentException("To date cannot be before from date");
		}

		Instant fromInclusive = fromDate.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant toExclusive = toDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

		List<Invoice> invoices = invoiceJpaRepository
				.findByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtAsc(fromInclusive, toExclusive)
				.stream()
				.map(invoiceMapper::toDomain)
				.toList();

		BigDecimal taxableAmount = invoices.stream()
				.map(Invoice::taxableAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal cgstAmount = invoices.stream()
				.map(Invoice::cgstAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal sgstAmount = invoices.stream()
				.map(Invoice::sgstAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal igstAmount = invoices.stream()
				.map(Invoice::igstAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal totalTaxAmount = invoices.stream()
				.map(Invoice::totalTaxAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal totalAmount = invoices.stream()
				.map(Invoice::totalAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new GstSummary(
				fromDate,
				toDate,
				invoices.size(),
				taxableAmount,
				cgstAmount,
				sgstAmount,
				igstAmount,
				totalTaxAmount,
				totalAmount
		);
	}

	public String gstSummaryCsv(LocalDate fromDate, LocalDate toDate) {
		GstSummary summary = gstSummary(fromDate, toDate);
		return "fromDate,toDate,invoiceCount,taxableAmount,cgstAmount,sgstAmount,igstAmount,totalTaxAmount,totalAmount\n"
				+ summary.fromDate() + ","
				+ summary.toDate() + ","
				+ summary.invoiceCount() + ","
				+ summary.taxableAmount() + ","
				+ summary.cgstAmount() + ","
				+ summary.sgstAmount() + ","
				+ summary.igstAmount() + ","
				+ summary.totalTaxAmount() + ","
				+ summary.totalAmount()
				+ "\n";
	}
}

