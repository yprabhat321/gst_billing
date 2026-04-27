package com.amanna.billingmanagement.infrastructure;

import com.amanna.billingmanagement.domain.invoice.Invoice;
import com.amanna.billingmanagement.domain.invoice.InvoiceLineItem;
import com.amanna.billingmanagement.infrastructure.persistence.entity.InvoiceEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class InvoiceMapper {

	private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

	public Invoice toDomain(InvoiceEntity entity) {
		return Invoice.reconstruct(
				entity.getId(),
				entity.getCustomerGstin(),
				entity.getSellerGstin(),
				entity.getPlaceOfSupply(),
				deserializeLineItems(entity.getLineItemsJson()),
				entity.getTaxableAmount(),
				entity.getStatus(),
				entity.getCreatedAt()
		);
	}

	public InvoiceEntity toEntity(Invoice invoice) {
		InvoiceEntity entity = new InvoiceEntity();
		entity.setId(invoice.id());
		entity.setCustomerGstin(invoice.customerGstin());
		entity.setSellerGstin(invoice.sellerGstin());
		entity.setPlaceOfSupply(invoice.placeOfSupply());
		entity.setLineItemsJson(serializeLineItems(invoice.lineItems()));
		entity.setTaxableAmount(invoice.taxableAmount());
		entity.setStatus(invoice.status());
		entity.setCreatedAt(invoice.createdAt());
		entity.setUpdatedAt(Instant.now());
		return entity;
	}

	private String serializeLineItems(List<InvoiceLineItem> lineItems) {
		try {
			return objectMapper.writeValueAsString(lineItems);
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Failed to serialize invoice line items", exception);
		}
	}

	private List<InvoiceLineItem> deserializeLineItems(String json) {
		try {
			return objectMapper.readValue(json, new TypeReference<>() {
			});
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Failed to deserialize invoice line items", exception);
		}
	}
}

