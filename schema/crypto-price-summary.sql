-- Spring Data JDBC doesn't work well with Postgres enum type
-- CREATE TYPE :SCHEMA.periodicity AS ENUM ('Daily', 'Monthly');

CREATE TABLE IF NOT EXISTS :SCHEMA.crypto_price_summary (
    symbol VARCHAR(5) NOT NULL,
    periodicity VARCHAR(10) NOT NULL, -- Daily, Monthly
    period TEXT NOT NULL,
    oldest_price NUMERIC(10,5) NOT NULL,
    newest_price NUMERIC(10,5) NOT NULL,
    min_price NUMERIC(10,5) NOT NULL,
    max_price NUMERIC(10,5) NOT NULL,
    normalized_range FLOAT NOT NULL,
    PRIMARY KEY (
        symbol,
        periodicity,
        period
    )
)
PARTITION BY LIST (periodicity);

CREATE TABLE :SCHEMA.crypto_price_summary_daily
PARTITION OF :SCHEMA.crypto_price_summary FOR VALUES IN ('Daily');

CREATE TABLE :SCHEMA.crypto_price_summary_monthly
PARTITION OF :SCHEMA.crypto_price_summary FOR VALUES IN ('Monthly');
