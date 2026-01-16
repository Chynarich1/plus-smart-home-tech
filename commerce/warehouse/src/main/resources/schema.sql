CREATE TABLE IF NOT EXISTS products (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN,
    weight REAL NOT NULL,
    width REAL NOT NULL,
    height REAL NOT NULL,
    depth REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS stocks (
    product_id UUID REFERENCES products(product_id) PRIMARY KEY,
    quantity BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings (
    order_id UUID PRIMARY KEY,
    delivery_id UUID
);

CREATE TABLE IF NOT EXISTS booking_products (
    order_id UUID REFERENCES bookings(order_id),
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (order_id, product_id)
);