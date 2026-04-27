package com.amanna.billingmanagement.api.report;

import com.amanna.billingmanagement.application.GstSummary;
import com.amanna.billingmanagement.application.InvoiceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(
        originPatterns = {"*"},
        exposedHeaders = {HttpHeaders.CONTENT_DISPOSITION}
)
public class GstReportController {

    private final InvoiceService invoiceService;

    public GstReportController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/gst-summary")
    public GstSummary gstSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return invoiceService.gstSummary(from, to);
    }

    @GetMapping("/gst-summary/export")
    public ResponseEntity<byte[]> gstSummaryExport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        String csv = invoiceService.gstSummaryCsv(from, to);
        String filename = "gst-summary-" + from + "-to-" + to + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }
}

