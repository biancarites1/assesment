package com.example.demo.model;

public class NormalizedValueForCrypto {
    public NormalizedValueForCrypto(String currency, float normalizedValue) {
        this.currency = currency;
        this.normalizedValue = normalizedValue;
    }

    public String getCurrency() {
        return currency;
    }

    public float getNormalizedValue() {
        return normalizedValue;
    }

    private String currency;
    private float normalizedValue;
}
