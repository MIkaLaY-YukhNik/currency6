package com.example.currency5.dto;

import jakarta.validation.constraints.NotNull;

public class CurrencyConversionRequest {
    @NotNull(message = "Amount is required")
    private Double amount;

    @NotNull(message = "From currency is required")
    private String fromCurrency;

    @NotNull(message = "To currency is required")
    private String toCurrency;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }
}