package com.amanna.billingmanagement.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    public void logInvoiceAction(String action, String invoiceId) {
        logger.info("invoice_audit timestamp={} invoiceId={} action={}", Instant.now(), invoiceId, action);
    }
}

