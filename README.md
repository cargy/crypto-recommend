# Crypto Recommendation Service


## TODO

- [x] Read all the prices from the csv files
- [ ] Create Daily and Monthly Aggregations
- [ ] Add crypto validator in endpoints
- [ ] Create architectural diagram for the suggested architecture



## Requirements

### Reads all the prices from the csv files

### Calculate Monthly prices

Calculates oldest/newest/min/max for each crypto for the whole month.

### All the cryptos ordered by normalized range

Exposes an endpoint that will return a descending sorted list of all the cryptos, comparing the normalized range (i.e. (max-min)/min)

`GET /ws/cryptos` (order by normalized range be default)
Response:

```json
[
    {
        "code": "BTC",
        "normalized_range": 0.25
    },
        {
        "code": "DOGE",
        "normalized_range": 0.25
    }
]
```

1. Too many cryptos, we might need pagination

### Exposes an endpoint that will return the oldest/newest/min/max values for a requested crypto

`GET /ws/cryptos/BTC`

Response:

```json
    {
        "code": "BTC",
        "prices": { // price object, might need to add more prices later like average price?
            "oldest": 900,
            "newest": 990,
            "min": 800,
            "max": 1000
        }
    }
```

### Exposes an endpoint that will return the crypto with the highest normalized range for a specific day

`GET /ws/cryptos?day=2022-01-05&limit=1`

## Analysis

### Options

#### Option1: Polling storage for new/updated files

A k8s cron job runs every (12 hours?) and finds new files (hash comparison).
When a new file is found import endpoint is called passing the crypto code and file to be imported.

[ ] Make sure that cryptos are only available after being fully imported.

Q: What if cron runs before file import is completed?
A: Various Options depending on how often that could happen

1. Idempotent implementation (waste of resources if too often)
1. Optimistic locking, second task will fail in commit (waste of resources if too often)
1. Locking mechanism either using a distributed lock or a database lock.

#### Advantages

1. Simple way of distributing import work to multiple pods
2. Simple storage parsing logic, checking with db if file was previously imported or not

#### Disadvantages

- /import end point has different user/consumer (cron job) that /cryptos, complicates security
- 

#### Option2: Polling storage for new/updated files

So we can easily scale the importing process to multiple k8s nodes the implementation
should be able:

- handle concurrent import by multiple instances
- idepondent process duplicate imports
- 

- Calculates oldest/newest/min/max for each crypto for the whole 

Q: Should we do it while importing or on demand.

- Exposes an endpoint that will return a descending sorted list of all the cryptos, comparing the normalized range (i.e. (max-min)/min)


## Things to consider

Documentation is our best friend, so it will be good to share one for the endpoints

- Initially the cryptos are only five, but what if we want to include more? Will the recommendation service be able to scale?

1. Add pagination to API when returning a list 
2. 

- New cryptos pop up every day, so we might need to safeguard recommendations service endpoints from not currently supported cryptos
- For some cryptos it might be safe to invest, by just checking only one month's time frame. However, for some of them it might be more accurate to check six months or even a year. Will the recommendation service be able to handle this?

## Extra mile for recommendation service (optional):

- In XM we run everything on Kubernetes, so containerizing the recommendation service will add great value
[ ] Containererize application

- Malicious users will always exist, so it will be really beneficial if at least we can rate limit them (based on IP)

1. We are in a kubernetes environment with possibly multiple pods serving the API. We need a way to track requests per IP between all pods
