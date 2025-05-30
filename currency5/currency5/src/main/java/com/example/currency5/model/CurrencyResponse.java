package com.example.currency5.model;

public class CurrencyResponse {
    private Double amount;
    private String fromCurrency;
    private String toCurrency;
    private Double convertedAmount;
    private Double exchangeRate;

    public CurrencyResponse(Double amount, String fromCurrency, String toCurrency, Double convertedAmount, Double exchangeRate) {
        this.amount = amount;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.convertedAmount = convertedAmount;
        this.exchangeRate = exchangeRate;
    }

    public Double getAmount() {
        return amount;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public Double getConvertedAmount() {
        return convertedAmount;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }
}