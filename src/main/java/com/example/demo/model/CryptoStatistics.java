package com.example.demo.model;

import java.util.Date;

public class CryptoStatistics {
    private final Date oldestDate;

    public Date getOldestDate() {
        return oldestDate;
    }

    public Date getNewestDate() {
        return newestDate;
    }

    public Float getMinPrice() {
        return minPrice;
    }

    public Float getMaxPrice() {
        return maxPrice;
    }

    private final Date newestDate;

    public CryptoStatistics(String crypto, Date oldestDate, Date newestDate, Float minPrice, Float maxPrice, float normalizedRange) {
        this.crypto = crypto;
        this.oldestDate = oldestDate;
        this.newestDate = newestDate;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.normalizedRange = normalizedRange;
    }

    private final Float minPrice;


    private final Float maxPrice;

    public String getCrypto() {
        return crypto;
    }
    private String crypto;

    public float getNormalizedRange() {
        return normalizedRange;
    }

    private final float normalizedRange;
}
