package com.example.demo.model;


import java.util.Date;

public class CryptoData {
    public CryptoData(Date timestamp, String symbol, float price) {
        this.timestamp = timestamp;
        this.symbol = symbol;
        this.price = price;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public float getPrice() {
        return price;
    }

    private Date timestamp;
    private String symbol;
    private float price;


}
