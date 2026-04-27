# GST Billing, Invoice, and Tax Management Backend

## Scope
- Simple Spring Boot backend for GST billing features
- Persistence-ready backend with JPA + Flyway migrations
- No complex architecture frameworks
- Frontend separate and out of scope

## Architecture Direction
- Everything stays inside `src/main/java`
- `api` for endpoints and DTOs
- `domain` for core business models and rules
- `application` for service layer (business orchestration)
- `infrastructure` for persistence adapters and entities
- Keep architecture practical and avoid unnecessary layers

## Engineering Rules
- Keep classes small and readable
- Prefer constructor injection
- Keep domain logic inside domain classes
- Avoid unnecessary interfaces and extra layers

## Current Implementation

Active packages in `src/main/java/com/amanna/billingmanagement`
- `api.invoice` with `InvoiceController` and DTOs
- `application` with `InvoiceService` orchestrating domain + persistence
- `domain.invoice` with `Invoice` and `InvoiceLineItem`
- `infrastructure.persistence` with JPA entities and repositories
- `POST /api/v1/invoices` creates invoice from line items
- `GET /api/v1/invoices/{id}` fetches invoice by id
- `GET /api/v1/invoices` lists invoices
- `GET /api/v1/invoices?status=DRAFT|ISSUED|CANCELLED` filters invoices by status
- `GET /api/v1/invoices?customerGstin=<GSTIN>` filters invoices by customer GSTIN
- `GET /api/v1/invoices?status=ISSUED&customerGstin=<GSTIN>` combines both filters
- `POST /api/v1/invoices/{id}/issue` marks an invoice as issued
- `POST /api/v1/invoices/{id}/cancel` marks an invoice as cancelled
- `POST /api/v1/invoices/{id}/update` updates draft invoice details
- `GET /api/v1/reports/gst-summary?from=YYYY-MM-DD&to=YYYY-MM-DD` returns CGST/SGST/IGST totals
- `GET /api/v1/reports/gst-summary/export?from=YYYY-MM-DD&to=YYYY-MM-DD` exports same summary as CSV

Dependency direction applied
- `api` depends on `domain`
- `domain` is framework-agnostic

## Features Implemented
- Invoice create (DRAFT status)
- Invoice fetch by id
- Invoice list (with optional status filter)
- Invoice list (optional status + customer GSTIN filters)
- Invoice lifecycle: DRAFT → ISSUED, DRAFT → CANCELLED
- Line-item based taxable amount calculation
- GST rule engine: intra-state => CGST+SGST, inter-state => IGST
- Invoice update (draft only)
- Validation for create/update payloads
- Default Spring error handling (minimal)
- JPA persistence through H2 + Flyway migration
- Minimal audit logs for invoice create/update/issue/cancel (timestamp + invoiceId + action)
- GST summary endpoint/export by date range

## Database Setup
- H2 in-memory for development (default)
- PostgreSQL for production (set via environment)
- Flyway migrations auto-run from `src/main/resources/db/migration/`

## How to Run

Build locally
```powershell
./mvnw clean package
```

Swagger / OpenAPI
```text
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```

This project is intentionally minimal, so there are no test files at the moment.

## Next Steps

- Add simple auth (single-role protected mutation endpoints)
- Add tests for domain and API lifecycle flows

