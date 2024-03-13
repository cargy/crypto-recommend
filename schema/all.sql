\set ON_ERROR_STOP on
\set SCHEMA `echo $SCHEMA` 

\ir create-schema.sql
\ir crypto-prices.sql
\ir crypto-price-summary.sql
\ir crypto-prices-daily-view.sql
\ir crypto-prices-monthly-view.sql
