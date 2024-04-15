# Getting Started

### Reference Documentation
Recommendation service:
● Reads all the prices from the csv files
● Calculates oldest/newest/min/max for each crypto for the whole month
● Exposes an endpoint that will return a descending sorted list of all the cryptos,
comparing the normalized range (i.e. (max-min)/min)
● Exposes an endpoint that will return the oldest/newest/min/max values for a requested
crypto
● Exposes an endpoint that will return the crypto with the highest normalized range for a
specific day