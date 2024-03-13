CREATE OR REPLACE VIEW :SCHEMA.crypto_prices_monthly_view AS
WITH open_closing_prices AS (
    SELECT
        timestamp,
        TO_CHAR(timestamp, 'YYYYMM00') AS period,
        symbol,
        price,
        FIRST_VALUE (price) OVER (PARTITION BY symbol, TO_CHAR(timestamp, 'YYYYMM00') ORDER BY timestamp) oldest_price,
        FIRST_VALUE (price) OVER (PARTITION BY symbol, TO_CHAR(timestamp, 'YYYYMM00') ORDER BY timestamp DESC) newest_price
    FROM crypto_prices
)
SELECT
    period,
    symbol,
    oldest_price,
    newest_price,
    min(price) as min_price,
    max(price) as max_price
FROM open_closing_prices
GROUP BY
    period,
    symbol,
    oldest_price,
    newest_price;