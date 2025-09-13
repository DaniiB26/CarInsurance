package com.example.carins.web.dto;

import java.time.LocalDate;

public record CarHistoryDto(
    Long carId,
    String vin,
    String make,
    String model,
    int year,
    String policyProvider,
    LocalDate policyStartDate,
    LocalDate policyEndDate,
    LocalDate claimDate,
    String claimDescription,
    Double claimAmount
) {}
