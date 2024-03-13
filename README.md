# Crypto Recommendation Service

This project is PoC on how we could design and implement a crypto recommendation service, according to the requirements provided in [CryptoRecommendationsService1.pdf](doc/CryptoRecommendationsService1.pdf).

## Requirements

The following requirements were identified and are outlined below, as the first iteration for the implementation of the crypto recommendations service:

1. Read all the prices from CSV files. Each file contains multiple prices per day for a specific a crypto. Each file contains a month of data.
1. Calculate oldest/newest/min/max for each crypto for the whole month
1. Implement an API, including documentation, with endpoints that provide the following information:
    - All available cryptos ordered by normalized range (i.e. (max-min)/min ) in descending order
    - A specific crypto monthly prices (oldest/newes/min/max)
    - The crypto with the highest normalized range for a specific day
1. Take into consideration future scaling of the service for further cryptos and/or further aggregation (six months, year)
1. Consider the service will be deployed to a Kubernetes cluster for high available and scalability

## Assesment

According to the current requirements the *high granularity of the data in the CSV files* (**multiple prices / day / crypto**) is not necesary at runtime. We currenly only need **Daily** and **Monthly** aggregations. Taking also into account the those are historical data we can perform pre-calculations and provide higher level aggregations to the recommendation service.

That comes ofcourse with the downside that an pipeline will be needed in order to orchestrate the data export, transformation and loading. In order to separate concerns it would be a good idea to have one component handle the **data loading** and another providing the **recommendation endpoints**.

But even with just **Daily** and **Monthly** prices in one traditional RDBMS, the size of the table can increase significantly over the years or even further when new cryptos are added. Partitioning tha table on the aggregation level (`periodicity`) can improve read perfrormance as we are not usually expected to query between different aggregation levels.

## Database Schema

The database schema can 

## Implementation

This PoC implementation includes two different components demonstrating how such a crypto recommendation service could be implemented.

### crypto-importer

The [crypto-importer](./crypto-importer/) is responsible for monitoring a bucket with CSV files, importing them into the database and generating *Daily* and *Monthly* summaries ([crypto_price_summary](./schema/crypto-price-summary.sql) table).

That is a *Spring Batch* project, that uses *Spring Integration* to monitor for changes into the bucket. Spring Integration was chosen for the file parsing so there a separation of concerns between with the batch process and also because it provides easy integratin with vairous Cloud Object Storage services (e.g. Amazon S3, GCS, ...) that could be a good option because of their various benefits in cloud environemnts (versioning, easy integration, ...).

The Spring Batch project includes one job with three (3) steps:

1. [ReadCryptoFileStep.java](./crypto-importer/src/main/java/com/agileactors/cryptoimporter/config/ReadCryptoFileStep.java) - read the CSV file and load it to [crypto_prices](./schema/crypto-prices.sql) table
1. [MonthlySummaryStep.java](./crypto-importer/src/main/java/com/agileactors/cryptoimporter/config/MonthlySummaryStep.java) - generate **Monthly** summaries
1. [DailySummaryStep.java](./crypto-importer/src/main/java/com/agileactors/cryptoimporter/config/DailySummaryStep.java) - generate **Daily** summaries


### crypto-api

The [crypto-api](./crypto-api/) provides a REST API and it's [OpenAPI spec](http://localhost:8080/swagger-ui), with the following endpoints:

#### Find all cryptos

    GET /cryptos

Get all Cryptos, sorted by their monthly normalized range.

<details>
<summary><em>Response 200 OK</em></summary>

```json
[
  {
    "symbol": "ETH",
    "periodicity": "Monthly",
    "period": "20220100",
    "oldestPrice": 3715.32000,
    "newestPrice": 2672.50000,
    "minPrice": 2336.52000,
    "maxPrice": 3828.11000,
    "normalizedRange": 0.638381
  },
  {
    "symbol": "XRP",
    "periodicity": "Monthly",
    "period": "20220100",
    "oldestPrice": 0.82980,
    "newestPrice": 0.58670,
    "minPrice": 0.56160,
    "maxPrice": 0.84580,
    "normalizedRange": 0.5060541
  },
  {
    "symbol": "LTC",
    "periodicity": "Monthly",
    "period": "20220100",
    "oldestPrice": 148.10000,
    "newestPrice": 109.60000,
    "minPrice": 103.40000,
    "maxPrice": 151.50000,
    "normalizedRange": 0.46518376
  },
  {
    "symbol": "BTC",
    "periodicity": "Monthly",
    "period": "20220100",
    "oldestPrice": 46813.21000,
    "newestPrice": 38415.79000,
    "minPrice": 33276.59000,
    "maxPrice": 47722.66000,
    "normalizedRange": 0.4341211
  }
]
```

</details>

#### Find Crypto by symbol

    GET /cryptos/{symbol}

Returns a single crypto with it's monthly oldest/newest/min/max values.

<details>
<summary><em>Response 200 OK</em></summary>

```json
{
  "symbol": "BTC",
  "periodicity": "Monthly",
  "period": "20220100",
  "oldestPrice": 46813.21000,
  "newestPrice": 38415.79000,
  "minPrice": 33276.59000,
  "maxPrice": 47722.66000,
  "normalizedRange": 0.4341211
}
```

</details>

#### Find crypto with highest normalized range

    GET /cryptos/highest-range/{date}

Return the crypto with the highest normalized range for a specific day.

<details>
<summary><em>Response 200 OK</em></summary>

```json
{
  "symbol": "XRP",
  "periodicity": "Daily",
  "period": "20220101",
  "oldestPrice": 0.82980,
  "newestPrice": 0.84580,
  "minPrice": 0.82980,
  "maxPrice": 0.84580,
  "normalizedRange": 0.019281754
}
```

</details>

## Future Work

Some things that could further future proof the current implementation

- Add pagination to `/cryptos`, that could improve performance and UX
- Introduce a `crypto_config` table that would hold the supported cryptos. That could also include upcoming cryptos that are still getting backfilled by *crypto-importer* but not returned by `crypto-api` until they are fully avaiable.
