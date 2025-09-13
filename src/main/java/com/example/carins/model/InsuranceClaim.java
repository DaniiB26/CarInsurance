package com.example.carins.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

@Entity
@Table(name = "insuranceclaim")
public class InsuranceClaim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    private InsurancePolicy policy;

    private LocalDate claimDate;
    private String description;
    private Double amount;
    
    public InsuranceClaim() {}
    public InsuranceClaim(Car car, InsurancePolicy policy, LocalDate claimDate, String description, Double amount) {
        this.car = car;
        this.policy = policy;
        this.claimDate = claimDate;
        this.description = description;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
    public InsurancePolicy getPolicy() { return policy; }
    public void setPolicy(InsurancePolicy policy) { this.policy = policy; }
    public LocalDate getClaimDate() { return claimDate; }
    public void setClaimDate(LocalDate claimDate) { this.claimDate = claimDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
