-- Create tracking_numbers table
CREATE TABLE IF NOT EXISTS tracking_numbers (
    id BIGSERIAL PRIMARY KEY,
    tracking_number VARCHAR(50) NOT NULL UNIQUE,
    origin_country_id VARCHAR(2) NOT NULL,
    destination_country_id VARCHAR(2) NOT NULL,
    weight DECIMAL(10, 3) NOT NULL,
    order_created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    customer_id UUID NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_slug VARCHAR(255) NOT NULL,
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create index on tracking_number for faster lookups
CREATE INDEX IF NOT EXISTS idx_tracking_number ON tracking_numbers(tracking_number);

-- Create index on customer_id for faster queries by customer
CREATE INDEX IF NOT EXISTS idx_customer_id ON tracking_numbers(customer_id);
