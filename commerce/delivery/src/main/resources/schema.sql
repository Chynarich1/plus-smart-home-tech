
CREATE TABLE IF NOT EXISTS addresses (
    address_id UUID PRIMARY KEY,
    country VARCHAR(255),
    city VARCHAR(255),
    street VARCHAR(255),
    house VARCHAR(50),
    flat VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id UUID PRIMARY KEY,
    from_address_id UUID REFERENCES addresses(address_id) NOT NULL,
    to_address_id UUID REFERENCES addresses(address_id) NOT NULL,
    order_id UUID NOT NULL,
    delivery_state VARCHAR(255) NOT NULL
);