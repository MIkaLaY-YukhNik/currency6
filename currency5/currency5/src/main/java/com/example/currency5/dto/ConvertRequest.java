package com.example.currency5.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ConvertRequest {
    @NotBlank(message = "From currency cannot be blank")
    private String from;

    @NotBlank(message = "To currency cannot be blank")
    private String to;

    @Positive(message = "Amount must be positive")
    private double amount;
}