CREATE TABLE invoices (
	id VARCHAR(64) PRIMARY KEY,
	customer_gstin VARCHAR(32) NOT NULL,
	taxable_amount DECIMAL(19, 2) NOT NULL,
	status VARCHAR(20) NOT NULL,
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_invoices_status ON invoices (status);
CREATE INDEX idx_invoices_created_at ON invoices (created_at);

