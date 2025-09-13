package com.example.carins.web.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ClaimDto(
    @NotNull LocalDate claimDate,
    @NotBlank String description,
    @NotNull @Positive Double amount
) {}
