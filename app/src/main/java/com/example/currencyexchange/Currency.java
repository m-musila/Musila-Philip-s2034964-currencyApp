package com.example.currencyexchange;

import java.io.Serializable;

public class Currency implements Serializable {
    private String countryName;
    private String currencyCode;
    private double exchangeRate;

    // ... Constructors, getters, and setters
    public Currency() {
        // Default constructor
    }
    public Currency(String countryName, String currencyCode, double exchangeRate) {
        this.countryName = countryName;
        this.currencyCode = currencyCode;
        this.exchangeRate = exchangeRate;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }


}

