CREATE TABLE IF NOT EXISTS :SCHEMA.crypto_prices (
    timestamp TIMESTAMP NOT NULL,
    symbol VARCHAR(5) NOT NULL,
    price NUMERIC(10,5) NOT NULL,
    PRIMARY KEY (
        timestamp,
        symbol,
        price
    )
);