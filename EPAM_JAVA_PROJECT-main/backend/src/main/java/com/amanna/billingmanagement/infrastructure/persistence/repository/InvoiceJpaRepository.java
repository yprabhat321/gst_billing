package com.amanna.billingmanagement.infrastructure.persistence.repository;

import com.amanna.billingmanagement.domain.invoice.InvoiceStatus;
import com.amanna.billingmanagement.infrastructure.persistence.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, String> {

	List<InvoiceEntity> findAllByOrderByCreatedAtAsc();

	List<InvoiceEntity> findByStatusOrderByCreatedAtAsc(InvoiceStatus status);

	List<InvoiceEntity> findByCustomerGstinOrderByCreatedAtAsc(String customerGstin);

	List<InvoiceEntity> findByStatusAndCustomerGstinOrderByCreatedAtAsc(InvoiceStatus status, String customerGstin);

	List<InvoiceEntity> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtAsc(Instant fromInclusive, Instant toExclusive);
}

